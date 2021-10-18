/*
package com.nari.slsd.msrv.waterdiversion.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.msrv.common.utils.IDGenerator;
import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.msrv.waterdiversion.interfaces.*;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WaterPlanFillinMonthMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.ActivitiHandle;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.*;
import com.nari.slsd.msrv.waterdiversion.model.vo.BuildingExt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

*/
/**
 * <p>
 * 年度用水计划填报 服务实现类
 * </p>
 *
 * @author zhs
 * @since 2021-08-04
 *//*

@Slf4j
@Service
public class WaterPlanFillinMonthServiceImpl extends ServiceImpl<WaterPlanFillinMonthMapper, WaterPlanFillinMonth> implements IWaterPlanFillinMonthService {
    @Resource
    WaterPlanFillinMonthMapper waterPlanFillinMonthMapper;
    @Resource
    IWrPlanInterDayService wrPlanInterDayService;
    @Resource
    IWaterBuildingManagerService waterBuildingManagerService;
    @Resource
    TransactionTemplate transactionTemplate;
    @Resource
    IWrPlanTaskService wrPlanTaskService;
    @Resource
    IWrPlanFillinService wrPlanFillinService;

    */
/**
     * 批量保存水量数据
     *//*

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void addWaterPlanValue(JSONArray jsonArray) {

        try {
            // 整合jsonArray生成多条年计划数据
            Map<String,Object> resustMap = jsonToList(jsonArray);
            List<WaterPlanFillinMonth> waterPlanFillinMonthList = (List<WaterPlanFillinMonth>)resustMap.get("waterPlanFillinMonthList");
            //整合多条年旬月计划数据
            List<WrPlanInterDay> wrPlanInterDayList = (List<WrPlanInterDay>)resustMap.get("wrPlanInterDayList");
            //整合计划任务数据
            WrPlanTask wrPlanTask = (WrPlanTask) resustMap.get("wrPlanTask");
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                //批量保存计划水量数据
                saveBatch(waterPlanFillinMonthList);
                wrPlanInterDayService.saveBatch(wrPlanInterDayList);
                wrPlanTaskService.insert(wrPlanTask);
                // wrPlanTaskService.
            });

        }catch(Exception e){
            e.printStackTrace();
            log.error("保存失败："+e.getMessage());
        }
    }
    */
/**
     * 年用水计划填报查询与
     * 年
     * @param
     * @param
     * @return
     *//*

    @Override
    public List<BuildingExt> findPlanValueByCodAndTime(String year,String month,String userId, List<Integer> unitLevels, List<String> buildingType,Integer fillReport,List<Integer> levels) {
        //获取用户权限下的引水口id
        List<String> buildId = new ArrayList<>();
        List<BuildingExt> buildingExt = waterBuildingManagerService.getBuildingExtListByUser(userId,unitLevels , buildingType,fillReport,levels);
        for (BuildingExt buildingExts:buildingExt){
            buildId.add(buildingExts.getBuildingId());
        }
        QueryWrapper<WaterPlanFillinMonth> wrapper= wrapper(year,month,buildId);
        List<WaterPlanFillinMonth> waterPlanFillinMonths = waterPlanFillinMonthMapper.selectList(wrapper);
        List<BuildingExt> result = planValue(buildingExt,waterPlanFillinMonths);

        return result;
    }
    */
/**
     *   月用水计划汇总查询(传mngUnitId,)
     *//*

    @Override
    public List<BuildingExt> findPlanAllValueByCodAndTime(String year, String month, String userId, List<String> mngUnitId, List<Integer> unitLevels, List<String> buildingType, Integer fillReport,List<Integer> levels) {
        //获取用户权限下的引水口id
        List<String> buildId = new ArrayList<>();
        List<BuildingExt> buildingExt = new ArrayList<>();
        //用水户预览
        if (StringUtils.isNotEmpty(userId)){
            buildingExt =  waterBuildingManagerService.getBuildingExtListByUser(userId,unitLevels , buildingType,fillReport,levels);
            //管理站预览
        }else {
            buildingExt = waterBuildingManagerService.getBuildingExtListByMng(mngUnitId,unitLevels,buildingType,fillReport,levels);
        }
        for (BuildingExt buildingExts:buildingExt){
            buildId.add(buildingExts.getBuildingId());
        }
        QueryWrapper<WaterPlanFillinMonth> wrapper= wrapper(year,month,buildId);
        List<WaterPlanFillinMonth> waterPlanFillinMonths = waterPlanFillinMonthMapper.selectList(wrapper);
        List<BuildingExt> result = planValue(buildingExt,waterPlanFillinMonths);

        return result;
    }
    //月用水计划查询整合
    private List<BuildingExt> planValue(List<BuildingExt> buildingExt, List<WaterPlanFillinMonth> waterPlanFillinMonths){
        if (waterPlanFillinMonths.size()>0){
            //根据引水口进行分类
            Map<String, List<WaterPlanFillinMonth>> buildingGroupMap = waterPlanFillinMonths.stream().collect(Collectors.groupingBy(WaterPlanFillinMonth::getBuildingId));
            for (String buildingCode: buildingGroupMap.keySet()) {
                List<WaterPlanFillinMonth> hourdbs = buildingGroupMap.get(buildingCode);
                // day升序
                Comparator<WaterPlanFillinMonth> byIdASC = Comparator.comparing(WaterPlanFillinMonth::getDay);
                List<WaterPlanFillinMonth> result = hourdbs.stream().sorted(byIdASC).collect(Collectors.toList());
                List<BigDecimal> bigDecimal = Arrays.asList(new BigDecimal[30]);
                for (WaterPlanFillinMonth waterPlanFillinMonth:result){
                    if ("4".equals(waterPlanFillinMonth.getTday())){
                        continue;
                    }
                    Integer day =  Integer.valueOf(waterPlanFillinMonth.getDay());
                    bigDecimal.set(day,waterPlanFillinMonth.getDemadWaterQuantity());
                }
                for (BuildingExt building:buildingExt){
                    if(building.getBuildingId().equals(buildingCode)){
                        building.setData(bigDecimal);
                    }
                }
            }
        }
        return buildingExt;
    }
    //wrapper
    private  QueryWrapper<WaterPlanFillinMonth> wrapper(String year, String month,List<String> buildId){
        QueryWrapper<WaterPlanFillinMonth> wrapper = new QueryWrapper<>();
        wrapper.eq("YEAR",year);
        wrapper.eq("MONTH",month);
        wrapper.in("BUILDING_ID",buildId);
        return  wrapper;
    }
    // 整合jsonArray生成多条年计划填报数据
    private static Map<String,Object> jsonToList(JSONArray jsonArray) throws ParseException{
        Map<String,Object> resultMap = new HashMap<>();
        List<WrPlanInterDay> wrPlanInterDayList = new ArrayList<>();
        List<Object> objectlists = JSON.parseArray(jsonArray.toJSONString(), Object.class);
        //月计划填报
        List<WaterPlanFillinMonth> waterPlanFillinMonthList = new ArrayList<>();
        //任务id
        String planId = IDGenerator.getId();
        //任务表关联数据
        List<WrPlanTaskSub> wrPlanTaskSubList = new ArrayList<>();
        for(Object object: objectlists) {
            Double monthPlnValue =0.0;
            Map<String, Object> valuemap = JSON.parseObject(String.valueOf(object), Map.class);
            int tday = 1;
            //获取当前月份
            String monthNum = String.valueOf(valuemap.get("month"));
            //获取当前年份
            String yearNum = String.valueOf(valuemap.get("year"));
            //获取填报人员id
            String userId = String.valueOf(valuemap.get("userId"));
            //获取当前年月的天数
            int tdayNum = dayNumForYearAndMonth(yearNum,monthNum);
            for (int i = 0; i < tdayNum; i++) {
                //获取当前日数与计划水量数据
                if (!valuemap.containsKey("planValue" + i)){
                    continue;
                }
                Double planValue = Double.valueOf(String.valueOf(valuemap.get("planValue" + i)));
                //获取当前日数据
                String dayNum = String.valueOf(i);
                //当前年月日对应的时间
                Date dayTime =dateTime(monthNum,yearNum,dayNum);
                //获取当前旬月对应的秒数
                Double secondNum = 86400.0;
                //获取管理站,用水单位,引水口对应数据
                String waterUnitId =String.valueOf(valuemap.get("waterUnitId"));
                String manageUnitId =String.valueOf(valuemap.get("manageUnitId"));
                String buildingId =String.valueOf(valuemap.get("buildingId"));
                if (StringUtils.isNotEmpty(planValue)) {
                    WaterPlanFillinMonth waterPlanFillinMonth = waterPlanFillinMonth(planValue,secondNum,yearNum,monthNum,dayNum);
                    waterPlanFillinMonth.setWaterUnitId(waterUnitId);
                    waterPlanFillinMonth.setManageUnitId(manageUnitId);
                    waterPlanFillinMonth.setBuildingId(buildingId);
                    waterPlanFillinMonthList.add(waterPlanFillinMonth);

                    WrPlanInterDay wrPlanInterDay = wrPlanInterDay(buildingId,dayTime,secondNum,planValue);
                    wrPlanInterDayList.add(wrPlanInterDay);

                }
                //添加旬别为全月的需求数据
                if (StringUtils.isNotEmpty(planValue)) {
                    monthPlnValue += planValue;
                }
                tday++;
            }
            //任务数据
            WrPlanTaskSub wrPlanTaskSub = new WrPlanTaskSub();
            wrPlanTaskSub.setId(IDGenerator.getId());
            wrPlanTaskSub.setTaskId(planId);
            wrPlanTaskSub.setUnitName(String.valueOf((valuemap.get("waterUnit3"))));
            wrPlanTaskSub.setUnitType("1");
            wrPlanTaskSub.setUnitId(String.valueOf((valuemap.get("waterUnitId"))));
            wrPlanTaskSubList.add(wrPlanTaskSub);
        }
        //任务数据存储
        WrPlanTask wrPlanTask =  new WrPlanTask();
        //任务名称
        String planName = "月度用水计划填报";
        wrPlanTask.setId(planId);
        //月度用水计划
        wrPlanTask.setPlanType("1");
        wrPlanTask.setTaskName(planName);
        wrPlanTask.setWaterPlanFillIn("");
        if (objectlists.size()>0){
            Map<String, Object> map = JSON.parseObject(String.valueOf(objectlists.get(0)), Map.class);
            //填报人id
            wrPlanTask.setPersonId(String.valueOf((map.get("userId"))));
            wrPlanTask.setContent(String.valueOf(map.get("content")));
            wrPlanTask.setMonth(String.valueOf(String.valueOf(map.get("month"))));
            wrPlanTask.setYear(String.valueOf(String.valueOf(map.get("year"))));
            wrPlanTask.setState("1");
            wrPlanTask.setWrPlanTaskSubList(wrPlanTaskSubList);
        }
        resultMap.put("waterPlanFillinMonthList",waterPlanFillinMonthList);
        resultMap.put("wrPlanInterDayList",wrPlanInterDayList);
        resultMap.put("wrPlanTask",wrPlanTask);
        return resultMap;
    }
    //年计划数据整合公共方法
    private static WaterPlanFillinMonth waterPlanFillinMonth(Double planValue,Double secondNum,String yearNum,String monthNum,String dayNum){
        WaterPlanFillinMonth waterPlanFillinMonth = new WaterPlanFillinMonth();
        //需求水量demadWaterQuantity
        BigDecimal demadWaterQuantity = new BigDecimal(planValue);
        waterPlanFillinMonth.setDemadWaterQuantity(demadWaterQuantity);
        //需求流量demadWaterFlow
        waterPlanFillinMonth.setProposalWaterFlow(new BigDecimal( planValue/secondNum));
        waterPlanFillinMonth.setYear(yearNum);
        waterPlanFillinMonth.setPlanName("月度计划-" + yearNum);
        waterPlanFillinMonth.setMonth(monthNum);
        waterPlanFillinMonth.setDay(dayNum);
        //计划id
        waterPlanFillinMonth.setPlanTaskId(IDGenerator.getId());
        //主键id
        waterPlanFillinMonth.setId(IDGenerator.getId());
        return waterPlanFillinMonth;
    }
    //日迭代表数据整理
    private static WrPlanInterDay wrPlanInterDay(String buildingId, Date time, Double secondNum, Double planValue){

        //需求流量demadWaterFlow
        BigDecimal demadWaterFlow = new BigDecimal( planValue/secondNum);
        WrPlanInterDay wrPlanInterDay = new WrPlanInterDay();
        wrPlanInterDay.setId(String.valueOf(IDGenerator.getId()));
        wrPlanInterDay.setBuildingId(buildingId);
        wrPlanInterDay.setSupplyTime(time);
        wrPlanInterDay.setWaterFlow(demadWaterFlow);
        //需求水量demadWaterQuantity
        wrPlanInterDay.setWaterQuantity(new BigDecimal(planValue));
        return wrPlanInterDay;
    }
    //根据年月日获取DATE时间格式
    private static Date dateTime(String month,String year,String day)throws ParseException {
        if (month.length()<2){
            month ="0"+month;
        }
        if(day.length()<2){
            day = "0"+day;
        }
        String dateTime = year+"-"+month+"-"+day;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormat.parse(dateTime);
        return date;
    }
    //根据年月获取天数
    private static int dayNumForYearAndMonth(String yearNum,String monthNum)throws ParseException{
        String yearAndMonth = yearNum+"/"+monthNum;
        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy/MM");
        rightNow.setTime(simpleDate.parse(yearAndMonth));

        int days = rightNow.getActualMaximum(Calendar.DAY_OF_MONTH);
        System.out.println(days);
        return days;
    }
    */
/**
     * 用水计划填报单位查询
     * @param
     * @return
     * @throws ParseException
     *//*

    @Override
    public List<BuildingExt> findUnitByCodAndTime(String year,String month, String userId, List<Integer> unitLevels, List<String> buildingType,Integer fillReport,List<Integer> levels) {
        //获取月填报状态
        Map<String,Object> map = wrPlanFillinService.state(userId,unitLevels,buildingType,year,month,fillReport,levels);
        String state = String.valueOf(map.get("state"));
        List<BuildingExt> buildingExt = waterBuildingManagerService.getBuildingExtListByUser(userId,unitLevels , buildingType,fillReport,levels);
        for (BuildingExt building:buildingExt){
            building.setData(new ArrayList<>());
            building.setState(state);
        }
        return buildingExt;
    }

}
*/
