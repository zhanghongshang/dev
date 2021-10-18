//package com.nari.slsd.msrv.waterdiversion.services;
//
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.nari.slsd.hu.mplat.imc.client.Param.Param;
//import com.nari.slsd.msrv.common.model.DataTableVO;
//import com.nari.slsd.msrv.common.model.ResultModel;
//import com.nari.slsd.msrv.common.utils.*;
//import com.nari.slsd.msrv.waterdiversion.commons.*;
//import com.nari.slsd.msrv.waterdiversion.interfaces.*;
//import com.nari.slsd.msrv.waterdiversion.mapper.primary.*;
//import com.nari.slsd.msrv.waterdiversion.model.dto.*;
//import com.nari.slsd.msrv.waterdiversion.model.primary.po.*;
//import com.nari.slsd.msrv.waterdiversion.model.vo.WrPlanAdjustVO;
//import com.nari.slsd.msrv.waterdiversion.model.vo.WrPlanFillInDayAndBuildingIdTreeVO;
//import com.nari.slsd.msrv.waterdiversion.model.vo.WrPlanFillinDayAdjustVO;
//import com.nari.slsd.msrv.waterdiversion.model.vo.WrPlanFillinDayVO;
//import com.nari.slsd.msrv.waterdiversion.utils.CommonUtil;
//import org.apache.commons.collections.CollectionUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.support.TransactionTemplate;
//
//import javax.annotation.Resource;
//import java.math.BigDecimal;
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * <p>
// * 近期计划调整 服务实现类
// * </p>
// *
// * @author reset zhs
// * @since 2021-08-20
// */
//@Service
//public class WrPlanFillInDayServiceImpls extends ServiceImpl<WrPlanFillinDayMapper, WrPlanFillinDay> implements IWrPlanFillinDayService {
//
//    @Resource
//    private WrPlanInterDayMapper wrPlanInterDayMapper;
//    @Resource
//    private WaterPlanFillinMonthMapper waterPlanFillinMonthMapper;
//    @Resource
//    private WrPlanInterTdayMapper wrPlanInterTdayMapper;
//    @Resource
//    TransactionTemplate transactionTemplate;
//    @Resource
//    private WrPlanTaskMapper wrPlanTaskMapper;
//    @Resource
//    private WrPlanAdjustMapper wrPlanAdjustMapper;
//    @Autowired
//    WaterBuildingManagerMapper waterBuildingManagerMapper;
//    @Autowired
//    IWrRecentPlanAdjustService wrRecentPlanAdjustService;
//    @Autowired
//    IDataService dataService;
//    @Autowired
//    IActiviciTaskService activiciTaskService;
//    @Autowired
//    WrRightTradeMapper wrRightTradeMapper;
//    @Autowired
//    IWrPlanGenerateMonthService wrPlanGenerateMonthService;
//
//    /**
//     *
//     * @param startTime
//     * @param endTime
//     * @param buildingId
//     * @param buildingName
//     * @return
//     */
//    @Override
//    public  WrPlanFillInDayAndBuildingIdTreeVO findDayPlanWaterValue(Long startTime,Long endTime,List<String> buildingId, List<String> buildingName,String waterUnitId,String type) {
//
//        WrPlanFillInDayAndBuildingIdTreeVO wrPlanFillInDayAndBuildingIdTreeVO = new WrPlanFillInDayAndBuildingIdTreeVO();
//        //调整类型
//        String adjustType = "0";
//        //获取引水口id与引水口名称对应关系
//        Map<String,Object> buildingMap = buildingMap(buildingId,buildingName);
//        List<WrPlanFillinDayVO> wrPlanFillinDayVOList = new ArrayList<>();
//        //获取日迭代表数据
//        QueryWrapper<WrPlanInterDay> queryWrapper = queryWrapper(startTime,endTime,buildingId,new ArrayList<String>(),adjustType);
//
//        List<WrPlanInterDay> wrPlanInterDayList = wrPlanInterDayMapper.selectList(queryWrapper);
//        //借出方
//        Map<String,List<BigDecimal>> dataThisYearDto =  new HashMap<>();
//
//        long dayNum =(endTime-startTime)/(1000*60*60*24)+1;//化为天
//        if (type.equals("0")){
//            wrPlanFillInDayAndBuildingIdTreeVO.setSimpleWrBuildingVO(wrRecentPlanAdjustService.getAllAdaptiveBorrowBuildings(buildingId.get(0),waterUnitId));
//        }
//        if(type.equals("1")){
//
//            List<DataBuildingDto> dataThisYearDtos = dataService.getSpecialDataRunDataType(buildingId, Arrays.asList(PointTypeEnum.WATER_VOLUME.getId()),startTime,endTime,
//                    Param.ValType.Special_V,Param.RunDataType.RUN_DAY,Param.CalcType.CALC_REAL);
//            List<BigDecimal> bigDecimal = new ArrayList<>();
//           //TODO 造数据后期删除
//            if(dataThisYearDtos.size()==0){
//
//                for (int i= 0;i<dayNum;i++){
//                    bigDecimal.add(CommonUtil.number(0.0));
//                }
//                buildingId.forEach(s -> {
//                    dataThisYearDto.put(s,bigDecimal);
//                });
//            }
//            dataThisYearDtos.forEach(dataBuildingDto -> {
//                //TODO 造数据后期删除
//                List<BigDecimal> bigDecimals = new ArrayList<>();
//                for (int i= 0;i<dayNum;i++){
//                    bigDecimals.add(CommonUtil.number(0.0));
//                }
//                dataThisYearDto.put(dataBuildingDto.getId(),bigDecimals);
//                List<DataPointDto> dataPointDtos = dataBuildingDto.getDataPointDtos();
//                int j= 0;
//                for (DataPointDto dataPointDto:dataPointDtos) {
//                    int num = bigDecimals.size()-1;
//                    if (j<=num){
//                        bigDecimals.set(j, CommonUtil.number(dataPointDto.getV()));
//                    }else{
//                        bigDecimals.add( CommonUtil.number(dataPointDto.getV()));
//                    }
//                    j++;
//                }
//            });
//        }
//        //根据引水口进行分类
//        Map<String, List<WrPlanInterDay>> buildingGroupMap = wrPlanInterDayList.stream().collect(Collectors.groupingBy(WrPlanInterDay::getBuildingId));
//        for (String buildingCode: buildingGroupMap.keySet()) {
//            String buildName = String.valueOf(buildingMap.get(buildingCode));
//
//            WrPlanFillinDayVO wrPlanFillinDayVO = new WrPlanFillinDayVO();
//            List<WrPlanInterDay> hourdbs = buildingGroupMap.get(buildingCode);
//            //TODO 造数据后期删除
//            List<BigDecimal> waterQuantityList = new ArrayList<>();
//            for (int i= 0;i<dayNum;i++){
//                waterQuantityList.add(CommonUtil.number(0.0));
//            }
//            int j= 0;
//            for (WrPlanInterDay wrPlanInterDay:hourdbs){
//                int num = waterQuantityList.size()-1;
//                if (j<=num){
//                    waterQuantityList.set(j,wrPlanInterDay.getWaterQuantity());
//                }else{
//                    waterQuantityList.add(wrPlanInterDay.getWaterQuantity());
//                }
//                j++;
//            }
//            wrPlanFillinDayVO.setName("原计划");
//            wrPlanFillinDayVO.setOldWaterValue(waterQuantityList);
//            wrPlanFillinDayVO.setBuildingId(buildingCode);
//            wrPlanFillinDayVO.setBuildingName(String.valueOf(buildingMap.get(buildingCode)));
//            wrPlanFillinDayVOList.add(wrPlanFillinDayVO);
//            //借用方
//            if (type.equals("0")){
//
//                WrPlanFillinDayVO wrPlanFillinDayVOTwo = wrPlanFillinDayVO("调整后",buildingCode,buildName,true,"","","");
//                wrPlanFillinDayVOTwo.setNewWaterValue(waterQuantityList);
//                wrPlanFillinDayVOTwo.setNotEdit(false);
//                wrPlanFillinDayVOTwo.setInputType("number");
//                wrPlanFillinDayVOList.add(wrPlanFillinDayVOTwo);
//
//                WrPlanFillinDayVO wrPlanFillinDayVOThree = wrPlanFillinDayVO("差值",buildingCode,buildName,"",true,"","");
//                wrPlanFillinDayVOList.add(wrPlanFillinDayVOThree);
//
//                WrPlanFillinDayVO wrPlanFillinDayVOFour = wrPlanFillinDayVO("差值百分比",buildingCode,buildName,"","",true,"");
//                wrPlanFillinDayVOList.add(wrPlanFillinDayVOFour);
//            }
//            if(type.equals("1")){
//                WrPlanFillinDayVO wrPlanFillinDayVOTwo = wrPlanFillinDayVO("实际引水量",buildingCode,buildName,true,"","","");
//                wrPlanFillinDayVOTwo.setNewWaterValue(dataThisYearDto.get(buildingCode));
//                wrPlanFillinDayVOTwo.setNotEdit(true);
//                wrPlanFillinDayVOList.add(wrPlanFillinDayVOTwo);
//
//                WrPlanFillinDayVO wrPlanFillinDayVOThree = wrPlanFillinDayVO("结余",buildingCode,buildName,"",true,"","");
//                wrPlanFillinDayVOList.add(wrPlanFillinDayVOThree);
//
//                WrPlanFillinDayVO wrPlanFillinDayVOFour = wrPlanFillinDayVO("结余调整",buildingCode,buildName,"","",false,true);
//                wrPlanFillinDayVOFour.setNewWaterValue(dataThisYearDto.get(buildingCode));
//                wrPlanFillinDayVOFour.setInputType("number");
//                wrPlanFillinDayVOList.add(wrPlanFillinDayVOFour);
//
//            }
//        }
//
//        wrPlanFillInDayAndBuildingIdTreeVO.setWrPlanFillinDayVO(wrPlanFillinDayVOList);
//        return wrPlanFillInDayAndBuildingIdTreeVO;
//    }
//    /**
//     * 查询近期调整原计划数据（0：跨月 ）
//     * @param buildingId
//     * @param buildingName
//     * @param months 借调月份
//     * @return
//     */
//    @Override
//    public WrPlanFillinDayAdjustVO findDayAndTdayPlanWaterValue(Long startTime, Long endTime, List<String> buildingId, List<String> buildingName, List<String> months) {
//        //获取引水口id与引水口名称对应关系
//        Integer size = buildingId.size();
//        Map<String,Object> buildingMap = buildingMap(buildingId,buildingName);
//        //获取旬迭代表数据
//        WrPlanFillinDayAdjustVO wrPlanFillinDayAdjustVO = new WrPlanFillinDayAdjustVO();
//        //跨月（日迭代表与月迭代表数据）
//        QueryWrapper<WrPlanInterTday> queryWrapperTday = queryWrapper(startTime,endTime,buildingId,months,"1");
//        List<WrPlanFillinDayVO> wrPlanFillinDayV0 = wrPlanFillinTday(wrPlanInterTdayMapper.selectList(queryWrapperTday),buildingMap);
//        wrPlanFillinDayAdjustVO.setWrPlanFillinTDay(wrPlanFillinDayV0);
//        QueryWrapper<WrPlanInterDay> queryWrapperDay = queryWrapper(startTime,endTime,buildingId,months,"0");
//        wrPlanFillinDayAdjustVO.setWrPlanFillinDay(wrPlanFillinDay(wrPlanInterDayMapper.selectList(queryWrapperDay),buildingMap));
//        return wrPlanFillinDayAdjustVO;
//    }
//
//    /**
//     *  近期计划调整（超年）
//     * @param startTime
//     * @param endTime
//     * @param buildingId
//     * @return
//     */
//    @Override
//    public List<WrPlanFillinDayVO> findDayPlanWaterValueByYear(Long startTime, Long endTime, List<String> buildingId,List<String> buildingName) {
//
//        long dayNum =(endTime-startTime)/(1000*60*60*24)+1;//化为天
//        List<WrPlanFillinDayVO> wrPlanFillinDayVOList = new ArrayList<>();
//        //获取引水口id与引水口名称对应关系
//        Map<String,Object> buildingMap = buildingMap(buildingId,buildingName);
//        //获取日迭代表数据
//        QueryWrapper<WrPlanInterDay> queryWrapper = queryWrapper(startTime,endTime,buildingId,new ArrayList<String>(),"0");
//        List<WrPlanInterDay> wrPlanInterDayList = wrPlanInterDayMapper.selectList(queryWrapper);
//        //根据引水口进行分类
//        Map<String, List<WrPlanInterDay>> buildingGroupMap = wrPlanInterDayList.stream().collect(Collectors.groupingBy(WrPlanInterDay::getBuildingId));
//        for (String buildingCode: buildingGroupMap.keySet()) {
//            String buildName = String.valueOf(buildingMap.get(buildingCode));
//
//            WrPlanFillinDayVO wrPlanFillinDayVO = new WrPlanFillinDayVO();
//            List<WrPlanInterDay> hourdbs = buildingGroupMap.get(buildingCode);
//            //TODO 造数据后期删除
//            List<BigDecimal> waterQuantityList = new ArrayList<>();
//            for (int i= 0;i<dayNum;i++){
//                waterQuantityList.add(CommonUtil.number(0.0));
//            }
//            int j= 0;
//            for (WrPlanInterDay wrPlanInterDay:hourdbs){
//                int num = waterQuantityList.size()-1;
//                if (j<=num){
//                    waterQuantityList.set(j,wrPlanInterDay.getWaterQuantity());
//                }else{
//                    waterQuantityList.add(wrPlanInterDay.getWaterQuantity());
//                }
//
//                j++;
//            }
//            wrPlanFillinDayVO.setName("原计划");
//            wrPlanFillinDayVO.setOldWaterValue(waterQuantityList);
//            wrPlanFillinDayVO.setBuildingId(buildingCode);
//            wrPlanFillinDayVO.setBuildingName(String.valueOf(buildingMap.get(buildingCode)));
//            wrPlanFillinDayVOList.add(wrPlanFillinDayVO);
//            //借用方
//
//            WrPlanFillinDayVO wrPlanFillinDayVOTwo = wrPlanFillinDayVO("调整后",buildingCode,buildName,true,"","","");
//            wrPlanFillinDayVOTwo.setNewWaterValue(waterQuantityList);
//            wrPlanFillinDayVOTwo.setNotEdit(false);
//            wrPlanFillinDayVOTwo.setInputType("number");
//            wrPlanFillinDayVOList.add(wrPlanFillinDayVOTwo);
//
//            WrPlanFillinDayVO wrPlanFillinDayVOThree = wrPlanFillinDayVO("差值",buildingCode,buildName,"",true,"","");
//            wrPlanFillinDayVOList.add(wrPlanFillinDayVOThree);
//
//            WrPlanFillinDayVO wrPlanFillinDayVOFour = wrPlanFillinDayVO("差值百分比",buildingCode,buildName,"","",true,"");
//            wrPlanFillinDayVOList.add(wrPlanFillinDayVOFour);
//            }
//        return wrPlanFillinDayVOList;
//    }
//
//    /***
//     *  保存近期计划数据
//     * @param wrPlanFillInDayAllDTO
//     */
//    @Override
//    public void updatePlanFullinDay(WrPlanFillInDayAllDTO wrPlanFillInDayAllDTO) {
//        List<String> mngUnitIds = new ArrayList<>();
//
//        //填报类型 0 月内 1 月内（多引水口） 2跨月 3超年
//        String subType = wrPlanFillInDayAllDTO.getType();
//        //借调类型 0借调 1借出
//        String startTime = wrPlanFillInDayAllDTO.getStartTime();
//        String endTime = wrPlanFillInDayAllDTO.getEndTime();
//        String content = wrPlanFillInDayAllDTO.getContent();
//        //借入
//        List<Map<String,Object>> borrow =   wrPlanFillInDayAllDTO.getBorrow();
//        borrow.forEach(map -> {
//            mngUnitIds.add(String.valueOf(map.get("mngUnitId")));
//        });
//
//        //借出
//        String lendStartTime = wrPlanFillInDayAllDTO.getLendStartTime();
//        String lendEndTime = wrPlanFillInDayAllDTO.getLendEndTime();
//        String months = wrPlanFillInDayAllDTO.getMonths();
//        List<Map<String,Object>> lend =   wrPlanFillInDayAllDTO.getLend();
//        lend.forEach(map -> {
//            mngUnitIds.add(String.valueOf(map.get("mngUnitId")));
//        });
//        List<String> mngList = myList(mngUnitIds);
//
//        //启动工作流程获取流程id
//        String porcessId = null;
//        if (subType.equals("3")){//超年 flag == 2
//            porcessId = processId(wrPlanFillInDayAllDTO.getUserId(),wrPlanFillInDayAllDTO.getUserName(),mngList,ActivtciEnum.FLAG_ADOPT_TWO.getId());
//        }else{ //其他 flag == 1
//            porcessId = processId(wrPlanFillInDayAllDTO.getUserId(),wrPlanFillInDayAllDTO.getUserName(),mngList,ActivtciEnum.FLAG_ADOPT_ONE.getId());
//        }
//        // 整合jsonArray生成多条年计划数据
//        //借调方数据
//        Map<String,Object> resustMap = jsonToList(borrow,subType,startTime,endTime,wrPlanFillInDayAllDTO.getUserId(),
//                wrPlanFillInDayAllDTO.getUserName(),wrPlanFillInDayAllDTO.getContent(),
//                wrPlanFillInDayAllDTO.getMngUnitName(),wrPlanFillInDayAllDTO.getMngUnitId(),porcessId);
//
//        List<WrPlanFillinDay> waterPlanFillinMonthList = (List<WrPlanFillinDay>)resustMap.get("wrPlanFillinDayList");
//        //填报计划任务
//        WrPlanTask wrPlanTask = (WrPlanTask) resustMap.get("wrPlanTask");
//        //wrPlanAdjust
//        WrPlanAdjust wrPlanAdjust = (WrPlanAdjust) resustMap.get("wrPlanAdjust");
//
//        transactionTemplate.executeWithoutResult(transactionStatus -> {
//            String planId = String.valueOf(resustMap.get("planId"));
//            String adjustId = String.valueOf(resustMap.get("adjustId"));
//            // 月内（跨引水口）借出方，超年借出方
//            if(subType.equals(RecentPlanEnum.OTHER_BUILDING_IN_MONTH)||subType.equals(RecentPlanEnum.BUILDING_IN_OTHER_WATER_UNIT)){
//                List<WrPlanFillinDay>  wrPlanFillinDayList = wrPlanFillinDayValue(lend,lendStartTime,lendEndTime,content,subType,RecentPlanEnum.LEND_OUT,planId,adjustId);
//                saveBatch(wrPlanFillinDayList);
//            }else if(subType.equals(RecentPlanEnum.BUILDING_IN_OTHER_MONTH)){//跨月借出方
//                List<WrPlanFillinDay>  wrPlanFillinDayList = wrPlanFillinDayValueByTDay(lend,months,content,subType,RecentPlanEnum.LEND_OUT,planId,adjustId);
//                saveBatch(wrPlanFillinDayList);
//            }
//            //批量保存计划水量数据
//            saveBatch(waterPlanFillinMonthList);
//            //更新日迭代数据
//            //updateDay(wrPlanInterDayList);
//            // 添加填报计划任务.
//            wrPlanTaskMapper.insert(wrPlanTask);
//            // 添加调整计划数据
//            wrPlanAdjustMapper.insert(wrPlanAdjust);
//            if(subType.equals("3")){
//                UpdateWrapper<WrRightTrade> updateWrapper = new UpdateWrapper<WrRightTrade>();
//                updateWrapper.eq("UNIQUE_CODE",wrPlanFillInDayAllDTO.getTradeId());
//                WrRightTrade wrRightTrade = new WrRightTrade();
//                wrRightTrade.setStatus(2);
//                wrRightTradeMapper.update(wrRightTrade,updateWrapper);
//            }
//        });
//    }
//    /**
//     *  查询调整计划列表
//     * @param startTime
//     * @param endTime
//     * @param mngUnitId
//     */
//    @Override
//    public DataTableVO findAdjustList(Long startTime, Long endTime, List<String> mngUnitId,Integer pageIndex, Integer pageSize) {
//        List<WrPlanAdjustVO> wrPlanAdjustVOList = new ArrayList<>();
//        QueryWrapper<WrPlanAdjust> queryWrapper = new QueryWrapper<>();
//        if (StringUtils.isNotEmpty(startTime)||StringUtils.isNotEmpty(endTime)){
//            queryWrapper.between("CREATE_TIME",DateUtils.convertTimeToDate(startTime),DateUtils.convertTimeToDate(endTime));
//        }
//        if(StringUtils.isNotEmpty(mngUnitId)){
//            queryWrapper.in("MNG_UNIT_ID",mngUnitId);
//        }
//        IPage<WrPlanAdjust> page = new Page<>(pageIndex, pageSize);
//        IPage<WrPlanAdjust> selectPage = wrPlanAdjustMapper.selectPage(page,queryWrapper);
//        List<WrPlanAdjust> wrPlanAdjustList= selectPage.getRecords();
//        for (WrPlanAdjust wrPlanAdjust:wrPlanAdjustList){
//            WrPlanAdjustVO wrPlanAdjustVO = new WrPlanAdjustVO();
//            BeanUtils.copyProperties(wrPlanAdjust, wrPlanAdjustVO);
//            wrPlanAdjustVO.setCreateTime(DateUtils.convertDateToLong(wrPlanAdjust.getCreateTime()));
//            wrPlanAdjustVO.setStartTime(DateUtils.convertDateToLong(wrPlanAdjust.getStartTime()));
//            wrPlanAdjustVO.setEndTime(DateUtils.convertDateToLong(wrPlanAdjust.getEndTime()));
//            wrPlanAdjustVOList.add(wrPlanAdjustVO);
//        }
//        //result
//        DataTableVO dataTableVO = new DataTableVO();
//        dataTableVO.setRecordsTotal(page.getTotal());
//        dataTableVO.setRecordsFiltered(page.getTotal());
//        dataTableVO.setData(wrPlanAdjustVOList);
//        return dataTableVO;
//    }
//
//    /**
//     *  findAdjustCurve
//     * @param startTime
//     * @param endTime
//     * @param buildingId
//     * @param buildingName
//     * @return
//     */
//    @Override
//    public WrPlanFillinDayVO findAdjustCurve(Long startTime, Long endTime, String buildingId, String buildingName) {
//
//        List<Map<String,Object>> oldWaterValue = new ArrayList<>();
//        List<Map<String,Object>> newWaterValue = new ArrayList<>();
//        QueryWrapper<WrPlanFillinDay> queryWrapper = new QueryWrapper<>();
//        if (StringUtils.isNotEmpty(startTime)||StringUtils.isNotEmpty(endTime)){
//            String startStr =  DateUtils.convertTimeToString(startTime);
//            String endStr =  DateUtils.convertTimeToString(endTime);
//            queryWrapper.eq("YEAR",startStr.substring(0,4));
//            queryWrapper.eq("MONTH",startStr.substring(5,7));
//            queryWrapper.eq("BUILDING_ID",buildingId);
//            queryWrapper.between("DAY",startStr.substring(8,10),endStr.substring(8,10));
//        }
//        queryWrapper.orderByAsc("DAY");
//        List<WrPlanFillinDay> wrPlanFillinDayList = baseMapper.selectList(queryWrapper);
//        WrPlanFillinDayVO wrPlanFillinDayVO = new WrPlanFillinDayVO();
//
//            for (WrPlanFillinDay waterPlanFillinMonth:wrPlanFillinDayList){
//                //调整前
//                String time = waterPlanFillinMonth.getYear()+"-"+waterPlanFillinMonth.getMonth()+"-"+waterPlanFillinMonth.getDay();
//                Map<String,Object> oldWatermap = new HashMap<>();
//                oldWatermap.put("time",time);
//                oldWatermap.put("value",waterPlanFillinMonth.getDemandWaterQuantuty());
//                oldWaterValue.add(oldWatermap);
//                //调整后
//                Map<String,Object> newWatermap = new HashMap<>();
//                newWatermap.put("time",time);
//                newWatermap.put("value",waterPlanFillinMonth.getDemandWaterQuantityAfter());
//                newWaterValue.add(newWatermap);
//
//                wrPlanFillinDayVO.setOldWaterValue(oldWaterValue);
//                wrPlanFillinDayVO.setNewWaterValue(newWaterValue);
//                wrPlanFillinDayVO.setBuildingId(buildingId);
//                wrPlanFillinDayVO.setBuildingName(buildingName);
//            }
//        return wrPlanFillinDayVO;
//    }
//    /**
//     * 通过管理站id查询引水口id
//     * @param mngUnitId
//     * @return
//     */
//    @Override
//    public ResultModel getBuildingIdByMngUnitId(String mngUnitId, String mngUnitName, String waterUnitId, String waterUnitName, List<String> buildingLevels) {
//        QueryWrapper<WaterBuildingManager> wrapper = new QueryWrapper<>();
//        wrapper.eq("wd.MNG_UNIT_ID", mngUnitId);
//        wrapper.in("wd.BUILDING_LEVEL", buildingLevels);
//        List<WrBuildingAndDiversion> wrBuildingAndDiversionList = waterBuildingManagerMapper.getBuildingAndDiversionList(wrapper);
//        //获取用水单位下的引水口
//        List<WrBuildingAndDiversion> wrBuildingAndDiversionListByWater = wrPlanGenerateMonthService.getAllWaterBuildingForAppointUseUnit(waterUnitId);
//        //取交集
//        //List<WrBuildingAndDiversion> result = wrBuildingAndDiversionList.stream().filter(item -> wrBuildingAndDiversionListByWater.contains(item)).collect(Collectors.toList());
//
//        List<WrBuildingAndDiversion> intersectList = wrBuildingAndDiversionListByWater.stream()
//                .filter(pe -> find(pe.getId(), wrBuildingAndDiversionList) > -1).collect(Collectors.toList());
//        if(CollectionUtils.isEmpty(intersectList)){
//            return ResultModelUtils.getInstance(false,"5010",mngUnitName+"管理站与"+waterUnitName+"用水单位下暂未配置引水口");
//        }
//        return ResultModelUtils.getSuccessInstanceExt(intersectList);
//    }
//    public int find(String playerId,List<WrBuildingAndDiversion> list) {
//        int res = -1;
//        for (int i = 0; i < list.size(); i++) {
//            if (list.get(i).getId().equals(playerId)) {
//                res = i;
//                break;
//            }
//        }
//        return res;
//    }
//    //整合json结构获取需存储的近期填报计划与日旬表数据更新
//    private Map<String,Object> jsonToList(List<Map<String,Object>> mapList,String type,String startTime,String endTime,String userId ,String userName,String content,
//                                          String mngUnitName,String mngUnitId,String porcessId){
//        Map<String,Object> resultMap = new HashMap<>();
//        String planType = "0";//月内，单引水口
//        String lendType = "0";//单引水口
//        if (type.equals("2")) { //跨月
//            planType = "1";
//        }
//        if(type.equals("3")){//超年
//            planType = "2";
//        }
//        //计划任务id
//        String planId = IDGenerator.getId();
//        //调整计划id
//        String adjustId = IDGenerator.getId();
//        //近期填报数据
//        List<WrPlanFillinDay> wrPlanFillinDayList = wrPlanFillinDayValue(mapList,startTime,endTime,content,type,lendType,planId,adjustId);
//        //生成调整计划数据
//        WrPlanAdjust wrPlanAdjust = wrPlanAdjust(adjustId,userId,userName,startTime,endTime,mngUnitId,mngUnitName,planType,content);
//       //生成填报计划任务数据
//        WrPlanTask wrPlanTask = wrPlanTask(planId,userId,startTime,content,planType,mngUnitId,mngUnitName,porcessId,startTime,endTime,type);
//        resultMap.put("wrPlanFillinDayList",wrPlanFillinDayList);
//        resultMap.put("wrPlanTask",wrPlanTask);
//        resultMap.put("wrPlanAdjust",wrPlanAdjust);
//        resultMap.put("planId",planId);
//        resultMap.put("adjustId",adjustId);
//
//        return resultMap;
//    }
//    //生成调整计划数据
//    private static WrPlanAdjust wrPlanAdjust(String adjustId,String personId,String userName,String startTime,String endTime,
//                                             String mngUnitId,String mngUnitName,String adjustType,String content){
//        WrPlanAdjust wrPlanAdjust = new WrPlanAdjust();
//        wrPlanAdjust.setId(adjustId);
//        wrPlanAdjust.setPersonId(personId);
//        wrPlanAdjust.setPersonName(userName);
//        wrPlanAdjust.setStartTime(DateUtils.convertStringTimeToDateExt(startTime));
//        wrPlanAdjust.setEndTime(DateUtils.convertStringTimeToDateExt(endTime));
//        wrPlanAdjust.setMngUnitId(mngUnitId);
//        wrPlanAdjust.setMngUnitName(mngUnitName);
//        wrPlanAdjust.setAdjustType(adjustType);
//        wrPlanAdjust.setContent(content);
//        return wrPlanAdjust;
//    }
//    //月内调整与跨月借调方调整
//    private static List<WrPlanFillinDay> wrPlanFillinDayValue(List<Map<String,Object>> mapList,String startTime,String endTime,
//                                                              String content,String type,String lendType,String planId,String adjustId){
//        // type 0 月内 1跨月 2超年
//        //近期填报数据
//        List<WrPlanFillinDay> wrPlanFillinDayList = new ArrayList<>();
//        for(Map<String,Object> valuemap: mapList) {
//            //用水单位ID
//            String waterUnitId = String.valueOf(valuemap.get("waterUnitId"));
//            //用水单位名称
//            String waterUnitName = String.valueOf(valuemap.get("waterUnitName"));
//            //管理单位ID
//            String mngUnitId = String.valueOf(valuemap.get("mngUnitId"));
//            //分水口ID
//            String buildingId = String.valueOf(valuemap.get("buildingId"));
//            //获取天数
//            int dayNum = Integer.valueOf(endTime.substring(endTime.length() - 2, endTime.length())) -
//                    Integer.valueOf(startTime.substring(startTime.length() - 2, startTime.length())) + 1;
//            for (int i = 0; i < dayNum; i++) {
//                //获取当前时间与原计划水量已经调整计划水量数据
//                if (!valuemap.containsKey("oldWaterValue" + i) ) {
//                    break;
//                }
//                //调整前数据
//                Double oldWaterValue = Double.valueOf(String.valueOf(valuemap.get("oldWaterValue" + i)));
//                //调整时间（String）
//                String num = startTime.substring(0, startTime.length() - 2);
//                String day = String.valueOf(Integer.valueOf( startTime.substring(8,10)) + i);
//                if (day.length()==1){
//                    day = "0"+day;
//                }
//                String time = num+day;
//                //时间段类别(0:日，1:旬)
//                String timeType = "0";
//                //旬（1，2，3）
//                //调整前需求流量
//                Double oldFlowValue = oldWaterValue * 10000 / 86400;
//                //调整后数据
//                Double newWaterValue = null;
//                Double newFlowValue = null;
//                if (valuemap.get("newWaterValue" + i)!=null){
//                    newWaterValue = Double.valueOf(String.valueOf(valuemap.get("newWaterValue" + i)));
//                    //调整后需求流量
//                    newFlowValue = newWaterValue * 10000 / 86400;
//                }
//                WrPlanFillinDay waterPlanFillinDay = waterPlanFillinDay(oldWaterValue,oldFlowValue,time,timeType,content,waterUnitId,waterUnitName,
//                        mngUnitId,buildingId,lendType,type,planId,adjustId);
//                waterPlanFillinDay.setDemandWaterQuantityAfter(CommonUtil.number(newWaterValue));
//                waterPlanFillinDay.setDemandWaterFlowAfter(CommonUtil.number(newFlowValue));
//                if(type.equals("1")&&lendType.equals("1")){//月内 借出（日）
//                    //实时引水数据
//                    Double realWaterValue = Double.valueOf(String.valueOf(valuemap.get("realWaterValue" + i)));
//                    //实时引水数据录入
//                    waterPlanFillinDay.setRealWaterQuantity(CommonUtil.number(realWaterValue));
//                    //结余调整数据
//                    Double balanceAdjustment = Double.valueOf(String.valueOf(valuemap.get("balanceAdjustment" + i)));
//                    //调整后数据
//                    newFlowValue = oldWaterValue-(oldWaterValue - realWaterValue-balanceAdjustment);
//                    waterPlanFillinDay.setDemandWaterQuantityAfter(CommonUtil.number(newFlowValue));
//                    waterPlanFillinDay.setDemandWaterFlowAfter(CommonUtil.number(newFlowValue * 10000 / 86400));
//                }
//                wrPlanFillinDayList.add(waterPlanFillinDay);
//            }
//        }
//        return wrPlanFillinDayList;
//    }
//    //跨月借出方调整
//    private static List<WrPlanFillinDay> wrPlanFillinDayValueByTDay(List<Map<String,Object>> mapList,String months,
//                                                              String content,String type,String lendType,String planId,String adjustId){
//        List<WrPlanFillinDay> wrPlanFillinDayList = new ArrayList<>();
//        for(Map<String,Object> valuemap: mapList) {
//            //用水单位ID
//            String waterUnitId = String.valueOf(valuemap.get("waterUnitId"));
//            //用水单位名称
//            String waterUnitName = String.valueOf(valuemap.get("waterUnitName"));
//            //管理单位ID
//            String mngUnitId = String.valueOf(valuemap.get("mngUnitId"));
//            //分水口ID
//            String buildingId = String.valueOf(valuemap.get("buildingId"));
//            //获取旬数
//            List<String>  monthList = Arrays.asList(months.split(","));
//            int dayNum = monthList.size() * 3;
//            for (int i = 0; i < dayNum; i++) {
//                //获取当前时间与原计划水量已经调整计划水量数据
//                if (!valuemap.containsKey("oldWaterValue" + i) ) {
//                    break;
//                }
//                //调整前数据
//                Double oldWaterValue = Double.valueOf(String.valueOf(valuemap.get("oldWaterValue" + i)));
//                //时间段类别(0:日，1:旬)
//                String timeType = "1";
//                //旬（1，2，3）
//                //调整前需求流量
//                Double oldFlowValue = oldWaterValue * 10000 / 86400;//调整后数据
//                Double  newWaterValue = Double.valueOf(String.valueOf(valuemap.get("newWaterValue" + i)));
//                //调整后需求流量
//                Double newFlowValue = newWaterValue * 10000 / 86400;
//                WrPlanFillinDay waterPlanFillinDay = waterPlanFillinDay(oldWaterValue,oldFlowValue,null,timeType,
//                        content,waterUnitId,waterUnitName,mngUnitId,buildingId,lendType,type,planId,adjustId);
//                waterPlanFillinDay.setDemandWaterQuantityAfter(CommonUtil.number(newWaterValue));
//                waterPlanFillinDay.setDemandWaterFlowAfter(CommonUtil.number(newFlowValue));
//
//                if ((i+1 )% 3 == 0){
//                    String yearMonth = monthList.get((i+1 ) / 3 - 1);
//                    List<String> times = new ArrayList<>(Arrays.asList(yearMonth.split("-")));
//                    waterPlanFillinDay.setYear( times.get(0));
//                    waterPlanFillinDay.setMonth( times.get(1));
//                }else{
//                    String yearMonth = monthList.get((i+1 )/3);;
//                    List<String> times = new ArrayList<>(Arrays.asList(yearMonth.split("-")));
//                    waterPlanFillinDay.setYear( times.get(0));
//                    waterPlanFillinDay.setMonth( times.get(1));
//                }
//                int tdaynum = 1;
//                waterPlanFillinDay.setTday(String.valueOf(tdaynum));
//                tdaynum++;
//                if (tdaynum == 3){
//                    tdaynum = 0;
//                }
//                wrPlanFillinDayList.add(waterPlanFillinDay);
//                }
//        }
//        return wrPlanFillinDayList;
//    }
//    // 生成填报计划任务数据
//    private static WrPlanTask wrPlanTask(String planId,String personId,String time,
//                                         String content,String planType,String mngUnitId,
//                                         String mngUnitName,String processId,String startTime,String endTime,String subType){
//        //年份
//        String year = time.substring(0,4);
//        //月份
//        String month = time.substring(5,7);
//        WrPlanTask wrPlanTask = new WrPlanTask();
//        wrPlanTask.setStartDate(DateUtils.convertStringTimeToDateExt(startTime));
//        wrPlanTask.setEndDate(DateUtils.convertStringTimeToDateExt(endTime));
//        wrPlanTask.setId(planId);
//        wrPlanTask.setPersonId(personId);
//        wrPlanTask.setYear(year);
//        wrPlanTask.setMonth(month);
//        wrPlanTask.setCreateDate(new Date());
//        wrPlanTask.setState(TaskStateEnum.UNDER_APPROVAL.getId());//审批中
//        wrPlanTask.setContent(content);
//        wrPlanTask.setPlanType(PlanFillInTypeEnum.DAY_PLAN_FILL_IN.getId());//近期用水计划调整
//        wrPlanTask.setSubType(subType);
//        //流程实例id
//        wrPlanTask.setWaterPlanFillIn(processId);
//        if (planType.equals("0")){
//            wrPlanTask.setTaskName(PlanFillInTypeEnum.DAY_PLAN_FILL_IN_WAM.getName());//月内
//        }else if (planType.equals("1")){
//            wrPlanTask.setTaskName(PlanFillInTypeEnum.DAY_PLAN_FILL_IN_TLI.getName());//跨月
//        }else if(planType.equals("2")){
//            wrPlanTask.setTaskName(PlanFillInTypeEnum.DAY_PLAN_FILL_IN_SUP.getName());//超年
//        }
//        //获取子表信息
//        List<WrPlanTaskSub> wrPlanTaskSubList = new ArrayList<>();
//        WrPlanTaskSub wrPlanTaskSub = new WrPlanTaskSub();
//        wrPlanTaskSub.setTaskId(planId);
//        wrPlanTaskSub.setUnitName(mngUnitName);
//        wrPlanTaskSub.setUnitId(mngUnitId);
//        wrPlanTaskSub.setUnitType(PlanFillInTypeEnum.MNG_UNIT.getId());//管理站id
//        wrPlanTaskSub.setId(IDGenerator.getId());
//        wrPlanTaskSubList.add(wrPlanTaskSub);
//        wrPlanTask.setWrPlanTaskSubList(wrPlanTaskSubList);
//        return wrPlanTask;
//    }
//    //近期计划数据整合公共方法
//    private static WrPlanFillinDay waterPlanFillinDay(Double oldWaterValue,Double oldFlowValue,String time,String timeType,
//                                                      String content, String waterUnitId,String waterUnitName,String mngUnitId,String buildingId,
//                                                      String lendType,String type,String planTaskId,String adjustId){
//        WrPlanFillinDay wrPlanFillinDay = new WrPlanFillinDay();
//        if (StringUtils.isNotEmpty(time)){//非跨月 借出（旬月）
//            wrPlanFillinDay.setYear(time.substring(0,4));
//            wrPlanFillinDay.setMonth(time.substring(5,7));
//            wrPlanFillinDay.setDay(time.substring(time.length() - 2,time.length()));
//        }
//        wrPlanFillinDay.setId(IDGenerator.getId());
//        wrPlanFillinDay.setPlanTaskId(planTaskId);
//        wrPlanFillinDay.setDemandWaterQuantuty(CommonUtil.number(oldWaterValue));
//        wrPlanFillinDay.setDemandWaterFlow(CommonUtil.number(oldFlowValue));
//        wrPlanFillinDay.setTimeType(timeType);
//        wrPlanFillinDay.setContent(content);
//        wrPlanFillinDay.setWaterUnitId(waterUnitId);
//        wrPlanFillinDay.setWaterUnitName(waterUnitName);
//        wrPlanFillinDay.setManageUnitId(mngUnitId);
//        wrPlanFillinDay.setBuildingId(buildingId);
//        //借调借出
//        wrPlanFillinDay.setLendType(lendType);
//        //时间类别
//        if (type.equals("0")) {
//            //填报类型
//            wrPlanFillinDay.setAdjustType(PlanFillInTypeEnum.DAY_PLAN_FILL_IN_WAM.getId());//月内
//        }else if (type.equals("1")){
//            //填报类型
//            wrPlanFillinDay.setAdjustType(PlanFillInTypeEnum.DAY_PLAN_FILL_IN_WAM.getId());//月内
//        }else if(type.equals("2")){
//            wrPlanFillinDay.setAdjustType(PlanFillInTypeEnum.DAY_PLAN_FILL_IN_TLI.getId());//跨月
//        }else if (type.equals("3")){
//            wrPlanFillinDay.setAdjustType(PlanFillInTypeEnum.DAY_PLAN_FILL_IN_SUP.getId());//超年
//        }
//        //调整计划id
//        wrPlanFillinDay.setAdjustId(adjustId);
//        //任务名称
//        wrPlanFillinDay.setPlanName(PlanFillInTypeEnum.DAY_PLAN_FILL_IN.getName());
//        return wrPlanFillinDay;
//    }
//    //获取日或旬月迭代表queryWrapper
//    private <T> QueryWrapper<T> queryWrapper(Long startTime,Long endTime,List<String> buildingId,List<String> months,String adjustType){
//        QueryWrapper<T> queryWrapper = new QueryWrapper<T>();
//        if (adjustType.equals("0")){
//            queryWrapper.between("SUPPLY_TIME", DateUtils.convertTimeToDate(startTime),DateUtils.convertTimeToDate(endTime));
//        }
//        if(CollectionUtils.isNotEmpty(buildingId)){
//            queryWrapper.in("BUILDING_ID",buildingId);
//        }
//        if (adjustType.equals("1")){
//            if(months.size()>0){
//                //开始时间
//                Date startTimeMonth =DateUtils.convertStringTimeToDateExt(months.get(0)+"-01");
//                //结束时间
//                Date endTimeMonth = DateUtils.convertStringTimeToDateExt(months.get(months.size()-1)+"-21");
//                queryWrapper.between("SUPPLY_TIME",startTimeMonth,endTimeMonth);
//                queryWrapper.ne("TIME_TYPE","4");
//            }
//        }
//        queryWrapper.orderByAsc("SUPPLY_TIME");
//        return queryWrapper;
//    }
//    //整合旬迭代表数据
//    private List<WrPlanFillinDayVO> wrPlanFillinTday(List<WrPlanInterTday> wrPlanInterTdayList,Map<String,Object> buildingMap){
//
//        List<WrPlanFillinDayVO> wrPlanFillinDayVOList = new ArrayList<>();
//        WrPlanFillinDayVO wrPlanFillinDayVO = new WrPlanFillinDayVO();
//        List<BigDecimal> waterQuantityList = new ArrayList<>();
//        for (WrPlanInterTday wrPlanInterTday:wrPlanInterTdayList){
//            waterQuantityList.add(wrPlanInterTday.getWaterQuantity());
//        }
//        String buildingId = null;
//        if (wrPlanInterTdayList.size()>0){
//            buildingId = wrPlanInterTdayList.get(0).getBuildingId();
//        }
//        String buildName = String.valueOf(buildingMap.get(buildingId));
//        wrPlanFillinDayVO.setOldWaterValue(waterQuantityList);
//        wrPlanFillinDayVO.setBuildingId(buildingId);
//        wrPlanFillinDayVO.setBuildingName(buildName);
//        wrPlanFillinDayVO.setName("原计划");
//        wrPlanFillinDayVOList.add(wrPlanFillinDayVO);
//
//        WrPlanFillinDayVO wrPlanFillinDayVOTwo = wrPlanFillinDayVO("调整后",buildingId,buildName,true,"","","");
//        wrPlanFillinDayVOTwo.setNewWaterValue(waterQuantityList);
//        wrPlanFillinDayVOTwo.setInputType("number");
//        wrPlanFillinDayVOList.add(wrPlanFillinDayVOTwo);
//
//        WrPlanFillinDayVO wrPlanFillinDayVOThree = wrPlanFillinDayVO("差值",buildingId,buildName,"",true,"","");
//        wrPlanFillinDayVOList.add(wrPlanFillinDayVOThree);
//        return wrPlanFillinDayVOList;
//    }
//    //整合日迭代表数据
//    private List<WrPlanFillinDayVO> wrPlanFillinDay(List<WrPlanInterDay> wrPlanInterDayList,Map<String,Object> buildingMap){
//
//        List<WrPlanFillinDayVO> wrPlanFillinDayVOList = new ArrayList<>();
//        WrPlanFillinDayVO wrPlanFillinDayVO = new WrPlanFillinDayVO();
//        List<BigDecimal> waterQuantityList = new ArrayList<>();
//
//        for (WrPlanInterDay wrPlanInterDay:wrPlanInterDayList){
//            waterQuantityList.add(wrPlanInterDay.getWaterQuantity());
//        }
//        String buildingId = null;
//        if (wrPlanInterDayList.size()>0){
//            buildingId = wrPlanInterDayList.get(0).getBuildingId();
//        }
//
//        String buildName = String.valueOf(buildingMap.get(buildingId));
//        wrPlanFillinDayVO.setOldWaterValue(waterQuantityList);
//        wrPlanFillinDayVO.setName("原计划");
//        wrPlanFillinDayVO.setBuildingId(buildingId);
//        wrPlanFillinDayVO.setBuildingName(buildName);
//        wrPlanFillinDayVOList.add(wrPlanFillinDayVO);
//
//        WrPlanFillinDayVO wrPlanFillinDayVOTwo = wrPlanFillinDayVO("调整后",buildingId,buildName,true,"","","");
//        wrPlanFillinDayVOTwo.setInputType("number");
//        wrPlanFillinDayVOTwo.setNewWaterValue(waterQuantityList);
//        wrPlanFillinDayVOList.add(wrPlanFillinDayVOTwo);
//
//        WrPlanFillinDayVO wrPlanFillinDayVOThree = wrPlanFillinDayVO("差值",buildingId,buildName,"",true,"","");
//        wrPlanFillinDayVOList.add(wrPlanFillinDayVOThree);
//
//        return wrPlanFillinDayVOList;
//    }
//    //补全差值差值百分比数据
//    private WrPlanFillinDayVO wrPlanFillinDayVO(String name,String buildingId,String buildingName,
//                                                Object adjustDifference,Object difference,Object differencePercentage,Object differenceEdit){
//        WrPlanFillinDayVO wrPlanFillinDayVO = new WrPlanFillinDayVO();
//        wrPlanFillinDayVO.setName(name);
//        wrPlanFillinDayVO.setBuildingId(buildingId);
//        wrPlanFillinDayVO.setBuildingName(buildingName);
//        wrPlanFillinDayVO.setAdjustDifference(adjustDifference);
//        wrPlanFillinDayVO.setDifference(difference);
//        wrPlanFillinDayVO.setDifferencePercentage(differencePercentage);
//        wrPlanFillinDayVO.setDifferenceEdit(differenceEdit);
//        return wrPlanFillinDayVO;
//    }
//    //将引水口与id整合到一个map中
//    private Map<String,Object> buildingMap(List<String> buildingId,List<String> buildingName){
//        //获取引水口id与引水口名称对应关系
//        Integer size = buildingId.size();
//        Map<String,Object> buildingMap = new HashMap<>();
//        for(int i=0;i<size;i++){
//            buildingMap.put(buildingId.get(i),buildingName.get(i));
//        }
//        return buildingMap;
//    }
//    //启动流程,获取流程id
//    private String processId(String userId,String userName,List<String> mngUnitIds,String flag){
//        ActivitiHandle activitiHandle = new ActivitiHandle();
//        //flowKey值
//        activitiHandle.setFlowKey("plan_day_id");
//        Map<String,Object> UserInfoMap = new HashMap<>();
//        UserInfoMap.put("id",userId);
//        UserInfoMap.put("name",userName);
//        activitiHandle.setUserInfo(UserInfoMap);
//        activitiHandle.setSrc("promng");
//        activitiHandle.setHandleType("submit");
//        Map<String,Object> map = new HashMap<>();
//        map.put("flag",flag);
//        activitiHandle.setExpression("flag=="+flag);//判断网关分支flag == 1 // flag ==2
//        activitiHandle.setVariables(map);
//        if (flag.equals(ActivtciEnum.FLAG_ADOPT_ONE.getId())){
//            Map<String,Object> unitIdMap = new HashMap<>();
//            unitIdMap.put("unitId",mngUnitIds);
//            activitiHandle.setParam(unitIdMap);
//        }
//        Map<String,Object> activiciMap = activiciTaskService.getProcessInstanceList(activitiHandle);
//        //流程id
//        String processId = String.valueOf(activiciMap.get("processId"));
//        return processId;
//    }
//    //去重
//    private static List<String> myList(List<String> list){
//        List<String> myList = list.stream().distinct().collect(Collectors.toList());
//        return myList ;
//    }
//}
