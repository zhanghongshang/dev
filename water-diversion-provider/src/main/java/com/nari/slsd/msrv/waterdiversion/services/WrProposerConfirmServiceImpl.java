package com.nari.slsd.msrv.waterdiversion.services;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nari.slsd.msrv.common.exception.TransactionException;
import com.nari.slsd.msrv.common.ext.enums.CodeEnum;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWaterBuildingManagerService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrProposerConfirmService;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrPlanFillinDayMapper;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrPlanTaskMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrBuildingAndDiversion;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrPlanFillinDay;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrPlanTask;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrPlanFillinDayVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrRecentPlanDetailForMultiBuildingVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrRecentPlanDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.nari.slsd.msrv.waterdiversion.commons.RecentPlanEnum.*;
import static com.nari.slsd.msrv.waterdiversion.utils.CommonUtil.getDateStrArrBetweenSpecialDate;
import static com.nari.slsd.msrv.waterdiversion.utils.CommonUtil.nonNullOfBigDecimal;

/**
 * @author bigb
 * @title
 * @description 近期计划调整，申请人确认
 * @updateTime 2021/9/14 13:54
 * @throws
 */
@Service
@Slf4j
public class WrProposerConfirmServiceImpl implements IWrProposerConfirmService {

    private static final String BUILDING_NAME = "引水口名称";
    private static final String REMAIN_WATER = "剩余水量";
    private static final String LEND_OUT_WATER = "借出水量";
    private static final List<String> HEAD_FIELD_LIST = Arrays.asList(BUILDING_NAME,REMAIN_WATER,LEND_OUT_WATER);

    @Autowired
    private IWaterBuildingManagerService waterBuildingManagerService;

    @Autowired
    private WrPlanFillinDayMapper wrPlanFillinDayMapper;

    @Autowired
    private WrPlanTaskMapper wrPlanTaskMapper;

    /**
     * @throws
     * @title showBackLog
     * @description 近期用水计划任务详情展示
     * @author bigb
     * @param: taskId
     * @param: flag
     * @updateTime 2021/9/14 22:35
     */
    @Override
    public WrRecentPlanDetailVO showBackLog(String taskId, String taskDefinitionKey,String batchState) {
        //查询任务
        WrPlanTask wrPlanTask = wrPlanTaskMapper.selectById(taskId);
        if (null == wrPlanTask) {
            throw new TransactionException(CodeEnum.NO_DATA, "未查询到任何任务信息！");
        }
        //近期调整类别
        String subType = wrPlanTask.getSubType();
        WrRecentPlanDetailVO vo = null;
        //月内本水口
        if (BUILDING_IN_MONTH.equals(subType)) {
            vo = buildRecentPlanRespDataInMonth(wrPlanTask);
        } else if (OTHER_BUILDING_IN_MONTH.equals(subType)) {
            //月内跨水口
            vo = buildRecentPlanRespDataInMonthWithMultiBuilding(wrPlanTask);
        } else if (BUILDING_IN_OTHER_MONTH.equals(subType)) {
            //跨月
            vo = buildRecentPlanRespDataWithMultiMonth(wrPlanTask);
        } else if (BUILDING_IN_OTHER_WATER_UNIT.equals(subType)) {
            //跨用水单位
            vo = buildRecentPlanRespDataWithMultiUnit(wrPlanTask);
        }
        if (null != vo) {
            vo.setProcessId(wrPlanTask.getWaterPlanFillIn());
            vo.setNodeFlag(taskDefinitionKey);
            vo.setSubType(subType);
            vo.setBatchState(batchState);
        }
        return vo;
    }

    /**
     * 跨用水单位审批详情展示
     *
     * @param task
     * @return
     */
    private WrRecentPlanDetailVO buildRecentPlanRespDataWithMultiUnit(WrPlanTask task) {
        List<WrPlanFillinDay> dayList = getWrPlanFillInDays(task);
        if (CollectionUtils.isEmpty(dayList)) {
            throw new TransactionException(CodeEnum.NO_DATA, "未查询到任何近期计划信息,task id is " + task.getId());
        }
        WrRecentPlanDetailVO detailVO = new WrRecentPlanDetailVO();
        //根据引水口进行分类,支持多水口
        Map<String, List<WrPlanFillinDay>> lendMap = dayList.stream().collect(Collectors.groupingBy(WrPlanFillinDay::getLendType));
        //借调方
        setWrPlanFillInDayForLendIn(task, detailVO, lendMap.get(LEND_IN),BUILDING_IN_OTHER_WATER_UNIT);
        //借出方
        setWrPlanFillInDayForLendOut(detailVO, lendMap.get(LEND_OUT),BUILDING_IN_OTHER_WATER_UNIT);
        detailVO.setProcessId(task.getWaterPlanFillIn());
        return detailVO;
    }

    /**
     * 跨月审批详情展示
     *
     * @param task
     * @return
     */
    private WrRecentPlanDetailVO buildRecentPlanRespDataWithMultiMonth(WrPlanTask task) {
        List<WrPlanFillinDay> dayList = getWrPlanFillInDays(task);
        if (CollectionUtils.isEmpty(dayList)) {
            throw new TransactionException(CodeEnum.NO_DATA, "未查询到任何近期计划信息,task id is " + task.getId());
        }
        WrRecentPlanDetailVO detailVO = new WrRecentPlanDetailVO();
        //根据引水口进行分类,支持多水口
        Map<String, List<WrPlanFillinDay>> lendMap = dayList.stream().collect(Collectors.groupingBy(WrPlanFillinDay::getLendType));
        //借调方
        setWrPlanFillInDayForLendIn(task, detailVO, lendMap.get(LEND_IN),BUILDING_IN_OTHER_MONTH);
        //借出方
        setWrPlanFillInDayForLendOut(detailVO, lendMap.get(LEND_OUT),BUILDING_IN_OTHER_MONTH);
        detailVO.setProcessId(task.getWaterPlanFillIn());
        return detailVO;
    }

    /**
     * 月内跨水口审批详情展示
     *
     * @param task
     * @return
     */
    private WrRecentPlanDetailVO buildRecentPlanRespDataInMonthWithMultiBuilding(WrPlanTask task) {
        List<WrPlanFillinDay> dayList = getWrPlanFillInDays(task);
        if (CollectionUtils.isEmpty(dayList)) {
            throw new TransactionException(CodeEnum.NO_DATA, "未查询到任何近期计划信息,task id is " + task.getId());
        }
        WrRecentPlanDetailVO detailVO = new WrRecentPlanDetailVO();
        //根据引水口进行分类,支持多水口
        Map<String, List<WrPlanFillinDay>> lendMap = dayList.stream().collect(Collectors.groupingBy(WrPlanFillinDay::getLendType));
        //借调方
        setWrPlanFillInDayForLendIn(task, detailVO, lendMap.get(LEND_IN),OTHER_BUILDING_IN_MONTH);
        //借出方
        setWrPlanFillInDayForLendOut(detailVO, lendMap.get(LEND_OUT),OTHER_BUILDING_IN_MONTH);
        detailVO.setProcessId(task.getWaterPlanFillIn());
        return detailVO;
    }

    /**
     * 月内本水口审批数据展示
     *
     * @param task
     * @return
     */
    private WrRecentPlanDetailVO buildRecentPlanRespDataInMonth(WrPlanTask task) {
        WrRecentPlanDetailVO detailVO = new WrRecentPlanDetailVO();
        List<WrPlanFillinDay> dayList = getWrPlanFillInDays(task);
        if (CollectionUtils.isEmpty(dayList)) {
            throw new TransactionException(CodeEnum.NO_DATA, "未查询到任何近期计划信息,task id is " + task.getId());
        }
        setWrPlanFillInDayForLendIn(task, detailVO, dayList,BUILDING_IN_MONTH);
        return detailVO;
    }

    /**
     * 借调方详情展示
     *
     * @param task
     * @param detailVO
     * @param dayList
     */
    private void setWrPlanFillInDayForLendIn(WrPlanTask task, WrRecentPlanDetailVO detailVO, List<WrPlanFillinDay> dayList , String planType) {
        //根据引水口进行分类,支持多水口
        Map<String, List<WrPlanFillinDay>> buildingGroupMap = dayList.stream().collect(Collectors.groupingBy(WrPlanFillinDay::getBuildingId));
        //获取引水口相关信息
        Set<String> buildingIdList = buildingGroupMap.keySet();
        List<WrBuildingAndDiversion> versionList = waterBuildingManagerService.getWrBuildingAndDiversionList(new ArrayList<>(buildingIdList));
        if (CollectionUtils.isEmpty(versionList)) {
            throw new TransactionException(CodeEnum.NO_DATA, "未查询到任何引水口相关信息!");
        }
        Map<String, WrBuildingAndDiversion> buildingMap = versionList.stream().collect(Collectors.toMap(WrBuildingAndDiversion::getId, e -> e));
        //开始和结束时间，生成表头
        List<String> headList = getDateStrArrBetweenSpecialDate(task.getStartDate(), task.getEndDate());
        Map<String, List<BigDecimal>> headValMap = new TreeMap<>();
        headList.forEach(head -> {
            headValMap.put(head,new ArrayList<>());
        });

        if(BUILDING_IN_OTHER_WATER_UNIT.equals(planType)){
            List<WrRecentPlanDetailForMultiBuildingVO> voList = new ArrayList<>();
            //返回信息
            buildingGroupMap.entrySet().forEach(entry -> {
                String buildingId = entry.getKey();
                List<WrPlanFillinDayVO> wrPlanFillInDayVOList = new ArrayList<>();
                singleBuildingProcess(buildingGroupMap, buildingMap, wrPlanFillInDayVOList, entry, LEND_IN , headValMap,planType);
                WrRecentPlanDetailForMultiBuildingVO vo = new WrRecentPlanDetailForMultiBuildingVO();
                vo.setBuildingId(buildingId);
                vo.setLendInHeadList(new ArrayList<>(headValMap.keySet()));
                vo.setLendInVOList(wrPlanFillInDayVOList);
                vo.setBuildingName(buildingMap.get(buildingId).getBuildingName());
                voList.add(vo);
            });
            detailVO.setLendInVOListForMultiUseUnit(voList);
        }else{
            //返回信息
            List<WrPlanFillinDayVO> wrPlanFillInDayVOList = new ArrayList<>();
            buildingGroupMap.entrySet().forEach(entry -> {
                singleBuildingProcess(buildingGroupMap, buildingMap, wrPlanFillInDayVOList, entry, LEND_IN , headValMap,planType);
            });
            detailVO.setLendInHeadList(headList);
            detailVO.setLendInVOList(wrPlanFillInDayVOList);
        }
    }

    /**
     * 借出方详情展示
     *
     * @param detailVO
     * @param dayList
     */
    private void setWrPlanFillInDayForLendOut(WrRecentPlanDetailVO detailVO, List<WrPlanFillinDay> dayList , String planType) {
        //根据引水口进行分类,支持多水口
        Map<String, List<WrPlanFillinDay>> buildingGroupMap = dayList.stream().collect(Collectors.groupingBy(WrPlanFillinDay::getBuildingId));
        //获取引水口相关信息
        Set<String> buildingIdList = buildingGroupMap.keySet();
        List<WrBuildingAndDiversion> versionList = waterBuildingManagerService.getWrBuildingAndDiversionList(new ArrayList<>(buildingIdList));
        if (CollectionUtils.isEmpty(versionList)) {
            throw new TransactionException(CodeEnum.NO_DATA, "未查询到任何引水口相关信息!");
        }
        Map<String, WrBuildingAndDiversion> buildingMap = versionList.stream().collect(Collectors.toMap(WrBuildingAndDiversion::getId, e -> e));
        //返回信息
        List<WrPlanFillinDayVO> wrPlanFillInDayVOList = new ArrayList<>();
        //按照引水口分组
        buildingGroupMap.entrySet().forEach(entry -> {
            Map<String, List<BigDecimal>> headValMap = new TreeMap<>();
            if(BUILDING_IN_OTHER_MONTH.equals(planType)){
                List<WrPlanFillinDay> fillDayList = entry.getValue();
                //按照时间分组
                Map<String, List<WrPlanFillinDay>> groupByDate = groupForRecentPlan(fillDayList,planType);
                if(null != groupByDate){
                    groupByDate.keySet().forEach(key -> headValMap.put(key, new ArrayList<>()));
                    if (null == detailVO.getLendOutHeadList()) {
                        setLendOutHeadList(detailVO, headValMap , planType);
                    }
                }
            }else {
                HEAD_FIELD_LIST.forEach(field -> {
                    headValMap.put(field, new ArrayList<>());
                });
                if(null == detailVO.getLendOutHeadList()){
                    setLendOutHeadList(detailVO, headValMap , planType);
                }
            }
            singleBuildingProcess(buildingGroupMap, buildingMap, wrPlanFillInDayVOList, entry, LEND_OUT, headValMap,planType);
        });
        detailVO.setLendOutVOList(wrPlanFillInDayVOList);
    }

    private void setLendOutHeadList(WrRecentPlanDetailVO detailVO, Map<String, List<BigDecimal>> headValMap , String planType) {
        Set<String> headListSet = headValMap.keySet();
        if(OTHER_BUILDING_IN_MONTH.equals(planType) || BUILDING_IN_OTHER_WATER_UNIT.equals(planType)){
            detailVO.setLendOutHeadList(new ArrayList<>(headListSet));
        }else if(BUILDING_IN_OTHER_MONTH.equals(planType)){
            Set<String> headSetLocal = new HashSet<>();
            headListSet.forEach(head -> {
                Integer everyHead = Integer.parseInt(StringUtils.split(head, "-")[1]);
                headSetLocal.add(everyHead + "月");
                detailVO.setLendOutHeadList(new ArrayList<>(headSetLocal));
            });
        }
    }

    private Map<String, List<WrPlanFillinDay>> groupForRecentPlan(List<WrPlanFillinDay> fillDayList , String planType){
        Map<String, List<WrPlanFillinDay>> groupByDate = null;
        if(OTHER_BUILDING_IN_MONTH.equals(planType) || BUILDING_IN_OTHER_WATER_UNIT.equals(planType)){
            groupByDate = fillDayList.stream().collect(Collectors.groupingBy(e ->
                    StringUtils.join(new String[]{e.getYear(),
                            StringUtils.leftPad(e.getMonth(), 2, "0"),
                            StringUtils.leftPad(e.getDay(), 2, "0")}, "-")));
        }else if(BUILDING_IN_OTHER_MONTH.equals(planType)){
            groupByDate = fillDayList.stream().collect(Collectors.groupingBy(e ->
                    StringUtils.join(new String[]{e.getYear(),
                            StringUtils.leftPad(e.getMonth(), 2, "0"),
                            StringUtils.leftPad(e.getTday(), 2, "0")}, "-")));
        }
        return groupByDate;
    }

    private void singleBuildingProcess(Map<String, List<WrPlanFillinDay>> buildingGroupMap, Map<String, WrBuildingAndDiversion> buildingMap,
                                       List<WrPlanFillinDayVO> wrPlanFillInDayVOList, Map.Entry<String, List<WrPlanFillinDay>> entry,
                                       String lendType, Map<String, List<BigDecimal>> headValMap , String planType) {
        String buildingId = entry.getKey();
        List<WrPlanFillinDay> fillDayList = buildingGroupMap.get(buildingId);
        if(LEND_IN.equals(lendType) && BUILDING_IN_OTHER_WATER_UNIT.equals(planType)){
            Map<String, WrPlanFillinDay> dayMap = fillDayList.stream().collect(Collectors.toMap(e -> (e.getYear() + "-" +
                    StringUtils.leftPad(e.getMonth(),2,'0') + "-" +
                    StringUtils.leftPad(e.getDay(),2,'0')), e -> e));
            dayMap.keySet().forEach(e -> headValMap.put(e,new ArrayList<>()));
        }
        for (WrPlanFillinDay wrPlanFillinDay : fillDayList) {
            String dayLevel = wrPlanFillinDay.getDay();
            if(LEND_OUT.equals(lendType) && BUILDING_IN_OTHER_MONTH.equals(planType)){
                dayLevel = wrPlanFillinDay.getTday();
            }
            String dateStr = StringUtils.join(new String[]{wrPlanFillinDay.getYear(),
                    StringUtils.leftPad(wrPlanFillinDay.getMonth(), 2, "0"),
                    StringUtils.leftPad(dayLevel, 2, "0")}, "-");
            if(LEND_IN.equals(lendType)){
                //跨用水单位,借调方使用水量,其他使用流量
                if(BUILDING_IN_OTHER_WATER_UNIT.equals(planType)){
                    headValMap.get(dateStr).add(wrPlanFillinDay.getDemandWaterQuantuty());
                    headValMap.get(dateStr).add(wrPlanFillinDay.getDemandWaterQuantityAfter());
                }else{
                    headValMap.get(dateStr).add(wrPlanFillinDay.getDemandWaterFlow());
                    headValMap.get(dateStr).add(wrPlanFillinDay.getDemandWaterFlowAfter());
                }
            }else{
                if(BUILDING_IN_OTHER_MONTH.equals(planType)){
                    headValMap.get(dateStr).add(wrPlanFillinDay.getDemandWaterQuantuty());
                    headValMap.get(dateStr).add(wrPlanFillinDay.getDemandWaterQuantityAfter());
                }else{
                    headValMap.get(REMAIN_WATER).add(wrPlanFillinDay.getDemandWaterQuantuty());
                    headValMap.get(LEND_OUT_WATER).add(NumberUtil.sub(wrPlanFillinDay.getDemandWaterQuantuty(),
                            wrPlanFillinDay.getDemandWaterQuantityAfter()));
                }
            }
        }

        String buildingName = buildingMap.get(buildingId) == null ? "" : buildingMap.get(buildingId).getBuildingName();
        String mngUnitName = buildingMap.get(buildingId) == null ? "" : buildingMap.get(buildingId).getMngUnitName();
        String waterUnitName = fillDayList.get(0).getWaterUnitName();
        if(LEND_IN.equals(lendType) || (LEND_OUT.equals(lendType) && BUILDING_IN_OTHER_MONTH.equals(planType))){
            //调整前
            List<BigDecimal> waterQuantityListBefore = new ArrayList<>();
            //调整后
            List<BigDecimal> waterQuantityListAfter = new ArrayList<>();
            //差值
            List<BigDecimal> waterQuantityListDiff = new ArrayList<>();
            //差值百分比
            List<Object> waterQuantityListPercent = new ArrayList<>();
            headValMap.entrySet().forEach(en -> {
                BigDecimal before = en.getValue().get(0) == null ? new BigDecimal(0) : en.getValue().get(0);
                BigDecimal after = nonNullOfBigDecimal(en.getValue().get(1));
                BigDecimal sub = NumberUtil.sub(after, before);
                BigDecimal percent = new BigDecimal(0);
                if(before.doubleValue() != 0){
                    percent = NumberUtil.mul(NumberUtil.div(sub, before,2), 100);
                }
                waterQuantityListBefore.add(before);
                waterQuantityListAfter.add(after);
                waterQuantityListDiff.add(sub);
                waterQuantityListPercent.add(percent.doubleValue() + "%");
            });
            setLendData(wrPlanFillInDayVOList, buildingId,waterQuantityListBefore, waterQuantityListAfter,
                    waterQuantityListDiff,waterQuantityListPercent,buildingName,mngUnitName, waterUnitName);
        }else{
            WrPlanFillinDayVO vo = new WrPlanFillinDayVO();
            vo.setBuildingName(buildingName);
            vo.setOldWaterValue(headValMap.get(REMAIN_WATER).get(0));
            vo.setDifference(headValMap.get(LEND_OUT_WATER).get(0));
            wrPlanFillInDayVOList.add(vo);
        }
    }

    private void setLendData(List<WrPlanFillinDayVO> wrPlanFillInDayVOList, String buildingId,
                             List<BigDecimal> waterQuantityListBefore,List<BigDecimal> waterQuantityListAfter,
                             List<BigDecimal> waterQuantityListDiff, List<Object> waterQuantityListPercent,
                             String buildingName , String mngUnitName  , String waterUnitName) {
        //原计划
        WrPlanFillinDayVO old = new WrPlanFillinDayVO();
        old.setName("原计划");
        old.setOldWaterValue(waterQuantityListBefore);
        old.setBuildingId(buildingId);
        old.setBuildingName(buildingName);
        old.setMngUnitName(mngUnitName);
        old.setUseUnitName(waterUnitName);
        wrPlanFillInDayVOList.add(old);
        //调整后
        WrPlanFillinDayVO newVO = new WrPlanFillinDayVO();
        newVO.setName("调整后");
        newVO.setNewWaterValue(waterQuantityListAfter);
        newVO.setBuildingId(buildingId);
        newVO.setBuildingName(buildingName);
        newVO.setMngUnitName(mngUnitName);
        newVO.setUseUnitName(waterUnitName);
        wrPlanFillInDayVOList.add(newVO);
        //差值
        WrPlanFillinDayVO diffVO = new WrPlanFillinDayVO();
        diffVO.setName("差值");
        diffVO.setDiffWaterValue(waterQuantityListDiff);
        diffVO.setBuildingId(buildingId);
        diffVO.setBuildingName(buildingName);
        diffVO.setMngUnitName(mngUnitName);
        diffVO.setUseUnitName(waterUnitName);
        wrPlanFillInDayVOList.add(diffVO);
        //差值百分比
        WrPlanFillinDayVO percentVO = new WrPlanFillinDayVO();
        percentVO.setName("差值百分比");
        percentVO.setPercentWaterValue(waterQuantityListPercent);
        percentVO.setBuildingId(buildingId);
        percentVO.setBuildingName(buildingName);
        percentVO.setMngUnitName(mngUnitName);
        percentVO.setUseUnitName(waterUnitName);
        wrPlanFillInDayVOList.add(percentVO);
    }

    private List<WrPlanFillinDay> getWrPlanFillInDays(WrPlanTask task) {
        //根据任务id,查询近期计划
        LambdaQueryWrapper<WrPlanFillinDay> dayMapper = new QueryWrapper().lambda();
        //当前月份月度指标
        dayMapper.eq(WrPlanFillinDay::getPlanTaskId, task.getId());
        return wrPlanFillinDayMapper.selectList(dayMapper);
    }
}
