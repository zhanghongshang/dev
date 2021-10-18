package com.nari.slsd.msrv.waterdiversion.services;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nari.slsd.hu.mplat.imc.client.Param.Param;
import com.nari.slsd.msrv.common.exception.TransactionException;
import com.nari.slsd.msrv.common.ext.enums.CodeEnum;
import com.nari.slsd.msrv.common.utils.DateUtils;
import com.nari.slsd.msrv.waterdiversion.commons.PointTypeEnum;
import com.nari.slsd.msrv.waterdiversion.interfaces.IDataService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrBalanceAnalyzeService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrPlanGenerateMonthService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrRecentPlanAdjustService;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WaterPlanFillinYearMapper;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrPlanInterDayMapper;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrPlanInterTdayMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.*;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WaterPlanFillinYear;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrPlanInterDay;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrPlanInterTday;
import com.nari.slsd.msrv.waterdiversion.model.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.nari.slsd.msrv.waterdiversion.commons.RecentPlanEnum.*;
import static com.nari.slsd.msrv.waterdiversion.commons.TDayTypeEnum.YEAR_PLAN_TDAY_TYPE_4;
import static com.nari.slsd.msrv.waterdiversion.utils.CommonUtil.*;

/**
 * @title 水量调度平衡分析
 * @description
 * @author bigb
 * @updateTime 2021/9/18 12:50
 * @throws
 */
@Service
@Slf4j
public class WrBalanceAnalyzeServiceImpl implements IWrBalanceAnalyzeService {

    @Autowired
    private IDataService dataService;

    @Autowired
    private IWrRecentPlanAdjustService wrRecentPlanAdjustService;

    @Autowired
    private IWrPlanGenerateMonthService wrPlanGenerateMonthService;

    @Autowired
    private WrPlanInterDayMapper wrPlanInterDayMapper;

    @Autowired
    private WrPlanInterTdayMapper wrPlanInterTdayMapper;

    @Autowired
    private WaterPlanFillinYearMapper waterPlanFillinYearMapper;

    /**
     * 水量平衡分析
     * @param allDTO
     * @return
     */
    @Override
    public WaterBalanceAnalyzeVO waterBalanceAnalyze(WrPlanFillInDayAllDTO allDTO){
        WaterBalanceAnalyzeVO analyzeVO = new WaterBalanceAnalyzeVO();
        if(BUILDING_IN_OTHER_WATER_UNIT.equals(allDTO.getType())){
            balanceAnalyzeForOtherUseUnit(allDTO,analyzeVO);
        }else{
            balanceAnalyzeForBuilding(allDTO, analyzeVO);
        }
        if(!BUILDING_IN_OTHER_MONTH.equals(allDTO.getType())){
            sorted(analyzeVO);
        }
        return analyzeVO;
    }

    private void sorted(WaterBalanceAnalyzeVO analyzeVO) {
        if(null != analyzeVO.getPlanAdjustVO()){
            List<PlanAdjustDetailVO> lendOutList = analyzeVO.getPlanAdjustVO().getLendOutList();
            if(CollectionUtils.isNotEmpty(lendOutList)){
                List<PlanAdjustDetailVO> collect = lendOutList.stream()
                        .sorted(Comparator.comparing(PlanAdjustDetailVO::getTimeStr))
                        .sorted(Comparator.comparing(PlanAdjustDetailVO::getBuildingName))
                        .collect(Collectors.toList());
                analyzeVO.getPlanAdjustVO().setLendOutList(collect);
            }
        }
    }

    private void balanceAnalyzeForOtherUseUnit(WrPlanFillInDayAllDTO allDTO, WaterBalanceAnalyzeVO analyzeVO) {
        List<LendInDTO> lendInList = allDTO.getLendIns();
        if(CollectionUtils.isEmpty(lendInList)){
            throw new TransactionException(CodeEnum.NO_DATA, "没有任何借入方近期计划调整信息！");
        }
        BigDecimal buyerAmount = new BigDecimal(0);
        PlanAdjustVO planAdjustVO = new PlanAdjustVO();
        analyzeVO.setPlanAdjustVO(planAdjustVO);
        //构建借调方
        List<PlanAdjustDetailVO> voList = new ArrayList<>();
        planAdjustVO.setLendInList(voList);
        //买入方
        for (LendInDTO lendInDTO : lendInList) {
            BigDecimal amount = singleLendInProcess(lendInDTO, allDTO.getType(),voList);
            buyerAmount = NumberUtil.add(buyerAmount,amount);
        }
        //卖出方
        setLendOutInOtherUseUnit(allDTO,analyzeVO,buyerAmount);
    }

    /**
     * 月内本水口水量平衡分析
     * @param allDTO
     * @param analyzeVO
     */
    private void balanceAnalyzeForBuilding(WrPlanFillInDayAllDTO allDTO, WaterBalanceAnalyzeVO analyzeVO) {
        List<LendInDTO> lendIns = allDTO.getLendIns();
        if(CollectionUtils.isEmpty(lendIns)){
            throw new TransactionException(CodeEnum.NO_DATA, "没有任何借入方近期计划调整信息！");
        }
        //借调方
        LendInDTO lendInDTO = lendIns.get(0);
        PlanAdjustVO planAdjustVO = new PlanAdjustVO();
        analyzeVO.setPlanAdjustVO(planAdjustVO);
        List<PlanAdjustDetailVO> lendInList = new ArrayList<>();
        //构建借调方
        planAdjustVO.setLendInList(lendInList);
        BigDecimal total = singleLendInProcess(lendInDTO,allDTO.getType(),lendInList);
        if(BUILDING_IN_MONTH.equals(allDTO.getType())){
            setLendOutInMonth(analyzeVO, lendInDTO, total);
        }else if(OTHER_BUILDING_IN_MONTH.equals(allDTO.getType())){
            setLendOutOtherBuildingsInMonth(allDTO,analyzeVO,total);
        }else if(BUILDING_IN_OTHER_MONTH.equals(allDTO.getType())){
            setLendOutInOtherMonth(allDTO,analyzeVO, lendInDTO, total);
        }
    }

    private BigDecimal singleLendInProcess(LendInDTO lendInDTO , String adjustType , List<PlanAdjustDetailVO> lendInList){
        //TODO 暂不校验开始到结束时间和调整前后数值是否能一一匹配
        Date startDate = DateUtils.convertStringTimeToDateExt(lendInDTO.getStartTime());
        Date endDate = DateUtils.convertStringTimeToDateExt(lendInDTO.getEndTime());
        List<String> dateList = getDateStrArrBetweenSpecialDate(startDate, endDate);
        //调整后
        List<BigDecimal> newPlanValue = lendInDTO.getNewPlanValue();
        //原计划
        List<BigDecimal> oldPlanValue = lendInDTO.getOldPlanValue();
        return getLendInWater(lendInDTO.getBuildingName(), dateList, newPlanValue, oldPlanValue, lendInList , adjustType);
    }

    /**
     * 月内本水口借出
     * @param analyzeVO
     * @param lendInDTO
     * @param total
     */
    private void setLendOutInMonth(WaterBalanceAnalyzeVO analyzeVO, LendInDTO lendInDTO, BigDecimal total) {
        Map<String, List<WrPlanInterDay>> buildingGroupMap = new HashMap<>();
        Map<String, List<DataPointDto>> buildingRealMap = new HashMap<>();
        String buildingId = lendInDTO.getBuildingId();
        setPlanAndRealWater(Arrays.asList(buildingId),buildingGroupMap,buildingRealMap);
        //借出方
        Date endDate = DateUtils.convertStringTimeToDateExt(lendInDTO.getEndTime());
        List<PlanAdjustDetailVO> lendOut = new ArrayList<>();
        generateLendOutDetailCommon(lendInDTO.getBuildingId(),lendInDTO.getBuildingName(),endDate,total,lendOut);
        analyzeVO.getPlanAdjustVO().setLendOutList(lendOut);
        analyzeVO.setBalanced(Boolean.valueOf(true));
        Map<String,String> buildingMap = new HashMap<>();
        buildingMap.put(lendInDTO.getBuildingId(),lendInDTO.getBuildingName());
        List<WrPlanDataCompareVO> comparisonDataList = getComparisonData(buildingMap);
        analyzeVO.setDataCompareAnalyze(comparisonDataList);
    }

    /**
     * 本水口跨月借出
     * @param analyzeVO
     * @param lendInDTO
     * @param total
     */
    private void setLendOutInOtherMonth(WrPlanFillInDayAllDTO allDTO , WaterBalanceAnalyzeVO analyzeVO, LendInDTO lendInDTO, BigDecimal total) {
        if(total.doubleValue() <= 0){
            throw new TransactionException(CodeEnum.PARAM_ERROR, "调整后流量不可低于调整前流量！");
        }
        Map<String, List<WrPlanInterDay>> buildingGroupMap = new HashMap<>();
        Map<String, List<DataPointDto>> buildingRealMap = new HashMap<>();
        String buildingId = lendInDTO.getBuildingId();
        setPlanAndRealWater(Arrays.asList(buildingId),buildingGroupMap,buildingRealMap);
        //借出方
        List<PlanAdjustDetailVO> lendOut = generateLendOutDetailForMultiMonth(analyzeVO,allDTO,lendInDTO.getBuildingName(),total);
        analyzeVO.getPlanAdjustVO().setLendOutList(lendOut);
        Map<String,String> buildingMap = new HashMap<>();
        buildingMap.put(lendInDTO.getBuildingId(),lendInDTO.getBuildingName());
        List<WrPlanDataCompareVO> comparisonDataList = getComparisonData(buildingMap);
        analyzeVO.setDataCompareAnalyze(comparisonDataList);
    }

    /**
     * 跨用水单位调整
     * @param analyzeVO
     * @param total
     */
    private void setLendOutInOtherUseUnit(WrPlanFillInDayAllDTO allDTO , WaterBalanceAnalyzeVO analyzeVO, BigDecimal total) {
        if(total.doubleValue() <= 0){
            throw new TransactionException(CodeEnum.PARAM_ERROR, "借入方水量不可低于0！");
        }
        Map<String, List<WrPlanInterDay>> buildingGroupMap = new HashMap<>();
        Map<String, List<DataPointDto>> buildingRealMap = new HashMap<>();
        List<LendOutDTO> lendOuts = allDTO.getLendOuts();
        Map<String, LendOutDTO> outMap = lendOuts.stream().collect(Collectors.toMap(LendOutDTO::getBuildingId, e -> e));
        List<String> buildingIdList = new ArrayList<>(outMap.keySet());
        setPlanAndRealWater(buildingIdList,buildingGroupMap,buildingRealMap);
        //所有借出水口调整情况
        List<PlanAdjustDetailVO> allLendOutList = new ArrayList<>();
        BigDecimal totalLendOut = new BigDecimal(0);
        Map<String,String> buildingMap = new HashMap<>();
        for (String buildingId : buildingIdList) {
            LendOutDTO lendOutDTO = outMap.get(buildingId);
            Map<String, BigDecimal> remainOfMonthMap = wrRecentPlanAdjustService.getRemainOfMonth(Arrays.asList(buildingId));
            //月剩余水量
            BigDecimal remainOfMonth = nonNullOfBigDecimal(remainOfMonthMap.get(buildingId));
            //借出水量
            BigDecimal lendOut = lendOutDTO.getLendOutWater();
            Date tomorrow = DateUtil.beginOfDay(DateUtil.tomorrow());
            //本月月剩余水量不够借,则从后续月份借
            BigDecimal lendOutForOtherMonth = NumberUtil.sub(lendOut,remainOfMonth);
            BigDecimal lendOutInMonth = lendOut;
            if(lendOutForOtherMonth.doubleValue() > 0){
                lendOutInMonth = remainOfMonth;
            }
            //本月内剩余水量借出
            generateLendOutDetailCommon(buildingId, lendOutDTO.getBuildingName(),tomorrow,lendOutInMonth,allLendOutList);
            if(lendOutForOtherMonth.doubleValue() > 0){
                lendOutInOtherMonth(buildingId,lendOutDTO.getBuildingName(),lendOutForOtherMonth,allLendOutList);
            }
            totalLendOut = NumberUtil.add(totalLendOut,lendOutDTO.getLendOutWater());
            buildingMap.put(buildingId,lendOutDTO.getBuildingName());
        }
        analyzeVO.getPlanAdjustVO().setLendOutList(allLendOutList);
        //判断是否水量平衡
        if(NumberUtil.compare(totalLendOut.doubleValue(),total.doubleValue()) != 0){
            analyzeVO.setBalanced(Boolean.valueOf(false));
        }
        //增加买入方对比
        List<LendInDTO> lendIns = allDTO.getLendIns();
        lendIns.forEach(lendInDTO -> buildingMap.put(lendInDTO.getBuildingId(),lendInDTO.getBuildingName()));
        List<WrPlanDataCompareVO> comparisonDataList = getComparisonData(buildingMap);
        //买入方用水单位对比
        String xsUseId = allDTO.getXsUseId();
        if(StringUtils.isNotEmpty(xsUseId)){
            getComparisonForXsUseUnit(buildingMap, comparisonDataList, xsUseId , allDTO.getXsUseName());
        }
        //卖出方用水单位对比
        String xsUseIdOut = allDTO.getXsUseIdOut();
        if(StringUtils.isNotEmpty(xsUseIdOut)){
            getComparisonForXsUseUnit(buildingMap, comparisonDataList, xsUseIdOut , allDTO.getXsUseIdOutName());
        }

        analyzeVO.setDataCompareAnalyze(comparisonDataList);
    }

    private void lendOutInOtherMonth(String buildingId ,String buildingName , BigDecimal lendOutForOtherMonth , List<PlanAdjustDetailVO> allLendOutList){
        //获取后续月份
        List<Date> remainMonth = getRemainMonth();
        //不太可能出现这个情况
        if(org.apache.commons.collections.CollectionUtils.isEmpty(remainMonth)){
            throw new TransactionException(CodeEnum.NO_DATA, "当前月份不存在后续月份！");
        }
        LambdaQueryWrapper<WrPlanInterTday> wrapper = new QueryWrapper().lambda();
        wrapper.eq(WrPlanInterTday::getBuildingId, buildingId);
        wrapper.eq(WrPlanInterTday::getTimeType, YEAR_PLAN_TDAY_TYPE_4);
        wrapper.in(WrPlanInterTday::getSupplyTime, remainMonth);
        List<WrPlanInterTday> tDayList = wrPlanInterTdayMapper.selectList(wrapper);
        if(CollectionUtils.isNotEmpty(tDayList)){
            List<WrPlanInterTday> remainList = tDayList.stream().filter(day -> nonNullOfBigDecimal(day.getWaterQuantity()).doubleValue() > 0).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(remainList)){
                BigDecimal avg = NumberUtil.div(lendOutForOtherMonth,remainList.size(),4);
                for (WrPlanInterTday tday : remainList) {
                    PlanAdjustDetailVO vo = new PlanAdjustDetailVO();
                    vo.setBuildingName(buildingName);
                    vo.setLendOutSource(LEND_OUT_SOURCE_REMAIN_OF_YEAR);
                    vo.setPlan(tday.getWaterQuantity());
                    vo.setResize(NumberUtil.sub(tday.getWaterQuantity(),avg));
                    vo.setDiff(NumberUtil.sub(vo.getResize(),vo.getPlan()));
                    vo.setTimeStr(getMonthDesc(tday.getSupplyTime()));
                    allLendOutList.add(vo);
                }
            }
        }
    }

    private String getMonthDesc(Date date){
        int year = DateUtil.year(date);
        int month = DateUtil.month(date);
        return year + "年" + month + "月";
    }

    private void getComparisonForXsUseUnit(Map<String, String> buildingMap, List<WrPlanDataCompareVO> comparisonDataList, String xsUseId , String xsUseName) {
        List<WrBuildingAndDiversion> allBuildings = wrPlanGenerateMonthService.getAllWaterBuildingForAppointUseUnit(xsUseId);
        if (CollectionUtils.isNotEmpty(allBuildings)) {
            buildingMap.clear();
            for (WrBuildingAndDiversion building : allBuildings) {
                buildingMap.put(building.getId(), building.getBuildingName());
            }
            List<WrPlanDataCompareVO> comparisonDataListForSameUseUnit = getComparisonData(buildingMap);
            WrPlanDataCompareVO voForSameUseUnit = new WrPlanDataCompareVO();
            BigDecimal actMtd = new BigDecimal(0);
            BigDecimal planMtd = new BigDecimal(0);
            BigDecimal rollMonthPlan = new BigDecimal(0);
            BigDecimal originalMonthPlan = new BigDecimal(0);
            BigDecimal actYtd = new BigDecimal(0);
            BigDecimal planYtd = new BigDecimal(0);
            BigDecimal planYear = new BigDecimal(0);
            for (WrPlanDataCompareVO wrPlanDataCompareVO : comparisonDataListForSameUseUnit) {
                actMtd = NumberUtil.add(actMtd, nonNullOfBigDecimal(wrPlanDataCompareVO.getActMtd()));
                planMtd = NumberUtil.add(planMtd, nonNullOfBigDecimal(wrPlanDataCompareVO.getPlanMtd()));
                rollMonthPlan = NumberUtil.add(rollMonthPlan, nonNullOfBigDecimal(wrPlanDataCompareVO.getRollMonthPlan()));
                originalMonthPlan = NumberUtil.add(originalMonthPlan, nonNullOfBigDecimal(wrPlanDataCompareVO.getOriginalMonthPlan()));
                actYtd = NumberUtil.add(actYtd, nonNullOfBigDecimal(wrPlanDataCompareVO.getActYtd()));
                planYtd = NumberUtil.add(planYtd, nonNullOfBigDecimal(wrPlanDataCompareVO.getPlanYtd()));
                planYear = NumberUtil.add(planYear, nonNullOfBigDecimal(wrPlanDataCompareVO.getPlanYear()));
            }
            voForSameUseUnit.setActMtd(actMtd);
            voForSameUseUnit.setPlanMtd(planMtd);
            voForSameUseUnit.setRollMonthPlan(rollMonthPlan);
            voForSameUseUnit.setOriginalMonthPlan(originalMonthPlan);
            voForSameUseUnit.setActYtd(actYtd);
            voForSameUseUnit.setPlanYtd(planYtd);
            voForSameUseUnit.setPlanYear(planYear);
            voForSameUseUnit.setBuildingName(xsUseName);
            comparisonDataList.add(voForSameUseUnit);
        }
    }

    /**
     * 跨水口借出
     * @param allDTO
     * @param analyzeVO
     * @param total
     */
    private void setLendOutOtherBuildingsInMonth(WrPlanFillInDayAllDTO allDTO , WaterBalanceAnalyzeVO analyzeVO, BigDecimal total) {
        List<LendOutDTO> lendOuts = allDTO.getLendOuts();
        if(CollectionUtils.isEmpty(lendOuts)){
            throw new TransactionException(CodeEnum.NO_DATA, "没有任何借出方近期计划调整信息！");
        }
        Map<String, LendOutDTO> outMap = lendOuts.stream().collect(Collectors.toMap(LendOutDTO::getBuildingId, e -> e));
        List<String> buildingIds = new ArrayList<>(outMap.keySet());
        Date tomorrow = DateUtil.beginOfDay(DateUtil.tomorrow());
        //所有借出水口调整情况
        List<PlanAdjustDetailVO> all = new ArrayList<>();
        BigDecimal totalLendOut = new BigDecimal(0);
        Map<String,String> buildingMap = new HashMap<>();
        for (String buildingId : buildingIds) {
            LendOutDTO lendOutDTO = outMap.get(buildingId);
            //借出方
            generateLendOutDetailCommon(buildingId, lendOutDTO.getBuildingName(),tomorrow,lendOutDTO.getLendOutWater(),all);
            totalLendOut = NumberUtil.add(totalLendOut,lendOutDTO.getLendOutWater());
            buildingMap.put(buildingId,lendOutDTO.getBuildingName());
        }
        analyzeVO.getPlanAdjustVO().setLendOutList(all);
        //判断是否水量平衡
        if(NumberUtil.compare(totalLendOut.doubleValue(),total.doubleValue()) != 0){
            analyzeVO.setBalanced(Boolean.valueOf(false));
        }
        //增加借入方对比
        LendInDTO lendInDTO = allDTO.getLendIns().get(0);
        buildingMap.put(lendInDTO.getBuildingId(),lendInDTO.getBuildingName());
        List<WrPlanDataCompareVO> comparisonDataList = getComparisonData(buildingMap);
        //增加所在用水单位对比
        String xsUseId = allDTO.getXsUseId();
        if(StringUtils.isNotEmpty(xsUseId)){
            getComparisonForXsUseUnit(buildingMap, comparisonDataList, xsUseId , allDTO.getXsUseName());
        }

        analyzeVO.setDataCompareAnalyze(comparisonDataList);
    }

    /**
     * 获取借入水量
     * @param buildingName
     * @param dateList
     * @param newPlanValue
     * @param oldPlanValue
     * @param lendInList
     * @return
     */
    private BigDecimal getLendInWater(String buildingName, List<String> dateList, List<BigDecimal> newPlanValue,
                                      List<BigDecimal> oldPlanValue, List<PlanAdjustDetailVO> lendInList , String adjustType) {
        int index = 0;
        BigDecimal total = new BigDecimal(0);
        for (String everyday : dateList) {
            //原计划值
            BigDecimal oldVal = oldPlanValue.get(index);
            BigDecimal newVal = newPlanValue.get(index);
            BigDecimal diff = NumberUtil.sub(newVal,oldVal);
            if(diff.doubleValue() != 0){
                PlanAdjustDetailVO vo = new PlanAdjustDetailVO();
                vo.setBuildingName(buildingName);
                vo.setTimeStr(everyday);
                if(BUILDING_IN_OTHER_WATER_UNIT.equals(adjustType)){
                    vo.setPlan(oldVal);
                    vo.setResize(newVal);
                }else{
                    vo.setPlan(Convert2Quantity(oldVal,1));
                    vo.setResize(Convert2Quantity(newVal,1));
                }
                vo.setDiff(NumberUtil.sub(vo.getResize(),vo.getPlan()));
                lendInList.add(vo);
                total = NumberUtil.add(total,vo.getDiff());
            }
            index++;
        }
        return total;
    }

    private List<WrPlanDataCompareVO> getComparisonData(Map<String,String> buildingMap){
        List<String> buildingList = new ArrayList<>(buildingMap.keySet());
        List<WrPlanDataCompareVO> voList = new ArrayList<>();
        DateTime date = DateUtil.date();
        //当月第一天
        Date firstDayOfMonth = DateUtil.beginOfMonth(date);
        //当年第一天
        Date firstDayOfYear = DateUtil.beginOfYear(date);
        //前一天
        Date yesterday = getEndTimeOfYesterday();

        //月截至实际引水量
        List<DataBuildingDto> actMtdList = dataService.getSpecialDataRunDataType(buildingList,
                Arrays.asList(PointTypeEnum.WATER_VOLUME.getId()), firstDayOfMonth.getTime(), yesterday.getTime(),
                Param.ValType.Special_V, Param.RunDataType.RUN_DAY, Param.CalcType.CALC_SUM);
        Map<String, DataBuildingDto> dtoMapAll = new HashMap<>();
        if(CollectionUtils.isNotEmpty(actMtdList)){
            Map<String, DataBuildingDto> dtoMap = actMtdList.stream().collect(Collectors.toMap(DataBuildingDto::getId, e -> e));
            if(MapUtils.isNotEmpty(dtoMap)){
                dtoMapAll.putAll(dtoMap);
            }
        }
        //月截至计划水量
        List<SimpleWrPlanInterDayVO> planMtdList = getSumWaterQuantityForPlan(buildingList, firstDayOfMonth, yesterday);
        Map<String, SimpleWrPlanInterDayVO> planMtdMapAll = new HashMap<>();
        if(CollectionUtils.isNotEmpty(planMtdList)){
            Map<String, SimpleWrPlanInterDayVO> planMtdMap = planMtdList.stream().collect(Collectors.toMap(SimpleWrPlanInterDayVO::getBuildingId, e -> e));
            if(MapUtils.isNotEmpty(planMtdMap)){
                planMtdMapAll.putAll(planMtdMap);
            }
        }
        //月滚存计划水量
        List<WrPlanInterTday> tDayList = getRollWaterForCurrentMonth(buildingList,Arrays.asList(getFirstDayOfMonth()));
        Map<String, WrPlanInterTday> tDayMapAll = new HashMap<>();
        if(CollectionUtils.isNotEmpty(tDayList)){
            Map<String, WrPlanInterTday> tDayMap = tDayList.stream().collect(Collectors.toMap(WrPlanInterTday::getBuildingId, e -> e));
            if(MapUtils.isNotEmpty(tDayMap)){
                tDayMapAll.putAll(tDayMap);
            }
        }
        //年初月计划水量
        List<WaterPlanFillinYear> originalMonthPlanList = getWaterPlanForCurrentMonth(buildingList);
        Map<String, WaterPlanFillinYear> originalMapAll = new HashMap<>();
        if(CollectionUtils.isNotEmpty(originalMonthPlanList)){
            Map<String, WaterPlanFillinYear> originalMap = originalMonthPlanList.stream().collect(Collectors.toMap(WaterPlanFillinYear::getBuildingId, e -> e));
            if(MapUtils.isNotEmpty(originalMap)){
                originalMapAll.putAll(originalMap);
            }
        }
        //年截至实际引水量
        List<DataBuildingDto> actYtdList = dataService.getSpecialDataRunDataType(buildingList,
                Arrays.asList(PointTypeEnum.WATER_VOLUME.getId()), firstDayOfYear.getTime(), yesterday.getTime(),
                Param.ValType.Special_V, Param.RunDataType.RUN_DAY, Param.CalcType.CALC_SUM);
        Map<String, DataBuildingDto> actYtdMapAll = new HashMap<>();
        if(CollectionUtils.isNotEmpty(actYtdList)){
            Map<String, DataBuildingDto> actYtdMap = actYtdList.stream().collect(Collectors.toMap(DataBuildingDto::getId, e -> e));
            if(MapUtils.isNotEmpty(actYtdMap)){
                actYtdMapAll.putAll(actYtdMap);
            }
        }
        //年截至计划水量
        List<SimpleWrPlanInterDayVO> planYtdList = getSumWaterQuantityForPlan(buildingList, firstDayOfYear, yesterday);
        Map<String, SimpleWrPlanInterDayVO> planYtdMapAll = new HashMap<>();
        if(CollectionUtils.isNotEmpty(planYtdList)){
            Map<String, SimpleWrPlanInterDayVO> planYtdMap = planYtdList.stream().collect(Collectors.toMap(SimpleWrPlanInterDayVO::getBuildingId, e -> e));
            if(MapUtils.isNotEmpty(planYtdMap)){
                planYtdMapAll.putAll(planYtdMap);
            }
        }
        //年度总计划水量
        List<SimpleWaterPlanFillInYearVO> yearPlanList = getSumWaterQuantityForYearPlan(buildingList);
        Map<String, SimpleWaterPlanFillInYearVO> yearPlanMapAll = new HashMap<>();
        if(CollectionUtils.isNotEmpty(yearPlanList)){
            Map<String, SimpleWaterPlanFillInYearVO> yearPlanMap = yearPlanList.stream().collect(Collectors.toMap(SimpleWaterPlanFillInYearVO::getBuildingId, e -> e));
            if(MapUtils.isNotEmpty(yearPlanMap)){
                yearPlanMapAll.putAll(yearPlanMap);
            }
        }

        for (String buildingId : buildingList) {
            WrPlanDataCompareVO vo = new WrPlanDataCompareVO();
            voList.add(vo);
            //引水口名称
            vo.setBuildingName(buildingMap.get(buildingId));
            //月截至实际引水量
            DataBuildingDto dto = dtoMapAll.get(buildingId);
            if(null != dto){
                List<DataPointDto> pointDtoList = dto.getDataPointDtos();
                if(CollectionUtils.isNotEmpty(pointDtoList)){
                    vo.setActMtd(number(pointDtoList.get(0).getV()));
                }
            }
            //月截至计划水量
            SimpleWrPlanInterDayVO simpleWrPlanInterDayVO = planMtdMapAll.get(buildingId);
            if(null != simpleWrPlanInterDayVO){
                vo.setPlanMtd(number(simpleWrPlanInterDayVO.getWaterQuantity()));
            }
            //月滚存计划水量
            WrPlanInterTday tDay = tDayMapAll.get(buildingId);
            if(null != tDay){
                vo.setRollMonthPlan(tDay.getWaterQuantity());
            }
            //年初月计划水量
            WaterPlanFillinYear planFillInYear = originalMapAll.get(buildingId);
            if(null != planFillInYear){
                vo.setOriginalMonthPlan(planFillInYear.getDemadWaterQuantity());
            }
            //年截至实际引水量
            DataBuildingDto dataBuildingDto = actYtdMapAll.get(buildingId);
            if(null != dataBuildingDto){
                List<DataPointDto> pointDtoList = dataBuildingDto.getDataPointDtos();
                if(CollectionUtils.isNotEmpty(pointDtoList)){
                    vo.setActYtd(number(pointDtoList.get(0).getV()));
                }
            }
            //年截至计划水量
            SimpleWrPlanInterDayVO dayVO = planYtdMapAll.get(buildingId);
            if(null != dayVO){
                vo.setPlanYtd(number(dayVO.getWaterQuantity()));
            }
            //年度总计划水量
            SimpleWaterPlanFillInYearVO yearVO = yearPlanMapAll.get(buildingId);
            if(null != yearVO){
                vo.setPlanYear(number(yearVO.getWaterQuantity()));
            }
        }
        return voList;
    }

    private List<WrPlanInterTday> getRollWaterForCurrentMonth(List<String> buildingList , List<Date> dateList){
        LambdaQueryWrapper<WrPlanInterTday> wrapper = new QueryWrapper().lambda();
        if(buildingList.size() == 1){
            wrapper.eq(WrPlanInterTday::getBuildingId,buildingList.get(0));
        }else{
            wrapper.in(WrPlanInterTday::getBuildingId,buildingList);
        }
        wrapper.eq(WrPlanInterTday::getTimeType,YEAR_PLAN_TDAY_TYPE_4);
        if(dateList.size() == 1){
            wrapper.eq(WrPlanInterTday::getSupplyTime,dateList.get(0));
        }else{
            wrapper.in(WrPlanInterTday::getSupplyTime,dateList);
        }
        return wrPlanInterTdayMapper.selectList(wrapper);
    }

    private List<WaterPlanFillinYear> getWaterPlanForCurrentMonth(List<String> buildingList){
        Date date = DateUtil.date();
        LambdaQueryWrapper<WaterPlanFillinYear> wrapper = new QueryWrapper().lambda();
        if(buildingList.size() == 1){
            wrapper.eq(WaterPlanFillinYear::getBuildingId,buildingList.get(0));
        }else{
            wrapper.in(WaterPlanFillinYear::getBuildingId,buildingList);
        }
        wrapper.eq(WaterPlanFillinYear::getTday,YEAR_PLAN_TDAY_TYPE_4);
        wrapper.eq(WaterPlanFillinYear::getYear,String.valueOf(DateUtil.year(date)));
        wrapper.eq(WaterPlanFillinYear::getMonth, StringUtils.leftPad(String.valueOf(DateUtil.month(date)),2,'0'));
        return waterPlanFillinYearMapper.selectList(wrapper);
    }

    private List<SimpleWrPlanInterDayVO> getSumWaterQuantityForPlan(List<String> buildingIdList, Date starTime, Date endTime) {
        LambdaQueryWrapper<WrPlanInterDay> wrapper = new QueryWrapper().lambda();
        wrapper.between(WrPlanInterDay::getSupplyTime,starTime,endTime);
        if(CollectionUtils.isNotEmpty(buildingIdList)){
            if(buildingIdList.size() == 1){
                wrapper.eq(WrPlanInterDay::getBuildingId,buildingIdList.get(0));
            }else{
                wrapper.in(WrPlanInterDay::getBuildingId,buildingIdList);
            }
        }
        wrapper.groupBy(WrPlanInterDay::getBuildingId);
        return wrPlanInterDayMapper.getSumWaterQuantity(wrapper);
    }

    private List<SimpleWaterPlanFillInYearVO> getSumWaterQuantityForYearPlan(List<String> buildingIdList) {
        LambdaQueryWrapper<WaterPlanFillinYear> wrapper = new QueryWrapper().lambda();
        if(buildingIdList.size() == 1){
            wrapper.eq(WaterPlanFillinYear::getBuildingId,buildingIdList.get(0));
        }else{
            wrapper.in(WaterPlanFillinYear::getBuildingId,buildingIdList);
        }
        wrapper.eq(WaterPlanFillinYear::getTday,YEAR_PLAN_TDAY_TYPE_4);
        wrapper.eq(WaterPlanFillinYear::getYear,String.valueOf(DateUtil.year(DateUtil.date())));
        wrapper.groupBy(WaterPlanFillinYear::getBuildingId);
        return waterPlanFillinYearMapper.getSumWaterQuantity(wrapper);
    }

    /**
     * 实引和计划数据
     * @param buildingIdList
     */
    private void setPlanAndRealWater(List<String> buildingIdList, Map<String, List<WrPlanInterDay>> buildingGroupMap, Map<String, List<DataPointDto>> buildingRealMap) {
        //当月第一天
        Date startTime = getFirstDayOfMonth();
        Date yesterday = getEndTimeOfYesterday();
        List<DataBuildingDto> dataList = dataService.getSpecialDataRunDataType(buildingIdList,
                Arrays.asList(PointTypeEnum.WATER_VOLUME.getId()), startTime.getTime(), yesterday.getTime(),
                Param.ValType.Special_V, Param.RunDataType.RUN_DAY, null);
        //全月的日迭代计划
        List<WrPlanInterDay> dayPlanList = wrRecentPlanAdjustService.getWrPlanInterDays(buildingIdList, startTime, yesterday);
        if(CollectionUtils.isEmpty(dayPlanList) || CollectionUtils.isEmpty(dataList)){
            return;
        }
        //按照引水口进行分组
        buildingGroupMap.putAll(dayPlanList.stream().collect(Collectors.groupingBy(WrPlanInterDay::getBuildingId)));
        //实引水量
        for (DataBuildingDto dataBuilding : dataList) {
            buildingRealMap.put(dataBuilding.getId(),dataBuilding.getDataPointDtos());
        }
    }

    /**
     * 查询指定引水口给定时间段实际用水情况
     * @param buildingIdList
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public List<WrUseDetailListVO> getWaterUseDetail(List<String> buildingIdList , Date startDate , Date endDate){
        /*Date today = DateUtil.date();
        //默认本月初
        if(null == startDate){
            startDate = DateUtil.beginOfMonth(today);
        }
        if(null == endDate){
            endDate = DateUtil.endOfMonth(today);
        }
        //实引水量
        List<DataBuildingDto> dataList = dataService.getSpecialDataRunDataType(buildingIdList,
                Arrays.asList(PointTypeEnum.WATER_VOLUME.getId()), startDate.getTime(), endDate.getTime(),
                Param.ValType.Special_V, Param.RunDataType.RUN_DAY, null);
        List<WrPlanInterDay> dayPlanList = wrRecentPlanAdjustService.getWrPlanInterDays(buildingIdList, startDate, endDate);
        if(CollectionUtils.isEmpty(dataList) || CollectionUtils.isEmpty(dayPlanList)){
            return Collections.emptyList();
        }
        List<WrUseDetailListVO> voList = new ArrayList<>();
        Map<String, DataBuildingDto> dtoMap = dataList.stream().collect(Collectors.toMap(DataBuildingDto::getId, e -> e));
        Map<String, List<WrPlanInterDay>> dayListMap = dayPlanList.stream().collect(Collectors.groupingBy(WrPlanInterDay::getBuildingId));
        Iterator<Map.Entry<String, DataBuildingDto>> iterator = dtoMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, DataBuildingDto> next = iterator.next();
            WrUseDetailListVO vo = new WrUseDetailListVO();
            String buildingId = next.getKey();
            vo.setBuildingId(buildingId);
            vo.setStartDate(startDate);
            vo.setEndDate(endDate);
            voList.add(vo);
            List<WrPlanInterDay> interDayList = dayListMap.get(buildingId);
            Map<Date,WrPlanInterDay> interDayMap = new HashMap<>();
            if(CollectionUtils.isNotEmpty(interDayList)){
                interDayList.stream().forEach(day -> interDayMap.put(day.getSupplyTime(),day));
            }
            DataBuildingDto dto = next.getValue();
            List<DataPointDto> dataPointDtoList = dto.getDataPointDtos();
            BigDecimal totalDifference = new BigDecimal(0);
            if(CollectionUtils.isNotEmpty(dataPointDtoList)){
                List<WrUseDetailVO> detailVOList = new ArrayList<>();
                for (DataPointDto dataPointDto : dataPointDtoList) {
                    WrUseDetailVO detailVO = new WrUseDetailVO();
                    Date date = DateUtils.convertTimeToDate(dataPointDto.getTime());
                    WrPlanInterDay day = interDayMap.get(date);
                    detailVO.setPlan(null == day ? new BigDecimal(0) : day.getWaterQuantity());
                    detailVO.setRealUse(number(dataPointDto.getV()));
                    detailVO.setEveryDay(date);
                    detailVO.setDifference(NumberUtil.sub(detailVO.getPlan(),detailVO.getRealUse()));
                    if(detailVO.getDifference().doubleValue() != 0){
                        detailVOList.add(detailVO);
                        totalDifference = NumberUtil.add(totalDifference,detailVO.getDifference());
                    }
                }
                vo.setTotalDifference(totalDifference);
                vo.setVoList(detailVOList.stream().filter(e -> nonNullOfBigDecimal(e.getDifference()).doubleValue() > 0).collect(Collectors.toList()));
            }
        }
        return voList;*/
        return getWaterUseDetailExt(buildingIdList,startDate,endDate);
    }

    /**
     * 查询指定引水口给定时间段实际用水情况
     * @param buildingIdList
     * @param startDate
     * @param endDate
     * @return
     */
    public List<WrUseDetailListVO> getWaterUseDetailExt(List<String> buildingIdList , Date startDate , Date endDate){
        Date today = DateUtil.date();
        //默认本月初
        if(null == startDate){
            startDate = DateUtil.beginOfMonth(today);
        }
        if(null == endDate){
            endDate = DateUtil.endOfMonth(today);
        }
        //实引水量
        List<DataBuildingDto> dataList = dataService.getSpecialDataRunDataType(buildingIdList,
                Arrays.asList(PointTypeEnum.WATER_VOLUME.getId()), startDate.getTime(), endDate.getTime(),
                Param.ValType.Special_V, Param.RunDataType.RUN_DAY, null);
        List<WrPlanInterDay> dayPlanList = wrRecentPlanAdjustService.getWrPlanInterDays(buildingIdList, startDate, endDate);
        if(CollectionUtils.isEmpty(dataList) || CollectionUtils.isEmpty(dayPlanList)){
            return Collections.emptyList();
        }
        List<WrUseDetailListVO> voList = new ArrayList<>();
        Map<String, DataBuildingDto> dtoMap = dataList.stream().collect(Collectors.toMap(DataBuildingDto::getId, e -> e));
        Map<String, List<WrPlanInterDay>> dayListMap = dayPlanList.stream().collect(Collectors.groupingBy(WrPlanInterDay::getBuildingId));
        Iterator<Map.Entry<String, List<WrPlanInterDay>>> iterator = dayListMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, List<WrPlanInterDay>> next = iterator.next();
            WrUseDetailListVO vo = new WrUseDetailListVO();
            String buildingId = next.getKey();
            vo.setBuildingId(buildingId);
            vo.setStartDate(startDate);
            vo.setEndDate(endDate);
            voList.add(vo);
            List<WrPlanInterDay> interDayList = next.getValue();
            Map<Date,WrPlanInterDay> interDayMap = new HashMap<>();
            if(CollectionUtils.isNotEmpty(interDayList)){
                interDayList.stream().forEach(day -> interDayMap.put(day.getSupplyTime(),day));
            }
            DataBuildingDto dto = dtoMap.get(buildingId);
            List<DataPointDto> dataPointDtoList = dto.getDataPointDtos();
            BigDecimal totalDifference = new BigDecimal(0);
            Map<Date,DataPointDto> pointDtoMap = new HashMap<>();
            if(CollectionUtils.isNotEmpty(dataPointDtoList)){
                Map<Date, DataPointDto> localMap = dataPointDtoList.stream().collect(Collectors.toMap(e -> DateUtils.convertTimeToDate(e.getTime()), e -> e));
                pointDtoMap.putAll(localMap);
            }
            List<WrUseDetailVO> detailVOList = new ArrayList<>();
            for (WrPlanInterDay day : interDayList) {
                WrUseDetailVO detailVO = new WrUseDetailVO();
                detailVO.setPlan(day.getWaterQuantity());
                DataPointDto localDto = pointDtoMap.get(day.getSupplyTime());
                BigDecimal realUse = null == localDto ? new BigDecimal(0) : number(localDto.getV());
                detailVO.setRealUse(realUse);
                detailVO.setEveryDay(day.getSupplyTime());
                detailVO.setDifference(NumberUtil.sub(detailVO.getPlan(),detailVO.getRealUse()));
                if(detailVO.getDifference().doubleValue() != 0){
                    detailVOList.add(detailVO);
                    totalDifference = NumberUtil.add(totalDifference,detailVO.getDifference());
                }
            }
            vo.setTotalDifference(totalDifference);
            List<WrUseDetailVO> localVoList = detailVOList.stream().filter(e -> nonNullOfBigDecimal(e.getDifference()).doubleValue() > 0).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(localVoList)){
                List<WrUseDetailVO> sortList = localVoList.stream().sorted(Comparator.comparing(WrUseDetailVO::getEveryDay)).collect(Collectors.toList());
                vo.setVoList(sortList);
            }
        }
        return voList;
    }

    /**
     * 构建月内借出方调整详情
     * @param buildingId
     * @param buildingName
     * @param resizeEndTime
     * @param totalResize
     * @return
     */
    private void generateLendOutDetailCommon(String buildingId , String buildingName , Date resizeEndTime, BigDecimal totalResize , List<PlanAdjustDetailVO> resultList){
        //当月第一天
        Date startTime = getFirstDayOfMonth();
        Date yesterday = getEndTimeOfYesterday();
        List<WrUseDetailListVO> detailList = getWaterUseDetail(Arrays.asList(buildingId), startTime, yesterday);
        if(detailList.size() == 0){
            return;
        }
        WrUseDetailListVO wrUseDetailListVO = detailList.get(0);
        //结余水量
        BigDecimal totalDifference = nonNullOfBigDecimal(wrUseDetailListVO.getTotalDifference());
        //TODO 结余水量小于0不考虑，如果确实存在实引超过计划，应该通过通知去提示站里调整。流量调整只影响存在结余的情况或者后续剩余指标。
        //将流量调小，则只将当天之后的日计划调大
        //流量调大,如果结余小于等于0,则直接从后续计划进行均摊扣除
        if(totalResize.doubleValue() < 0 || totalDifference.doubleValue() <= 0){
            shareEqualForRemainDaysInMonth(totalResize, buildingId , buildingName, resizeEndTime, resultList);
            return;
        }
        //结余及剩余月指标调整
        List<WrUseDetailVO> voList = wrUseDetailListVO.getVoList();
        List<WrUseDetailVO> updateList = new ArrayList<>();
        for (WrUseDetailVO detailVO : voList) {
            BigDecimal diff = NumberUtil.sub(totalResize, nonNullOfBigDecimal(detailVO.getDifference()));
            //还需要继续扣减
            if(diff.doubleValue() > 0){
                detailVO.setPlanAfter(detailVO.getRealUse());
                updateList.add(detailVO);
                totalResize = diff;
            }else{
                detailVO.setPlanAfter(NumberUtil.sub(detailVO.getPlan(),totalResize));
                updateList.add(detailVO);
                totalResize = new BigDecimal(0);
                break;
            }
        }
        //结余调整部分
        if(updateList.size() > 0){
            for (WrUseDetailVO day : updateList) {
                PlanAdjustDetailVO vo = new PlanAdjustDetailVO();
                vo.setBuildingName(buildingName);
                vo.setLendOutSource(LEND_OUT_SOURCE_REMAIN_PAST);
                vo.setPlan(day.getPlan());
                vo.setResize(day.getPlanAfter());
                vo.setDiff(NumberUtil.sub(vo.getResize(),vo.getPlan()));
                vo.setTimeStr(DateUtils.parseDateToString(day.getEveryDay()));
                resultList.add(vo);
            }
        }
        //结余足够借调
        if(totalResize.doubleValue() <= 0){
            return;
        }
        //结余不够，先扣除结余，剩余部分由月剩余指标进行扣除
        shareEqualForRemainDaysInMonth(totalResize,buildingId,buildingName, resizeEndTime, resultList);
    }

    private void shareEqualForRemainDaysInMonth(BigDecimal totalResize, String buildingId , String buildingName, Date resizeEndDate, List<PlanAdjustDetailVO> resultList) {
        Date endTime = DateUtil.endOfMonth(resizeEndDate);
        List<WrPlanInterDay> interDayList = wrRecentPlanAdjustService.getWrPlanInterDays(Arrays.asList(buildingId), resizeEndDate, endTime);
        if(CollectionUtils.isNotEmpty(interDayList)){
            BigDecimal avg = NumberUtil.div(totalResize,interDayList.size(),4);
            for (WrPlanInterDay day : interDayList) {
                PlanAdjustDetailVO vo = new PlanAdjustDetailVO();
                vo.setBuildingName(buildingName);
                vo.setLendOutSource(LEND_OUT_SOURCE_REMAIN_OF_MONTH);
                vo.setPlan(day.getWaterQuantity());
                vo.setResize(NumberUtil.sub(vo.getPlan(),avg));
                vo.setDiff(NumberUtil.sub(vo.getResize(),vo.getPlan()));
                vo.setTimeStr(DateUtils.parseDateToString(day.getSupplyTime()));
                resultList.add(vo);
            }
        }
    }

    private List<PlanAdjustDetailVO> generateLendOutDetailForMultiMonth(WaterBalanceAnalyzeVO analyzeVO, WrPlanFillInDayAllDTO allDTO , String buildingName , BigDecimal total){
        //构建借出方
        List<PlanAdjustDetailVO> lendOutList = new ArrayList<>();
        String months = allDTO.getMonths();
        if(StringUtils.isEmpty(months)){
            throw new TransactionException(CodeEnum.NO_DATA, "近期计划跨月调整，请选择借出月份！");
        }
        List<LendOutSpanMonthsDTO> spanMonthLendOuts = allDTO.getSpanMonthLendOuts();
        if(CollectionUtils.isEmpty(spanMonthLendOuts)){
            throw new TransactionException(CodeEnum.NO_DATA, "近期计划跨月调整，未传入任何借出调整信息！");
        }
        String[] monthArr = StringUtils.split(months, ",");
        LendOutSpanMonthsDTO dto = spanMonthLendOuts.get(0);
        //调整前
        List<BigDecimal> oldPlanValue = dto.getOldPlanValue();
        //调整后
        List<BigDecimal> newPlanValue = dto.getNewPlanValue();
        if(CollectionUtils.isEmpty(newPlanValue) || CollectionUtils.isEmpty(oldPlanValue)){
            throw new TransactionException(CodeEnum.NO_DATA, "近期计划跨月调整，需传入调整前后旬迭代计划值！");
        }
        if(newPlanValue.size() != oldPlanValue.size()){
            throw new TransactionException(CodeEnum.PARAM_ERROR, "近期计划跨月调整，需传入调整前后旬迭代计划值数量不等！");
        }
        List<String> tDayDescChList = getTDayDescCh(monthArr);
        int index = 0;
        for (String tday : tDayDescChList) {
            PlanAdjustDetailVO vo = new PlanAdjustDetailVO();
            vo.setBuildingName(buildingName);
            vo.setPlan(oldPlanValue.get(index));
            vo.setResize(newPlanValue.get(index));
            vo.setDiff(NumberUtil.sub(vo.getPlan(),vo.getResize()));
            vo.setTimeStr(tday);
            vo.setLendOutSource(LEND_OUT_SOURCE_REMAIN_OF_YEAR);
            lendOutList.add(vo);
            total = NumberUtil.sub(total,vo.getDiff());
            index++;
        }
        if(total.doubleValue() != 0){
            analyzeVO.setBalanced(Boolean.valueOf(false));
        }else{
            analyzeVO.setBalanced(Boolean.valueOf(true));
        }
        return lendOutList;
    }

    /**
     * TODO 代码太丑陋，未做越界判断
     * @param monthArr
     * @return
     */
    private List<String> getTDayDescCh(String[] monthArr){
        List<String> tDayList = new ArrayList<>();
        for (String month : monthArr) {
            String realMonth = StringUtils.split(month,"-")[1];
            int monthInt = Integer.parseInt(realMonth);
            tDayList.add(monthInt + "月上旬");
            tDayList.add(monthInt + "月中旬");
            tDayList.add(monthInt + "月下旬");
        }
        return tDayList;
    }

}
