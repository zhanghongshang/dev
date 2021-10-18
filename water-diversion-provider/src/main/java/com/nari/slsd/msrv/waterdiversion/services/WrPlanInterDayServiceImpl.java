package com.nari.slsd.msrv.waterdiversion.services;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.hu.mplat.imc.client.Param.Param;
import com.nari.slsd.msrv.common.utils.DateUtils;
import com.nari.slsd.msrv.waterdiversion.cache.RedisCacheKeyDef;
import com.nari.slsd.msrv.waterdiversion.commons.PointTypeEnum;
import com.nari.slsd.msrv.waterdiversion.config.RedisUtil;
import com.nari.slsd.msrv.waterdiversion.interfaces.IDataService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrPlanInterDayService;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WaterBuildingManagerMapper;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrPlanInterDayMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.DataBuildingDto;
import com.nari.slsd.msrv.waterdiversion.model.dto.DataPointDto;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrBuildingAndDiversion;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrPlanInterDayDTO;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WaterBuildingManager;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrPlanInterDay;
import com.nari.slsd.msrv.waterdiversion.model.vo.PlanContrast;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrPlanDataContrast;
import com.nari.slsd.msrv.waterdiversion.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.List;

/**
 * @Description 日迭代 实现类
 * @Author ZHS
 * @Date 2021/10/8 16:21
 */
@Service
public class WrPlanInterDayServiceImpl extends ServiceImpl<WrPlanInterDayMapper, WrPlanInterDay> implements IWrPlanInterDayService {

    @Autowired
    WrPlanInterDayMapper wrPlanInterDayMapper;
    @Autowired
    IDataService dataService;
    @Autowired
    WaterBuildingManagerMapper waterBuildingManagerMapper;
    @Autowired
    RedisUtil redisUtil;
    /**
     * 更新日迭代数据
     * @param wrPlanInterDayDTOList
     */
    @Override
    public void updateDay(List<WrPlanInterDayDTO> wrPlanInterDayDTOList) {
        for (WrPlanInterDayDTO wrPlanInterDayDTO:wrPlanInterDayDTOList){
            UpdateWrapper<WrPlanInterDay> updateWrapper = new UpdateWrapper<WrPlanInterDay>();
            updateWrapper.eq("SUPPLY_TIME",wrPlanInterDayDTO.getSupplyTime());
            updateWrapper.eq("BUILDING_ID",wrPlanInterDayDTO.getBuildingId());
            WrPlanInterDay wrPlanInterDay = new WrPlanInterDay();
            wrPlanInterDay.setWaterQuantity(wrPlanInterDayDTO.getWaterQuantity());
            wrPlanInterDay.setWaterFlow(wrPlanInterDayDTO.getWaterFlow());
            wrPlanInterDayMapper.update(wrPlanInterDay,updateWrapper);
        }
    }

    /**
     * 根据时间查询引水口对应值类型
     * @param starTime
     * @param endTime
     * @param buildingId
     * @return
     */
    @Override
    public List<WrPlanInterDay> planDayValue(Date starTime, Date endTime, List<String> buildingId) {
        QueryWrapper<WrPlanInterDay> wrapper = new QueryWrapper<>();
        wrapper.between("SUPPLY_TIME",starTime,endTime);
        wrapper.in("BUILDING_ID",buildingId);
        wrapper.groupBy("BUILDING_ID");
        List<WrPlanInterDay> wrPlanInterDay = wrPlanInterDayMapper.getPlanDaySumForTime(wrapper);
        return wrPlanInterDay;
    }

    /**
     * 计划水引数据对比值
     * @param buildingIds
     * @return
     */

    public List<WrPlanDataContrast> planYearAndMinthValue(List<String> buildingIds) {
        DateUtil.yesterday();
        String today = DateUtil.today();
        int thisYear = DateUtil.thisYear();
        String thisDay =  today.substring(today.length()-2,today.length());

        String thisMonth = DateUtils.getMonthToString(new Date());
        //获取引水口对应的本月截止统计值（年计划）
        List<WrPlanInterDay> wrPlanThisMonthList = planDayValue(DateUtils.convertStringTimeToDateExt(thisYear+"-"+thisMonth+"-01"),
                DateUtils.convertStringTimeToDateExt(today),buildingIds);
        Map<String,BigDecimal> planMtdMap = planInterDayValue(wrPlanThisMonthList);

        //获取引水口对应的本月截止统计值（实际引水量）
        Long str = DateUtils.convertStringTimeToLongExt(thisYear+"-"+thisMonth+"-01");
        Long edt = DateUtils.convertStringTimeToLongExt(today);
        List<DataBuildingDto> dataThisMonthDtos = dataService.getSpecialDataRunDataType(buildingIds, Arrays.asList(PointTypeEnum.WATER_VOLUME.getId()),str,edt,
                Param.ValType.Special_V,Param.RunDataType.RUN_DAY,Param.CalcType.CALC_SUM);
        Map<String,BigDecimal> actMtdMap = dataBuildingDtoValue(dataThisMonthDtos);

        //获取引水口对应的本年截止统计（年计划）
        List<WrPlanInterDay> wrPlanThisYearList = planDayValue(DateUtils.convertStringTimeToDateExt(thisYear+"-01-01"),
                DateUtils.convertStringTimeToDateExt(today),buildingIds);
        Map<String,BigDecimal> planYtdMap = planInterDayValue(wrPlanThisYearList);

        //获取引水口对应的本年截止统计（实际引水量）
        str = DateUtils.convertStringTimeToLongExt(thisYear+"-01-01");
        List<DataBuildingDto> dataThisYearDtos = dataService.getSpecialDataRunDataType(buildingIds, Arrays.asList(PointTypeEnum.WATER_VOLUME.getId()),str,edt,
                Param.ValType.Special_V,Param.RunDataType.RUN_DAY,Param.CalcType.CALC_SUM);
        Map<String,BigDecimal> actYtdMap = dataBuildingDtoValue(dataThisYearDtos);

        //获取引水口对应的去年月同期（年计划）
        List<WrPlanInterDay> wrPlanBeforeMonthList = planDayValue(DateUtils.convertStringTimeToDateExt(thisYear-1+"-"+thisMonth+"-01"),
                DateUtils.convertStringTimeToDateExt(thisYear-1+"-"+thisMonth+"-"+thisDay),buildingIds);
        Map<String,BigDecimal> planStlmMap = planInterDayValue(wrPlanBeforeMonthList);

        //获取引水口对应的去年月同期（实际引水量）
        str = DateUtils.convertStringTimeToLongExt(thisYear-1+"-"+thisMonth+"-01");
        edt = DateUtils.convertStringTimeToLongExt(thisYear-1+"-"+thisMonth+"-"+thisDay);
        List<DataBuildingDto> dataBeforeMonthDtos = dataService.getSpecialDataRunDataType(buildingIds, Arrays.asList(PointTypeEnum.WATER_VOLUME.getId()),str,edt,
                Param.ValType.Special_V,Param.RunDataType.RUN_DAY,Param.CalcType.CALC_SUM);
        Map<String,BigDecimal> actStlmMap = dataBuildingDtoValue(dataBeforeMonthDtos);

        //获取引水口对应的去年年同期（年计划）
        List<WrPlanInterDay> wrPlanBeforeYearList = planDayValue(DateUtils.convertStringTimeToDateExt(thisYear-1+"-01-01"),
                DateUtils.convertStringTimeToDateExt(thisYear-1+"-"+thisMonth+"-"+thisDay),buildingIds);
        Map<String,BigDecimal> planStlyMap = planInterDayValue(wrPlanBeforeYearList);

        //获取引水口对应的去年年同期（实际引水量）
        str = DateUtils.convertStringTimeToLongExt(thisYear-1+"-01-01");
        List<DataBuildingDto> dataBeforeYearDtos = dataService.getSpecialDataRunDataType(buildingIds, Arrays.asList(PointTypeEnum.WATER_VOLUME.getId()),str,edt,
                Param.ValType.Special_V,Param.RunDataType.RUN_DAY,Param.CalcType.CALC_SUM);
        Map<String,BigDecimal> actStlyMap = dataBuildingDtoValue(dataBeforeYearDtos);

        //获取统计值当前
        List<WrPlanDataContrast> wrPlanDataContrasts = new ArrayList<>();
        Map<String,PlanContrast> planContrastMap = wrPlanContrastList(planMtdMap,planYtdMap,planStlmMap,planStlyMap,actMtdMap,actYtdMap,actStlmMap,actStlyMap);
        for (String buildingId: planContrastMap.keySet()) {

            WrPlanDataContrast wrPlanDataContrast = new WrPlanDataContrast();
            wrPlanDataContrast.setBuildingId(buildingId);
            wrPlanDataContrast.setPlanAndActContrast(planContrastMap.get(buildingId));
            wrPlanDataContrasts.add(wrPlanDataContrast);
        }
        String value = JSON.toJSONString(wrPlanDataContrasts);
        Boolean b = redisUtil.set(RedisCacheKeyDef.ModelKey.CONTRAST,value);
        //System.out.println(b);
        return wrPlanDataContrasts;
    }
    //查询所有引水口
    @Override
    public List<WrPlanDataContrast> planInterDayValue(){
        QueryWrapper<WaterBuildingManager> wrapper = new QueryWrapper<>();
        //获取1、2级引水口
        List<Integer> buildingLevels = Arrays.asList(1,2,3);
        wrapper.in(buildingLevels != null && buildingLevels.size() != 0, "wd.BUILDING_LEVEL", buildingLevels);
        List<String> buildingIds = new ArrayList<>();
        List<WrBuildingAndDiversion> wrBuildingAndDiversionList = waterBuildingManagerMapper.getBuildingAndDiversionList(wrapper);
        wrBuildingAndDiversionList.forEach(wrBuildingAndDiversion -> {
            buildingIds.add(wrBuildingAndDiversion.getId());
        });
        return  planYearAndMinthValue(buildingIds);
    }
    /**
     * 累加处理日迭代
     */
    private Map<String,BigDecimal> planInterDayValue(List<WrPlanInterDay> wrPlanInterDayList){
        Map<String,BigDecimal> map = new HashMap<>();
        //Map<String, List<WrPlanInterDay>> buildingGroupMap = wrPlanInterDayList.stream().collect(Collectors.groupingBy(WrPlanInterDay::getBuildingId));
        //for (String buildingCode: buildingGroupMap.keySet()) {
            //List<WrPlanInterDay> wrPlanInterDays = buildingGroupMap.get(buildingCode);
            //累加
            //BigDecimal sunValue = wrPlanInterDays.stream().map(WrPlanInterDay::getWaterQuantity).reduce(BigDecimal.ZERO,BigDecimal::add);
           // map.put(buildingCode,sunValue);
        //}
        wrPlanInterDayList.forEach(wrPlanInterDay -> {
            String buildingId = wrPlanInterDay.getBuildingId();
            BigDecimal value = wrPlanInterDay.getWaterQuantity();
            map.put(buildingId,value);
        });
        return map;
    }
    /**
     * 累加处理实时数据
     */
    private Map<String,BigDecimal> dataBuildingDtoValue( List<DataBuildingDto> dataBuildingDtoList){
        Map<String,BigDecimal> map = new HashMap<>();
        dataBuildingDtoList.forEach(dataBuildingDto->{
            String buildingId = dataBuildingDto.getId();
            List<DataPointDto> dataPointDtos = dataBuildingDto.getDataPointDtos();
            BigDecimal sunValue = CommonUtil.number(dataPointDtos.stream().mapToDouble(DataPointDto::getV).sum());
            map.put(buildingId,sunValue);
        });
        return map;
    }
    private  Map<String,PlanContrast> wrPlanContrastList(Map<String,BigDecimal> planMtdMap,Map<String,BigDecimal> planYtdMap,Map<String,BigDecimal> planStlmMap,Map<String,BigDecimal> planStlyMap,
                                                         Map<String,BigDecimal> actMtdMap,Map<String,BigDecimal> actYtdMap,Map<String,BigDecimal> actStlmMap,Map<String,BigDecimal> actStlyMap){
        Map<String,PlanContrast> buildingMap = new HashMap<>();
            for (String buildingId: planMtdMap.keySet()) {
                PlanContrast planContrast = new PlanContrast();
                planContrast.setPlanMtd(planMtdMap.get(buildingId));
                buildingMap.put(buildingId,planContrast);
            }
            for (String buildingId: planYtdMap.keySet()){
                //判断引水口id是否存在
                if (buildingMap.containsKey(buildingId)){
                    PlanContrast planContrast = buildingMap.get(buildingId);
                    planContrast.setPlanYtd(planYtdMap.get(buildingId));
                }else{
                    PlanContrast planContrast = new PlanContrast();
                    planContrast.setPlanYtd(planYtdMap.get(buildingId));
                    buildingMap.put(buildingId,planContrast);
                }
            }
            for (String buildingId: planStlmMap.keySet()){
                //判断引水口id是否存在
                if (buildingMap.containsKey(buildingId)){
                    PlanContrast planContrast = buildingMap.get(buildingId);
                    planContrast.setPlanStlm(planStlmMap.get(buildingId));
                }else{
                    PlanContrast planContrast = new PlanContrast();
                    planContrast.setPlanStlm(planStlmMap.get(buildingId));
                    buildingMap.put(buildingId,planContrast);
                }
            }
            for (String buildingId: planStlyMap.keySet()){
                //判断引水口id是否存在
                if (buildingMap.containsKey(buildingId)){
                    PlanContrast planContrast = buildingMap.get(buildingId);
                    planContrast.setPlanStly(planStlyMap.get(buildingId));
                }else{
                    PlanContrast planContrast = new PlanContrast();
                    planContrast.setPlanStly(planStlyMap.get(buildingId));
                    buildingMap.put(buildingId,planContrast);
                }
            }
            for (String buildingId: actMtdMap.keySet()){
                //判断引水口id是否存在
                if (buildingMap.containsKey(buildingId)){
                    PlanContrast planContrast = buildingMap.get(buildingId);
                    planContrast.setActMtd(actMtdMap.get(buildingId));
                }else{
                    PlanContrast planContrast = new PlanContrast();
                    planContrast.setActMtd(actMtdMap.get(buildingId));
                    buildingMap.put(buildingId,planContrast);
                }
            }
            for (String buildingId: actYtdMap.keySet()){
                //判断引水口id是否存在
                if (buildingMap.containsKey(buildingId)){
                    PlanContrast planContrast = buildingMap.get(buildingId);
                    planContrast.setActYtd(actYtdMap.get(buildingId));
                }else{
                    PlanContrast planContrast = new PlanContrast();
                    planContrast.setActYtd(actYtdMap.get(buildingId));
                    buildingMap.put(buildingId,planContrast);
                }
            }
           for (String buildingId: actStlmMap.keySet()){
                 //判断引水口id是否存在
                if (buildingMap.containsKey(buildingId)){
                    PlanContrast planContrast = buildingMap.get(buildingId);
                    planContrast.setActStlm(actStlmMap.get(buildingId));
                }else{
                    PlanContrast planContrast = new PlanContrast();
                    planContrast.setActStlm(actStlmMap.get(buildingId));
                    buildingMap.put(buildingId,planContrast);
                }
            }
            for (String buildingId: actStlyMap.keySet()){
                //判断引水口id是否存在
                if (buildingMap.containsKey(buildingId)){
                    PlanContrast planContrast = buildingMap.get(buildingId);
                    planContrast.setActStly(actStlyMap.get(buildingId));
                }else{
                    PlanContrast planContrast = new PlanContrast();
                    planContrast.setActStly(actStlyMap.get(buildingId));
                    buildingMap.put(buildingId,planContrast);
                }
            }
            return buildingMap;
    }
    //流程启动
}
