package com.nari.slsd.msrv.waterdiversion.services;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.msrv.common.exception.TransactionException;
import com.nari.slsd.msrv.common.ext.enums.CodeEnum;
import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.DateUtils;
import com.nari.slsd.msrv.common.utils.IDGenerator;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.msrv.waterdiversion.commons.PlanFillInTypeEnum;
import com.nari.slsd.msrv.waterdiversion.commons.TaskStateEnum;
import com.nari.slsd.msrv.waterdiversion.commons.WrBuildingEnum;
import com.nari.slsd.msrv.waterdiversion.interfaces.*;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WaterBuildingManagerMapper;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WaterPlanFillinYearMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.*;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.*;
import com.nari.slsd.msrv.waterdiversion.model.vo.BuildingExt;
import com.nari.slsd.msrv.waterdiversion.model.vo.PlanFillinStateExt;
import com.nari.slsd.msrv.waterdiversion.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.w3c.dom.stylesheets.LinkStyle;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 年度计划填报相关实现类（new）
 * @Author ZHS
 * @Date 2021/9/7 12:37
 */
@Slf4j
@Service
public class WrPlanFillInYearServiceImp extends ServiceImpl<WaterPlanFillinYearMapper, WaterPlanFillinYear> implements IWrPlanFillInYearService {
    @Resource
    IWaterBuildingManagerService waterBuildingManagerService;
    @Resource
    WaterPlanFillinYearMapper waterPlanFillinYearMapper;
    @Resource
    TransactionTemplate transactionTemplate;
    @Resource
    IWrPlanInterTdayService wrPlanInterTdayAndMonthService;
    @Resource
    IWrPlanTaskService wrPlanTaskService;
    @Resource
    IWrPlanFillinService wrPlanFillinService;
    @Resource
    IWrPlanInterTdayService wrPlanInterTdayService;
    @Resource
    WaterBuildingManagerMapper waterBuildingManagerMapper;
    /**
     *  年填报数据相关数据保存
     * @param wrPlanFileYearValue
     */
    @Override
    public ResultModel addWaterPlanValue(WrPlanFillInValue wrPlanFileYearValue) {
        //获取年填报相关数据信息
        List<Map<String,Object>> fillinValue = wrPlanFileYearValue.getFillinValue();
        //填报人id
        String userId = wrPlanFileYearValue.getUserId();
        //填报内容
        String content = wrPlanFileYearValue.getContent();
        //
        String time = wrPlanFileYearValue.getTime();
        try {
            // 整合jsonArray生成多条年计划数据
            WrPlanFillinRelevantTable resust = fillinTask(fillinValue,userId,content,time);
            List<WaterPlanFillinYear> waterPlanFillinYearList = resust.getWaterPlanFillinYear();
            //整合多条年旬月计划数据
            List<WrPlanInterTday> wrPlanInterTdayAndMonthList = resust.getWrPlanInterTday();
            //整合计划任务数据
            WrPlanTask wrPlanTask = resust.getWrPlanTask();
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                //批量保存计划水量数据
                saveBatch(waterPlanFillinYearList);
                wrPlanInterTdayAndMonthService.saveBatch(wrPlanInterTdayAndMonthList);
                wrPlanTaskService.insert(wrPlanTask);
            });
            return ResultModelUtils.getAddSuccessInstance(true);
        }catch(Exception e){
            log.error("保存失败：{}",e.getMessage());
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 根据时间（年 月 旬）引水口来修改 月总
     * @param wrPlanFillInValue
     * @return
     */

    @Override
    public ResultModel updateWaterPlanValue(WrPlanFillInValue wrPlanFillInValue) {
        List<Map<String,Object>> fillInValue =  wrPlanFillInValue.getFillinValue();
        String year = wrPlanFillInValue.getTime();
        WrPlanFillinRelevantTable wrPlanFillinRelevantTable = fillInAndTDayValue(fillInValue,year);
        try {
            //需更新旬迭代数据
            List<WrPlanInterTdayDTO> wrPlanInterTDayDTOList = wrPlanFillinRelevantTable.getWrPlanInterTDayDTOList();
            //需更新的年填报数据
            List<WaterPlanFillinYearDTO> waterPlanFillInYearList = wrPlanFillinRelevantTable.getWaterPlanFillInYearDTOList();
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                wrPlanInterTdayService.updateTday(wrPlanInterTDayDTOList);
                for (WaterPlanFillinYearDTO waterPlanFillinYearDTO:waterPlanFillInYearList){
                    UpdateWrapper<WaterPlanFillinYear> updateWrapper = new UpdateWrapper<WaterPlanFillinYear>();
                    updateWrapper.eq("BUILDING_ID",waterPlanFillinYearDTO.getBuildingId());
                    updateWrapper.eq("YEAR",waterPlanFillinYearDTO.getYear());
                    updateWrapper.eq("MONTH",waterPlanFillinYearDTO.getMonth());
                    updateWrapper.eq("TDAY",waterPlanFillinYearDTO.getTday());
                    WaterPlanFillinYear waterPlanFillinYears = new WaterPlanFillinYear();
                    waterPlanFillinYears.setDemadWaterQuantity(waterPlanFillinYearDTO.getDemadWaterQuantity());
                    waterPlanFillinYears.setDemadWaterFlow(waterPlanFillinYearDTO.getDemadWaterFlow());
                    waterPlanFillinYearMapper.update(waterPlanFillinYears,updateWrapper);
                }
            });
            return ResultModelUtils.getEdiSuccessInstance(true);
        }catch(Exception e){
            log.error("更新失败：{}",e.getMessage());
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     *  查询年填报数据（管理站）
     * @param time
     * @param mngUnitId 管理站
     * @param unitLevels
     * @param buildingType
     * @param fillReport
     * @param buildingLevels
     * @return
     */
    @Override
    public PlanFillinStateExt findUnitByCodAndTime(String time,List<String> mngUnitId,List<Integer> unitLevels, List<String> buildingType, Integer fillReport, List<Integer> buildingLevels) {
        PlanFillinStateExt planFillinStateExt = new PlanFillinStateExt();
        String state = wrPlanFillinService.fillInState(mngUnitId,time,null);
        //通过管理站集合获取管理站-用水单位-引水口层级
        List<BuildingExt> buildingExts = waterBuildingManagerService.getBuildingExtListByMng(mngUnitId,unitLevels,buildingType,fillReport,buildingLevels);
        if (buildingExts.size()==0){
            return planFillinStateExt;
        }
        //获取年填报状态
        //Map<String,Object> map = wrPlanFillinService.state(userId,unitLevels,buildingType,time,null,fillReport,levels);
        //获取数据层级关系展示
        planFillinStateExt.setBuildingExtList(buildingExts);
        //用户已填报
        if (state.equals("1")){
            //获取管理下的填报数据
            List<WaterPlanFillinYear> waterPlanFillinYearList= waterPlanFillinYearList(time,mngUnitId);
            buildingExts = planValue(waterPlanFillinYearList,buildingExts,state);
            planFillinStateExt.setBuildingExtList(buildingExts);
            planFillinStateExt.setState(state);
        }else if(state.equals("0")){
           //用户未填报
            for (BuildingExt building:buildingExts){
                building.setData(new ArrayList<>());
                //0:未填报 1：已填报
                building.setState(state);
            }
            planFillinStateExt.setBuildingExtList(buildingExts);
            planFillinStateExt.setState(state);
        }
        return planFillinStateExt;
    }

    /**
     *  年计划汇总(管理单位)
     * @param year
     * @param userId
     * @param mngUnitId
     * @param unitLevels
     * @param buildingType
     * @param fillReport
     * @return
     */
    @Override
    public List<BuildingExt> findPlanAllValueByCodAndTime(String year, String userId, List<String> mngUnitId, List<Integer> unitLevels, List<String> buildingType, Integer fillReport, List<Integer> levels,String type) {
        //QueryWrapper<WaterBuildingManager> wrapper = new QueryWrapper<WaterBuildingManager>();
        //wrapper.eq();
        //waterBuildingManagerMapper.getBuildingAndDiversionList(wrapper);
        //List<BuildingExt>  buildingExt = waterBuildingManagerService.getBuildingExtListByMng(mngUnitId,unitLevels,buildingType,fillReport,levels);

        List<BuildingExt> buildingExt = new ArrayList<>();
        //System.out.println(buildingExt);
        //用水户预览
        if (StringUtils.isNotEmpty(userId)){
            buildingExt =  waterBuildingManagerService.getBuildingExtListByUser(userId,unitLevels , buildingType,fillReport,levels);
            //管理站预览
        }else {
            buildingExt = waterBuildingManagerService.getBuildingExtListByMng(mngUnitId,unitLevels,buildingType,fillReport,levels);
        }
        //获取用户权限下的单位
        List<BuildingExt> result = new ArrayList<>();
        if("1".equals(type)){
            result = planValue(waterPlanFillinYearList(year,mngUnitId),buildingExt,"1");
        }else if("0".equals(type)){
            result = findPlanAllBybuildId(year,mngUnitId,buildingExt);
        }
        return result;
    }

    @Override
    public List<BuildingExt> findPlanAllBybuildId(String year, List<String> mngUnitId, List<BuildingExt> buildingExts) {
        //a.YEAR = '2021' and b.PID IS NOT NULL and b.PID != ''
        QueryWrapper<WaterPlanFillinYearAndPId> wrapper = new QueryWrapper<>();
        wrapper.eq("a.YEAR",year);
        wrapper.in("a.MANAGE_UNIT_ID",mngUnitId);
        wrapper.ne("a.TDAY","4");
        wrapper.isNotNull("b.PID");
        wrapper.ne("b.PID","");
        List<WaterPlanFillinYearAndPId> waterPlanFillinYearAndPIds = waterPlanFillinYearMapper.getWaterPlanFillinYearAndPId(wrapper);

        QueryWrapper<WaterPlanFillinYearAndPId> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("a.YEAR",year);
        queryWrapper.ne("a.TDAY","4");
        queryWrapper.in("a.MANAGE_UNIT_ID",mngUnitId);
        queryWrapper.isNull("b.PID").or().eq("b.PID","");
        List<WaterPlanFillinYearAndPId> waterPlanFillinYearAndPIdNull = waterPlanFillinYearMapper.getWaterPlanFillinYearAndPIdisNull(queryWrapper);
        waterPlanFillinYearAndPIds.addAll(waterPlanFillinYearAndPIdNull);

        if (buildingExts.size()==0){
            throw new TransactionException(CodeEnum.NO_PARAM,"暂无数据");
        }
        buildingExts = planValueByBuildingId(waterPlanFillinYearAndPIds,buildingExts,null);
        return buildingExts;
    }
    // 获取当前条件下的填报数据
    private List<BuildingExt> planValueByBuildingId(List<WaterPlanFillinYearAndPId> waterPlanFillinYearAndPIds ,List<BuildingExt> buildingExt,String state){
        if (waterPlanFillinYearAndPIds.size()>0){
            //根据引水口进行分类
            Map<String, List<WaterPlanFillinYearAndPId>> buildingGroupMap = waterPlanFillinYearAndPIds.stream().collect(Collectors.groupingBy(WaterPlanFillinYearAndPId::getBuildingId));
            for (String buildingCode: buildingGroupMap.keySet()) {
                List<WaterPlanFillinYearAndPId> hourdbs = buildingGroupMap.get(buildingCode);
                // month升序
                Comparator<WaterPlanFillinYearAndPId> byIdASC = Comparator.comparing(WaterPlanFillinYearAndPId::getMonth);
                List<WaterPlanFillinYearAndPId> result = hourdbs.stream().sorted(byIdASC).collect(Collectors.toList());
                List<BigDecimal> bigDecimal = Arrays.asList(new BigDecimal[36]);
                for (WaterPlanFillinYearAndPId waterPlanFillinYear:result){
                    if ("4".equals(waterPlanFillinYear.getTday())){
                        continue;
                    }
                    Integer month = Integer.valueOf(waterPlanFillinYear.getMonth());
                    Integer tday =  Integer.valueOf(waterPlanFillinYear.getTday());
                    //计算值所在集合位置
                    Integer num = tday+(month-1)*3-1;
                    if (num==36){
                        throw new TransactionException(CodeEnum.NO_PARAM,"参数为null");
                    }
                    bigDecimal.set(num,waterPlanFillinYear.getDemadWaterQuantity());
                }
                for (BuildingExt building:buildingExt){
                    if(building.getData()==null){
                        building.setData(new ArrayList<BigDecimal>());
                    }
                    if(building.getBuildingId().equals(buildingCode)){
                        building.setData(bigDecimal);
                        building.setState(state);
                    }
                }
            }
        }
        return buildingExt;
    }
    //获取填报数据与旬月修改数据
    private WrPlanFillinRelevantTable fillInAndTDayValue(List<Map<String,Object>> fillInValue,String year){
        List<WaterPlanFillinYearDTO> waterPlanFillInYearDTOList = new ArrayList<>();
        List<WrPlanInterTdayDTO> wrPlanInterTDayDTOList = new ArrayList<>();
        for(Map<String,Object> valueMap:fillInValue){
            String month = null;
            //月总需求水量
            Double monthWaterDifference = 0.0;

            for (int i = 0; i < 36; i++) {
                if (!valueMap.containsKey("planValue" + i)){
                    continue;
                }
                //获取月份
                month = MonthOrTday("month",i);
                //获取旬类别
                String tDay = MonthOrTday("tDay",i);
                //获取引水口id
                String buildingId = String.valueOf(valueMap.get("buildingId"));
                //获取当前旬月对应的秒数
                Double secondNum = findSecond(year,month,tDay);
                //获取需求水量
                BigDecimal waterQuantity = new BigDecimal(String.valueOf(valueMap.get("planValue" + i)));
                //获取需求流量
                BigDecimal waterFlow =CommonUtil.number(waterQuantity.doubleValue()/secondNum);
                //
                waterPlanFillInYearDTOList.add(waterPlanFillinYearDTO(buildingId,year,month,tDay,waterQuantity,waterFlow));

                //根据年月旬获取当前date时间
                Date dayTime =dateTime(month,year,tDay);
                wrPlanInterTDayDTOList.add(wrPlanInterTDayDTO(buildingId,dayTime,waterQuantity,waterFlow));
                //获取上次的需求水量
                Double oldWaterQuantity = oldWaterQuantity(dayTime,buildingId,null);
                //获取需求水量差值
                Double difference = waterQuantity.doubleValue()-oldWaterQuantity;

                monthWaterDifference += difference;

                if ("3".equals(tDay)){
                    Date monthDayTime =dateTime(month,year,"4");
                    //获取上次的需求水量
                    Double oldMonthWaterQuantity = oldWaterQuantity(monthDayTime,buildingId,"4");
                    //月总需求水量
                    Double monthWaterQuantityValue = oldMonthWaterQuantity+monthWaterDifference;
                    BigDecimal monthWaterQuantity = CommonUtil.number(monthWaterQuantityValue);

                    //月秒数
                    Double monthSecondNum =sum(year,month);
                    //月总需求流量
                    BigDecimal monthWaterFlow = CommonUtil.number(monthWaterQuantityValue/monthSecondNum);
                    waterPlanFillInYearDTOList.add(waterPlanFillinYearDTO(buildingId,year,month,"4",monthWaterQuantity,monthWaterFlow));

                    wrPlanInterTDayDTOList.add(wrPlanInterTDayDTO(buildingId,monthDayTime,monthWaterQuantity,monthWaterFlow));
                    monthWaterDifference = 0.0;
                }
            }
        }
        WrPlanFillinRelevantTable wrPlanFillinRelevantTable = new WrPlanFillinRelevantTable();
        wrPlanFillinRelevantTable.setWaterPlanFillInYearDTOList(waterPlanFillInYearDTOList);
        wrPlanFillinRelevantTable.setWrPlanInterTDayDTOList(wrPlanInterTDayDTOList);
        return wrPlanFillinRelevantTable;
    }
    //需要更新的年数据（DTO类）
    private  WaterPlanFillinYearDTO waterPlanFillinYearDTO(String buildingId, String year,String month,String tDay,BigDecimal waterQuantity,BigDecimal waterFlow){
        WaterPlanFillinYearDTO waterPlanFillinYearDTO = new WaterPlanFillinYearDTO();
        waterPlanFillinYearDTO.setBuildingId(buildingId);
        waterPlanFillinYearDTO.setYear(year);
        waterPlanFillinYearDTO.setMonth(month);
        waterPlanFillinYearDTO.setTday(tDay);
        waterPlanFillinYearDTO.setDemadWaterQuantity(waterQuantity);
        waterPlanFillinYearDTO.setDemadWaterFlow(waterFlow);
        return waterPlanFillinYearDTO;
    }
    //需要更新的旬月数据（DTO类）
    private  WrPlanInterTdayDTO wrPlanInterTDayDTO(String buildingId, Date time,BigDecimal waterQuantity,BigDecimal waterFlow){
        WrPlanInterTdayDTO wrPlanInterTDayDTO = new WrPlanInterTdayDTO();
        wrPlanInterTDayDTO.setBuildingId(buildingId);
        wrPlanInterTDayDTO.setSupplyTime(time);
        wrPlanInterTDayDTO.setWaterQuantity(waterQuantity);
        wrPlanInterTDayDTO.setWaterFlow(waterFlow);

        return wrPlanInterTDayDTO;
    }
    //查询单条数据获取差值
    private Double oldWaterQuantity(Date time, String buildingId,String timeType){
        WrPlanInterTday wrPlanInterTday = wrPlanInterTdayService.wrPlanInterTday(time,buildingId,timeType);
        if (wrPlanInterTday==null){
            return 0.0;
        }
        Double waterQuantity = wrPlanInterTday.getWaterQuantity().doubleValue();
        return waterQuantity;
    }
    // 获取当前条件下的填报数据
    private List<BuildingExt> planValue( List<WaterPlanFillinYear> waterPlanFillinYears,List<BuildingExt> buildingExt,String state){
        if (waterPlanFillinYears.size()>0){
            //根据引水口进行分类
            Map<String, List<WaterPlanFillinYear>> buildingGroupMap = waterPlanFillinYears.stream().collect(Collectors.groupingBy(WaterPlanFillinYear::getBuildingId));
            for (String buildingCode: buildingGroupMap.keySet()) {
                List<WaterPlanFillinYear> hourdbs = buildingGroupMap.get(buildingCode);
                // month升序
                Comparator<WaterPlanFillinYear> byIdASC = Comparator.comparing(WaterPlanFillinYear::getMonth);
                List<WaterPlanFillinYear> result = hourdbs.stream().sorted(byIdASC).collect(Collectors.toList());
                List<BigDecimal> bigDecimal = Arrays.asList(new BigDecimal[36]);
                for (WaterPlanFillinYear waterPlanFillinYear:result){
                    if ("4".equals(waterPlanFillinYear.getTday())){
                        continue;
                    }
                    Integer month = Integer.valueOf(waterPlanFillinYear.getMonth());
                    Integer tday =  Integer.valueOf(waterPlanFillinYear.getTday());
                    //计算值所在集合位置
                    Integer num = tday+(month-1)*3-1;
                    bigDecimal.set(num,waterPlanFillinYear.getDemadWaterQuantity());
                }
                for (BuildingExt building:buildingExt){
                    if(building.getData()==null){
                        building.setData(new ArrayList<BigDecimal>());
                    }
                    if(building.getBuildingId().equals(buildingCode)){
                        building.setData(bigDecimal);
                        building.setState(state);
                    }
                }
            }
        }
        return buildingExt;
    }
    //获取管理下的填报数据
    private List<WaterPlanFillinYear> waterPlanFillinYearList(String time,List<String> mngUnitId){
        QueryWrapper<WaterPlanFillinYear> wrapper = wrapper(time,mngUnitId);
        List<WaterPlanFillinYear> waterPlanFillinYears = waterPlanFillinYearMapper.selectList(wrapper);
        return waterPlanFillinYears;
    }
    //wrapper
    private  QueryWrapper<WaterPlanFillinYear> wrapper(String year,List<String> mngUnitId){
        QueryWrapper<WaterPlanFillinYear> wrapper = new QueryWrapper<>();
        wrapper.eq("YEAR",year);
        if(mngUnitId.size()>0){
            wrapper.in("MANAGE_UNIT_ID",mngUnitId);
        }
        return  wrapper;
    }
    // 整合jsonArray生成多条年计划填报数据
    private WrPlanFillinRelevantTable fillinTask(List<Map<String,Object>> fillinValue,String userId,String content,String year){
        WrPlanFillinRelevantTable wrPlanFillinRelevantTable = new WrPlanFillinRelevantTable();
        //年填报数据保存
        List<WaterPlanFillinYear> waterPlanFillinYearList = new ArrayList<>();
        //旬月数据保存
        List<WrPlanInterTday> wrPlanInterTdayList = new ArrayList<>();
        //任务id
        String planId = IDGenerator.getId();
        //管理单位id
         String  mngUnitId = null;
        //管理单位名称
        String mngUnitName = null;
        //填报类型
        String planType = PlanFillInTypeEnum.YEAR_PLAN_FILL_IN.getId();
        //计划名称
        String taskNmae = PlanFillInTypeEnum.YEAR_PLAN_FILL_IN.getName();
        for(Map<String,Object> valuemap:fillinValue){
            //单个引水口月总数据
            Double monthPlanValue = 0.0;

            //获取36组旬月数据
            for (int i = 0; i < 36; i++) {
                //当前旬月填报数据，i为填报位置

               if (valuemap.get("planValue" + i)==null){
                    continue;
               }
                Double planValue = Double.valueOf(String.valueOf(valuemap.get("planValue" + i)));

                //获取当前旬月类别
                String tDay = MonthOrTday("tDay",i);
                //获取当前月份
                String month = MonthOrTday("month",i);
                //获取当前旬类别
                String tDayNum = String.valueOf(tDay);
                //当前年月旬对应的时间
                Date dayTime =dateTime(month,year,tDayNum);
                //获取当前旬月对应的秒数
                Double secondNum = findSecond(year,month,tDay);
                //获取管理站,用水单位,引水口对应id
                String waterUnitId =String.valueOf(valuemap.get("waterUnitId"));
                mngUnitId = String.valueOf(valuemap.get("manageUnitId"));
                mngUnitName =  String.valueOf(valuemap.get("manageUnitName"));
                String buildingId =String.valueOf(valuemap.get("buildingId"));

                WaterPlanFillinYear waterPlanFillinYear = waterPlanFillinYear(planValue,secondNum,year,month,tDay,waterUnitId,
                            mngUnitId,buildingId,planId);
                //年计划填报数据
                waterPlanFillinYearList.add(waterPlanFillinYear);
                //旬月迭代数据
                WrPlanInterTday wrPlanInterTdayAndMonth = wrPlanInterTdayAndMonth(buildingId,dayTime,secondNum,planValue,tDay);
                wrPlanInterTdayList.add(wrPlanInterTdayAndMonth);

                //获取全月累计值
                monthPlanValue = monthPlanValue+planValue;
                if (tDay.equals("3")){
                    //月总填报数据
                    secondNum = 30.0*86400;
                    WaterPlanFillinYear waterMonthPlanFillinYear = waterPlanFillinYear(monthPlanValue,secondNum,year,month,"4",waterUnitId,
                            mngUnitId,buildingId,planId);
                    waterPlanFillinYearList.add(waterMonthPlanFillinYear);
                    //月总迭代数据
                    dayTime =dateTime(month,year,"4");
                    WrPlanInterTday wrMonthPlanInterTdayAndMonth = wrPlanInterTdayAndMonth(buildingId,dayTime,secondNum,monthPlanValue,"4");
                    wrPlanInterTdayList.add(wrMonthPlanInterTdayAndMonth);
                    monthPlanValue = 0.0;
                }
            }
        }
        //填报任务数据
        WrPlanTask wrPlanTask = wrPlanFillinService.wrPlanTask(planId,mngUnitId,mngUnitName,userId,content,null,year,planType,taskNmae);;
        wrPlanFillinRelevantTable.setWaterPlanFillinYear(waterPlanFillinYearList);
        wrPlanFillinRelevantTable.setWrPlanInterTday(wrPlanInterTdayList);
        wrPlanFillinRelevantTable.setWrPlanTask(wrPlanTask);
        return wrPlanFillinRelevantTable;
    }

    //根据当前填报位置i，获取当前月数据与旬数据
    private static String MonthOrTday(String dayType,int i){
        String monthOrTday = null;
        int monthOrTdayNum = 0;
        int num = i+1;
        //获取月份
        if (num%3==0){
            monthOrTdayNum = num/3;
        }else{
            monthOrTdayNum = num/3+1;
        }
        if (dayType.equals("month")){
            monthOrTday = String.valueOf(monthOrTdayNum);
        }
        if (dayType.equals("tDay")){
            //计算值所在集合位置
            monthOrTday = String.valueOf(num-(monthOrTdayNum-1)*3);
        }
        return monthOrTday;
    }
    //根据年月旬获取DATE时间格式
    private static Date dateTime(String month,String year,String tDay){
        if (month.length()<2){
            month ="0"+month;
        }
        String dateTime = null;
        if (tDay.equals("1")){
            dateTime = year+"-"+month+"-"+"0"+tDay;
        }else if (tDay.equals("2")){
            dateTime = year+"-"+month+"-"+"11";
        }else if (tDay.equals("3")){
            dateTime = year+"-"+month+"-"+"21";
        }else if (tDay.equals("4")){
            dateTime = year+"-"+month+"-"+"01";
        }
        Date date = DateUtils.convertStringTimeToDateExt(dateTime);
        return date;
    }
    //获取某年某月某旬对应的秒数
    private static Double findSecond(String year,String month,String tday){
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, Integer.valueOf(year));
        a.set(Calendar.MONTH, Integer.valueOf(month) - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        //86400
        if ("3".equals(tday)){
            maxDate = maxDate-20;
        }
        Double secondNumber = Double.valueOf(maxDate)*86400;
        return  secondNumber;
    }
    //年计划数据整合公共方法
    private static WaterPlanFillinYear waterPlanFillinYear(Double planValue,Double secondNum,String yearNum,String month,
                                                           String tDay,String waterUnitId,String manageUnitId,String buildingId,
                                                           String planId){
        WaterPlanFillinYear waterPlanFillinYear = new WaterPlanFillinYear();
        //需求水量demadWaterQuantity
        BigDecimal demadWaterQuantity = new BigDecimal(planValue);
        waterPlanFillinYear.setDemadWaterQuantity(demadWaterQuantity);
        //需求流量demadWaterFlow
        BigDecimal demadWaterFlow = new BigDecimal( planValue/secondNum);
        waterPlanFillinYear.setDemadWaterFlow(demadWaterFlow);
        waterPlanFillinYear.setYear(yearNum);
        waterPlanFillinYear.setPlanName(PlanFillInTypeEnum.YEAR_PLAN_FILL_IN.getName());
        waterPlanFillinYear.setMonth(month);
        //主键id
        waterPlanFillinYear.setId(IDGenerator.getId());
        waterPlanFillinYear.setTday(tDay);
        waterPlanFillinYear.setWaterUnitId(waterUnitId);
        waterPlanFillinYear.setManageUnitId(manageUnitId);
        waterPlanFillinYear.setBuildingId(buildingId);
        //计划id
        waterPlanFillinYear.setPlanTaskId(planId);
        return waterPlanFillinYear;
    }
    //旬月迭代表数据整理
    private static WrPlanInterTday wrPlanInterTdayAndMonth(String buildingId, Date time, Double secondNum, Double planValue, String tdayType){
        //需求水量demadWaterQuantity
        BigDecimal demadWaterQuantity = new BigDecimal(planValue);
        //需求流量demadWaterFlow
        BigDecimal demadWaterFlow = new BigDecimal( planValue/secondNum);
        WrPlanInterTday wrPlanInterTdayAndMonth = new WrPlanInterTday();
        wrPlanInterTdayAndMonth.setId(IDGenerator.getId());
        wrPlanInterTdayAndMonth.setBuildingId(buildingId);
        wrPlanInterTdayAndMonth.setSupplyTime(time);
        wrPlanInterTdayAndMonth.setWaterFlow(demadWaterFlow);
        wrPlanInterTdayAndMonth.setWaterQuantity(demadWaterQuantity);
        wrPlanInterTdayAndMonth.setTimeType(tdayType);
        return wrPlanInterTdayAndMonth;
    }
    //根据年月获取天数
    private static Double sum(String year,String month){
        String yearAndMonth = year+"/"+month;
        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy/MM");
        try {
            rightNow.setTime(simpleDate.parse(yearAndMonth));
        } catch (ParseException e) {
            log.error("时间格式有误：{}"+e.getMessage());
        }
        int days = rightNow.getActualMaximum(Calendar.DAY_OF_MONTH);
        Double sum = days*86400.0;
        return sum;
    }
}
