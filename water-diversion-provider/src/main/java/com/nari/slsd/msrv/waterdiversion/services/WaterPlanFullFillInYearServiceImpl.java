package com.nari.slsd.msrv.waterdiversion.services;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nari.slsd.msrv.common.exception.TransactionException;
import com.nari.slsd.msrv.common.ext.enums.CodeEnum;
import com.nari.slsd.msrv.common.utils.DateUtils;
import com.nari.slsd.msrv.common.utils.IDGenerator;
import com.nari.slsd.msrv.waterdiversion.commons.PlanTypeEnum;
import com.nari.slsd.msrv.waterdiversion.commons.TDayTypeEnum;
import com.nari.slsd.msrv.waterdiversion.commons.TaskStateEnum;
import com.nari.slsd.msrv.waterdiversion.commons.WrBuildingEnum;
import com.nari.slsd.msrv.waterdiversion.config.excel.WaterPlanYearModel;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWaterPlanFullFillInYearService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrPlanFillInYearService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrPlanInterTdayService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrPlanTaskSubService;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.*;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.nari.slsd.msrv.waterdiversion.commons.WrBuildingEnum.BUILDING_LEVEL_1_2;
import static com.nari.slsd.msrv.waterdiversion.config.listener.WaterPlanYearDataListener.getNameFieldMap;
import static com.nari.slsd.msrv.waterdiversion.utils.CommonUtil.*;


/**
 * @title
 * @description 全量年计划填报
 * @author bigb
 * @updateTime 2021/8/23 11:13
 * @throws
 */
@Slf4j
@Service
public class WaterPlanFullFillInYearServiceImpl  implements IWaterPlanFullFillInYearService {

    private static final int BATCH_SIZE = 45;

    private static final String MONTH = "month";

    private static final int SCALE = 4;

    private static String YEAR;

    @Autowired
    @Qualifier("planFillThreadPool")
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private IWrPlanTaskSubService wrPlanTaskSubService;

    @Autowired
    private IWrPlanFillInYearService wrPlanFillInYearService;

    @Autowired
    private IWrPlanInterTdayService wrPlanInterTdayService;

    @Autowired
    private WaterPlanFillinYearMapper waterPlanFillinYearMapper;

    @Autowired
    private WrPlanTaskMapper wrPlanTaskMapper;

    @Autowired
    private WrUseUnitManagerMapper wrUseUnitManagerMapper;

    @Autowired
    private WrDiversionPortMapper wrDiversionPortMapper;

    @Autowired
    private WrPlanInterTdayMapper wrPlanInterTdayMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public void planFullFill(List<WaterPlanYearModel> modelList , String personId , String year){
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            YEAR = year;
            WrPlanTask wrPlanTask = getWrPlanTask();
            //生成任务表信息
            String taskId = generateTask(wrPlanTask,personId);
            //获取所有用水单位信息
            List<WrUseUnitManager> managerList = wrUseUnitManagerMapper.getAllWrUseUnitManager();
            batchProcessForYearPlan(modelList, wrPlanTask, taskId);
        });
    }

    private void batchProcessForYearPlan(List<WaterPlanYearModel> modelList, WrPlanTask wrPlanTask, String taskId) {
        log.info("<---批量导入年计划开始！--->");
        List<CompletableFuture> completableFutureList = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        boolean update_plan = ObjectUtil.isNotNull(wrPlanTask);
        //转换成年填报计划表
        int index = modelList.size() % BATCH_SIZE == 0 ? modelList.size() / BATCH_SIZE : modelList.size() / BATCH_SIZE + 1;
        for (int i = 0; i < index; i++) {
            //每行对应着36条年填报数据
            List<WaterPlanYearModel> subList = modelList.stream()
                    .skip(i * BATCH_SIZE).limit(BATCH_SIZE)
                    .collect(Collectors.toList());
            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> singleProcessForYearPlan(taskId, update_plan, subList), threadPoolExecutor)
                    .handle((result, throwable) -> {
                        if (null != throwable) {
                            log.error("singleProcess execute fail , error is {}", throwable);
                            return 0;
                        }
                        //年计划导入成功
                        return result;
                    });
            completableFutureList.add(future);
        }
        //主线程阻塞，等待任务处理完成
        if(completableFutureList.size() > 0){
            CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[completableFutureList.size()])).join();
        }
        long endTime = System.currentTimeMillis();
        log.info("<---批量导入年计划结束，花费时间：{} --->",(endTime-startTime));
    }

    private Integer singleProcessForYearPlan(String taskId, boolean update_plan, List<WaterPlanYearModel> subList) {
        //根据测站编码，获取测站id和所属用水单位及管理站信息
        List<String> codeList = subList.stream().map(WaterPlanYearModel::getBuildingCode).collect(Collectors.toList());
        Map<String,WrDiversionPort> portMap = new HashMap<>();
        //引水口id
        List<String> buildingIdList = new ArrayList<>();
        getWrDiversionPortMap(codeList,portMap,buildingIdList);
        if(buildingIdList.size() == 0){
            return 0;
        }
        //年计划数据
        List<WaterPlanFillinYear> yearPlanList = new ArrayList();
        //旬月迭代数据
        List<WrPlanInterTday> tdayList = new ArrayList();
        //根据测站id，校验是年计划是否已存在
        subList.stream().forEach(model -> {
            List<WaterPlanFillinYear> yearPlanListEveryBuilding = new ArrayList<>();
            List<WrPlanInterTday> tdayListEveryBuilding = new ArrayList<>();
            //年填报计划、旬月迭代数据
            generateYearPlan(model,portMap, taskId , yearPlanListEveryBuilding,tdayListEveryBuilding);
            //生成月指标
            generateMonthIndex(yearPlanListEveryBuilding,tdayListEveryBuilding);
            yearPlanList.addAll(yearPlanListEveryBuilding);
            tdayList.addAll(tdayListEveryBuilding);
        });
        //覆盖更新
        if(update_plan){
            //查看是否存在审批中的年计划填报
            LambdaQueryWrapper<WaterPlanFillinYear> wrapper = new QueryWrapper().lambda();
            wrapper.eq(WaterPlanFillinYear::getYear,getYear());
            wrapper.in(WaterPlanFillinYear::getBuildingId,buildingIdList);
            List<WaterPlanFillinYear> existYearPlanList = waterPlanFillinYearMapper.selectList(wrapper);
            if(CollectionUtils.isNotEmpty(existYearPlanList)){
                existYearPlanList = getDiffList(yearPlanList,existYearPlanList);
            }else{
                existYearPlanList = yearPlanList;
            }
            if(existYearPlanList.size() > 0){
                this.wrPlanFillInYearService.saveOrUpdateBatch(existYearPlanList);
                //TODO 更新旬月迭代表,可能会导致近期计划调整后,旬月数据被覆盖
                List<WrPlanInterTday> tdayUpdateList = getDiffListForTday(existYearPlanList,tdayList);
                this.wrPlanInterTdayService.saveOrUpdateBatch(tdayUpdateList);
            }
        }else{
            //插入
            this.wrPlanFillInYearService.saveBatch(yearPlanList);
            this.wrPlanInterTdayService.saveBatch(tdayList);
        }
        return 1;
    }

    /**
     * 待操作的数据包含:新增+更新
     * @param sourceList
     * @param destList
     */
    private List<WaterPlanFillinYear> getDiffList(List<WaterPlanFillinYear> sourceList , List<WaterPlanFillinYear> destList){
        List<WaterPlanFillinYear> allUpdateList = new ArrayList<>();
        Map<String, WaterPlanFillinYear> sourceMap = sourceList.stream().collect(Collectors.toMap(e -> getMapKeyForYear(e), e -> e));
        Map<String, WaterPlanFillinYear> destMap = destList.stream().collect(Collectors.toMap(e -> getMapKeyForYear(e), e -> e));
        destMap.entrySet().stream().forEach(entry -> {
            WaterPlanFillinYear dest = entry.getValue();
            WaterPlanFillinYear source = sourceMap.get(entry.getKey());
            //只覆盖已存在,且值不一样的数据
            if(null != source && NumberUtil.compare(convertToDouble(dest.getDemadWaterFlow()),convertToDouble(source.getDemadWaterQuantity())) != 0){
                dest.setDemadWaterQuantity(source.getDemadWaterQuantity());
                dest.setDemadWaterFlow(source.getDemadWaterFlow());
                allUpdateList.add(dest);
            }
            sourceMap.remove(entry.getKey());
        });
        if(sourceMap.size() > 0){
            allUpdateList.addAll(sourceMap.values());
        }
        return allUpdateList;
    }

    /**
     * 待操作的数据包含:新增+更新
     * @param updateList
     * @param destList
     */
    private List<WrPlanInterTday> getDiffListForTday(List<WaterPlanFillinYear> updateList , List<WrPlanInterTday> tdayList){
        List<WrPlanInterTday> allUpdateList = new ArrayList<>();
        Map<String, WrPlanInterTday> tdayMap = tdayList.stream().collect(Collectors.toMap(e -> getMapKeyForTday(e), e -> e));
        updateList.stream().forEach(e -> {
            String key = getMapKeyForYear(e);
            WrPlanInterTday tday = tdayMap.get(key);
            if(null != tday){
                allUpdateList.add(tday);
            }
        });
        return allUpdateList;
    }

    private String getMapKeyForYear(WaterPlanFillinYear year){
        StringBuilder sb = new StringBuilder(year.getBuildingId());
        sb.append("_");
        sb.append(year.getYear());
        sb.append("_");
        sb.append(year.getMonth());
        sb.append("_");
        sb.append(year.getTday());
        return sb.toString();
    }

    private String getMapKeyForTday(WrPlanInterTday tday){
        StringBuilder sb = new StringBuilder(tday.getBuildingId());
        sb.append("_");
        String year = String.valueOf(DateUtil.year(tday.getSupplyTime()));
        sb.append(year);
        sb.append("_");
        String month = org.apache.commons.lang3.StringUtils.leftPad(String.valueOf(DateUtil.month(tday.getSupplyTime())),2,'0');
        sb.append(month);
        sb.append("_");
        sb.append(tday.getTimeType());
        return sb.toString();
    }

    private double convertToDouble(BigDecimal source){
        if(null == source){
            return 0D;
        }
        return source.doubleValue();
    }

    private WrPlanTask getWrPlanTask() {
        //查看是否存在审批中的年计划填报
        LambdaQueryWrapper<WrPlanTask> wrapper = new QueryWrapper().lambda();
        wrapper.eq(WrPlanTask::getYear,getYear());
        wrapper.eq(WrPlanTask::getPlanType,PlanTypeEnum.YEAR_PLAN.getId());
        wrapper.eq(WrPlanTask::getFillType,"1");
        wrapper.eq(WrPlanTask::getState,TaskStateEnum.UNDER_APPROVAL.getId());
        List<WrPlanTask> wrPlanTaskList = wrPlanTaskMapper.selectList(wrapper);
        if(CollectionUtils.isNotEmpty(wrPlanTaskList)){
            throw new TransactionException(CodeEnum.OPERAT_ERROR,"目前尚存在正在审批中的年填报计划,不能进行导入！");
        }
        wrapper.clear();
        wrapper.eq(WrPlanTask::getYear,getYear());
        wrapper.eq(WrPlanTask::getPlanType,PlanTypeEnum.YEAR_PLAN.getId());
        //已导入过年计划
        wrapper.eq(WrPlanTask::getFillType,"0");
        return wrPlanTaskMapper.selectOne(wrapper);
    }


    private void getWrDiversionPortMap(List<String> codeList , Map<String,WrDiversionPort> portMap , List<String> buildingIdList) {
        LambdaQueryWrapper<WrDiversionPort> wrapper = new QueryWrapper().lambda();
        wrapper.in(WrDiversionPort::getBuildingCode,codeList);
        wrapper.in(WrDiversionPort::getBuildingLevel,Arrays.asList(WrBuildingEnum.BUILDING_LEVEL_2,BUILDING_LEVEL_1_2));
        List<WrDiversionPort> portList = wrDiversionPortMapper.selectList(wrapper);
        if(CollectionUtils.isNotEmpty(portList)){
            buildingIdList.addAll(portList.stream().map(WrDiversionPort::getId).collect(Collectors.toList()));
            Map<String, WrDiversionPort> map = portList.stream()
                    .filter(e -> StringUtils.isNotEmpty(e.getBuildingCode()))
                        .collect(Collectors.toMap(WrDiversionPort::getBuildingCode, e -> e));
            portMap.putAll(map);
        }
    }

    /**
     * 生成任务表
     * @param existTask
     * @param personId
     */
    private String generateTask(WrPlanTask existTask , String personId) {
        WrPlanTask wrPlanTask = existTask;
        Date now = new Date();
        if(null == wrPlanTask){
            wrPlanTask = new WrPlanTask();
            //id
            wrPlanTask.setId(IDGenerator.getId());
            String year = getYear();
            //任务名称
            wrPlanTask.setTaskName(year + "年用水计划全量导入");
            //计划类型
            wrPlanTask.setPlanType(PlanTypeEnum.YEAR_PLAN.getId());
            //计划开始时间
            wrPlanTask.setStartDate(now);
            //操作人
            wrPlanTask.setPersonId(personId);
            //发起时间
            wrPlanTask.setCreateDate(now);
            //任务状态,已完成
            wrPlanTask.setState(TaskStateEnum.END_APPROVAL.getId());
            //年份
            wrPlanTask.setYear(year);
            //全量导入或自动生成
            wrPlanTask.setFillType("0");
            this.wrPlanTaskMapper.insert(wrPlanTask);
        }else{
            //计划开始时间
            wrPlanTask.setStartDate(now);
            //操作人
            wrPlanTask.setPersonId(personId);
            //发起时间
            wrPlanTask.setCreateDate(now);
            this.wrPlanTaskMapper.updateById(wrPlanTask);
        }
        return wrPlanTask.getId();
    }

    /**
     * 生成月度指标
     * @param yearPlanList
     */
    private void generateMonthIndex(List<WaterPlanFillinYear> yearPlanList , List<WrPlanInterTday> tdayList) {
        //按照年月分组，生成年月的数据
        if (yearPlanList.size() > 0) {
            Map<String, List<WaterPlanFillinYear>> monthMap = yearPlanList.stream().collect(Collectors.groupingBy(year -> fetchGroupKeyForYear(year)));
            monthMap.entrySet().stream().forEach(en -> {
                List<WaterPlanFillinYear> localList = en.getValue();
                WaterPlanFillinYear month = new WaterPlanFillinYear();
                BeanUtils.copyProperties(localList.get(0), month);
                //id
                month.setId(IDGenerator.getId());
                //设置时间类别
                month.setTday(TDayTypeEnum.YEAR_PLAN_TDAY_TYPE_4);
                BigDecimal dv = new BigDecimal(0);
                //水量
                for (WaterPlanFillinYear fill : localList) {
                    BigDecimal local = fill.getDemadWaterQuantity() == null ? new BigDecimal(0) : fill.getDemadWaterQuantity();
                    dv = dv.add(local);
                }
                month.setDemadWaterQuantity(dv);
                //流量
                double secondsOfMonth = getSecondsOfMonth(Integer.parseInt(month.getYear()), Integer.parseInt(month.getMonth())) / 10000.0;
                month.setDemadWaterFlow(NumberUtil.div(dv, secondsOfMonth,SCALE));
                yearPlanList.add(month);
                WrPlanInterTday tday = new WrPlanInterTday();
                tday.setId(IDGenerator.getId());
                tday.setBuildingId(month.getBuildingId());
                tday.setTimeType(TDayTypeEnum.YEAR_PLAN_TDAY_TYPE_4);
                tday.setWaterQuantity(month.getDemadWaterQuantity());
                tday.setWaterFlow(month.getDemadWaterFlow());
                //指定月份的第一天
                tday.setSupplyTime(getAppointDate(month.getYear(),month.getMonth(),"01"));
                tdayList.add(tday);
            });
        }
    }

    private static Date getAppointDate(String year , String month , String day){
        StringBuilder sb = new StringBuilder(year);
        sb.append("-");
        sb.append(month);
        sb.append("-");
        sb.append(day);
        return DateUtils.convertStringTimeToDateExt(sb.toString());
    }

    private String fetchGroupKeyForYear(WaterPlanFillinYear year){
        return year.getBuildingId() + "_" + year.getMonth();
    }

    /**
     * @title generateYearPlan
     * @description 生成年填报计划
     * @author bigb
     * @param: obj
     * @param: portMap
     * @param: taskId
     * @param: yearPlanList
     * @param: tdayList
     * @updateTime 2021/9/12 22:03
     * @throws
     */
    private void generateYearPlan(WaterPlanYearModel obj , Map<String,WrDiversionPort> portMap , String taskId ,
                                                       List<WaterPlanFillinYear> yearPlanList , List<WrPlanInterTday> tdayList) {
        Map<String, Field> name_field_map = getNameFieldMap();
        name_field_map.entrySet().stream()
                .filter(e -> e.getKey().startsWith(MONTH))
                .forEach(e -> {
                    String fieldName = e.getKey();
                    String[] monthAndTd = fieldName.substring(MONTH.length()).split("_");
                    if (ArrayUtil.isNotEmpty(monthAndTd) && monthAndTd.length > 1) {
                        WrDiversionPort port = portMap.get(obj.getBuildingCode());
                        //该编码必须是在巴音局维护的
                        if(null != port){
                            //0-月份 1-旬
                            Field field = name_field_map.get(fieldName);
                            WaterPlanFillinYear plan = new WaterPlanFillinYear();
                            //id
                            plan.setId(IDGenerator.getId());
                            //task id
                            plan.setPlanTaskId(taskId);
                            //引水口id
                            plan.setBuildingId(port.getId());
                            //管理站id
                            plan.setManageUnitId(port.getMngUnitId());
                            //用水单位id
                            plan.setWaterUnitId(port.getWaterUnitId());
                            //年份
                            plan.setYear(getYear());
                            //月份
                            plan.setMonth(StringUtils.leftPad(monthAndTd[0],2,'0'));
                            //1：上旬，2：中旬，3：下旬，4：全月
                            plan.setTday(monthAndTd[1]);
                            Object val = ReflectionUtils.getField(field, obj);
                            Double dVal = new Double(0);
                            if (val instanceof Double) {
                                dVal = (Double) val;
                            }
                            //需求水量
                            plan.setDemadWaterQuantity(number(dVal));
                            //毫秒数
                            int seconds;
                            //下旬毫秒数
                            if (TDayTypeEnum.YEAR_PLAN_TDAY_TYPE_3.equals(monthAndTd[1])) {
                                seconds = getSecondsOfTD3(Integer.parseInt(plan.getYear()), Integer.parseInt(plan.getMonth()));
                            }else{
                                seconds = getSecondsOfDays(10);
                            }
                            //需求流量,水量是单位为万立方米,保留4位小数
                            plan.setDemadWaterFlow(NumberUtil.div(plan.getDemadWaterQuantity(), seconds / 10000.0 , SCALE));
                            yearPlanList.add(plan);
                            WrPlanInterTday tday = new WrPlanInterTday();
                            tday.setId(IDGenerator.getId());
                            tday.setTimeType(plan.getTday());
                            tday.setBuildingId(plan.getBuildingId());
                            tday.setWaterQuantity(plan.getDemadWaterQuantity());
                            tday.setWaterFlow(plan.getDemadWaterFlow());
                            String dayStr;
                            if(TDayTypeEnum.YEAR_PLAN_TDAY_TYPE_1.equals(plan.getTday())){
                                dayStr = "01";
                            }else if(TDayTypeEnum.YEAR_PLAN_TDAY_TYPE_2.equals(plan.getTday())){
                                dayStr = "11";
                            }else{
                                dayStr = "21";
                            }
                            tday.setSupplyTime(getAppointDate(plan.getYear(),plan.getMonth(),dayStr));
                            tdayList.add(tday);
                        }
                    }
                });
    }



    private String getYear() {
        if(StringUtils.isNotEmpty(YEAR)){
            return YEAR;
        }
        return String.valueOf(LocalDate.now().getYear());
    }
}
