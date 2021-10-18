 //package com.nari.slsd.msrv.waterdiversion.services;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.nari.slsd.msrv.common.utils.IDGenerator;
//import com.nari.slsd.msrv.common.utils.StringUtils;
//import com.nari.slsd.msrv.waterdiversion.cache.interfaces.IModelCacheService;
//import com.nari.slsd.msrv.waterdiversion.feign.interfaces.ActivitiFeignClient;
//import com.nari.slsd.msrv.waterdiversion.interfaces.*;
//import com.nari.slsd.msrv.waterdiversion.mapper.primary.WaterPlanFillinYearMapper;
//import com.nari.slsd.msrv.waterdiversion.model.dto.ActivitiHandle;
//import com.nari.slsd.msrv.waterdiversion.model.dto.MngUnitGrade;
//import com.nari.slsd.msrv.waterdiversion.model.dto.PlanFillinExamineDTO;
//import com.nari.slsd.msrv.waterdiversion.model.dto.WrPlanFileYearValue;
//import com.nari.slsd.msrv.waterdiversion.model.primary.po.*;
//import com.nari.slsd.msrv.waterdiversion.model.vo.BuildingExt;
//import com.nari.slsd.msrv.waterdiversion.model.vo.PlanFillinStateExt;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.transaction.support.TransactionTemplate;
//
//import javax.annotation.Resource;
//import java.math.BigDecimal;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * <p>
// * 年度用水计划填报 服务实现类
// * </p>
// *
// * @author zhs
// * @since 2021-08-04
// */
//@Slf4j
//@Service
//public class WaterPlanFillinYearServiceImpl extends ServiceImpl<WaterPlanFillinYearMapper, WaterPlanFillinYear> implements IWaterPlanFillinYearService {
//    @Resource
//    WaterPlanFillinYearMapper waterPlanFillinYearMapper;
//    @Resource
//    IWrPlanInterTdayService wrPlanInterTdayAndMonthService;
//    @Resource
//    IWaterBuildingManagerService waterBuildingManagerService;
//    @Resource
//    TransactionTemplate transactionTemplate;
//    @Resource
//    IWrPlanFillinService wrPlanFillinService;
//    @Resource
//    IWrPlanTaskService wrPlanTaskService;
//    @Resource
//    ActivitiFeignClient activitiFeignClient;
//    @Resource
//    IActiviciTaskService activiciTaskService;
//    @Resource
//    IModelCacheService modelCacheService;
//
//    /**
//     * 批量保存水量数据
//     */
//    @Override
//    @Transactional(rollbackFor = {Exception.class})
//    public void addWaterPlanValue(WrPlanFileYearValue wrPlanFileYearValue) {
//        String userId = wrPlanFileYearValue.getUserId();
//        List<String> mngUnitIds = wrPlanFileYearValue.getMngUnitId();
//        try {
//            JSONArray jsonArray = wrPlanFileYearValue.getJsonArray();
//            // 整合jsonArray生成多条年计划数据
//            Map<String,Object> resustMap = fillinTask(wrPlanFileYearValue);
//            List<WaterPlanFillinYear> waterPlanFillinYearList = (List<WaterPlanFillinYear>)resustMap.get("waterPlanFillinYearList");
//            //整合多条年旬月计划数据
//            List<WrPlanInterTday> wrPlanInterTdayAndMonthList = (List<WrPlanInterTday>)resustMap.get("wrPlanInterTdayAndMonthList");
//            //整合计划任务数据
//            WrPlanTask wrPlanTask = (WrPlanTask) resustMap.get("wrPlanTask");
//            transactionTemplate.executeWithoutResult(transactionStatus -> {
//                //批量保存计划水量数据
//                saveBatch(waterPlanFillinYearList);
//                wrPlanInterTdayAndMonthService.saveBatch(wrPlanInterTdayAndMonthList);
//                wrPlanTaskService.insert(wrPlanTask);
//                // wrPlanTaskService.
//            });
//        }catch(Exception e){
//            activitiFeignClient.getDelInstance("");
//           //TODO 删除流程
//            log.error("保存失败："+e);
//        }
//    }
//    /**
//     * 用水计划填报单位查询()
//     * @param
//     * @return
//     * @throws ParseException
//     */
//    @Override
//    public PlanFillinStateExt findUnitByCodAndTime(String time, String userId, List<Integer> unitLevels, List<String> buildingType, Integer fillReport,List<Integer> levels) {
//
//        PlanFillinStateExt planFillinStateExt = new PlanFillinStateExt();
//        //获取年填报状态
//        Map<String,Object> map = wrPlanFillinService.state(userId,unitLevels,buildingType,time,null,fillReport,levels);
//        String state = String.valueOf(map.get("state"));
//        String processId = String.valueOf(map.get("processId"));
////        String state = "1";
////        String processId = null;
//        List<BuildingExt> buildingExt = waterBuildingManagerService.getBuildingExtListByUser(userId,unitLevels , buildingType,fillReport,levels);
//        if (buildingExt.size()==0){
//            return planFillinStateExt;
//        }
//        //用户已填报
//        if (state.equals("1")){
//            //流程id(流程表中获取)
//            List<String> buildId = new ArrayList<>();
//            for (BuildingExt buildingExts:buildingExt){
//                buildId.add(buildingExts.getBuildingId());
//            }
//            buildingExt = planValue(waterPlanFillinYearList(time,buildId),buildingExt,"1");
//            planFillinStateExt.setBuildingExtList(buildingExt);
//            planFillinStateExt.setProcessId(processId);
//            planFillinStateExt.setState(state);
//        }else if(state.equals("0")){
//            //用户未填报
//            for (BuildingExt building:buildingExt){
//                building.setData(new ArrayList<>());
//                //0:未填报 1：已填报
//                building.setState(state);
//            }
//            planFillinStateExt.setBuildingExtList(buildingExt);
//            planFillinStateExt.setProcessId(processId);
//            planFillinStateExt.setState(state);
//        }
//        return planFillinStateExt;
//    }
//    /**
//     * 用水计划审批查询
//     * @return
//     */
//    @Override
//    public List<BuildingExt> findPlanValueByCodAndTime(PlanFillinExamineDTO planFillinExamineDTO) {
//      ;
//        String planId = planFillinExamineDTO.getPlanId();
//        List<MngUnitGrade> mngUnitGradeList = planFillinExamineDTO.getMngUnitGrade();
//        List<String> mngUnitList = new ArrayList<>();
//        mngUnitGradeList.forEach(mngUnitGrade->{
//            //获取用户组中的管理站
//            mngUnitList.add(mngUnitGrade.getDeptId());
//        });
//        QueryWrapper<WaterPlanFillinYear> wrapper = new QueryWrapper<>();
//        wrapper.eq("PLAN_TASK_ID",planId);
//        wrapper.in("MANAGE_UNIT_ID",mngUnitList);
//        //查询任务id与管理站下的计划填报
//        List<WaterPlanFillinYear> waterPlanFillinYearList = waterPlanFillinYearMapper.selectList(wrapper);
//        List<String> waterUnitIds = new ArrayList<>();
//        waterPlanFillinYearList.forEach(waterPlanFillinYear->{
//            //获取填报的用水单位
//            waterUnitIds.add(waterPlanFillinYear.getWaterUnitId());
//        });
//        //获取用户权限下的单位
//        List<String> buildingIds = new ArrayList<>();
//        List<BuildingExt> buildingExt = waterBuildingManagerService.getBuildingExtListByUnit(waterUnitIds,planFillinExamineDTO.getUnitLevels(),
//                planFillinExamineDTO.getBuildingTypes(),planFillinExamineDTO.getFillReport(),planFillinExamineDTO.getLevels());
//        for (BuildingExt buildingExts:buildingExt){
//            //获取引水口id
//            buildingIds.add(buildingExts.getBuildingId());
//        }
//        //获取填报数据
//        List<BuildingExt> result = planValue(waterPlanFillinYearList,buildingExt,"1");
//        PlanFillinStateExt planFillinStateExt = new PlanFillinStateExt();
//        planFillinStateExt.setBuildingExtList(result);
//        planFillinStateExt.setProcessId(planFillinExamineDTO.getProcessId());
//        planFillinStateExt.setPalnId(planFillinExamineDTO.getPlanId());
//
//        return result;
//    }
//    /**
//     *  年计划汇总(管理单位)
//     * @param year
//     * @param userId
//     * @param mngUnitId
//     * @param unitLevels
//     * @param buildingType
//     * @param fillReport
//     * @return
//     */
//    @Override
//    public List<BuildingExt> findPlanAllValueByCodAndTime(String year, String userId, List<String> mngUnitId, List<Integer> unitLevels, List<String> buildingType, Integer fillReport,List<Integer> levels) {
//        List<BuildingExt> buildingExt = new ArrayList<>();
//        //用水户预览
//        if (StringUtils.isNotEmpty(userId)){
//            buildingExt =  waterBuildingManagerService.getBuildingExtListByUser(userId,unitLevels , buildingType,fillReport,levels);
//            //管理站预览
//        }else {
//            buildingExt = waterBuildingManagerService.getBuildingExtListByMng(mngUnitId,unitLevels,buildingType,fillReport,levels);
//        }
//        //获取用户权限下的单位
//        List<String> buildId = new ArrayList<>();
//
//        for (BuildingExt buildingExts:buildingExt){
//            buildId.add(buildingExts.getBuildingId());
//        }
//        List<BuildingExt> result = planValue(waterPlanFillinYearList(year,buildId),buildingExt,"1");
//        return result;
//    }
//    // 获取当前条件下的填报数据
//    private List<BuildingExt> planValue( List<WaterPlanFillinYear> waterPlanFillinYears,List<BuildingExt> buildingExt,String state){
//        if (waterPlanFillinYears.size()>0){
//            //根据引水口进行分类
//            Map<String, List<WaterPlanFillinYear>> buildingGroupMap = waterPlanFillinYears.stream().collect(Collectors.groupingBy(WaterPlanFillinYear::getBuildingId));
//            for (String buildingCode: buildingGroupMap.keySet()) {
//                List<WaterPlanFillinYear> hourdbs = buildingGroupMap.get(buildingCode);
//                // month升序
//                Comparator<WaterPlanFillinYear> byIdASC = Comparator.comparing(WaterPlanFillinYear::getMonth);
//                List<WaterPlanFillinYear> result = hourdbs.stream().sorted(byIdASC).collect(Collectors.toList());
//                List<BigDecimal> bigDecimal = Arrays.asList(new BigDecimal[36]);
//                for (WaterPlanFillinYear waterPlanFillinYear:result){
//                    if ("4".equals(waterPlanFillinYear.getTday())){
//                        continue;
//                    }
//                    Integer month = Integer.valueOf(waterPlanFillinYear.getMonth());
//                    Integer tday =  Integer.valueOf(waterPlanFillinYear.getTday());
//                    //计算值所在集合位置
//                    Integer num = tday+(month-1)*3-1;
//                    bigDecimal.set(num,waterPlanFillinYear.getDemadWaterQuantity());
//                }
//                for (BuildingExt building:buildingExt){
//                    building.setData(new ArrayList<BigDecimal>());
//                    if(building.getBuildingId().equals(buildingCode)){
//                        building.setData(bigDecimal);
//                        building.setState(state);
//                    }
//                }
//            }
//        }
//        return buildingExt;
//    }
//    //获取
//    private List<WaterPlanFillinYear> waterPlanFillinYearList(String time,List<String> buildId){
//        QueryWrapper<WaterPlanFillinYear> wrapper = wrapper(time,buildId);
//        List<WaterPlanFillinYear> waterPlanFillinYears = waterPlanFillinYearMapper.selectList(wrapper);
//        return waterPlanFillinYears;
//    }
//    //wrapper
//    private  QueryWrapper<WaterPlanFillinYear> wrapper(String year,List<String> buildId){
//        QueryWrapper<WaterPlanFillinYear> wrapper = new QueryWrapper<>();
//        wrapper.eq("YEAR",year);
//        if(buildId.size()>0){
//            wrapper.in("BUILDING_ID",buildId);
//        }
//        return  wrapper;
//    }
//    // 整合jsonArray生成多条年计划填报数据
//    private Map<String,Object> fillinTask(WrPlanFileYearValue wrPlanFileYearValue) throws ParseException{
//        //获取管理站id
//        JSONArray jsonArray = wrPlanFileYearValue.getJsonArray();
//        List<Object> objectlists = JSON.parseArray(jsonArray.toJSONString(), Object.class);
//        List<String> mngUnitList = new ArrayList<>();
//        for(Object object: objectlists) {
//            Map<String, Object> manageUnitMap = JSON.parseObject(String.valueOf(object), Map.class);
//            mngUnitList.add(String.valueOf(manageUnitMap.get("manageUnitId")));
//        }
//        //启动流程
//        String userId = wrPlanFileYearValue.getUserId();
//        //提交
//        Map<String,Object> mapId = new HashMap<>();
//        mapId.put("id",userId);
//        ActivitiHandle activitiHandle = new ActivitiHandle();
//        activitiHandle.setFlowKey("plan_year_id");
//        activitiHandle.setHandleType("submit");
//        activitiHandle.setSrc("promng");
//        activitiHandle.setUserInfo(mapId);
//        Map<String,Object> param = new HashMap<>();
//        param.put("unitId",mngUnitList);
//        activitiHandle.setParam(param);
//        Map<String,Object> mapObject = activiciTaskService.getProcessInstanceList(activitiHandle);
//        //获取流程id
//        String processId = String.valueOf(mapObject.get("processId"));
//        Map<String,Object> resultMap = new HashMap<>();
//        //年计划填报
//        List<WaterPlanFillinYear> waterPlanFillinYearList = new ArrayList<>();
//        //旬月数据保存
//        List<WrPlanInterTday> wrPlanInterTdayAndMonthList = new ArrayList<>();
//        //任务表关联数据
//        List<WrPlanTaskSub> wrPlanTaskSubList = new ArrayList<>();
//        //任务id
//        String planId = IDGenerator.getId();
//        for(Object object: objectlists) {
//            Double monthPlnValue =0.0;
//            Map<String, Object> valuemap = JSON.parseObject(String.valueOf(object), Map.class);
//            int tday = 1;
//            int month = 1;
//            for (int i = 0; i <= 36; i++) {
//                //获取当前某旬数与计划水量数据
//                if (!valuemap.containsKey("planValue" + i)){
//                    break;
//                }
//                Double planValue = Double.valueOf(String.valueOf(valuemap.get("planValue" + i)));
//                //获取当前旬类别
//                if (tday==4){
//                    tday=1;
//                    //获取当前月份
//                    month++;
//                }
//                //获取当前月份
//                String monthNum = String.valueOf(month);
//                //获取当前年份
//                String yearNum = String.valueOf(valuemap.get("year"));
//                //获取当前旬类别
//                String tdayNum = String.valueOf(tday);
//                //当前年月旬对应的时间
//                Date dayTime =dateTime(monthNum,yearNum,tdayNum);
//                //获取当前旬月对应的秒数
//                Double secondNum = findSecond(yearNum,monthNum,tdayNum);
//                //获取管理站,用水单位,引水口对应数据
//                String waterUnitCode =String.valueOf(valuemap.get("waterUnitId"));
//                String manageUnitCode =String.valueOf(valuemap.get("manageUnitId"));
//                String buildingCode =String.valueOf(valuemap.get("buildingId"));
//                if (StringUtils.isNotEmpty(planValue)) {
//                    WaterPlanFillinYear waterPlanFillinYear = waterPlanFillinYear(planValue,secondNum,yearNum,monthNum);
//                    waterPlanFillinYear.setTday(tdayNum);
//                    waterPlanFillinYear.setWaterUnitId(waterUnitCode);
//                    waterPlanFillinYear.setManageUnitId(manageUnitCode);
//                    waterPlanFillinYear.setBuildingId(buildingCode);
//                    //计划id
//                    waterPlanFillinYear.setPlanTaskId(planId);
//                    waterPlanFillinYearList.add(waterPlanFillinYear);
//
//                    WrPlanInterTday wrPlanInterTdayAndMonth = wrPlanInterTdayAndMonth(buildingCode,dayTime,secondNum,planValue,tdayNum);
//                    wrPlanInterTdayAndMonthList.add(wrPlanInterTdayAndMonth);
//
//                }
//                //添加旬别为全月的需求数据
//                if (StringUtils.isNotEmpty(planValue)) {
//                    monthPlnValue += planValue;
//                }
//                if (tday==3){
//                    //当前年月旬对应的时间
//                    Date dayTimes =dateTime(monthNum,yearNum,"4");
//                    //获取当前旬类别
//                    WaterPlanFillinYear waterPlanFillinYear = waterPlanFillinYear(monthPlnValue,secondNum,yearNum,monthNum);
//                    waterPlanFillinYear.setTday("4");
//                    waterPlanFillinYear.setWaterUnitId(waterUnitCode);
//                    waterPlanFillinYear.setManageUnitId(manageUnitCode);
//                    waterPlanFillinYear.setBuildingId(buildingCode);
//                    waterPlanFillinYearList.add(waterPlanFillinYear);
//
//                    WrPlanInterTday wrPlanInterTdayAndMonth = wrPlanInterTdayAndMonth(buildingCode,dayTimes,secondNum,monthPlnValue,"4");
//                    wrPlanInterTdayAndMonthList.add(wrPlanInterTdayAndMonth);
//                    monthPlnValue = 0.0;
//                }
//                tday++;
//            }
//            //任务数据
//            WrPlanTaskSub wrPlanTaskSub = new WrPlanTaskSub();
//            wrPlanTaskSub.setId(IDGenerator.getId());
//            wrPlanTaskSub.setTaskId(planId);
//            wrPlanTaskSub.setUnitName(String.valueOf((valuemap.get("waterUnit3"))));
//            wrPlanTaskSub.setUnitType("1");
//            wrPlanTaskSub.setUnitId(String.valueOf((valuemap.get("waterUnitId"))));
//            wrPlanTaskSubList.add(wrPlanTaskSub);
//        }
//        List<WrPlanTaskSub> wrPlanTaskSubs = waterUnitId(wrPlanTaskSubList);
//        //任务数据存储
//        WrPlanTask wrPlanTask =  new WrPlanTask();
//        //任务名称
//        String planName = "年度用水计划填报";
//        wrPlanTask.setId(planId);
//        //年度用水计划
//        wrPlanTask.setPlanType("0");
//        wrPlanTask.setTaskName(planName);
//        //流程id
//        wrPlanTask.setWaterPlanFillIn(processId);
//        if (objectlists.size()>0){
//            Map<String, Object> map = JSON.parseObject(String.valueOf(objectlists.get(0)), Map.class);
//            //填报人id
//            wrPlanTask.setPersonId(String.valueOf((map.get("userId"))));
//            wrPlanTask.setContent(String.valueOf(map.get("content")));
//            wrPlanTask.setMonth(String.valueOf(String.valueOf(map.get("month"))));
//            wrPlanTask.setYear(String.valueOf(String.valueOf(map.get("year"))));
//            wrPlanTask.setState("1");
//            wrPlanTask.setWrPlanTaskSubList(wrPlanTaskSubs);
//        }
//        resultMap.put("waterPlanFillinYearList",waterPlanFillinYearList);
//        resultMap.put("wrPlanInterTdayAndMonthList",wrPlanInterTdayAndMonthList);
//        resultMap.put("wrPlanTask",wrPlanTask);
//        resultMap.put("processId",processId);
//        return resultMap;
//    }
//    //移除重复的用水单位所在对象
//    private List<WrPlanTaskSub> waterUnitId(List<WrPlanTaskSub> subs){
//        List<WrPlanTaskSub> wrPlanTaskSubs = new ArrayList<>();
//        List<String> unitIds = new ArrayList<>();
//        subs.forEach(wrPlanTaskSub->{
//            //获取用户组中的管理站
//            unitIds.add(wrPlanTaskSub.getUnitId());
//        });
//        myList(unitIds);
//        unitIds.forEach(s->{
//            WrPlanTaskSub wrPlanTaskSub = new WrPlanTaskSub();
//            //获取用户组中的管理站
//            subs.forEach(sub->{
//                if (sub.getUnitId().equals(s)){
//                    wrPlanTaskSub.setId(sub.getId());
//                    wrPlanTaskSub.setUnitType(sub.getUnitType());
//                    wrPlanTaskSub.setUnitId(sub.getUnitId());
//                    wrPlanTaskSub.setUnitName(sub.getUnitName());
//                    wrPlanTaskSub.setTaskId(sub.getTaskId());
//                }
//            });
//            wrPlanTaskSubs.add(wrPlanTaskSub);
//        });
//        return wrPlanTaskSubs;
//    }
//    //去重
//    private void  myList(List<String> list){
//         list = list.stream().distinct().collect(Collectors.toList());
//    }
//    //获取某年某月某旬对应的秒数
//    private static Double findSecond(String year,String month,String tday){
//        Calendar a = Calendar.getInstance();
//        a.set(Calendar.YEAR, Integer.valueOf(year));
//        a.set(Calendar.MONTH, Integer.valueOf(month) - 1);
//        a.set(Calendar.DATE, 1);
//        a.roll(Calendar.DATE, -1);
//        int maxDate = a.get(Calendar.DATE);
//        //86400
//        if ("3".equals(tday)){
//            maxDate = maxDate-20;
//        }
//        Double secondNumber = Double.valueOf(maxDate)*86400;
//        return  secondNumber;
//    }
//    //年计划数据整合公共方法
//    private static WaterPlanFillinYear waterPlanFillinYear(Double planValue,Double secondNum,String yearNum,String monthNum){
//        WaterPlanFillinYear waterPlanFillinYear = new WaterPlanFillinYear();
//        //需求水量demadWaterQuantity
//        BigDecimal demadWaterQuantity = new BigDecimal(planValue);
//        waterPlanFillinYear.setDemadWaterQuantity(demadWaterQuantity);
//        //需求流量demadWaterFlow
//        BigDecimal demadWaterFlow = new BigDecimal( planValue/secondNum);
//        waterPlanFillinYear.setProposalWaterFlow(demadWaterFlow);
//        waterPlanFillinYear.setYear(yearNum);
//        waterPlanFillinYear.setPlanName("年度计划-" + yearNum);
//        waterPlanFillinYear.setMonth(monthNum);
//        //主键id
//        waterPlanFillinYear.setId(IDGenerator.getId());
//        return waterPlanFillinYear;
//    }
//    //旬月迭代表数据整理
//    private static WrPlanInterTday wrPlanInterTdayAndMonth(String buildingId, Date time, Double secondNum, Double planValue, String tdayType){
//        //需求水量demadWaterQuantity
//        BigDecimal demadWaterQuantity = new BigDecimal(planValue);
//        //需求流量demadWaterFlow
//        BigDecimal demadWaterFlow = new BigDecimal( planValue/secondNum);
//        WrPlanInterTday wrPlanInterTdayAndMonth = new WrPlanInterTday();
//        wrPlanInterTdayAndMonth.setId(IDGenerator.getId());
//        wrPlanInterTdayAndMonth.setBuildingId(buildingId);
//        wrPlanInterTdayAndMonth.setSupplyTime(time);
//        wrPlanInterTdayAndMonth.setWaterFlow(demadWaterFlow);
//        wrPlanInterTdayAndMonth.setWaterQuantity(demadWaterQuantity);
//        wrPlanInterTdayAndMonth.setTimeType(tdayType);
//        return wrPlanInterTdayAndMonth;
//    }
//    //根据年月旬获取DATE时间格式
//    private static Date dateTime(String month,String year,String tDay)throws ParseException {
//        if (month.length()<2){
//            month ="0"+month;
//        }
//        String dateTime = null;
//        if (tDay.equals("1")){
//            dateTime = year+"-"+month+"-"+"0"+tDay;
//        }else if (tDay.equals("2")){
//            dateTime = year+"-"+month+"-"+"11";
//        }else if (tDay.equals("3")){
//            dateTime = year+"-"+month+"-"+"21";
//        }else if (tDay.equals("4")){
//            dateTime = year+"-"+month+"-"+"01";
//        }
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        Date date = simpleDateFormat.parse(dateTime);
//        return date;
//    }
//}
