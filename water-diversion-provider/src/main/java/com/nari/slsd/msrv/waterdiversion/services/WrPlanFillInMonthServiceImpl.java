package com.nari.slsd.msrv.waterdiversion.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.DateUtils;
import com.nari.slsd.msrv.common.utils.IDGenerator;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.msrv.waterdiversion.commons.PlanFillInTypeEnum;
import com.nari.slsd.msrv.waterdiversion.commons.TaskStateEnum;
import com.nari.slsd.msrv.waterdiversion.interfaces.*;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WaterBuildingManagerMapper;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WaterPlanFillinMonthMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.*;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.*;
import com.nari.slsd.msrv.waterdiversion.model.vo.BuildingExt;
import com.nari.slsd.msrv.waterdiversion.model.vo.PlanFillinStateExt;
import com.nari.slsd.msrv.waterdiversion.utils.CommonUtil;
import com.nari.slsd.msrv.waterdiversion.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 年度用水计划填报 服务实现类
 * </p>
 *
 * @author zhs
 * @since 2021-08-04
 */
@Slf4j
@Service
public class WrPlanFillInMonthServiceImpl extends ServiceImpl<WaterPlanFillinMonthMapper, WaterPlanFillinMonth> implements IWrPlanFillInMonthService {
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


    /**
     * 批量保存水量数据
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ResultModel addWaterPlanValue(WrPlanFillInValue wrPlanFillInValue) {
        //获取月填报相关数据信息
        List<Map<String,Object>> fillInValue = wrPlanFillInValue.getFillinValue();
        //填报人id
        String userId = wrPlanFillInValue.getUserId();
        //填报内容
        String content = wrPlanFillInValue.getContent();
        //
        String time = wrPlanFillInValue.getTime();
        List<String> times = new ArrayList<>(Arrays.asList(time.split("-")));
        String year = times.get(0);
        String month = times.get(1);
        try {
            // 整合jsonArray生成多条月计划数据
            WrPlanFillinRelevantTable resust = fillInTask(fillInValue,userId,content,year,month);
            List<WaterPlanFillinMonth> waterPlanFillinMonthList = resust.getWaterPlanFillinMonth();
            //整合多条年旬月计划数据
            List<WrPlanInterDay> wrPlanInterDayList = resust.getWrPlanInterDay();
            //整合计划任务数据
            WrPlanTask wrPlanTask = resust.getWrPlanTask();
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                //批量保存计划水量数据
                saveBatch(waterPlanFillinMonthList);
                wrPlanInterDayService.saveBatch(wrPlanInterDayList);
                wrPlanTaskService.insert(wrPlanTask);
            });
            return ResultModelUtils.getAddSuccessInstance(true);
        }catch(Exception e){
            log.error("保存失败：{}",e.getMessage());
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    @Override
    public ResultModel updateWaterPlanValue(WrPlanFillInValue wrPlanFillInValue) {
        List<Map<String,Object>> fillInValue =  wrPlanFillInValue.getFillinValue();
        String time = wrPlanFillInValue.getTime();
        List<String> times = new ArrayList<>(Arrays.asList(time.split("-")));
        String year = times.get(0);
        String month = times.get(1);
        WrPlanFillinRelevantTable wrPlanFillinRelevantTable = fillInAndDayValue(fillInValue,year,month);
        try {
            //需更新旬迭代数据
            List<WrPlanInterDayDTO> wrPlanInterDayDTOList = wrPlanFillinRelevantTable.getWrPlanInterDayDTOList();
            //需更新的年填报数据
            List<WaterPlanFillinMonthDTO> waterPlanFillinMonthList = wrPlanFillinRelevantTable.getWaterPlanFillinMonthDTOList();
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                wrPlanInterDayService.updateDay(wrPlanInterDayDTOList);
                for (WaterPlanFillinMonthDTO waterPlanFillinMonthDTO:waterPlanFillinMonthList){
                    UpdateWrapper<WaterPlanFillinMonth> updateWrapper = new UpdateWrapper<WaterPlanFillinMonth>();
                    updateWrapper.eq("BUILDING_ID",waterPlanFillinMonthDTO.getBuildingId());
                    updateWrapper.eq("YEAR",waterPlanFillinMonthDTO.getYear());
                    updateWrapper.eq("MONTH",waterPlanFillinMonthDTO.getMonth());
                    updateWrapper.eq("DAY",waterPlanFillinMonthDTO.getDay());
                    WaterPlanFillinMonth waterPlanFillinMonths = new WaterPlanFillinMonth();
                    waterPlanFillinMonths.setDemadWaterQuantity(waterPlanFillinMonthDTO.getDemadWaterQuantity());
                    waterPlanFillinMonths.setDemadWaterFlow(waterPlanFillinMonthDTO.getDemadWaterFlow());
                    waterPlanFillinMonthMapper.update(waterPlanFillinMonths,updateWrapper);
                }
            });
            return ResultModelUtils.getEdiSuccessInstance(true);
        }catch(Exception e){
            log.error("更新失败：{}",e.getMessage());
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 用水计划填报单位查询
     * @param
     * @return
     * @throws ParseException
     */
    @Override
    public PlanFillinStateExt findUnitByCodAndTime(String time, List<String> mngUnitId, List<Integer> unitLevels, List<String> buildingType, Integer fillReport, List<Integer> buildingLevels) {
        PlanFillinStateExt planFillinStateExt = new PlanFillinStateExt();
        List<String> times = new ArrayList<>(Arrays.asList(time.split("-")));
        String year = times.get(0);
        String month = times.get(1);
        //获取月填报状态
        String state = wrPlanFillinService.fillInState(mngUnitId,year,month);

        List<BuildingExt> buildingExt = waterBuildingManagerService.getBuildingExtListByMng(mngUnitId,unitLevels , buildingType,fillReport,buildingLevels);
        if ("0".equals(state)){
            for (BuildingExt building:buildingExt){
                building.setData(new ArrayList<>());
                building.setState(state);
            }

        }else if ("1".equals(state)){
            QueryWrapper<WaterPlanFillinMonth> wrapper= wrapper(year,month,mngUnitId);
            List<WaterPlanFillinMonth> waterPlanFillInMonths = waterPlanFillinMonthMapper.selectList(wrapper);
            buildingExt = planValue(buildingExt,waterPlanFillInMonths);
        }
        planFillinStateExt.setBuildingExtList(buildingExt);
        planFillinStateExt.setState(state);
        return planFillinStateExt;
    }
    /**
     *   月用水计划汇总查询(传mngUnitId,)
     */
    @Override
    public List<BuildingExt> findPlanAllValueByCodAndTime(String time, String userId, List<String> mngUnitId, List<Integer> unitLevels, List<String> buildingType, Integer fillReport,List<Integer> levels) {
        List<String> times = new ArrayList<>(Arrays.asList(time.split("-")));
        String year = times.get(0);
        String month = times.get(1);
        List<BuildingExt> buildingExt = new ArrayList<>();
        //用水户预览
        if (StringUtils.isNotEmpty(userId)){
            buildingExt =  waterBuildingManagerService.getBuildingExtListByUser(userId,unitLevels , buildingType,fillReport,levels);
            //管理站预览
        }else {
            buildingExt = waterBuildingManagerService.getBuildingExtListByMng(mngUnitId,unitLevels,buildingType,fillReport,levels);
        }
        QueryWrapper<WaterPlanFillinMonth> wrapper= wrapper(year,month,mngUnitId);
        List<WaterPlanFillinMonth> waterPlanFillinMonths = waterPlanFillinMonthMapper.selectList(wrapper);
        List<BuildingExt> result = planValue(buildingExt,waterPlanFillinMonths);

        return result;
    }
    //获取填报数据与日修改数据
    private WrPlanFillinRelevantTable fillInAndDayValue(List<Map<String,Object>> fillInValue,String year,String month){
        List<WaterPlanFillinMonthDTO> waterPlanFillinMonthDTOList = new ArrayList<>();
        List<WrPlanInterDayDTO> wrPlanInterDayDTOList = new ArrayList<>();
        //获取当前年月对应的数据
        int dauNum = dayNumForYearAndMonth(year,month);
        for(Map<String,Object> valueMap:fillInValue){
            for (int i = 0; i < dauNum; i++) {
                if (!valueMap.containsKey("planValue" + i)){
                    continue;
                }
                //获取引水口id
                String buildingId = String.valueOf(valueMap.get("buildingId"));
                //获取需求水量
                BigDecimal waterQuantity = new BigDecimal(String.valueOf(valueMap.get("planValue" + i)));
                //获取需求流量
                BigDecimal waterFlow =CommonUtil.number(waterQuantity.doubleValue()/86400.0);
                //获取当前日
                String day = String.valueOf(i+1);
                waterPlanFillinMonthDTOList.add(waterPlanFillinMonthDTO(buildingId,year,month,day,waterQuantity,waterFlow));

                //根据年月日获取当前date时间
                Date dayTime =dateTime(month,year,day);
                wrPlanInterDayDTOList.add(wrPlanInterDayDTO(buildingId,dayTime,waterQuantity,waterFlow));
            }
        }
        WrPlanFillinRelevantTable wrPlanFillinRelevantTable = new WrPlanFillinRelevantTable();
        wrPlanFillinRelevantTable.setWaterPlanFillinMonthDTOList(waterPlanFillinMonthDTOList);
        wrPlanFillinRelevantTable.setWrPlanInterDayDTOList(wrPlanInterDayDTOList);
        return wrPlanFillinRelevantTable;
    }
    //需要更新的月填报数据（DTO类）
    private  WaterPlanFillinMonthDTO waterPlanFillinMonthDTO(String buildingId, String year,String month,String day,BigDecimal waterQuantity,BigDecimal waterFlow){
        WaterPlanFillinMonthDTO waterPlanFillinMonthDTO = new WaterPlanFillinMonthDTO();
        waterPlanFillinMonthDTO.setBuildingId(buildingId);
        waterPlanFillinMonthDTO.setYear(year);
        waterPlanFillinMonthDTO.setMonth(month);
        waterPlanFillinMonthDTO.setDay(day);
        waterPlanFillinMonthDTO.setDemadWaterQuantity(CommonUtil.number(waterQuantity.doubleValue()));
        waterPlanFillinMonthDTO.setDemadWaterFlow(waterFlow);
        return waterPlanFillinMonthDTO;
    }
    //需要更新的日数据（DTO类）
    private  WrPlanInterDayDTO wrPlanInterDayDTO(String buildingId, Date time,BigDecimal waterQuantity,BigDecimal waterFlow){
        WrPlanInterDayDTO wrPlanInterDayDTO = new WrPlanInterDayDTO();
        wrPlanInterDayDTO.setBuildingId(buildingId);
        wrPlanInterDayDTO.setSupplyTime(time);
        wrPlanInterDayDTO.setWaterQuantity(CommonUtil.number(waterQuantity.doubleValue()));
        wrPlanInterDayDTO.setWaterFlow(waterFlow);

        return wrPlanInterDayDTO;
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
                List<BigDecimal> bigDecimal = Arrays.asList(new BigDecimal[
                        DateUtil.getDaysOfMonth(Integer.valueOf(waterPlanFillinMonths.get(0).getYear()),
                                Integer.valueOf(waterPlanFillinMonths.get(0).getMonth()))]);
                for (WaterPlanFillinMonth waterPlanFillinMonth:result){
                    if ("4".equals(waterPlanFillinMonth.getTday())){
                        continue;
                    }
                    Integer day =  Integer.valueOf(waterPlanFillinMonth.getDay())-1;
                    bigDecimal.set(day,waterPlanFillinMonth.getDemadWaterQuantity());
                }
                for (BuildingExt building:buildingExt){
                    if(building.getData()==null){
                        building.setData(new ArrayList<BigDecimal>());
                    }
                    if(building.getBuildingId().equals(buildingCode)){
                        building.setData(bigDecimal);
                    }
                }
            }
        }
        return buildingExt;
    }
    //wrapper
    private  QueryWrapper<WaterPlanFillinMonth> wrapper(String year, String month,List<String> mngUnitId){
        QueryWrapper<WaterPlanFillinMonth> wrapper = new QueryWrapper<>();
        wrapper.eq("YEAR",year);
        wrapper.eq("MONTH",month);
        wrapper.in("MANAGE_UNIT_ID",mngUnitId);
        return  wrapper;
    }
    // 整合多条年计划填报数据
    private  WrPlanFillinRelevantTable fillInTask(List<Map<String,Object>> fillInValue,String userId,String content,String year,String month) {
        //月填报数据
        List<WaterPlanFillinMonth> waterPlanFillInMonthList = new ArrayList<>();
        //日迭代表数据
        List<WrPlanInterDay> wrPlanInterDayList = new ArrayList<>();
        //任务计划id
        String planId = IDGenerator.getId();
        String mngUnitId = null;
        String mngUnitName =null;
        //填报类型
        String planType = PlanFillInTypeEnum.MONTH_PLAN_FILL_IN.getId();
        //计划名称
        String taskNmae = PlanFillInTypeEnum.MONTH_PLAN_FILL_IN.getName();
        for(Map<String,Object> valuemap: fillInValue) {
            //获取当前年月的天数
            int tdayNum = dayNumForYearAndMonth(year,month);
            for (int i = 0; i < tdayNum; i++) {
                //获取当前日数与计划水量数据
                if (valuemap.get("planValue" + i) == null) {
                    continue;
                }
                Double planValue = Double.valueOf(String.valueOf(valuemap.get("planValue" + i)));
                //获取当前日数据
                String day = String.valueOf(i + 1);
                //当前年月日对应的时间
                Date dayTime = dateTime(month, year, day);
                //获取管理站,用水单位,引水口对应数据
                String waterUnitId = String.valueOf(valuemap.get("waterUnitId"));
                mngUnitId = String.valueOf(valuemap.get("mngUnitId"));
                mngUnitName = String.valueOf(valuemap.get("mngUnitName"));
                String buildingId = String.valueOf(valuemap.get("buildingId"));
                //填报数据
                WaterPlanFillinMonth waterPlanFillinMonth = waterPlanFillinMonth(planValue, year, month, day,
                        waterUnitId, mngUnitId, buildingId, planId);
                waterPlanFillInMonthList.add(waterPlanFillinMonth);
                //日迭代数据
                WrPlanInterDay wrPlanInterDay = wrPlanInterDay(buildingId, dayTime, planValue);
                wrPlanInterDayList.add(wrPlanInterDay);
            }
        }
        //填报任务数据
        WrPlanTask wrPlanTask = wrPlanFillinService.wrPlanTask(planId,mngUnitId,mngUnitName,userId,content,month,year,planType,taskNmae);
        WrPlanFillinRelevantTable wrPlanFillinRelevantTable = new WrPlanFillinRelevantTable();
        wrPlanFillinRelevantTable.setWaterPlanFillinMonth(waterPlanFillInMonthList);
        wrPlanFillinRelevantTable.setWrPlanInterDay(wrPlanInterDayList);
        wrPlanFillinRelevantTable.setWrPlanTask(wrPlanTask);
        return wrPlanFillinRelevantTable;
    }

    //月计划数据整合公共方法
    private static WaterPlanFillinMonth waterPlanFillinMonth(Double planValue,String year,String month,
                                                             String day,String waterUnitId,String manageUnitId,String buildingId,
                                                             String planId){
        WaterPlanFillinMonth waterPlanFillinMonth = new WaterPlanFillinMonth();
        //需求水量demadWaterQuantity
        waterPlanFillinMonth.setDemadWaterQuantity(CommonUtil.number(planValue));
        //需求流量demadWaterFlow
        waterPlanFillinMonth.setProposalWaterFlow(CommonUtil.number( planValue/86400.0));
        waterPlanFillinMonth.setYear(year);
        waterPlanFillinMonth.setPlanName(PlanFillInTypeEnum.MONTH_PLAN_FILL_IN.getName());//月计划填报
        waterPlanFillinMonth.setMonth(month);
        waterPlanFillinMonth.setDay(day);
        //计划id
        waterPlanFillinMonth.setPlanTaskId(planId);
        //主键id
        waterPlanFillinMonth.setId(IDGenerator.getId());
        waterPlanFillinMonth.setWaterUnitId(waterUnitId);
        waterPlanFillinMonth.setManageUnitId(manageUnitId);
        waterPlanFillinMonth.setBuildingId(buildingId);
        return waterPlanFillinMonth;
    }
    //日迭代表数据整理
    private static WrPlanInterDay wrPlanInterDay(String buildingId, Date time, Double planValue){

        //需求流量demadWaterFlow
        BigDecimal demadWaterFlow = CommonUtil.number( planValue/86400.0);
        WrPlanInterDay wrPlanInterDay = new WrPlanInterDay();
        wrPlanInterDay.setId(String.valueOf(IDGenerator.getId()));
        wrPlanInterDay.setBuildingId(buildingId);
        wrPlanInterDay.setSupplyTime(time);
        wrPlanInterDay.setWaterFlow(demadWaterFlow);
        //需求水量demadWaterQuantity
        wrPlanInterDay.setWaterQuantity(CommonUtil.number(planValue));
        return wrPlanInterDay;
    }
    //根据年月日获取DATE时间格式
    private static Date dateTime(String month,String year,String day) {
        if (month.length()<2){
            month ="0"+month;
        }
        if(day.length()<2){
            day = "0"+day;
        }
        String dateTime = year+"-"+month+"-"+day;
        Date date = DateUtils.convertStringTimeToDateExt(dateTime);
        return date;
    }
    //根据年月获取天数
    private static int dayNumForYearAndMonth(String year,String month){
        String yearAndMonth = year+"/"+month;
        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy/MM");
        try {
            rightNow.setTime(simpleDate.parse(yearAndMonth));
        } catch (ParseException e) {
            log.error("时间格式有误：{}"+e.getMessage());
        }
        int days = rightNow.getActualMaximum(Calendar.DAY_OF_MONTH);
        System.out.println(days);
        return days;
    }

}
