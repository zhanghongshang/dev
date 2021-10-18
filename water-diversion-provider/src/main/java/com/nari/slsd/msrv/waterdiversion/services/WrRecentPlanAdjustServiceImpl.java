package com.nari.slsd.msrv.waterdiversion.services;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nari.slsd.hu.mplat.imc.client.Param.Param;
import com.nari.slsd.msrv.common.exception.TransactionException;
import com.nari.slsd.msrv.common.ext.enums.CodeEnum;
import com.nari.slsd.msrv.common.utils.DateUtils;
import com.nari.slsd.msrv.common.utils.IDGenerator;
import com.nari.slsd.msrv.waterdiversion.cache.interfaces.IModelCacheService;
import com.nari.slsd.msrv.waterdiversion.commons.PointTypeEnum;
import com.nari.slsd.msrv.waterdiversion.commons.RedisOperationTypeEnum;
import com.nari.slsd.msrv.waterdiversion.commons.WrCmdManagerEnum;
import com.nari.slsd.msrv.waterdiversion.interfaces.*;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.*;
import com.nari.slsd.msrv.waterdiversion.model.dto.DataBuildingDto;
import com.nari.slsd.msrv.waterdiversion.model.dto.DataPointDto;
import com.nari.slsd.msrv.waterdiversion.model.dto.SimpleWrBuildingAndDiversion;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrBuildingAndDiversion;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.*;
import com.nari.slsd.msrv.waterdiversion.model.vo.*;
import com.nari.slsd.msrv.waterdiversion.utils.UniqueCodeGenerateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.nari.slsd.msrv.waterdiversion.commons.InstructionEnum.*;
import static com.nari.slsd.msrv.waterdiversion.commons.RecentPlanEnum.*;
import static com.nari.slsd.msrv.waterdiversion.commons.TDayTypeEnum.YEAR_PLAN_TDAY_TYPE_4;
import static com.nari.slsd.msrv.waterdiversion.utils.CommonUtil.*;

/**
 * @author bigb
 * @title
 * @description 近期计划调整
 * @updateTime 2021/9/8 12:10
 * @throws
 */
@Service
@Slf4j
public class WrRecentPlanAdjustServiceImpl implements IWrRecentPlanAdjustService {

    private static final String ORDER_TEMPLATE = "调度指令【{0}】号";

    @Autowired
    private IDataService dataService;

    @Autowired
    private IWrUseUnitManagerService wrUseUnitManagerService;

    @Autowired
    private IWrPlanGenerateMonthService wrPlanGenerateMonthService;

    @Resource
    private IWrPlanInterDayService wrPlanInterDayService;

    @Resource
    private IWrPlanInterTdayService wrPlanInterTdayService;

    @Resource
    private IWaterBuildingManagerService waterBuildingManagerService;

    @Autowired
    private IWrBalanceAnalyzeService wrBalanceAnalyzeService;

    @Autowired
    private IWrRecentPlanAdjustService wrRecentPlanAdjustService;

    @Autowired
    private IModelCacheService modelCacheService;

    @Autowired
    private WrUseUnitManagerMapper wrUseUnitManagerMapper;

    @Autowired
    private WrPlanInterTdayMapper wrPlanInterTdayMapper;

    @Autowired
    private WrPlanInterDayMapper wrPlanInterDayMapper;

    @Autowired
    private WrCmdManagerMapper wrCmdManagerMapper;

    @Autowired
    private WrDispatchInstructionMapper wrDispatchInstructionMapper;

    @Autowired
    private UniqueCodeGenerateUtil uniqueCodeGenerateUtil;

    @Autowired
    private WrPlanTaskMapper wrPlanTaskMapper;

    @Autowired
    private WrPlanFillinDayMapper wrPlanFillinDayMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void proposerConfirm(String operator , String taskId){
        //查询填报任务
        WrPlanTask wrPlanTask = wrPlanTaskMapper.selectById(taskId);
        if(null == wrPlanTask){
            throw new TransactionException(CodeEnum.NO_DATA, "未获取到任何任务信息,任务id为：" + taskId);
        }
        //近期填报类别
        String subType = wrPlanTask.getSubType();
        //根据任务id查询近期用水计划
        LambdaQueryWrapper<WrPlanFillinDay> dayMapper = new QueryWrapper().lambda();
        dayMapper.eq(WrPlanFillinDay::getPlanTaskId,taskId);
        List<WrPlanFillinDay> dayList = wrPlanFillinDayMapper.selectList(dayMapper);
        if(CollectionUtils.isEmpty(dayList)){
            throw new TransactionException(CodeEnum.NO_DATA, "未查询到任何近期填报信息，任务id为：" + taskId);
        }
        if(BUILDING_IN_MONTH.equals(subType)){
            proposerConfirmInMonth(operator, wrPlanTask, dayList);
        }else if(OTHER_BUILDING_IN_MONTH.equals(subType)){
            proposerConfirmOtherBuildingsInMonth(operator, wrPlanTask, dayList);
        }else if(BUILDING_IN_OTHER_MONTH.equals(subType)){
            proposerConfirmInOtherMonth(operator, wrPlanTask, dayList);
        }else if(BUILDING_IN_OTHER_WATER_UNIT.equals(subType)){
            proposerConfirmInOtherUseUnit(dayList);
        }
    }

    private void proposerConfirmInMonth(String operator, WrPlanTask wrPlanTask, List<WrPlanFillinDay> dayList) {
        updateWrPlanIteratorInMonth(dayList);
        //借入方生成指令且超年调整不生成调度指令
        generateInstructions(operator, wrPlanTask, dayList);
    }

    private void proposerConfirmOtherBuildingsInMonth(String operator, WrPlanTask wrPlanTask, List<WrPlanFillinDay> dayList) {
        Map<String, List<WrPlanFillinDay>> lendMap = dayList.stream().collect(Collectors.groupingBy(WrPlanFillinDay::getLendType));
        List<WrPlanFillinDay> lendInList = lendMap.get(LEND_IN);
        updateWrPlanIteratorLendIn(lendInList);
        //借入方生成指令且超年调整不生成调度指令
        generateInstructions(operator, wrPlanTask, lendInList);
        List<WrPlanFillinDay> lendOutList = lendMap.get(LEND_OUT);
        updateWrPlanIteratorOtherBuildingsInMonthForLendOut(lendOutList);
    }

    private void proposerConfirmInOtherMonth(String operator, WrPlanTask wrPlanTask, List<WrPlanFillinDay> dayList) {
        Map<String, List<WrPlanFillinDay>> lendMap = dayList.stream().collect(Collectors.groupingBy(WrPlanFillinDay::getLendType));
        List<WrPlanFillinDay> lendInList = lendMap.get(LEND_IN);
        updateWrPlanIteratorLendIn(lendInList);
        //借入方生成指令且超年调整不生成调度指令
        generateInstructions(operator, wrPlanTask, lendInList);
        List<WrPlanFillinDay> lendOutList = lendMap.get(LEND_OUT);
        updateWrPlanIteratorInOtherMonthForLendOut(lendOutList);
    }

    private void proposerConfirmInOtherUseUnit(List<WrPlanFillinDay> dayList) {
        Map<String, List<WrPlanFillinDay>> lendMap = dayList.stream().collect(Collectors.groupingBy(WrPlanFillinDay::getLendType));
        List<WrPlanFillinDay> lendInList = lendMap.get(LEND_IN);
        updateWrPlanIteratorLendInForMultiUseUnit(lendInList);
        List<WrPlanFillinDay> lendOutList = lendMap.get(LEND_OUT);
        updateWrPlanIteratorInMultiUseUnitForLendOut(lendOutList);
    }

    /**
     * 跨用水单位借出方,更新日迭代计划及旬月迭代计划
     * @param dayList
     */
    private void updateWrPlanIteratorInMultiUseUnitForLendOut(List<WrPlanFillinDay> dayList){
        if(CollectionUtils.isEmpty(dayList)){
            return;
        }
        //每个引水口一条近期计划数据
        Map<String, WrPlanFillinDay> dayMap = dayList.stream().collect(Collectors.toMap(WrPlanFillinDay::getBuildingId, e -> e));
        dayMap.entrySet().forEach(entry -> {
            String buildingId = entry.getKey();
            WrPlanFillinDay fillInDay = entry.getValue();
            Map<String, BigDecimal> remainOfMonthMap = getRemainOfMonth(Arrays.asList(buildingId));
            BigDecimal remainOfMonth = nonNullOfBigDecimal(remainOfMonthMap.get(buildingId));
            BigDecimal lendOut = NumberUtil.sub(fillInDay.getDemandWaterQuantityAfter(), fillInDay.getDemandWaterQuantuty());
            BigDecimal lendOutInMonth = lendOut;
            //本月月剩余水量不够借,则从后续月份借
            BigDecimal lendOutForOtherMonth = NumberUtil.sub(lendOut,remainOfMonth);
            if(lendOutForOtherMonth.doubleValue() > 0){
                lendOutInMonth = remainOfMonth;
            }
            //月内剩余水量借出
            lendOutInMonth(fillInDay.getBuildingId(),lendOutInMonth);
            if(lendOutForOtherMonth.doubleValue() > 0){
                lendOutInOtherMonth(buildingId,lendOutForOtherMonth);
            }
        });
    }

    private void lendOutInOtherMonth(String buildingId , BigDecimal lendOutForOtherMonth){
        //获取后续月份
        List<Date> remainMonth = getRemainMonth();
        //不太可能出现这个情况
        if(CollectionUtils.isEmpty(remainMonth)){
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
                    tday.setWaterQuantity(NumberUtil.sub(tday.getWaterQuantity(),avg));
                }
                wrPlanInterTdayService.saveOrUpdateBatch(remainList);
            }
        }
    }

    private void lendOutInMonth(String buildingId , BigDecimal lendOutWater) {
        //当月第一天
        Date startTime = getFirstDayOfMonth();
        Date yesterday = getEndTimeOfYesterday();
        List<WrUseDetailListVO> detailList = wrBalanceAnalyzeService.getWaterUseDetail(Arrays.asList(buildingId), startTime, yesterday);
        if (detailList.size() == 0) {
            return;
        }
        WrUseDetailListVO wrUseDetailListVO = detailList.get(0);
        //结余水量
        BigDecimal totalDifference = nonNullOfBigDecimal(wrUseDetailListVO.getTotalDifference());
        List<WrPlanInterDay> allUpdateDayList = new ArrayList<>();
        //流量调大,如果结余小于等于0,则直接从后续计划进行均摊扣除
        Date today = DateUtil.beginOfDay(DateUtil.date());
        if (totalDifference.doubleValue() <= 0) {
            shareEqualForRemainDaysInMonth(lendOutWater, buildingId, today, allUpdateDayList);
            return;
        }
        //结余及剩余月指标调整
        List<WrUseDetailVO> voList = wrUseDetailListVO.getVoList();
        BigDecimal sub = NumberUtil.sub(lendOutWater, totalDifference);
        Map<Date, WrUseDetailVO> surplusMap = new HashMap<>();
        for (WrUseDetailVO detailVO : voList) {
            BigDecimal diff = NumberUtil.sub(sub, nonNullOfBigDecimal(detailVO.getDifference()));
            //还需要继续扣减
            if (diff.doubleValue() > 0) {
                detailVO.setPlan(detailVO.getRealUse());
                surplusMap.put(detailVO.getEveryDay(), detailVO);
                sub = diff;
            } else {
                detailVO.setPlan(NumberUtil.sub(detailVO.getPlan(), sub));
                surplusMap.put(detailVO.getEveryDay(), detailVO);
                break;
            }
        }
        //结余调整部分
        if (surplusMap.size() > 0) {
            List<WrPlanInterDay> surplusDayList = getInterDayList(buildingId, new ArrayList<>(surplusMap.keySet()));
            if (CollectionUtils.isNotEmpty(surplusDayList)) {
                for (WrPlanInterDay day : surplusDayList) {
                    WrUseDetailVO detailVO = surplusMap.get(day.getSupplyTime());
                    day.setWaterQuantity(detailVO.getPlan());
                    day.setWaterFlow(NumberUtil.div(day.getWaterQuantity(), 8.64, 4));
                    allUpdateDayList.add(day);
                }
            }
        }
        //结余足够借调
        if (sub.doubleValue() <= 0) {
            if (allUpdateDayList.size() > 0) {
                wrPlanInterDayService.saveOrUpdateBatch(allUpdateDayList);
            }
            return;
        }
        //剩余部分由月后剩余天数均摊
        shareEqualForRemainDaysInMonth(sub, buildingId, today, allUpdateDayList);
        //更改旬月迭代表
        Date currentMonth = getFirstDayOfMonth();
        LambdaQueryWrapper<WrPlanInterTday> wrapper = new QueryWrapper().lambda();
        wrapper.eq(WrPlanInterTday::getBuildingId, buildingId);
        wrapper.eq(WrPlanInterTday::getTimeType, YEAR_PLAN_TDAY_TYPE_4);
        wrapper.eq(WrPlanInterTday::getSupplyTime, currentMonth);
        WrPlanInterTday tday = wrPlanInterTdayMapper.selectOne(wrapper);
        if (null != tday) {
            tday.setWaterQuantity(NumberUtil.sub(tday.getWaterQuantity(), lendOutWater));
            //TODO 暂不更新流量
            wrPlanInterTdayMapper.updateById(tday);
        }
    }

    /**
     * 借调方,更新日迭代计划及旬月迭代计划
     * @param dayList
     */
    private void updateWrPlanIteratorLendIn(List<WrPlanFillinDay> dayList){
        if(CollectionUtils.isEmpty(dayList)){
            return;
        }
        BigDecimal totalResize = new BigDecimal(0);
        Map<Date, WrPlanFillinDay> dayMap = new HashMap<>();
        for (WrPlanFillinDay day : dayList) {
            totalResize = NumberUtil.add(totalResize,NumberUtil.sub(day.getDemandWaterQuantityAfter(),day.getDemandWaterQuantuty()));
            dayMap.put(convert2Date(day), day);
        }
        //引水口id
        String buildingId = dayList.get(0).getBuildingId();
        //时间进行排序
        List<Date> dateList = dayMap.keySet().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        List<WrPlanInterDay> lendInDayList = getInterDayList(buildingId, dateList);
        //更新日迭代计划
        List<WrPlanInterDay> allUpdateDayList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(lendInDayList)){
            for (WrPlanInterDay day : lendInDayList) {
                WrPlanFillinDay wrPlanFillinDay = dayMap.get(day.getSupplyTime());
                if(null != wrPlanFillinDay){
                    day.setWaterQuantity(wrPlanFillinDay.getDemandWaterQuantityAfter());
                    day.setWaterFlow(NumberUtil.div(day.getWaterQuantity(),8.64,4));
                    allUpdateDayList.add(day);
                }
            }
        }
        if(allUpdateDayList.size() > 0){
            wrPlanInterDayService.saveOrUpdateBatch(allUpdateDayList);
        }
        //修改旬月迭代计划
        Date firstDayOfMonth = getFirstDayOfMonth();
        LambdaQueryWrapper<WrPlanInterTday> wrapper = new QueryWrapper().lambda();
        wrapper.eq(WrPlanInterTday::getBuildingId,buildingId);
        wrapper.eq(WrPlanInterTday::getTimeType,YEAR_PLAN_TDAY_TYPE_4);
        wrapper.eq(WrPlanInterTday::getSupplyTime,firstDayOfMonth);
        WrPlanInterTday tday = wrPlanInterTdayMapper.selectOne(wrapper);
        if(null != tday){
            tday.setWaterQuantity(NumberUtil.add(tday.getWaterQuantity(),totalResize));
            int secondsOfMonth = getSecondsOfMonth(DateUtil.year(firstDayOfMonth), DateUtil.month(firstDayOfMonth));
            tday.setWaterFlow(NumberUtil.div(NumberUtil.mul(tday.getWaterQuantity(),10000),secondsOfMonth,4));
            wrPlanInterTdayMapper.updateById(tday);
        }
    }

    /**
     * 跨用水单位借调方,更新日迭代计划及旬月迭代计划
     * @param dayList
     */
    private void updateWrPlanIteratorLendInForMultiUseUnit(List<WrPlanFillinDay> dayList){
        if(CollectionUtils.isEmpty(dayList)){
            return;
        }
        Map<String, List<WrPlanFillinDay>> dayListForBuildingMap = dayList.stream().collect(Collectors.groupingBy(WrPlanFillinDay::getBuildingId));
        dayListForBuildingMap.entrySet().forEach(entry -> updateWrPlanIteratorLendIn(entry.getValue()));
    }

    /**
     * 跨水口借出方,更新日迭代计划及旬月迭代计划
     * @param dayList
     */
    private void updateWrPlanIteratorOtherBuildingsInMonthForLendOut(List<WrPlanFillinDay> dayList){
        if(CollectionUtils.isEmpty(dayList)){
            return;
        }
        //每个引水口一条近期计划数据
        Map<String, WrPlanFillinDay> dayMap = dayList.stream().collect(Collectors.toMap(WrPlanFillinDay::getBuildingId, e -> e));
        dayMap.entrySet().forEach(entry -> {
            WrPlanFillinDay fillInDay = entry.getValue();
            BigDecimal lendOutWater = NumberUtil.sub(fillInDay.getDemandWaterQuantityAfter(), fillInDay.getDemandWaterQuantuty());
            lendOutInMonth(entry.getKey(),lendOutWater);
        });
    }

    /**
     * 跨月借出方,更新旬月迭代计划
     * @param dayList
     */
    private void updateWrPlanIteratorInOtherMonthForLendOut(List<WrPlanFillinDay> dayList){
        if(CollectionUtils.isEmpty(dayList)){
            return;
        }
        String buildingId = dayList.get(0).getBuildingId();
        Map<Date, List<WrPlanFillinDay>> monthMap = dayList.stream().collect(Collectors.groupingBy(e -> DateUtils.convertStringTimeToDateExt(e.getYear() + "-"
                + StringUtils.leftPad(e.getMonth(),2,'0') + "-01")));
        //修改旬月迭代计划
        LambdaQueryWrapper<WrPlanInterTday> wrapper = new QueryWrapper().lambda();
        wrapper.eq(WrPlanInterTday::getBuildingId,buildingId);
        wrapper.eq(WrPlanInterTday::getTimeType,YEAR_PLAN_TDAY_TYPE_4);
        wrapper.in(WrPlanInterTday::getSupplyTime,monthMap.keySet());
        List<WrPlanInterTday> tDayList = wrPlanInterTdayMapper.selectList(wrapper);
        if(CollectionUtils.isNotEmpty(tDayList)){
            for (WrPlanInterTday tday : tDayList) {
                Date month = tday.getSupplyTime();
                List<WrPlanFillinDay> dayListMonth = monthMap.get(month);
                BigDecimal totalForMonth = new BigDecimal(0);
                if(CollectionUtils.isNotEmpty(dayListMonth)){
                    for (WrPlanFillinDay wrPlanFillinDay : dayListMonth) {
                        totalForMonth = NumberUtil.add(totalForMonth,wrPlanFillinDay.getDemandWaterQuantityAfter());
                    }
                    tday.setWaterQuantity(totalForMonth);
                    //TODO 暂时不设置流量
                }
            }
            wrPlanInterTdayService.saveOrUpdateBatch(tDayList);
        }
    }

    private void updateWrPlanIteratorInMonth(List<WrPlanFillinDay> dayList){
        //月内本水口近期调整，已水平衡，则不需要修改月迭代，只修改日迭代
        if(CollectionUtils.isEmpty(dayList)){
            return;
        }
        BigDecimal totalResize = new BigDecimal(0);
        Map<Date, WrPlanFillinDay> dayMap = new HashMap<>();
        for (WrPlanFillinDay day : dayList) {
            totalResize = NumberUtil.add(totalResize,NumberUtil.sub(day.getDemandWaterQuantityAfter(),day.getDemandWaterQuantuty()));
            dayMap.put(convert2Date(day), day);
        }
        //引水口id
        String buildingId = dayList.get(0).getBuildingId();
        //时间进行排序
        List<Date> dateList = dayMap.keySet().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        //当月第一天
        Date startTime = getFirstDayOfMonth();
        Date yesterday = getEndTimeOfYesterday();
        List<WrUseDetailListVO> detailList = wrBalanceAnalyzeService.getWaterUseDetail(Arrays.asList(buildingId), startTime, yesterday);
        if(detailList.size() == 0){
            return;
        }
        Date date = dateList.get(dateList.size() - 1);
        List<WrPlanInterDay> allUpdateDayList = new ArrayList<>();
        List<WrPlanInterDay> lendInDayList = getInterDayList(buildingId, dateList);
        if(CollectionUtils.isNotEmpty(lendInDayList)){
            for (WrPlanInterDay day : lendInDayList) {
                WrPlanFillinDay wrPlanFillinDay = dayMap.get(day.getSupplyTime());
                if(null != wrPlanFillinDay){
                    day.setWaterQuantity(wrPlanFillinDay.getDemandWaterQuantityAfter());
                    day.setWaterFlow(NumberUtil.div(day.getWaterQuantity(),8.64,4));
                    allUpdateDayList.add(day);
                }
            }
        }
        WrUseDetailListVO wrUseDetailListVO = detailList.get(0);
        //结余水量
        BigDecimal totalDifference = nonNullOfBigDecimal(wrUseDetailListVO.getTotalDifference());
        //TODO 结余水量小于0不考虑，如果确实存在实引超过计划，应该通过通知去提示站里调整。流量调整只影响存在结余的情况或者后续剩余指标。
        //将流量调小，则只将当天之后的日计划调大
        //流量调大,如果结余小于等于0,则直接从后续计划进行均摊扣除
        if(totalResize.doubleValue() < 0 || totalDifference.doubleValue() <= 0){
            shareEqualForRemainDaysInMonth(totalResize, buildingId, date, allUpdateDayList);
            return;
        }
        //结余及剩余月指标调整
        List<WrUseDetailVO> voList = wrUseDetailListVO.getVoList();
        Map<Date,WrUseDetailVO> surplusMap = new HashMap<>();
        for (WrUseDetailVO detailVO : voList) {
            BigDecimal diff = NumberUtil.sub(totalResize, nonNullOfBigDecimal(detailVO.getDifference()));
            //还需要继续扣减
            if(diff.doubleValue() > 0){
                detailVO.setPlanAfter(detailVO.getRealUse());
                surplusMap.put(detailVO.getEveryDay(),detailVO);
                totalResize = diff;
            }else{
                detailVO.setPlanAfter(NumberUtil.sub(detailVO.getPlan(),totalResize));
                surplusMap.put(detailVO.getEveryDay(),detailVO);
                break;
            }
        }
        //结余调整部分
        if(surplusMap.size() > 0){
            List<WrPlanInterDay> surplusDayList = getInterDayList(buildingId, new ArrayList<>(surplusMap.keySet()));
            if(CollectionUtils.isNotEmpty(surplusDayList)){
                for (WrPlanInterDay day : surplusDayList) {
                    WrUseDetailVO detailVO = surplusMap.get(day.getSupplyTime());
                    day.setWaterQuantity(detailVO.getPlanAfter());
                    day.setWaterFlow(NumberUtil.div(day.getWaterQuantity(),8.64,4));
                    allUpdateDayList.add(day);
                }
            }
        }
        //结余足够借调
        if(totalResize.doubleValue() <= 0){
            if(allUpdateDayList.size() > 0){
                wrPlanInterDayService.saveOrUpdateBatch(allUpdateDayList);
            }
            return;
        }
        shareEqualForRemainDaysInMonth(totalResize,buildingId,date,allUpdateDayList);
    }

    private void shareEqualForRemainDaysInMonth(BigDecimal totalResize, String buildingId, Date resizeEndDate, List<WrPlanInterDay> allUpdateDayList) {
        Date endTime = DateUtil.endOfMonth(resizeEndDate);
        Date startTime = org.apache.commons.lang3.time.DateUtils.addDays(resizeEndDate,1);
        List<WrPlanInterDay> interDayList = wrRecentPlanAdjustService.getWrPlanInterDays(Arrays.asList(buildingId), startTime, endTime);
        if(CollectionUtils.isNotEmpty(interDayList)){
            BigDecimal avg = NumberUtil.div(totalResize,interDayList.size(),4);
            for (WrPlanInterDay day : interDayList) {
                day.setWaterQuantity(NumberUtil.sub(day.getWaterQuantity(),avg));
                day.setWaterFlow(NumberUtil.div(day.getWaterQuantity(),8.64,4));
                allUpdateDayList.add(day);
            }
        }
        if(allUpdateDayList.size() > 0){
            wrPlanInterDayService.saveOrUpdateBatch(allUpdateDayList);
        }
    }

    @Override
    public List<WrPlanInterDay> getInterDayList(String buildingId, List<Date> dateList){
        LambdaQueryWrapper<WrPlanInterDay> dayMapper = new QueryWrapper().lambda();
        //引水口id
        dayMapper.eq(WrPlanInterDay::getBuildingId, buildingId);
        if(CollectionUtils.isNotEmpty(dateList)){
            //时间
            dayMapper.in(WrPlanInterDay::getSupplyTime, dateList);
        }
        return wrPlanInterDayMapper.selectList(dayMapper);
    }

    private Date convert2Date(WrPlanFillinDay day){
        String dayStr = StringUtils.join(new String[]{day.getYear(),
                StringUtils.leftPad(day.getMonth(), 2, '0'),
                StringUtils.leftPad(day.getDay(), 2, '0')}, '-');
        return DateUtils.convertStringTimeToDateExt(dayStr);
    }

    private void generateInstructions(String operator ,WrPlanTask wrPlanTask , List<WrPlanFillinDay> dayList) {
        String buildingId = dayList.get(0).getBuildingId();
        Map<String, WrBuildingAndDiversion> diversionMap = waterBuildingManagerService.getBuildingMapByIds(Arrays.asList(buildingId));
        if(MapUtils.isEmpty(diversionMap)){
            return;
        }
        //将当前水口结束时间晚于当前时间的指令置为无效
        Date now = DateUtil.date();
        LambdaQueryWrapper<WrDispatchInstruction> queryWrapper = new QueryWrapper().lambda();
        queryWrapper.eq(WrDispatchInstruction::getActiveFlag,1);
        queryWrapper.eq(WrDispatchInstruction::getBuildingId,buildingId);
        queryWrapper.gt(WrDispatchInstruction::getEndTime,now);
        //使用的是逻辑删除
        wrDispatchInstructionMapper.delete(queryWrapper);

        WrBuildingAndDiversion diversion = diversionMap.get(buildingId);
        //生成调度指令管理表信息
        String managerId = saveWrManager(operator,diversion,wrPlanTask,dayList.get(0));
        //生成执行指令
        WrDispatchInstruction instruction = new WrDispatchInstruction();
        instruction.setId(IDGenerator.getId());
        instruction.setCmdManagerId(managerId);
        instruction.setBuildingId(buildingId);
        instruction.setBuildingName(diversion.getBuildingName());
        instruction.setStartTime(wrPlanTask.getStartDate());
        instruction.setEndTime(wrPlanTask.getEndDate());
        instruction.setSetValue(nonNullOfBigDecimal(dayList.get(0).getDemandWaterFlowAfter()).doubleValue());
        instruction.setSourceValue(nonNullOfBigDecimal(dayList.get(0).getDemandWaterFlow()).doubleValue());
        //可远控的话,需发起辅助确认
        instruction.setStatus(PENDING_DISPATCH_ISSUE);
        instruction.setPersonId(operator);
        instruction.setCommandType(SCHEDULE_PLAN);
        //生成调度指令信息
        wrDispatchInstructionMapper.insert(instruction);
    }

    private String saveWrManager(String userName , WrBuildingAndDiversion diversion , WrPlanTask wrPlanTask , WrPlanFillinDay fillInDay) {
        WrCmdManager manager = new WrCmdManager();
        manager.setId(IDGenerator.getId());
        //调令编码
        String key = RedisOperationTypeEnum.WATER_CMD_MANAGER + DateUtil.today();
        String uniqueCode = uniqueCodeGenerateUtil.generateUniqueCode(key, RedisOperationTypeEnum.WATER_CMD_MANAGER, true, 4);
        manager.setOrderCode(uniqueCode);
        //调令名称
        String orderName = StringUtils.replaceEach(ORDER_TEMPLATE, new String[]{"{0}"}, new String[]{uniqueCode});
        manager.setOrderName(orderName);
        //近期计划生成的指令,已经审批完成.
        manager.setOrderStatus(WrCmdManagerEnum.APPROVED);
        //指令类型
        manager.setOrderType(WrCmdManagerEnum.PLAN_ORDER);
        //年份
        manager.setYear(String.valueOf(DateUtil.thisYear()));
        //管理站名称
        String mngUnitName = modelCacheService.getMngUnitName(diversion.getMngUnitId());
        //指令内容 TODO 流量空指针判断没做
        String orderContent = StringUtils.replaceEach(ORDER_CONTENT_TEMPLATE,
                new String[]{"{0}","{1}","{2}","{3}","{4}","{5}"},
                new String[]{StringUtils.defaultString(mngUnitName,""),
                        DateUtils.parseDateToString(wrPlanTask.getStartDate()),
                        DateUtils.parseDateToString(wrPlanTask.getEndDate()),
                        StringUtils.defaultString(diversion.getBuildingName(),""),
                        StringUtils.defaultString(fillInDay.getDemandWaterFlow().toPlainString(),""),
                        StringUtils.defaultString(fillInDay.getDemandWaterFlowAfter().toPlainString(),"")});
        manager.setOrderContent(orderContent);
        //指令发起人
        manager.setLaunchName(userName);
        //管理站id
        manager.setManageUnitId(diversion.getMngUnitId());
        manager.setOrderTime(DateUtil.date());
        //审批人
        manager.setApproveName("SYSTEM");
        //审批意见
        manager.setApproveContent("流程已审批,系统自动审批");
        manager.setActiveFlag(1);
        wrCmdManagerMapper.insert(manager);
        return manager.getId();
    }

    @Override
    public WrRecentPlanForMultiUseUnitVO getAllBuildingsForUseUnit(String buyerUnitId, String saleUnitId) {
        WrRecentPlanForMultiUseUnitVO vo = new WrRecentPlanForMultiUseUnitVO();
        List<WrBuildingAndDiversion> buyerList = wrPlanGenerateMonthService.getAllWaterBuildingForAppointUseUnit(buyerUnitId);
        List<WrBuildingAndDiversion> saleList = wrPlanGenerateMonthService.getAllWaterBuildingForAppointUseUnit(saleUnitId);
        if (CollectionUtils.isNotEmpty(buyerList)) {
            vo.setLendInBuildingList(convert2EntityList(buyerList, SimpleWrBuildingAndDiversion.class, null));
        }
        if (CollectionUtils.isNotEmpty(saleList)) {
            //TODO 借出方展示年剩余水量（月剩余水量+剩余月份旬月迭代总和）
            List<SimpleWrBuildingAndDiversion> versionList = convert2EntityList(saleList, SimpleWrBuildingAndDiversion.class, null);
            List<String> buildingIdList = versionList.stream().map(SimpleWrBuildingAndDiversion::getId).collect(Collectors.toList());
            Map<String, BigDecimal> remainOfYear = getRemainOfYear(buildingIdList);
            for (SimpleWrBuildingAndDiversion diversion : versionList) {
                diversion.setRemainOfYear(nonNullOfBigDecimal(remainOfYear.get(diversion.getId())));
            }
            vo.setLendOutBuildingList(versionList);
        }
        return vo;
    }

    /**
     * 获取给定用水单位月剩余水量之和
     * @param useUnitIt
     * @return
     */
    @Override
    public BigDecimal getRemainWaterOfUseUnit(String useUnitIt){
        if(StringUtils.isEmpty(useUnitIt)){
            return nonNullOfBigDecimal(null);
        }
        List<WrBuildingAndDiversion> unitList = wrPlanGenerateMonthService.getAllWaterBuildingForAppointUseUnit(useUnitIt);
        if(CollectionUtils.isEmpty(unitList)){
            return nonNullOfBigDecimal(null);
        }
        List<String> buildingIdList = unitList.stream().map(WrBuildingAndDiversion::getId).collect(Collectors.toList());
        Map<String, BigDecimal> remainOfYear = getRemainOfMonth(buildingIdList);
        if(MapUtils.isEmpty(remainOfYear)){
            return nonNullOfBigDecimal(null);
        }
        BigDecimal total = nonNullOfBigDecimal(null);
        Iterator<Map.Entry<String, BigDecimal>> iterator = remainOfYear.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, BigDecimal> entry = iterator.next();
            total = NumberUtil.add(total,entry.getValue());
        }
        return total;
    }

    /**
     * 获取给定引水口月剩余水量
     * @param buildingIdList
     * @return
     */
    @Override
    public Map<String,BigDecimal> getRemainOfMonth(List<String> buildingIdList){
        Map<String,BigDecimal> remainMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(buildingIdList)){
            //查询旬月迭代表
            LambdaQueryWrapper<WrPlanInterTday> tDayMapper = new QueryWrapper().lambda();
            if(buildingIdList.size() == 1){
                tDayMapper.eq(WrPlanInterTday::getBuildingId,buildingIdList.get(0));
            }else{
                tDayMapper.in(WrPlanInterTday::getBuildingId,buildingIdList);
            }
            //月计划
            tDayMapper.eq(WrPlanInterTday::getSupplyTime,getFirstDayOfMonth());
            tDayMapper.eq(WrPlanInterTday::getTimeType,YEAR_PLAN_TDAY_TYPE_4);
            List<WrPlanInterTday> tdayList = wrPlanInterTdayMapper.selectList(tDayMapper);
            if(CollectionUtils.isNotEmpty(tdayList)){
                Map<String, BigDecimal> totalUpToNow = getTotalUpToNow(buildingIdList);
                tdayList.stream().forEach(tday -> {
                    BigDecimal indexOfMonth = nonNullOfBigDecimal(tday.getWaterQuantity());
                    BigDecimal realUse = nonNullOfBigDecimal(totalUpToNow.get(tday.getBuildingId()));
                    BigDecimal remain = NumberUtil.sub(indexOfMonth,realUse);
                    remainMap.put(tday.getBuildingId(),remain);
                });
            }
        }
        return remainMap;
    }

    /**
     * 获取给定引水口年剩余水量（月剩余水量+底下月份旬月计划）
     * @param buildingIdList
     * @return
     */
    @Override
    public Map<String,BigDecimal> getRemainOfYear(List<String> buildingIdList){
        Map<String, BigDecimal> remainOfMonth = getRemainOfMonth(buildingIdList);
        //当年剩余月份
        List<Date> remainMonth = getRemainMonth();
        if(CollectionUtils.isEmpty(remainMonth)){
            return remainOfMonth;
        }
        //查询旬月迭代表
        LambdaQueryWrapper<WrPlanInterTday> tDayMapper = new QueryWrapper().lambda();
        tDayMapper.in(WrPlanInterTday::getBuildingId,buildingIdList);
        //月计划
        tDayMapper.in(WrPlanInterTday::getSupplyTime,remainMonth);
        tDayMapper.eq(WrPlanInterTday::getTimeType,YEAR_PLAN_TDAY_TYPE_4);
        List<WrPlanInterTday> dayList = wrPlanInterTdayMapper.selectList(tDayMapper);
        if(CollectionUtils.isEmpty(dayList)){
            return remainOfMonth;
        }
        Map<String, List<WrPlanInterTday>> dayListMap = dayList.stream().collect(Collectors.groupingBy(WrPlanInterTday::getBuildingId));
        dayListMap.entrySet().stream().forEach(entry -> {
            String buildingId = entry.getKey();
            BigDecimal total = new BigDecimal(0D);
            List<WrPlanInterTday> localDayList = entry.getValue();
            if(CollectionUtils.isNotEmpty(localDayList)){
                for (WrPlanInterTday tday : localDayList) {
                    total = NumberUtil.add(total,nonNullOfBigDecimal(tday.getWaterQuantity()));
                }
            }
            BigDecimal monthRemain = remainOfMonth.get(buildingId);
            if(null == monthRemain){
                remainOfMonth.put(buildingId,total);
            }else{
                remainOfMonth.put(buildingId,NumberUtil.add(total,monthRemain));
            }
        });
        return remainOfMonth;
    }

    /**
     * 暂时只支持一个引水口借调调整
     *
     * @param buildingId
     */
    @Override
    public List<SimpleWrBuildingVO> getAllAdaptiveBorrowBuildings(String buildingId, String waterUseId) {
        //获取引水口月指标
        if (StringUtils.isEmpty(buildingId) || StringUtils.isEmpty(waterUseId)) {
            throw new TransactionException(CodeEnum.NO_PARAM, "请传入相关id！");
        }
        //获取该引水口所在的县市师团级用水单位
        WrUseUnitManager wrUseUnitManager = wrUseUnitManagerMapper.selectById(waterUseId);
        if (null == wrUseUnitManager) {
            throw new TransactionException(CodeEnum.NO_DATA, "查询不到任何用水单位信息,id is " + waterUseId);
        }
        //获取该节点根节点
        String rootId = wrUseUnitManagerService.getRootId(wrUseUnitManager);
        //县市师团级用水单位
        String xsUseId;
        //巴州/第二师级用水单位
        if (waterUseId.equals(rootId)) {
            xsUseId = rootId;
        } else {
            //非根节点，则第二级即为县市师团级用水单位
            xsUseId = StringUtils.split(wrUseUnitManager.getPath(), "/")[1];
        }
        WrUseUnitManager xsUnitManager = wrUseUnitManagerMapper.selectById(xsUseId);
        if (null == xsUnitManager) {
            throw new TransactionException(CodeEnum.NO_DATA, "查询不到任何用水单位信息,id is " + xsUseId);
        }
        List<WrBuildingAndDiversion> allBuildings = wrPlanGenerateMonthService.getAllWaterBuildingForAppointUseUnit(xsUseId);
        //与借调方在同一个用水单位下的所有引水口（市县师团）
        if (CollectionUtils.isEmpty(allBuildings)) {
            throw new TransactionException(CodeEnum.NO_DATA, "未查询到任何引水口信息，用水单位id为：" + xsUseId);
        }
        Map<String, WrBuildingAndDiversion> versionMap = allBuildings.stream().collect(Collectors.toMap(WrBuildingAndDiversion::getId, e -> e));
        List<String> buildingIdList = new ArrayList<>(versionMap.keySet());
        //过滤掉自身引水口
        buildingIdList.remove(buildingId);
        //截至到当前实引水量
        List<SimpleWrBuildingVO> voList = new ArrayList<>();
        Map<String, BigDecimal> remainOfMonth = getRemainOfMonth(buildingIdList);
        remainOfMonth.entrySet().stream().forEach(entry -> {
            BigDecimal remain = entry.getValue();
            if(remain.doubleValue() > 0){
                String buildId = entry.getKey();
                WrBuildingAndDiversion wrBuildingAndDiversion = versionMap.get(buildId);
                SimpleWrBuildingVO vo = new SimpleWrBuildingVO();
                BeanUtils.copyProperties(wrBuildingAndDiversion, vo);
                vo.setBuildingId(wrBuildingAndDiversion.getId());
                vo.setRemainOfMonth(remain);
                vo.setXsUnitId(xsUseId);
                vo.setXsUnitName(xsUnitManager.getUnitName());
                voList.add(vo);
            }
        });
        return voList;
    }

    /**
     * 截至到当前时间日迭代计划累计
     * @param buildingIdList
     * @return
     */
    /*private Map<String, Double> getAccuDayPlan(List<String> buildingIdList) {
        Map<String, Double> result = new HashMap<>();
        if (buildingIdList.size() == 0) {
            return result;
        }
        LambdaQueryWrapper<WrPlanInterDay> dayMapper = new new QueryWrapper().lambda();
        //引水口id
        dayMapper.in(WrPlanInterDay::getBuildingId, buildingIdList);
        //时间
        dayMapper.notBetween(WrPlanInterDay::getSupplyTime, getFirstDayOfMonth(), DateUtil.beginOfDay(DateUtil.date()));
        List<WrPlanInterDay> dayPlanList = wrPlanInterDayMapper.selectList(dayMapper);
        if (CollectionUtils.isEmpty(dayPlanList)) {
            return result;
        }
        Map<String, List<WrPlanInterDay>> dayGroupMap = dayPlanList.stream().collect(Collectors.groupingBy(WrPlanInterDay::getBuildingId));
        dayGroupMap.entrySet().stream().forEach(entry -> {
            double total = 0D;
            List<WrPlanInterDay> dayList = entry.getValue();
            if (CollectionUtils.isNotEmpty(dayList)) {
                for (WrPlanInterDay day : dayList) {
                    double waterQuantity = day.getWaterQuantity() == null ? 0D : day.getWaterQuantity().doubleValue();
                    total += waterQuantity;
                }
            }
            result.put(entry.getKey(), total);
        });
        return result;
    }*/

    /**
     * @title adjustDetail
     * @description 近期用水计划调整详情
     * @author bigb
     * @param: buildingId
     * @param: lendWater
     * @param: adjustType
     * @param: startTime
     * @param: endTime
     * @updateTime 2021/9/23 14:26
     * @throws
     */
    @Override
    public List<LendOutDetailVO> adjustDetail(String buildingId , BigDecimal lendWater , String adjustType , Long startTime , Long endTime){
        //存在结余，则逐日扣除
        List<LendOutVO> voList = getRealUseWater(Arrays.asList(buildingId));
        if(CollectionUtils.isEmpty(voList)){
            return null;
        }
        LendOutVO vo = voList.get(0);
        List<LendOutDetailVO> detailVOList = vo.getDetailVOList();
        if(CollectionUtils.isEmpty(detailVOList)){
            return null;
        }
        List<LendOutDetailVO> lendOutVOList = vo.getDetailVOList();
        BigDecimal lendOfUse = vo.getLendOutWater();
        BigDecimal remain = lendWater;
        //1、月截至当前结余扣除
        for (LendOutDetailVO detailVO : detailVOList) {
            //可借出的水量
            BigDecimal lend = detailVO.getLend();
            BigDecimal sub = NumberUtil.sub(remain, lend);
            if(sub.doubleValue() >= 0){
                detailVO.setAfter(detailVO.getReal());
                detailVO.setLend(new BigDecimal("0.0"));
                lendOutVOList.add(detailVO);
            }else{
                lend = NumberUtil.sub(lend, remain);
                detailVO.setLend(lend);
                detailVO.setAfter(NumberUtil.add(detailVO.getReal(),detailVO.getLend()));
                lendOutVOList.add(detailVO);
                break;
            }
        }
        BigDecimal lendOfIndex = NumberUtil.sub(lendWater,lendOfUse);
        //本月结余够扣除，不需要再借后续指标，直接返回
        if(lendOfIndex.doubleValue() <= 0){
            return lendOutVOList;
        }
        //TODO 均摊借调部分，是否超出每日计划？后续再做超出逻辑处理
        //借出指标开始时间
        Date today = DateUtil.beginOfDay(DateUtil.date());
        Date endQueryTime = DateUtil.beginOfMonth(today);
        Date startQueryTime = today;
        //本水口调整
        if(BUILDING_IN_MONTH.equals(adjustType)){
            Date endTimeDate = DateUtils.convertTimeToDate(endTime);
            //当前已经是最后一天,本水口本月无可借调指标
            if(today.equals(endQueryTime)){
                throw new TransactionException(CodeEnum.OPERAT_ERROR, "本水口本月无用水指标可供使用,请选择用水单位内部或跨月借调！");
            }else if(endQueryTime.equals(endTimeDate)){
                throw new TransactionException(CodeEnum.OPERAT_ERROR, "本水口本月调整,调整结束时间请勿选择本月最后一天！");
            }
            startQueryTime = org.apache.commons.lang3.time.DateUtils.addDays(endTimeDate,1);
        }else if(OTHER_BUILDING_IN_MONTH.equals(adjustType)
                || BUILDING_IN_OTHER_WATER_UNIT.equals(adjustType)){
            //跨水口借调,当天指标不可借调,从第二天开始借
            startQueryTime = org.apache.commons.lang3.time.DateUtils.addDays(today,1);
        }
        //2、月剩余指标扣除
        List<WrPlanInterDay> dayPlanList = getWrPlanInterDays(Arrays.asList(buildingId),startQueryTime, endQueryTime);
        if(CollectionUtils.isEmpty(dayPlanList)){
            throw new TransactionException(CodeEnum.NO_DATA, "未查询到任何日迭代数据，引水口id为：" + buildingId);
        }
        BigDecimal remainForPlan = new BigDecimal("0");
        for (WrPlanInterDay day : dayPlanList) {
            remainForPlan = NumberUtil.add(remainForPlan,day.getWaterQuantity());
        }
        BigDecimal sub = NumberUtil.sub(remainForPlan,lendOfIndex);
        //月剩余指标足够借调，则均摊
        if(sub.doubleValue() >= 0){
            //每天均摊
            BigDecimal share = NumberUtil.div(lendOfIndex,dayPlanList.size(),4);
            for (WrPlanInterDay day : dayPlanList) {
                LendOutDetailVO detailVO = new LendOutDetailVO();
                detailVO.setPlan(day.getWaterQuantity());
                detailVO.setAfter(NumberUtil.sub(day.getWaterQuantity(),share));
                detailVO.setDay(day.getSupplyTime().getTime());
                lendOutVOList.add(detailVO);
            }
            return lendOutVOList;
        }
        if(!BUILDING_IN_OTHER_WATER_UNIT.equals(adjustType)){
            throw new TransactionException(CodeEnum.OPERAT_ERROR, "本水口月剩余指标不够借调！");
        }
        //超年的话,如果本月不够借调,则从后续月份借调
        //获取剩余月份，均摊到每个月
        List<Date> remainMonth = getRemainMonth();
        if(CollectionUtils.isEmpty(remainMonth)){
            throw new TransactionException(CodeEnum.OPERAT_ERROR, "本年度没有后续月计划指标可供借调！");
        }
        BigDecimal subOfMonth = NumberUtil.div(sub,remainMonth.size(),4);
        List<WrPlanInterTday> tdayPlans = getTdayPlans(buildingId, remainMonth);
        if(CollectionUtils.isEmpty(tdayPlans)){
            throw new TransactionException(CodeEnum.NO_DATA, "未查询到任何旬月计划，引水口id为：" + buildingId);
        }
        for (WrPlanInterTday tdayPlan : tdayPlans) {
            LendOutDetailVO detailVO = new LendOutDetailVO();
            detailVO.setPlan(tdayPlan.getWaterQuantity());
            detailVO.setAfter(NumberUtil.sub(tdayPlan.getWaterQuantity(),subOfMonth));
            detailVO.setDay(tdayPlan.getSupplyTime().getTime());
            lendOutVOList.add(detailVO);
        }
        return lendOutVOList;
    }

    private List<WrPlanInterTday> getTdayPlans(String buildingId, List<Date> remainMonth) {
        LambdaQueryWrapper<WrPlanInterTday> dayMapper = new QueryWrapper().lambda();
        //引水口id
        dayMapper.eq(WrPlanInterTday::getBuildingId, buildingId);
        //时间
        dayMapper.in(WrPlanInterTday::getSupplyTime, remainMonth);
        return wrPlanInterTdayMapper.selectList(dayMapper);
    }

    /**
     * 本月截至到前一天24点累计实引水量
     *
     * @param buildingIdList
     */
    private Map<String, BigDecimal> getTotalUpToNow(List<String> buildingIdList) {
        Map<String, BigDecimal> result = new HashMap<>();
        //当月第一天
        Date startTime = getFirstDayOfMonth();
        List<DataBuildingDto> dataList = dataService.getSpecialDataRunDataType(buildingIdList,
                Arrays.asList(PointTypeEnum.WATER_VOLUME.getId()), startTime.getTime(), getEndTimeOfYesterday().getTime(),
                Param.ValType.Special_V, Param.RunDataType.RUN_DAY, Param.CalcType.CALC_SUM);
        if (CollectionUtils.isEmpty(dataList)) {
            return result;
        }
        dataList.stream().forEach(dataBuilding -> {
            BigDecimal total = new BigDecimal(0D);
            List<DataPointDto> dataPointList = dataBuilding.getDataPointDtos();
            if (CollectionUtils.isNotEmpty(dataPointList)) {
                //只查一种类型，不做循环了
                /*Map<String, List<DataPointDto>> pointDtoMap = dataPointList.stream().collect(Collectors.groupingBy(DataPointDto::getPointType));
                List<DataPointDto> pointList = pointDtoMap.get(PointTypeEnum.WATER_VOLUME.getId());*/
                for (DataPointDto dataPointDto : dataPointList) {
                    total = NumberUtil.add(total,number(dataPointDto.getV()));
                }
            }
            result.put(dataBuilding.getId(), total);
        });
        return result;
    }

    /**
     * 实引和计划对比
     * @param buildingIdList
     */
    private List<LendOutVO> getRealUseWater(List<String> buildingIdList) {
        //当月第一天
        Date startTime = getFirstDayOfMonth();
        Date endTime = getEndTimeOfYesterday();
        List<DataBuildingDto> dataList = dataService.getSpecialDataRunDataType(buildingIdList,
                Arrays.asList(PointTypeEnum.WATER_VOLUME.getId()), startTime.getTime(), endTime.getTime(),
                Param.ValType.Special_V, Param.RunDataType.RUN_DAY, null);
        List<WrPlanInterDay> dayPlanList = getWrPlanInterDays(buildingIdList, startTime, endTime);
        if(CollectionUtils.isEmpty(dayPlanList) || CollectionUtils.isEmpty(dataList)){
            return null;
        }
        Map<String, List<WrPlanInterDay>> dayMap = dayPlanList.stream().collect(Collectors.groupingBy(WrPlanInterDay::getBuildingId));
        Map<String, List<DataPointDto>> buildingMap = new HashMap<>();
        for (DataBuildingDto dataBuilding : dataList) {
            buildingMap.put(dataBuilding.getId(),dataBuilding.getDataPointDtos());
        }
        List<LendOutVO> voList = new ArrayList<>();
        dayMap.entrySet().forEach(entry -> {
            LendOutVO vo = new LendOutVO();
            String buildingId = entry.getKey();
            vo.setBuildingId(buildingId);
            //迭代计划
            List<WrPlanInterDay> dayList = entry.getValue();
            //实引水量
            List<DataPointDto> dtoList = buildingMap.get(buildingId);
            Map<Long,Double> dtoMap;
            if(CollectionUtils.isNotEmpty(dtoList)){
                dtoMap = dtoList.stream().collect(Collectors.toMap(DataPointDto::getTime, e -> e.getV()));
            }else{
                dtoMap = new HashMap<>();
            }
            if(CollectionUtils.isNotEmpty(dayList)){
                BigDecimal lend = new BigDecimal("0");
                List<LendOutDetailVO> detailVOList = new ArrayList<>();
                for (WrPlanInterDay day : dayList) {
                    LendOutDetailVO detailVO = new LendOutDetailVO();
                    detailVO.setDayDate(day.getSupplyTime());
                    detailVO.setDay(day.getSupplyTime().getTime());
                    detailVO.setPlan(day.getWaterQuantity());
                    detailVO.setReal(number(dtoMap.get(day.getSupplyTime().getTime())));
                    BigDecimal diff = NumberUtil.sub(detailVO.getPlan(), detailVO.getReal());
                    detailVO.setLend(diff);
                    //可借调总水量
                    lend = lend.add(detailVO.getLend());
                    //只有结余大于0，当天水量才可借出
                    if(diff.doubleValue() > 0){
                        detailVOList.add(detailVO);
                    }
                }
                vo.setDetailVOList(detailVOList);
                vo.setLendOutWater(lend);
            }
            voList.add(vo);
        });
        return voList;
    }

    @Override
    public List<WrPlanInterDay> getWrPlanInterDays(List<String> buildingIdList, Date startTime, Date endTime) {
        LambdaQueryWrapper<WrPlanInterDay> dayMapper = new QueryWrapper().lambda();
        //引水口id
        if(buildingIdList.size() == 1){
            dayMapper.eq(WrPlanInterDay::getBuildingId, buildingIdList.get(0));
        }else{
            dayMapper.in(WrPlanInterDay::getBuildingId, buildingIdList);
        }
        if(null != startTime && null != endTime){
            //时间
            dayMapper.between(WrPlanInterDay::getSupplyTime, startTime, endTime);
        }
        List<WrPlanInterDay> dayPlanList = wrPlanInterDayMapper.selectList(dayMapper);
        return dayPlanList;
    }
}
