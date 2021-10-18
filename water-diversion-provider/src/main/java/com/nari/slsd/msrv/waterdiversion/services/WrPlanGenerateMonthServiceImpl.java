package com.nari.slsd.msrv.waterdiversion.services;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nari.slsd.msrv.common.exception.TransactionException;
import com.nari.slsd.msrv.common.ext.enums.CodeEnum;
import com.nari.slsd.msrv.common.utils.DateUtils;
import com.nari.slsd.msrv.common.utils.IDGenerator;
import com.nari.slsd.msrv.waterdiversion.commons.PlanTypeEnum;
import com.nari.slsd.msrv.waterdiversion.commons.TDayTypeEnum;
import com.nari.slsd.msrv.waterdiversion.commons.TaskStateEnum;
import com.nari.slsd.msrv.waterdiversion.interfaces.*;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.*;
import com.nari.slsd.msrv.waterdiversion.mapper.third.WrJcmdOMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrBuildingAndDiversion;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.*;
import com.nari.slsd.msrv.waterdiversion.model.third.po.WrJcmdO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WaterPlanFillInYearSimpleVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseUnitNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.nari.slsd.msrv.waterdiversion.commons.TDayTypeEnum.YEAR_PLAN_TDAY_TYPE_4;
import static com.nari.slsd.msrv.waterdiversion.commons.WrBuildingEnum.BUILDING_LEVEL_1_2;
import static com.nari.slsd.msrv.waterdiversion.commons.WrBuildingEnum.BUILDING_LEVEL_2;
import static com.nari.slsd.msrv.waterdiversion.utils.CommonUtil.*;

/**
 * @author bigb
 * @title
 * @description 月计划生成
 * @updateTime 2021/9/10 19:49
 * @throws
 */
@Slf4j
@Service
public class WrPlanGenerateMonthServiceImpl implements IWrPlanGenerateMonthService {

    private static final String MONTH = "month";

    private static final int BATCH_PROCESS_SIZE = 40;

    /**
     * 巴州：PDY65419-5
     * 第二师：PDY65420-8
     * 生态用水：PDYKKHSTYS
     */
    private static final String[] UNIT_ARR = {"PDY65419-5", "PDY65420-8", "PDYKKHSTYS"};

    private static Map<Integer, Field> MONTH_FIELD_MAP = new HashMap<>();

    @Autowired
    @Qualifier("planFillThreadPool")
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private IWrUseUnitManagerService wrUseUnitManagerService;

    @Autowired
    private IWaterBuildingManagerService waterBuildingManagerService;

    @Autowired
    private IWrPlanFillInMonthService wrPlanFillInMonthService;

    @Autowired
    private IWrPlanInterDayService wrPlanInterDayService;

    @Autowired
    private IWrPlanInterTdayService wrPlanInterTdayService;

    @Autowired
    private IWrDwaMonthService wrDwaMonthService;

    @Autowired
    private WrJcmdOMapper wrJcmdOMapper;

    @Autowired
    private WrPlanTaskMapper wrPlanTaskMapper;

    @Autowired
    private WrUseUnitManagerMapper wrUseUnitManagerMapper;

    @Autowired
    private WaterPlanFillinYearMapper waterPlanFillinYearMapper;

    @Autowired
    private WaterPlanFillinMonthMapper waterPlanFillinMonthMapper;

    @Autowired
    private WrPlanInterDayMapper wrPlanInterDayMapper;

    @Autowired
    private WrPlanInterTdayMapper wrPlanInterTdayMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    static {
        ReflectionUtils.doWithLocalFields(WrJcmdO.class, field -> {
            String fieldName = field.getName();
            if (fieldName.startsWith(MONTH) && fieldName.length() > MONTH.length()) {
                ReflectionUtils.makeAccessible(field);
                MONTH_FIELD_MAP.put(Integer.parseInt(fieldName.substring(MONTH.length())), field);
            }
        });
    }

    @Override
    public void autoGenerateMonthPlan() {
        transactionTemplate.executeWithoutResult(ts -> {
            autoGenerateMonthPlanTransaction();
        });
    }

    public void autoGenerateMonthPlanTransaction() {
        log.info("<----- autoGenerateMonthPlan process start -----> ");
        long startTime = System.currentTimeMillis();
        Date now = new Date();
        String year = DateUtils.getYearToString(now);
        String month = DateUtils.getMonthToString(now);
        if (monthPlanFillValidate(year, month)) {
            log.info("月计划已生成，无需再次填报，时间为：{}-{}", new Object[]{year, month});
            return;
        }
        WrPlanTask yearFillPlan = getYearFillPlan(year);
        if (null == yearFillPlan) {
            log.info("还未填报过年初用水计划,时间为：{}", year);
            return;
        }
        //生成月计划填报任务信息
        String taskId = getOrGenerateMonthPlanFillTask(year,month);
        //获取巴州/第二师的每个月滚存指标
        LambdaQueryWrapper<WrJcmdO> jCmdMapper = new QueryWrapper().lambda();
        jCmdMapper.eq(WrJcmdO::getYear, Integer.parseInt(year));
        jCmdMapper.eq(WrJcmdO::getMonth, Integer.parseInt(month));
        jCmdMapper.in(WrJcmdO::getDfagcd, UNIT_ARR);
        //调令状态 0：审核中；1：审核后并下达 TODO 改为枚举
        jCmdMapper.eq(WrJcmdO::getState, 1);
        List<WrJcmdO> jCmdList = wrJcmdOMapper.selectList(jCmdMapper);
        if (CollectionUtils.isEmpty(jCmdList)) {
            log.info("当前月份还不存在任何滚存指标，时间为：{}-{}", new Object[]{year, month});
            throw new TransactionException(CodeEnum.NO_DATA,"当前月份还不存在任何滚存指标!");
        }
        //同样的月份可能存在多条指标数据，只取其中一条即可
        Map<String, WrJcmdO> jCmdMap = new HashMap<>();
        jCmdList.stream().forEach(jCmd -> {
            //TODO 暂不考虑空指针
            String key = String.valueOf(jCmd.getDfagcd());
            if (!jCmdMap.containsKey(key)) {
                jCmdMap.put(key, jCmd);
            }
        });
        jCmdMap.keySet().stream().forEach(unitCode -> {
            //根据编码获取用水单位id
            LambdaQueryWrapper<WrUseUnitManager> unitMangerMapper = new QueryWrapper().lambda();
            unitMangerMapper.eq(WrUseUnitManager::getCode, unitCode);
            WrUseUnitManager wr = wrUseUnitManagerMapper.selectOne(unitMangerMapper);
            if (null != wr) {
                autoGeneratePlan(wr, year, month, yearFillPlan.getId(), jCmdMap.get(unitCode),taskId);
            }
        });
        long endTime = System.currentTimeMillis();
        log.info("<----- autoGenerateMonthPlan process end , cost time {} -----> ",(endTime-startTime));
    }

    /**
     * 校验月计划是否填报过
     *
     * @param year
     * @param month
     * @return
     */
    private boolean monthPlanFillValidate(String year, String month) {
        //查询任务表,判断是否已填报,如已填报,则直接返回
        LambdaQueryWrapper<WrPlanTask> taskMapper = new QueryWrapper().lambda();
        //月计划
        taskMapper.eq(WrPlanTask::getPlanType, PlanTypeEnum.MONTH_PLAN.getId());
        //年份、月份
        taskMapper.eq(WrPlanTask::getYear, year);
        taskMapper.eq(WrPlanTask::getMonth, month);
        //填报方式 0-全量导入/自动生成 1-人工填报
        taskMapper.eq(WrPlanTask::getFillType, "0");
        //填报状态
        taskMapper.eq(WrPlanTask::getState, TaskStateEnum.END_APPROVAL.getId());
        Integer taskCount = wrPlanTaskMapper.selectCount(taskMapper);
        return null != taskCount && taskCount > 0;
    }

    /**
     * 生成月填报计划任务信息
     *
     * @param year
     * @param month
     * @return
     */
    private String getOrGenerateMonthPlanFillTask(String year, String month) {
        WrPlanTask wrPlanTask = new WrPlanTask();
        wrPlanTask.setId(IDGenerator.getId());
        wrPlanTask.setPlanType(PlanTypeEnum.MONTH_PLAN.getId());
        wrPlanTask.setYear(year);
        wrPlanTask.setMonth(month);
        Date now = new Date();
        wrPlanTask.setCreateDate(now);
        wrPlanTask.setPersonId("SYSTEM");
        wrPlanTask.setState(TaskStateEnum.END_APPROVAL.getId());
        wrPlanTask.setStartDate(now);
        wrPlanTask.setFillType("0");
        wrPlanTask.setTaskName("【" +year + month + "】月计划自动填报");
        wrPlanTaskMapper.insert(wrPlanTask);
        return wrPlanTask.getId();
    }

    /**
     * 获取年计划填报任务
     *
     * @param year
     * @return
     */
    private WrPlanTask getYearFillPlan(String year) {
        //查询任务表,判断是否已填报,如已填报,则直接返回
        LambdaQueryWrapper<WrPlanTask> taskMapper = new QueryWrapper().lambda();
        //月计划
        taskMapper.eq(WrPlanTask::getPlanType, PlanTypeEnum.YEAR_PLAN.getId());
        //年份
        taskMapper.eq(WrPlanTask::getYear, year);
        //填报方式 0-全量导入/自动生成 1-人工填报
        taskMapper.eq(WrPlanTask::getFillType, "0");
        //填报状态
        taskMapper.eq(WrPlanTask::getState, TaskStateEnum.END_APPROVAL.getId());
        return wrPlanTaskMapper.selectOne(taskMapper);
    }

    private void autoGeneratePlan(WrUseUnitManager wr, String year, String month, String yearTaskId, WrJcmdO wrJcmdO , String monthTaskId) {
        //获取该用水单位下所有引水口
        List<WrBuildingAndDiversion> buildingList = getAllSmallWaterBuildingForRootId(wr.getId());
        if (CollectionUtils.isEmpty(buildingList)) {
            return;
        }
        Map<String, WrBuildingAndDiversion> versionMap = buildingList.stream().collect(Collectors.toMap(WrBuildingAndDiversion::getId, e -> e));
        //获取所有引水口当月年初计划,计算每个引水口占比
        QueryWrapper<WaterPlanFillinYear> yearMapper = new QueryWrapper<>();
        yearMapper.eq("PLAN_TASK_ID", yearTaskId);
        yearMapper.eq("YEAR", year);
        yearMapper.eq("MONTH", month);
        //查询月计划数据
        yearMapper.eq("TDAY", TDayTypeEnum.YEAR_PLAN_TDAY_TYPE_4);
        yearMapper.in("BUILDING_ID", versionMap.keySet());
        //查询数据量,大概几百条,尽量的使查询的对象属性少一点
        List<WaterPlanFillInYearSimpleVO> yearList = waterPlanFillinYearMapper.getYearPlan(yearMapper);
        if (CollectionUtils.isNotEmpty(yearList)) {
            //计算巴州/第二师总计划水量
            BigDecimal total = new BigDecimal(0);
            for (WaterPlanFillInYearSimpleVO vo : yearList) {
                total = NumberUtil.add(total, vo.getWaterQuantity());
            }
            //被除数不可为0
            if(total.doubleValue() == 0){
                total = new BigDecimal(0.00001);
            }
            Double indVal = Double.valueOf("0");
            //获取当月巴州/第二师滚存指标
            Field monthField = MONTH_FIELD_MAP.get(Integer.valueOf(month));
            if (null != monthField) {
                Object val = ReflectionUtils.getField(monthField, wrJcmdO);
                if (val instanceof Double) {
                    indVal = (Double) val;
                }
            }
            //计算每个引水口占比
            for (WaterPlanFillInYearSimpleVO vo : yearList) {
                vo.setRate(NumberUtil.div(vo.getWaterQuantity(), total));
                vo.setAccuWaterQuantity(NumberUtil.mul(indVal, vo.getRate()));
            }
            //生成每个引水口的日计划,均分到每一天
            //获取当月天数
            int days = getDayNumOfCurrentMonth(Integer.parseInt(year), Integer.parseInt(month));
            log.info("<---批量生成月计划开始！--->");
            List<CompletableFuture> completableFutureList = new ArrayList<>();
            long startTime = System.currentTimeMillis();
            //转换成年填报计划表
            int index = yearList.size() % BATCH_PROCESS_SIZE == 0 ? yearList.size() / BATCH_PROCESS_SIZE : yearList.size() / BATCH_PROCESS_SIZE + 1;
            for (int i = 0; i < index; i++) {
                //每个线程处理的任务
                List<WaterPlanFillInYearSimpleVO> processList = yearList.stream()
                        .skip(i * BATCH_PROCESS_SIZE).limit(BATCH_PROCESS_SIZE)
                        .collect(Collectors.toList());
                CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> batchGenerateDayPlan(processList, days, versionMap, year, month , monthTaskId , wrJcmdO), threadPoolExecutor)
                        .handle((result, throwable) -> {
                            if (null != throwable) {
                                log.error("batchGenerateDayPlan execute fail , error is {}", throwable);
                                return 0;
                            }
                            //生成月计划成功
                            return result;
                        });
                completableFutureList.add(future);
            }
            //主线程阻塞，等待任务处理完成
            if(completableFutureList.size() > 0){
                CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[completableFutureList.size()])).join();
            }
            long endTime = System.currentTimeMillis();
            log.info("<---批量生成月计划结束，花费时间：{} --->",(endTime-startTime));
        }
    }

    /**
     * @title batchGenerateDayPlan
     * @description 批量生成月计划及日迭代 TODO 暂不考虑更新
     * @author bigb
     * @param: processList
     * @param: days
     * @param: versionMap
     * @param: year
     * @param: month
     * @param: taskId
     * @updateTime 2021/9/12 13:25
     * @throws
     */
    private Integer batchGenerateDayPlan(List<WaterPlanFillInYearSimpleVO> processList,
                                         int days, Map<String, WrBuildingAndDiversion> versionMap,
                                         String year, String month , String taskId , WrJcmdO wrJcmdO) {
        List<WaterPlanFillinMonth> monthList = new ArrayList<>();
        List<WrPlanInterDay> dayList = new ArrayList<>();
        List<WrDwaMonth> dwaMonthList = new ArrayList<>();
        processList.stream().forEach(e -> {
            //每日平均水量
            BigDecimal waterQuantity = NumberUtil.div(e.getAccuWaterQuantity(),days,5);
            //每日平均流量 8.64=86400/10000
            BigDecimal waterFlow = NumberUtil.div(waterQuantity,8.64, 5);
            for(int i=1;i<=days;i++){
                monthList.add(generateDayPlan(versionMap, year, month, taskId, e, waterQuantity, waterFlow, i));
                dayList.add(getWrPlanInterDay(year, month, e, waterQuantity, waterFlow, i));
            }
            dwaMonthList.add(generateWrDwaMonth(versionMap,year, month, e));
        });
        updateTDayIndexForRemainMonthInYear(processList, wrJcmdO);
        wrPlanFillInMonthService.saveBatch(monthList);
        wrPlanInterDayService.saveBatch(dayList);
        wrDwaMonthService.saveBatch(dwaMonthList);
        return 1;
    }

    private void updateTDayIndexForRemainMonthInYear(List<WaterPlanFillInYearSimpleVO> processList, WrJcmdO wrJcmdO) {
        //获取当前及后续月份
        List<Date> dateList = getRemainMonthContainsCurrent();
        Map<String, WaterPlanFillInYearSimpleVO> voMap = processList.stream().collect(Collectors.toMap(WaterPlanFillInYearSimpleVO::getBuildingId, e -> e));
        LambdaQueryWrapper<WrPlanInterTday> wrapper = new QueryWrapper().lambda();
        wrapper.in(WrPlanInterTday::getBuildingId,voMap.keySet());
        wrapper.in(WrPlanInterTday::getSupplyTime,dateList);
        wrapper.eq(WrPlanInterTday::getTimeType,YEAR_PLAN_TDAY_TYPE_4);
        List<WrPlanInterTday> tdayList = wrPlanInterTdayMapper.selectList(wrapper);
        Map<Date,Double> indexMap = new HashMap<>();
        //获取每月巴州/第二师滚存指标
        for (Date date : dateList) {
            String everyMonth = DateUtil.format(date,"MM");
            Field monthField = MONTH_FIELD_MAP.get(Integer.valueOf(everyMonth));
            if (null != monthField) {
                Object val = ReflectionUtils.getField(monthField, wrJcmdO);
                if(val instanceof Double){
                    Double indexVal = (Double) val;
                    indexMap.put(date,indexVal);
                }
            }
            indexMap.putIfAbsent(date,new Double(0));
        }
        if(CollectionUtils.isNotEmpty(tdayList)){
            Map<String, List<WrPlanInterTday>> tDayListMap = tdayList.stream().collect(Collectors.groupingBy(WrPlanInterTday::getBuildingId));
            tDayListMap.entrySet().forEach(entry -> {
                String buildingId = entry.getKey();
                WaterPlanFillInYearSimpleVO vo = voMap.get(buildingId);
                List<WrPlanInterTday> tDayListForMonth = entry.getValue();
                for (WrPlanInterTday tday : tDayListForMonth) {
                    //本月总指标
                    Double indexOfMonth = indexMap.get(tday.getSupplyTime());
                    tday.setWaterQuantity(NumberUtil.mul(vo.getRate(),number(indexOfMonth)));
                    int secondsOfMonth = getSecondsOfMonth(DateUtil.year(tday.getSupplyTime()), DateUtil.month(tday.getSupplyTime()));
                    tday.setWaterFlow(NumberUtil.div(NumberUtil.mul(tday.getWaterQuantity(),10000),secondsOfMonth,4));
                }
            });
            wrPlanInterTdayService.saveOrUpdateBatch(tdayList);
        }
    }

    private WrPlanInterDay getWrPlanInterDay(String year, String month, WaterPlanFillInYearSimpleVO e, BigDecimal waterQuantity, BigDecimal waterFlow, int day) {
        WrPlanInterDay wrPlanInterDay = new WrPlanInterDay();
        wrPlanInterDay.setId(IDGenerator.getId());
        wrPlanInterDay.setBuildingId(e.getBuildingId());
        wrPlanInterDay.setWaterQuantity(waterQuantity);
        wrPlanInterDay.setWaterFlow(waterFlow);
        //指定时间
        StringBuilder sb = new StringBuilder(year);
        sb.append("-");
        sb.append(month);
        sb.append("-");
        sb.append(StringUtils.leftPad(String.valueOf(day),2,"0"));
        wrPlanInterDay.setSupplyTime(DateUtils.convertStringTimeToDateExt(sb.toString()));
        return wrPlanInterDay;
    }

    private WaterPlanFillinMonth generateDayPlan(Map<String, WrBuildingAndDiversion> versionMap, String year, String month, String taskId, WaterPlanFillInYearSimpleVO e, BigDecimal waterQuantity, BigDecimal waterFlow, int i) {
        //月度填报表、日迭代表
        WaterPlanFillinMonth monthPlan = new WaterPlanFillinMonth();
        monthPlan.setId(IDGenerator.getId());
        WrBuildingAndDiversion version = versionMap.get(e.getBuildingId());
        if(null != version){
            //用水单位
            monthPlan.setWaterUnitId(version.getWaterUnitId());
            //管理站
            monthPlan.setManageUnitId(version.getMngUnitId());
        }
        monthPlan.setBuildingId(e.getBuildingId());
        monthPlan.setYear(year);
        monthPlan.setMonth(month);
        monthPlan.setDay(StringUtils.leftPad(String.valueOf(i),2,'0'));
        monthPlan.setPlanTaskId(taskId);
        //计划水量
        monthPlan.setDemadWaterQuantity(waterQuantity);
        //计划流量
        monthPlan.setDemadWaterFlow(waterFlow);
        return monthPlan;
    }

    private WrDwaMonth generateWrDwaMonth(Map<String, WrBuildingAndDiversion> versionMap, String year, String month, WaterPlanFillInYearSimpleVO e) {
        //月度指标表
        WrDwaMonth wrDwaMonth = new WrDwaMonth();
        wrDwaMonth.setId(IDGenerator.getId());
        WrBuildingAndDiversion version = versionMap.get(e.getBuildingId());
        if(null != version){
            //引水口id
            wrDwaMonth.setBuildingId(e.getBuildingId());
            //引水口名称
            wrDwaMonth.setBuildingName(version.getBuildingName());
        }
        wrDwaMonth.setYear(year);
        wrDwaMonth.setMonth(month);
        BigDecimal rate = e.getRate() == null ? new BigDecimal(0) : e.getRate();
        String rateStr = NumberUtil.mul(rate,100).toPlainString() + "%";
        //占比
        wrDwaMonth.setProportion(rateStr);
        //滚存指标
        wrDwaMonth.setTarger(e.getAccuWaterQuantity());
        return wrDwaMonth;
    }


    /**
     * @throws
     * @title getAllWaterBuildingForAppointUseUnit
     * @description 获取给定用水单位下所有引水口
     * @author bigb
     * @param: useUnitId
     * @updateTime 2021/9/11 17:26
     */
    @Override
    public List<WrBuildingAndDiversion> getAllWaterBuildingForAppointUseUnit(String useUnitId) {
        //查询该用水单位信息
        if (StringUtils.isEmpty(useUnitId)) {
            throw new TransactionException(CodeEnum.NO_PARAM, "用水单位id不可为空!");
        }
        WrUseUnitManager wrUseUnitManager = wrUseUnitManagerMapper.selectById(useUnitId);
        if (null == wrUseUnitManager) {
            throw new TransactionException(CodeEnum.NO_DATA, "查询不到任何用水单位信息,id is " + useUnitId);
        }
        //获取该节点根节点
        String rootId = wrUseUnitManagerService.getRootId(wrUseUnitManager);
        //根节点直接获取所有引水口
        if (useUnitId.equals(rootId)) {
            return getAllSmallWaterBuildingForRootId(rootId);
        }
        WrUseUnitNode useUnitNodeRoot = wrUseUnitManagerService.getTreeFromCacheById(rootId);
        if (null == useUnitNodeRoot) {
            throw new TransactionException(CodeEnum.NO_DATA, "缓存中查询不到任何用水单位树形信息,id is " + rootId);
        }
        //path
        String path = wrUseUnitManager.getPath();
        String[] pathArr = StringUtils.split(path, "/");
        if (ArrayUtil.isEmpty(pathArr)) {
            throw new TransactionException(CodeEnum.NO_DATA, "给定用水单位path为空,id is " + useUnitId);
        }
        WrUseUnitNode currentNode = useUnitNodeRoot;
        //非根节点,从第二级开始遍历
        for (int loop = 1; loop < pathArr.length; loop++) {
            currentNode = parsePath(pathArr[loop], currentNode);
        }
        if (null != currentNode) {
            return getWrBuildingAndDiversions(currentNode);
        }
        return null;
    }

    private WrUseUnitNode parsePath(String nodeId, WrUseUnitNode currentNode) {
        if (null == currentNode || CollectionUtils.isEmpty(currentNode.getChildren())) {
            return null;
        }
        WrUseUnitNode node = null;
        for (WrUseUnitNode child : currentNode.getChildren()) {
            if (nodeId.equals(child.getId())) {
                node = child;
                break;
            }
        }
        return node;
    }

    private List<WrBuildingAndDiversion> getAllSmallWaterBuildingForRootId(String useUnitId) {
        //缓存中查询用水单位tree
        WrUseUnitNode useUnitNode = wrUseUnitManagerService.getTreeFromCacheById(useUnitId);
        return getWrBuildingAndDiversions(useUnitNode);
    }

    private List<WrBuildingAndDiversion> getWrBuildingAndDiversions(WrUseUnitNode useUnitNode) {
        List<WrBuildingAndDiversion> versionList = null;
        Set<String> resultSet = new HashSet<>();
        getAllBuildingId(useUnitNode, resultSet);
        if (resultSet.size() > 0) {
            List<Integer> buildingLevelList = Arrays.asList(BUILDING_LEVEL_2.intValue(), BUILDING_LEVEL_1_2.intValue());
            //获取指定用水单位下所有小引水口
            versionList = waterBuildingManagerService.getWrBuildingAndDiversionListByUnit(new ArrayList<>(resultSet), null, 1, buildingLevelList);
        }
        return versionList;
    }

    /**
     * 递归获取指定用水单位所有最后一级用水单位
     *
     * @param useUnitNode
     * @param resultSet
     */
    private void getAllBuildingId(WrUseUnitNode useUnitNode, Set<String> resultSet) {
        if (null == useUnitNode) {
            return;
        }
        if (useUnitNode.getIsLeaf()) {
            resultSet.add(useUnitNode.getId());
            return;
        }
        List<WrUseUnitNode> children = useUnitNode.getChildren();
        if (CollectionUtils.isNotEmpty(children)) {
            children.stream().forEach(child -> {
                getAllBuildingId(child, resultSet);
            });
        }
    }
}
