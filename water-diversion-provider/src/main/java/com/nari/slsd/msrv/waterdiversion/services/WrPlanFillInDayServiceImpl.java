package com.nari.slsd.msrv.waterdiversion.services;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.msrv.common.exception.TransactionException;
import com.nari.slsd.msrv.common.ext.enums.CodeEnum;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.*;
import com.nari.slsd.msrv.waterdiversion.commons.*;
import com.nari.slsd.msrv.waterdiversion.interfaces.*;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.*;
import com.nari.slsd.msrv.waterdiversion.model.dto.*;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.*;
import com.nari.slsd.msrv.waterdiversion.model.vo.*;
import com.nari.slsd.msrv.waterdiversion.utils.CommonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 近期计划调整 服务实现类
 * </p>
 *
 * @author reset zhs
 * @since 2021-08-20
 */
@Service
public class WrPlanFillInDayServiceImpl extends ServiceImpl<WrPlanFillinDayMapper, WrPlanFillinDay> implements IWrPlanFillinDayService {

    @Resource
    private WrPlanInterDayMapper wrPlanInterDayMapper;
    @Resource
    private WaterPlanFillinMonthMapper waterPlanFillinMonthMapper;
    @Resource
    private WrPlanInterTdayMapper wrPlanInterTdayMapper;
    @Resource
    TransactionTemplate transactionTemplate;
    @Autowired
    private IWrPlanTaskService wrPlanTaskService;
    @Resource
    private WrPlanAdjustMapper wrPlanAdjustMapper;
    @Autowired
    WaterBuildingManagerMapper waterBuildingManagerMapper;
    @Autowired
    IWrRecentPlanAdjustService wrRecentPlanAdjustService;
    @Autowired
    IDataService dataService;
    @Autowired
    IActiviciTaskService activiciTaskService;
    @Autowired
    WrRightTradeMapper wrRightTradeMapper;
    @Autowired
    IWrPlanGenerateMonthService wrPlanGenerateMonthService;
    @Autowired
    WrSuperYearRecordMapper wrSuperYearRecordMapper;


    /**
     *  月内调整查询
     * @param startTime
     * @param endTime
     * @param buildingIds
     * @param buildingName
     * @return
     */
    @Override
    public  WrPlanFillInDayAndBuildingIdTreeVO findWithinMonth(Long startTime,Long endTime,List<String> buildingIds, List<String> buildingName,String waterUnitId) {

        WrPlanFillInDayAndBuildingIdTreeVO wrPlanFillInDayAndBuildingIdTreeVO = new WrPlanFillInDayAndBuildingIdTreeVO();
        //获取月内调整借调方
        List<WrPlanFillinDayVO> wrPlanFillinDayVOList = lendIn(startTime,endTime,buildingIds,buildingName,PlanFillInTypeEnum.DAY_PLAN_FILL_IN_WAM.getId());
        wrPlanFillInDayAndBuildingIdTreeVO.setWrPlanFillinDayVO(wrPlanFillinDayVOList);
       //获取结余调整引水口和剩余水量值，暂时只支持一个引水口借调调整
        List<SimpleWrBuildingVO> simpleWrBuildingVOS = wrRecentPlanAdjustService.getAllAdaptiveBorrowBuildings(buildingIds.get(0),waterUnitId);
        //获取引水口名称、id对应的剩余水量集合
        wrPlanFillInDayAndBuildingIdTreeVO.setSimpleWrBuildingVOS(simpleWrBuildingVOS);
        return wrPlanFillInDayAndBuildingIdTreeVO;
    }

    /**
     *  跨月调整查询
     * @param buildingIds
     * @param buildingName
     * @param months 借调月份
     * @return
     */
    @Override
    public WrPlanFillinDayAdjustVO findSpanMonth(Long startTime, Long endTime, List<String> buildingIds, List<String> buildingName, List<String> months) {
        //获取引水口id与引水口名称对应关系
        Map<String,Object> buildingMap = buildingMap(buildingIds,buildingName);
        //获取旬迭代表数据
        WrPlanFillinDayAdjustVO wrPlanFillinDayAdjustVO = new WrPlanFillinDayAdjustVO();
        //跨月（旬月迭代表数据）
        QueryWrapper<WrPlanInterTday> queryWrapperTday = queryWrapper(startTime,endTime,buildingIds,months,WrPlanInterEnum.PLNA_INTER_TDAY.getId());
        List<WrPlanFillinDayVO> wrPlanFillinDayV0 = wrPlanFillinTday(wrPlanInterTdayMapper.selectList(queryWrapperTday),buildingMap);
        //TODO 跨月借出方暂时改为每月均分3份
        wrPlanFillinDayAdjustVO.setWrPlanFillinTDay(wrPlanFillinDayV0);

        //获取月内调整借调方
        List<WrPlanFillinDayVO> wrPlanFillinDayVOList = lendIn(startTime,endTime,buildingIds,buildingName,PlanFillInTypeEnum.DAY_PLAN_FILL_IN_TLI.getId());
        wrPlanFillinDayAdjustVO.setWrPlanFillinDay(wrPlanFillinDayVOList);
        return wrPlanFillinDayAdjustVO;
    }

    /**
     *  超年调整查询
     * @param startTime
     * @param endTime
     * @param buildingIds
     * @param buildingName
     * @return
     */
    @Override
    public List<WrPlanFillinDayVO> findSuperYear(Long startTime, Long endTime, List<String> buildingIds, List<String> buildingName) {
        List<WrPlanFillinDayVO> wrPlanFillinDayVOList = lendIn(startTime,endTime,buildingIds,buildingName,PlanFillInTypeEnum.DAY_PLAN_FILL_IN_SUP.getId());
        return wrPlanFillinDayVOList;
    }
    /**
     *  月内，跨月，超年 借调方共用方法
     */
    private List<WrPlanFillinDayVO> lendIn(Long startTime,Long endTime,List<String> buildingIds,List<String> buildingName,String type) {
        //获取引水口id与引水口名称对应关系
        Map<String,Object> buildingMap = buildingMap(buildingIds,buildingName);

        //获取日迭代表数据
        QueryWrapper<WrPlanInterDay> queryWrapper = queryWrapper(startTime,endTime,buildingIds,new ArrayList<String>(),WrPlanInterEnum.PLNA_INTER_DAY.getId());

        List<WrPlanInterDay> wrPlanInterDayList = wrPlanInterDayMapper.selectList(queryWrapper);
        List<WrPlanFillinDayVO> wrPlanFillinDayVOList = new ArrayList<>();
        //根据引水口进行分类
        Map<String, List<WrPlanInterDay>> buildingGroupMap = wrPlanInterDayList.stream().collect(Collectors.groupingBy(WrPlanInterDay::getBuildingId));
        for (String buildingId: buildingGroupMap.keySet()) {
            String buildName = String.valueOf(buildingMap.get(buildingId));

            List<WrPlanInterDay> hourdbs = buildingGroupMap.get(buildingId);
            //TODO 造数据后期删除
            List<BigDecimal> waterQuantityList = new ArrayList<>();
            if (CollectionUtils.isEmpty(hourdbs)){
                throw new TransactionException(CodeEnum.NO_DATA, "该引水口计划数据暂未填报");
            }
            for (WrPlanInterDay wrPlanInterDay:hourdbs){
                //流量
                waterQuantityList.add(wrPlanInterDay.getWaterFlow());
            }
            wrPlanFillinDayVOList = wrPlanFillinDayVOList(waterQuantityList,buildingId,buildingMap);
            if (!type.equals(PlanFillInTypeEnum.DAY_PLAN_FILL_IN_SUP.getId())){
                WrPlanFillinDayVO wrPlanFillinDayVOFour = wrPlanFillinDayVO("差值百分比",buildingId,buildName,"","",true,"");
                wrPlanFillinDayVOList.add(wrPlanFillinDayVOFour);
            }
        }
        return wrPlanFillinDayVOList;
    }
    //整合旬迭代表数据（跨月借出方查询）
    private List<WrPlanFillinDayVO> wrPlanFillinTday(List<WrPlanInterTday> wrPlanInterTdayList,Map<String,Object> buildingMap){

        List<BigDecimal> waterQuantityList = new ArrayList<>();
        for (WrPlanInterTday wrPlanInterTday:wrPlanInterTdayList){
            //水量
            BigDecimal tdayWaterQuantity = CommonUtil.number(wrPlanInterTday.getWaterQuantity().doubleValue()/3);
            //waterQuantityList.add(wrPlanInterTday.getWaterFlow());
            waterQuantityList.add(tdayWaterQuantity);
            waterQuantityList.add(tdayWaterQuantity);
            waterQuantityList.add(tdayWaterQuantity);
        }
        String buildingId = null;
        if (wrPlanInterTdayList.size()>0){
            buildingId = wrPlanInterTdayList.get(0).getBuildingId();
        }
        List<WrPlanFillinDayVO> wrPlanFillinDayVOList = wrPlanFillinDayVOList(waterQuantityList,buildingId,buildingMap);
        return wrPlanFillinDayVOList;
    }
    //原计划、调整后、差值结构整理
    private List<WrPlanFillinDayVO> wrPlanFillinDayVOList(List<BigDecimal> waterQuantityList,String buildingId,Map<String,Object> buildingMap){

        List<WrPlanFillinDayVO> wrPlanFillinDayVOList = new ArrayList<>();
        WrPlanFillinDayVO wrPlanFillinDayVO = new WrPlanFillinDayVO();

        String buildName = String.valueOf(buildingMap.get(buildingId));
        wrPlanFillinDayVO.setOldWaterValue(waterQuantityList);
        wrPlanFillinDayVO.setBuildingId(buildingId);
        wrPlanFillinDayVO.setBuildingName(buildName);
        wrPlanFillinDayVO.setName("原计划");
        wrPlanFillinDayVOList.add(wrPlanFillinDayVO);

        WrPlanFillinDayVO wrPlanFillinDayVOTwo = wrPlanFillinDayVO("调整后",buildingId,buildName,true,"","","");
        wrPlanFillinDayVOTwo.setNewWaterValue(waterQuantityList);
        wrPlanFillinDayVOTwo.setInputType("number");
        wrPlanFillinDayVOList.add(wrPlanFillinDayVOTwo);

        WrPlanFillinDayVO wrPlanFillinDayVOThree = wrPlanFillinDayVO("差值",buildingId,buildName,"",true,"","");
        wrPlanFillinDayVOList.add(wrPlanFillinDayVOThree);

        return wrPlanFillinDayVOList;
    }
    //超年填报记录数据整合
    private WrSuperYearRecord wrSuperYearRecord(String taskId,String waterRegimeCode,BigDecimal totalWater){
        WrSuperYearRecord wrSuperYearRecord = new WrSuperYearRecord();
        wrSuperYearRecord.setId(IDGenerator.getId());
        wrSuperYearRecord.setTaskId(taskId);
        wrSuperYearRecord.setWaterRegimeCode(waterRegimeCode);
        wrSuperYearRecord.setTotalWater(totalWater);
        return wrSuperYearRecord;
    }
    /***
     *  保存近期计划数据
     * @param wrPlanFillInDayAllDTO
     */
    @Override
    public void updatePlanFullinDay(WrPlanFillInDayAllDTO wrPlanFillInDayAllDTO) {
        //管理站集合(工作流用)
        List<String> mngList = new ArrayList<>();
        //借调类型 0 月内 1 月内（跨引水口） 2跨月 3超年
        String type = wrPlanFillInDayAllDTO.getType();
        //借调方
        List<LendInDTO> lendIns = wrPlanFillInDayAllDTO.getLendIns();
        //借调方开始结束时间
        String startTime = null;
        String endTime = null;
        //获取借调方管理站id
        for (LendInDTO lendIn:lendIns){
            mngList.add(lendIn.getMngUnitId());
            startTime = lendIn.getStartTime();
            endTime = lendIn.getEndTime();
        }
        //借出方
        List<LendOutDTO> lendOuts = wrPlanFillInDayAllDTO.getLendOuts();//本月跨引水口（月内）超年
        if(CollectionUtils.isNotEmpty(lendOuts)){
            //获取（月内）借出方管理站id
            lendOuts.forEach(lendIn->{
                mngList.add(lendIn.getMngUnitId());
            });
        }
        List<LendOutSpanMonthsDTO> lendOutSpanMonths = wrPlanFillInDayAllDTO.getSpanMonthLendOuts();//本引水口跨月（跨月）
        if (CollectionUtils.isNotEmpty(lendOutSpanMonths)){
            //获取（跨月）借出方管理站id
            lendOutSpanMonths.forEach(lendOutSpanMonth->{
                mngList.add(lendOutSpanMonth.getMngUnitId());
            });
        }

        //启动工作流程获取流程id
        String porcessId = null;
        if (type.equals("3")){//超年 flag == 2
            porcessId = processId(wrPlanFillInDayAllDTO.getUserId(),wrPlanFillInDayAllDTO.getUserName(),mngList, ActivitiEnum.FLAG_ADOPT_TWO.getId(),"1");
        }else{ //其他 月内、跨月 flag == 1
            //获取审批状态（是否通过总调审批）
            String batchState = batchState(wrPlanFillInDayAllDTO.getLendIns().get(0).getOldPlanValue(),wrPlanFillInDayAllDTO.getLendIns().get(0).getNewPlanValue());
            porcessId = processId(wrPlanFillInDayAllDTO.getUserId(),wrPlanFillInDayAllDTO.getUserName(),mngList, ActivitiEnum.FLAG_ADOPT_ONE.getId(),batchState);
        }
        //借调方数据
        Map<String,Object> resustMap = jsonToList(lendIns,type,startTime,endTime,wrPlanFillInDayAllDTO.getUserId(),
                wrPlanFillInDayAllDTO.getUserName(),wrPlanFillInDayAllDTO.getContent(),
                wrPlanFillInDayAllDTO.getMngUnitName(),wrPlanFillInDayAllDTO.getMngUnitId(),porcessId);

        List<WrPlanFillinDay> waterPlanFillinMonthList = (List<WrPlanFillinDay>)resustMap.get("wrPlanFillinDayList");
        //填报计划任务
        WrPlanTask wrPlanTask = (WrPlanTask) resustMap.get("wrPlanTask");
        //wrPlanAdjust
        WrPlanAdjust wrPlanAdjust = (WrPlanAdjust) resustMap.get("wrPlanAdjust");
        //内容
        String content = wrPlanFillInDayAllDTO.getContent();

        transactionTemplate.executeWithoutResult(transactionStatus -> {
            String planId = String.valueOf(resustMap.get("planId"));
            String adjustId = String.valueOf(resustMap.get("adjustId"));
            // 月内（跨引水口）借出方，超年借出方
            if(type.equals(RecentPlanEnum.OTHER_BUILDING_IN_MONTH)||type.equals(RecentPlanEnum.BUILDING_IN_OTHER_WATER_UNIT)){
                List<WrPlanFillinDay>  wrPlanFillinDayList = findWrPlanLendOuts(lendOuts,content,type,RecentPlanEnum.LEND_OUT,planId,adjustId);
                saveBatch(wrPlanFillinDayList);
                //wrPlanFillinDayValueByTDay(List<LendOutSpanMonthsDTO> spanMonthLendOuts,String months,
                        //String content,String type,String lendType,String planId,String adjustId){
            }else if(type.equals(RecentPlanEnum.BUILDING_IN_OTHER_MONTH)){//跨月借出方
                List<WrPlanFillinDay>  wrPlanFillinDayList = wrPlanFillinDayValueByTDay(lendOutSpanMonths, wrPlanFillInDayAllDTO.getMonths(),
                        content,type,RecentPlanEnum.LEND_OUT,planId,adjustId);
                saveBatch(wrPlanFillinDayList);
            }
            //批量保存计划水量数据
            saveBatch(waterPlanFillinMonthList);
            //更新日迭代数据
            //updateDay(wrPlanInterDayList);
            // 添加填报计划任务.
            wrPlanTaskService.insert(wrPlanTask);
            // 添加调整计划数据
            wrPlanAdjustMapper.insert(wrPlanAdjust);
            if(type.equals(RecentPlanEnum.BUILDING_IN_OTHER_WATER_UNIT)){//更新水权交易编码对应状态，保存超年存借水信息
                /*UpdateWrapper<WrRightTrade> updateWrapper = new UpdateWrapper<WrRightTrade>();
                updateWrapper.eq("UNIQUE_CODE",wrPlanFillInDayAllDTO.getTradeId());
                WrRightTrade wrRightTrade = new WrRightTrade();
                wrRightTrade.setStatus(2);
                wrRightTradeMapper.update(wrRightTrade,updateWrapper);*/

                //保存超年存借水信息
                BigDecimal sunValue = CommonUtil.number(lendIns.stream().mapToDouble(LendInDTO::getDistributionWater).sum());
                WrSuperYearRecord wrSuperYearRecord = wrSuperYearRecord( wrPlanTask.getId(),wrPlanFillInDayAllDTO.getTradeId(),sunValue);
                wrSuperYearRecordMapper.insert(wrSuperYearRecord);
            }
        });
    }
    /**
     *  查询调整计划列表
     * @param startTime
     * @param endTime
     * @param mngUnitId
     */
    @Override
    public DataTableVO findAdjustList(Long startTime, Long endTime, List<String> mngUnitId,Integer pageIndex, Integer pageSize) {
        List<WrPlanAdjustVO> wrPlanAdjustVOList = new ArrayList<>();
        QueryWrapper<WrPlanAdjust> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(startTime)||StringUtils.isNotEmpty(endTime)){
            queryWrapper.between("CREATE_TIME",DateUtils.convertTimeToDate(startTime),DateUtils.convertTimeToDate(endTime));
        }
        if(StringUtils.isNotEmpty(mngUnitId)){
            queryWrapper.in("MNG_UNIT_ID",mngUnitId);
        }
        IPage<WrPlanAdjust> page = new Page<>(pageIndex, pageSize);
        IPage<WrPlanAdjust> selectPage = wrPlanAdjustMapper.selectPage(page,queryWrapper);
        List<WrPlanAdjust> wrPlanAdjustList= selectPage.getRecords();
        for (WrPlanAdjust wrPlanAdjust:wrPlanAdjustList){
            WrPlanAdjustVO wrPlanAdjustVO = new WrPlanAdjustVO();
            BeanUtils.copyProperties(wrPlanAdjust, wrPlanAdjustVO);
            wrPlanAdjustVO.setCreateTime(DateUtils.convertDateToLong(wrPlanAdjust.getCreateTime()));
            wrPlanAdjustVO.setStartTime(DateUtils.convertDateToLong(wrPlanAdjust.getStartTime()));
            wrPlanAdjustVO.setEndTime(DateUtils.convertDateToLong(wrPlanAdjust.getEndTime()));
            wrPlanAdjustVOList.add(wrPlanAdjustVO);
        }
        //result
        DataTableVO dataTableVO = new DataTableVO();
        dataTableVO.setRecordsTotal(page.getTotal());
        dataTableVO.setRecordsFiltered(page.getTotal());
        dataTableVO.setData(wrPlanAdjustVOList);
        return dataTableVO;
    }

    /**
     *  findAdjustCurve
     * @param startTime
     * @param endTime
     * @param buildingId
     * @param buildingName
     * @return
     */
    @Override
    public WrPlanFillinDayVO findAdjustCurve(Long startTime, Long endTime, List<String> buildingId, String buildingName) {

        List<Map<String,Object>> oldWaterValue = new ArrayList<>();
        List<Map<String,Object>> newWaterValue = new ArrayList<>();
        QueryWrapper<WaterPlanFillinMonth> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(startTime)||StringUtils.isNotEmpty(endTime)){
            String startStr =  DateUtils.convertTimeToString(startTime);
            String endStr =  DateUtils.convertTimeToString(endTime);
            String year = startStr.substring(0,4);
            String startMonth =startStr.substring(5,7);

            String endMonth =endStr.substring(5,7);
            startMonth.compareTo(endMonth);
            String startDay =startStr.substring(8,10);
            String endDay =endStr.substring(8,10);
            queryWrapper.eq("YEAR",year);
            queryWrapper.between("MONTH",startMonth,endMonth);
            queryWrapper.in("BUILDING_ID",buildingId);
            queryWrapper.between("DAY",startDay,endDay);
            queryWrapper.orderByAsc("DAY");
        }

        List<WaterPlanFillinMonth> wrPlanFillinDayList = waterPlanFillinMonthMapper.selectList(queryWrapper);
        WrPlanFillinDayVO wrPlanFillinDayVO = new WrPlanFillinDayVO();
        for (WaterPlanFillinMonth waterPlanFillinMonth:wrPlanFillinDayList){
            //调整前
            String time = waterPlanFillinMonth.getYear()+"-"+waterPlanFillinMonth.getMonth()+"-"+waterPlanFillinMonth.getDay();
            Map<String,Object> oldWatermap = new HashMap<>();
            oldWatermap.put("time",time);
            oldWatermap.put("value",CommonUtil.number(waterPlanFillinMonth.getDemadWaterQuantity().doubleValue()));
            oldWaterValue.add(oldWatermap);


        }

        //获取日迭代表数据
        QueryWrapper<WrPlanInterDay> wrapper = queryWrapper(startTime,endTime,buildingId,new ArrayList<String>(),WrPlanInterEnum.PLNA_INTER_DAY.getId());

        List<WrPlanInterDay> wrPlanInterDayList = wrPlanInterDayMapper.selectList(wrapper);
        for (WrPlanInterDay wrPlanInterDay:wrPlanInterDayList){
            //调整后
            Map<String,Object> newWatermap = new HashMap<>();
            newWatermap.put("time", DateUtils.parseDateToString(wrPlanInterDay.getSupplyTime()));
            newWatermap.put("value",wrPlanInterDay.getWaterQuantity());
            newWaterValue.add(newWatermap);

        }
     /*   newWaterValue = newWaterValue.stream()
                .sorted(Comparator.comparingInt(map -> Integer.parseInt(map.get("time").toString().substring(map.get("time").toString().length()-3,map.get("time").toString().length()))))
                .collect(Collectors.toList());*/
        wrPlanFillinDayVO.setOldWaterValue(oldWaterValue);
        wrPlanFillinDayVO.setNewWaterValue(newWaterValue);
        wrPlanFillinDayVO.setBuildingId(buildingId.get(0));
        wrPlanFillinDayVO.setBuildingName(buildingName);
        return wrPlanFillinDayVO;
    }
    /**
     * 通过管理站id查询引水口id
     * @param mngUnitId
     * @return
     */
    @Override
    public ResultModel getBuildingIdByMngUnitId(List<String> mngUnitId, String mngUnitName, String waterUnitId, String waterUnitName, List<String> buildingLevels) {
        QueryWrapper<WaterBuildingManager> wrapper = new QueryWrapper<>();
        wrapper.in("wd.MNG_UNIT_ID", mngUnitId);
        wrapper.in("wd.BUILDING_LEVEL", buildingLevels);
        List<WrBuildingAndDiversion> wrBuildingAndDiversionList = waterBuildingManagerMapper.getBuildingAndDiversionList(wrapper);
        if (StringUtils.isNotEmpty(waterUnitId)){
            //获取用水单位下的引水口
            List<WrBuildingAndDiversion> wrBuildingAndDiversionListByWater = wrPlanGenerateMonthService.getAllWaterBuildingForAppointUseUnit(waterUnitId);
            //取交集
            List<WrBuildingAndDiversion> intersectList = wrBuildingAndDiversionListByWater.stream()
                    .filter(pe -> find(pe.getId(), wrBuildingAndDiversionList) > -1).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(intersectList)){
                return ResultModelUtils.getInstance(false,"5010",mngUnitName+"管理站与"+waterUnitName+"用水单位下暂未配置引水口");
            }
            return ResultModelUtils.getSuccessInstanceExt(intersectList);
        }
        return ResultModelUtils.getSuccessInstanceExt(wrBuildingAndDiversionList);

    }

    /**
     *  剩余水量对比 校验
     * @param startTime
     * @param endTime
     * @param flowValue
     * @param type
     * @return
     */
    @Override
    public ResultModel checkWaterValue(Long startTime, Long endTime,List<String> months,String buildingName,String waterUnitId,List<String> buildingIds, Double flowValue, String type) {
        //查询原计划水量
        //根据时间段与流量数据获取总水量
        //对比，符合一键调整成功，不符合返回错误信息为进行下个节点借调
        //获取日迭代表数据
        QueryWrapper<WrPlanInterDay> queryWrapper = queryWrapper(startTime,endTime,buildingIds,new ArrayList<String>(),WrPlanInterEnum.PLNA_INTER_DAY.getId());

        List<WrPlanInterDay> wrPlanInterDayList = wrPlanInterDayMapper.selectList(queryWrapper);
        //获取原计划水量
        Double oldWaterValue = wrPlanInterDayList.stream().map(WrPlanInterDay::getWaterQuantity).reduce(BigDecimal.ZERO,BigDecimal::add).doubleValue();
        //获取天数
        long dayNum =(endTime-startTime)/(1000*60*60*24)+1;//化为天
        //获取调整后水量
        Double newWaterValue = flowValue*86400/10000*dayNum;
        //调整水量总差值
        Double differenceValue = newWaterValue-oldWaterValue;
        if(type.equals(RecentPlanEnum.BUILDING_IN_MONTH)){//本水口月内调整
            Map<String,BigDecimal> remain = wrRecentPlanAdjustService.getRemainOfMonth(buildingIds);
            //该引水口的剩余水量
            Double surplusValue = remain.get(buildingIds.get(0)).doubleValue();
            if(differenceValue>surplusValue){
                return ResultModelUtils.getInstance(false,"5010",buildingName+"引水口本月可借调剩余水量不足，请进行跨引水口或跨月借调！");
            }
        }else if (type.equals(RecentPlanEnum.OTHER_BUILDING_IN_MONTH)){//本月跨引水口
            //该用水单位剩余水量
            Double surplusValue = wrRecentPlanAdjustService.getRemainWaterOfUseUnit(waterUnitId).doubleValue();
            if(differenceValue>surplusValue){
                return ResultModelUtils.getInstance(false,"5010",buildingName+"引水口所在用水单位本月可借调剩余水量不足，请进行跨月借调！");
            }
        }else if (type.equals(RecentPlanEnum.BUILDING_IN_OTHER_MONTH)) {//跨月
            //查询旬月数据
            //跨月（旬月迭代表数据）
            QueryWrapper<WrPlanInterTday> queryWrapperTday = queryWrapper(startTime, endTime, buildingIds, months, WrPlanInterEnum.PLNA_INTER_TDAY.getId());
            List<WrPlanInterTday> wrPlanInterTDayList = wrPlanInterTdayMapper.selectList(queryWrapperTday);
            //获取旬月原计划水量
            Double oldWaterValueMonth = wrPlanInterTDayList.stream().map(WrPlanInterTday::getWaterQuantity).reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue();
            if (differenceValue > oldWaterValueMonth) {
                return ResultModelUtils.getInstance(false, "5010", buildingName + "引水口本月跨月可借调剩余水量不足，请进行水权交易！");
            }
        }
//        else if (type.equals(RecentPlanEnum.BUILDING_IN_OTHER_WATER_UNIT)){//超年
//            Double oldWaterValueYear = wrRecentPlanAdjustService.getRemainWaterOfUseUnit(waterUnitId).doubleValue();
//            if(differenceValue>oldWaterValueYear){
//                return ResultModelUtils.getInstance(false,"5010",buildingName+"引水口可借调剩余用水量不足");
//            }
//        }
        return ResultModelUtils.getInstance(true,"5001",buildingName+"引水口可借调剩余用水量");
    }

    public int find(String playerId,List<WrBuildingAndDiversion> list) {
        int res = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(playerId)) {
                res = i;
                break;
            }
        }
        return res;
    }
    //整合json结构获取需存储的近期填报计划与日旬表数据更新
    private Map<String,Object> jsonToList(List<LendInDTO> lendIns,String type,String startTime,String endTime,String userId ,String userName,
                                          String content,String mngUnitName,String mngUnitId,String porcessId){
        Map<String,Object> resultMap = new HashMap<>();
        String planType = PlanFillInTypeEnum.DAY_PLAN_FILL_IN_WAM.getId();//月内
        String lendType = RecentPlanEnum.LEND_IN;//借调方（0）
        if (type.equals(RecentPlanEnum.BUILDING_IN_OTHER_MONTH)) {//本引水口跨月
            planType = PlanFillInTypeEnum.DAY_PLAN_FILL_IN_TLI.getId();//跨月
        }
        if(type.equals(RecentPlanEnum.BUILDING_IN_OTHER_WATER_UNIT)){//跨用水单位调整
            planType = PlanFillInTypeEnum.DAY_PLAN_FILL_IN_SUP.getId();//超年
        }
        //计划任务id
        String planId = IDGenerator.getId();
        //调整计划id
        String adjustId = IDGenerator.getId();
        //近期填报数据
        List<WrPlanFillinDay> wrPlanFillinDayList = wrPlanFillinDayValue(lendIns,content,type,lendType,planId,adjustId);
        //生成调整计划数据
        WrPlanAdjust wrPlanAdjust = wrPlanAdjust(adjustId,userId,userName,startTime,endTime,mngUnitId,mngUnitName,planType,content);
       //生成填报计划任务数据
        WrPlanTask wrPlanTask = wrPlanTask(planId,userId,startTime,content,planType,mngUnitId,mngUnitName,porcessId,startTime,endTime,type);
        resultMap.put("wrPlanFillinDayList",wrPlanFillinDayList);
        resultMap.put("wrPlanTask",wrPlanTask);
        resultMap.put("wrPlanAdjust",wrPlanAdjust);
        resultMap.put("planId",planId);
        resultMap.put("adjustId",adjustId);

        return resultMap;
    }
    //生成调整计划数据
    private static WrPlanAdjust wrPlanAdjust(String adjustId,String personId,String userName,String startTime,String endTime,
                                             String mngUnitId,String mngUnitName,String adjustType,String content){
        WrPlanAdjust wrPlanAdjust = new WrPlanAdjust();
        wrPlanAdjust.setId(adjustId);
        wrPlanAdjust.setPersonId(personId);
        wrPlanAdjust.setPersonName(userName);
        wrPlanAdjust.setMngUnitId(mngUnitId);
        wrPlanAdjust.setMngUnitName(mngUnitName);
        wrPlanAdjust.setAdjustType(adjustType);
        wrPlanAdjust.setContent(content);
        if(!adjustType.equals(PlanFillInTypeEnum.DAY_PLAN_FILL_IN_SUP.getId())){//超年水量调整
            wrPlanAdjust.setStartTime(DateUtils.convertStringTimeToDateExt(startTime));
            wrPlanAdjust.setEndTime(DateUtils.convertStringTimeToDateExt(endTime));
        }
        return wrPlanAdjust;
    }
    //借调方调整(月内、跨月、超年 )
    private static List<WrPlanFillinDay> wrPlanFillinDayValue(List<LendInDTO> lendIns, String content, String type, String lendType, String planId, String adjustId){

        // type 0 月内 1月内跨引水口 2跨月 2超年
        //近期填报数据
        List<WrPlanFillinDay> wrPlanFillinDayList = new ArrayList<>();
        for(LendInDTO lendIn: lendIns) {
            String startTime = lendIn.getStartTime();
            String endTime = lendIn.getEndTime();
            //用水单位ID
            String waterUnitId = String.valueOf(lendIn.getWaterUnitId());
            //用水单位名称
            String waterUnitName = String.valueOf(lendIn.getWaterUnitName());
            //管理单位ID
            String mngUnitId = String.valueOf(lendIn.getMngUnitId());
            //分水口ID
            String buildingId = String.valueOf(lendIn.getBuildingId());
            List<BigDecimal> newPlanValue =  lendIn.getNewPlanValue();
            List<BigDecimal> oldPlanValue = lendIn.getOldPlanValue();
            //获取天数
            int dayNum = Integer.valueOf(endTime.substring(endTime.length() - 2, endTime.length())) -
                    Integer.valueOf(startTime.substring(startTime.length() - 2, startTime.length())) + 1;
            for (int i = 0; i < dayNum; i++) {
                //获取当前时间与原计划水量已经调整计划水量数据
                if (newPlanValue.get(i)==null ||oldPlanValue.get(i)==null) {
                    throw new TransactionException(CodeEnum.NO_PARAM,"调整数据不能为空");
                }
                //调整前数据
                Double oldValue = oldPlanValue.get(i).doubleValue();
                //调整后数据
                Double  newValue = newPlanValue.get(i).doubleValue();
                //调整时间（String）
                String num = startTime.substring(0, startTime.length() - 2);
                String day = String.valueOf(Integer.valueOf( startTime.substring(8,10)) + i);
                if (day.length()==1){
                    day = "0"+day;
                }
                String time = num+day;
                //时间段类别(0:日，1:旬)
                String timeType = TDayTypeEnum.TIME_TYPE_DAY;
                Double oldWaterValue = 0.0;
                Double oldFlowValue = 0.0;
                Double newWaterValue = 0.0;
                Double newFlowValue = 0.0;

                if(type.equals(RecentPlanEnum.BUILDING_IN_OTHER_WATER_UNIT)){//超年为水量查流量
                    //调整前需求流量
                    oldFlowValue = oldValue * 10000 / 86400;
                    //调整后需求流量
                    newFlowValue = newValue * 10000 / 86400;
                    //调整前需求水量
                    oldWaterValue = oldValue;
                    //调整后需求水量
                    newWaterValue = newValue;
                }else{//其他为流量查水量
                    //调整前需求水量
                    oldWaterValue = oldValue * 86400 / 10000;
                    //调整后需求水量
                    newWaterValue = newValue * 86400 / 10000;
                    //调整前需求流量
                    oldFlowValue = oldValue;
                    //调整后需求流量
                    newFlowValue = newValue;
                }
                WrPlanFillinDay waterPlanFillinDay = waterPlanFillinDay(oldWaterValue,newWaterValue,oldFlowValue,newFlowValue,time,timeType,content,waterUnitId,waterUnitName,
                        mngUnitId,buildingId,lendType,type,planId,adjustId);
                wrPlanFillinDayList.add(waterPlanFillinDay);
            }
        }
        return wrPlanFillinDayList;
    }
    //跨月借出方调整
    private static List<WrPlanFillinDay> wrPlanFillinDayValueByTDay(List<LendOutSpanMonthsDTO> spanMonthLendOuts,String months,
                                                              String content,String type,String lendType,String planId,String adjustId){
        List<WrPlanFillinDay> wrPlanFillinDayList = new ArrayList<>();
        for(LendOutSpanMonthsDTO lendOutSpanMonth: spanMonthLendOuts) {
            //用水单位ID
            String waterUnitId = lendOutSpanMonth.getWaterUnitId();
            //用水单位名称
            String waterUnitName = lendOutSpanMonth.getWaterUnitName();
            //管理单位ID
            String mngUnitId = lendOutSpanMonth.getMngUnitId();
            //分水口ID
            String buildingId = lendOutSpanMonth.getBuildingId();

            List<BigDecimal> oldPlanValue = lendOutSpanMonth.getOldPlanValue();

            List<BigDecimal> newPlanValue = lendOutSpanMonth.getNewPlanValue();
            //获取旬数
            List<String>  monthList = Arrays.asList(months.split(","));
            int dayNum = monthList.size() * 3;
            int tdaynum = 1;
            for (int i = 0; i < dayNum; i++) {
                //获取当前时间与原计划水量已经调整计划水量数据
                if (oldPlanValue.get(i)==null||newPlanValue.get(i)==null) {
                    throw new TransactionException(CodeEnum.NO_PARAM,"调整数据不能为空");
                }
                //调整前数据
                Double oldWaterValue = oldPlanValue.get(i).doubleValue();
                //调整后数据
                Double newWaterValue = newPlanValue.get(i).doubleValue();
                //时间段类别(0:日，1:旬)
                String timeType = "1";
                //旬（1，2，3）
                //调整前需求水量
                Double oldFlowValue = oldWaterValue * 86400/10000 ;//调整后数据
                //调整后需求水量
                Double  newFlowValue = newWaterValue * 86400/10000 ;;

                WrPlanFillinDay waterPlanFillinDay = waterPlanFillinDay(oldWaterValue,newWaterValue,oldFlowValue,newFlowValue,null,timeType,
                        content,waterUnitId,waterUnitName,mngUnitId,buildingId,lendType,type,planId,adjustId);
                waterPlanFillinDay.setDemandWaterQuantityAfter(CommonUtil.number(newWaterValue));
                waterPlanFillinDay.setDemandWaterFlowAfter(CommonUtil.number(newFlowValue));

                if ((i+1 )% 3 == 0){
                    String yearMonth = monthList.get((i+1 ) / 3 - 1);
                    List<String> times = new ArrayList<>(Arrays.asList(yearMonth.split("-")));
                    waterPlanFillinDay.setYear( times.get(0));
                    waterPlanFillinDay.setMonth( times.get(1));
                }else{
                    String yearMonth = monthList.get((i+1 )/3);;
                    List<String> times = new ArrayList<>(Arrays.asList(yearMonth.split("-")));
                    waterPlanFillinDay.setYear( times.get(0));
                    waterPlanFillinDay.setMonth( times.get(1));
                }
                waterPlanFillinDay.setTday(String.valueOf(tdaynum));
                if (tdaynum == 3){
                    tdaynum = 0;
                }
                tdaynum++;
                wrPlanFillinDayList.add(waterPlanFillinDay);
                }
        }
        return wrPlanFillinDayList;
    }
    /*
     *月内、超年借出方调整
     */
    private static List<WrPlanFillinDay> findWrPlanLendOuts(List<LendOutDTO> lendOuts,String content,String type,
                                                            String lendType,String planId,String adjustId){
        List<WrPlanFillinDay> wrPlanFillinDayList = new ArrayList<>();
        lendOuts.forEach(lendOut->{
            //计划水量（剩余水量）
            Double newWaterValue = lendOut.getSurplusWater().doubleValue();
            //调整后水量（剩余水量-借出水量）
            Double oldWaterValue = newWaterValue - lendOut.getLendOutWater().doubleValue();
            WrPlanFillinDay wrPlanFillinDay = new WrPlanFillinDay();
            wrPlanFillinDay.setId(IDGenerator.getId());
            wrPlanFillinDay.setManageUnitId(lendOut.getMngUnitId());
            wrPlanFillinDay.setBuildingId(lendOut.getBuildingId());
            wrPlanFillinDay.setDemandWaterQuantuty(CommonUtil.number(newWaterValue));
            wrPlanFillinDay.setDemandWaterQuantityAfter(CommonUtil.number(oldWaterValue));
            wrPlanFillinDay.setYear(lendOut.getYear());
            wrPlanFillinDay.setAdjustId(adjustId);//调整计划id
            wrPlanFillinDay.setPlanTaskId(planId);//任务id
            wrPlanFillinDay.setLendType(lendType);//借调类型
            wrPlanFillinDay.setContent(content);//内容
            wrPlanFillinDay.setState(TaskStateEnum.UNDER_APPROVAL.getId());//审批中
           if (type.equals("1")){
                //填报类型
               wrPlanFillinDay.setAdjustType(PlanFillInTypeEnum.DAY_PLAN_FILL_IN_WAM.getId());//月内
               //时间类别
               wrPlanFillinDay.setTimeType(TDayTypeEnum.TIME_TYPE_MONTH);
               wrPlanFillinDay.setMonth(lendOut.getMonth());
            }else if (type.equals("3")){
               wrPlanFillinDay.setAdjustType(PlanFillInTypeEnum.DAY_PLAN_FILL_IN_SUP.getId());//超年
               wrPlanFillinDay.setTimeType(TDayTypeEnum.TIME_TYPE_YEAR);
            }
            wrPlanFillinDayList.add(wrPlanFillinDay);
        });
        return wrPlanFillinDayList;
    }
    // 生成填报计划任务数据
    private static WrPlanTask wrPlanTask(String planId,String personId,String time,
                                         String content,String planType,String mngUnitId,
                                         String mngUnitName,String processId,String startTime,String endTime,String subType){
        //年份
        String year = time.substring(0,4);
        //月份
        String month = time.substring(5,7);
        WrPlanTask wrPlanTask = new WrPlanTask();
        wrPlanTask.setId(planId);
        wrPlanTask.setPersonId(personId);
        wrPlanTask.setYear(year);
        wrPlanTask.setMonth(month);
        wrPlanTask.setCreateDate(new Date());
        wrPlanTask.setState(TaskStateEnum.UNDER_APPROVAL.getId());//审批中
        wrPlanTask.setContent(content);
        wrPlanTask.setPlanType(PlanFillInTypeEnum.DAY_PLAN_FILL_IN.getId());//近期用水计划调整
        wrPlanTask.setSubType(subType);
        //流程实例id
        wrPlanTask.setWaterPlanFillIn(processId);
        if (planType.equals("0")){
            wrPlanTask.setTaskName(PlanFillInTypeEnum.DAY_PLAN_FILL_IN_WAM.getName());//月内
            wrPlanTask.setStartDate(DateUtils.convertStringTimeToDateExt(startTime));
            wrPlanTask.setEndDate(DateUtils.convertStringTimeToDateExt(endTime));
        }else if (planType.equals("1")){
            wrPlanTask.setTaskName(PlanFillInTypeEnum.DAY_PLAN_FILL_IN_TLI.getName());//跨月
            wrPlanTask.setStartDate(DateUtils.convertStringTimeToDateExt(startTime));
            wrPlanTask.setEndDate(DateUtils.convertStringTimeToDateExt(endTime));
        }else if(planType.equals("2")){
            wrPlanTask.setTaskName(PlanFillInTypeEnum.DAY_PLAN_FILL_IN_SUP.getName());//超年
        }
        //获取子表信息
        List<WrPlanTaskSub> wrPlanTaskSubList = new ArrayList<>();
        WrPlanTaskSub wrPlanTaskSub = new WrPlanTaskSub();
        wrPlanTaskSub.setTaskId(planId);
        wrPlanTaskSub.setUnitName(mngUnitName);
        wrPlanTaskSub.setUnitId(mngUnitId);
        wrPlanTaskSub.setUnitType(PlanFillInTypeEnum.MNG_UNIT.getId());//管理站id
        wrPlanTaskSub.setId(IDGenerator.getId());
        wrPlanTaskSubList.add(wrPlanTaskSub);
        wrPlanTask.setWrPlanTaskSubList(wrPlanTaskSubList);
        return wrPlanTask;
    }
    //近期计划数据整合公共方法
    private static WrPlanFillinDay waterPlanFillinDay(Double oldWaterValue, Double newWaterValue, Double oldFlowValue, Double newFlowValue,
                                                      String time,String timeType, String content, String waterUnitId,String waterUnitName,
                                                      String mngUnitId,String buildingId, String lendType,String type,String planTaskId,String adjustId){
        WrPlanFillinDay wrPlanFillinDay = new WrPlanFillinDay();
        if (StringUtils.isNotEmpty(time)){//非跨月 借出（旬月）
            wrPlanFillinDay.setYear(time.substring(0,4));
            wrPlanFillinDay.setMonth(time.substring(5,7));
            wrPlanFillinDay.setDay(time.substring(time.length() - 2,time.length()));
        }
        wrPlanFillinDay.setDemandWaterQuantuty(CommonUtil.number(oldWaterValue));
        wrPlanFillinDay.setDemandWaterQuantityAfter(CommonUtil.number(newWaterValue));
        wrPlanFillinDay.setDemandWaterFlow(CommonUtil.number(oldFlowValue));
        wrPlanFillinDay.setDemandWaterFlowAfter(CommonUtil.number(newFlowValue));
        wrPlanFillinDay.setId(IDGenerator.getId());
        wrPlanFillinDay.setPlanTaskId(planTaskId);
        wrPlanFillinDay.setTimeType(timeType);
        wrPlanFillinDay.setContent(content);
        wrPlanFillinDay.setWaterUnitId(waterUnitId);
        wrPlanFillinDay.setWaterUnitName(waterUnitName);
        wrPlanFillinDay.setManageUnitId(mngUnitId);
        wrPlanFillinDay.setBuildingId(buildingId);
        //借调借出
        wrPlanFillinDay.setLendType(lendType);
        //时间类别
        if (type.equals("0")) {
            //填报类型
            wrPlanFillinDay.setAdjustType(PlanFillInTypeEnum.DAY_PLAN_FILL_IN_WAM.getId());//月内
        }else if (type.equals("1")){
            //填报类型
            wrPlanFillinDay.setAdjustType(PlanFillInTypeEnum.DAY_PLAN_FILL_IN_WAM.getId());//月内
        }else if(type.equals("2")){
            wrPlanFillinDay.setAdjustType(PlanFillInTypeEnum.DAY_PLAN_FILL_IN_TLI.getId());//跨月
        }else if (type.equals("3")){
            wrPlanFillinDay.setAdjustType(PlanFillInTypeEnum.DAY_PLAN_FILL_IN_SUP.getId());//超年
        }
        //调整计划id
        wrPlanFillinDay.setAdjustId(adjustId);
        //任务名称
        wrPlanFillinDay.setPlanName(PlanFillInTypeEnum.DAY_PLAN_FILL_IN.getName());
        return wrPlanFillinDay;
    }
    //获取日或旬月迭代表queryWrapper(0：日迭代，1：旬迭代)
    private <T> QueryWrapper<T> queryWrapper(Long startTime,Long endTime,List<String> buildingId,List<String> months,String adjustType){
        QueryWrapper<T> queryWrapper = new QueryWrapper<T>();
        //日迭代
        if (adjustType.equals(WrPlanInterEnum.PLNA_INTER_DAY.getId())){
            queryWrapper.between("SUPPLY_TIME", DateUtils.convertTimeToDate(startTime),DateUtils.convertTimeToDate(endTime));
            queryWrapper.orderByAsc("SUPPLY_TIME");
        }
        if(CollectionUtils.isNotEmpty(buildingId)){
            queryWrapper.in("BUILDING_ID",buildingId);
        }

        //旬迭代,不查询月总
        if (adjustType.equals(WrPlanInterEnum.PLNA_INTER_TDAY.getId())){
            if(months.size()>0){
                //开始时间
                Date startTimeMonth =DateUtils.convertStringTimeToDateExt(months.get(0)+"-01");
                //结束时间
                Date endTimeMonth = DateUtils.convertStringTimeToDateExt(months.get(months.size()-1)+"-21");
                queryWrapper.between("SUPPLY_TIME",startTimeMonth,endTimeMonth);
                queryWrapper.eq("TIME_TYPE","4");
                //queryWrapper.ne("TIME_TYPE","4");
            }
        }
        queryWrapper.orderByAsc("SUPPLY_TIME");
        return queryWrapper;
    }
    //补全差值差值百分比数据
    private WrPlanFillinDayVO wrPlanFillinDayVO(String name,String buildingId,String buildingName,
                                                Object adjustDifference,Object difference,Object differencePercentage,Object differenceEdit){
        WrPlanFillinDayVO wrPlanFillinDayVO = new WrPlanFillinDayVO();
        wrPlanFillinDayVO.setName(name);
        wrPlanFillinDayVO.setBuildingId(buildingId);
        wrPlanFillinDayVO.setBuildingName(buildingName);
        wrPlanFillinDayVO.setAdjustDifference(adjustDifference);
        wrPlanFillinDayVO.setDifference(difference);
        wrPlanFillinDayVO.setDifferencePercentage(differencePercentage);
        wrPlanFillinDayVO.setDifferenceEdit(differenceEdit);
        return wrPlanFillinDayVO;
    }
    //将引水口与id整合到一个map中
    private Map<String,Object> buildingMap(List<String> buildingId,List<String> buildingName){
        //获取引水口id与引水口名称对应关系
        Integer size = buildingId.size();
        Map<String,Object> buildingMap = new HashMap<>();
        for(int i=0;i<size;i++){
            buildingMap.put(buildingId.get(i),buildingName.get(i));
        }
        return buildingMap;
    }
    //启动流程,获取流程id
    private String processId(String userId,String userName,List<String> mngUnitIds,String flag,String batchState){
        ActivitiHandle activitiHandle = new ActivitiHandle();
        //flowKey值
        activitiHandle.setFlowKey("plan_day_id");
        Map<String,Object> UserInfoMap = new HashMap<>();
        UserInfoMap.put("id",userId);
        UserInfoMap.put("name",userName);
        activitiHandle.setUserInfo(UserInfoMap);
        activitiHandle.setSrc("promng");
        activitiHandle.setHandleType("submit");
        Map<String,Object> map = new HashMap<>();
        map.put("flag",flag);
        map.put("batchState",batchState);
        activitiHandle.setExpression("flag=="+flag);//判断网关分支flag == 1 // flag ==2
        activitiHandle.setVariables(map);
        if (flag.equals(ActivitiEnum.FLAG_ADOPT_ONE.getId())){
            Map<String,Object> unitIdMap = new HashMap<>();
            unitIdMap.put("unitId",mngUnitIds);
            activitiHandle.setParam(unitIdMap);
        }
        Map<String,Object> activiciMap = activiciTaskService.getProcessInstanceList(activitiHandle);
        //流程id
        String processId = String.valueOf(activiciMap.get("processId"));
        return processId;
    }
    //计算调整后流量是否超过原计划的20%，0：不超过 1：超过
    private String batchState(List<BigDecimal> oldPlanValue,List<BigDecimal> newPlanValue){
        //原计划求和
        Double oldPlanValueSum =  oldPlanValue.stream().mapToDouble(e->e.doubleValue()).reduce(0, Double::sum);
        //调整后求和
        Double newPlanValueSum =  newPlanValue.stream().mapToDouble(e->e.doubleValue()).reduce(0, Double::sum);
        //获取c差值百分比
        Double dValue = (newPlanValueSum - oldPlanValueSum)/oldPlanValueSum*100;
        //超过原计划的百分之20
        String batchState = WrPlanInterEnum.PLNA_ADJUST_EXCEED.getId();
        //不超过原计划的百分之20
        if (dValue<20){
            batchState = WrPlanInterEnum.PLNA_ADJUST_NOT_EXCEED.getId();
        }
        return batchState;
    }
}
