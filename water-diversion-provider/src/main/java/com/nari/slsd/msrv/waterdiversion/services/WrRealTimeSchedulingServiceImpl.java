package com.nari.slsd.msrv.waterdiversion.services;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nari.slsd.msrv.common.exception.TransactionException;
import com.nari.slsd.msrv.common.ext.enums.CodeEnum;
import com.nari.slsd.msrv.waterdiversion.commons.PointTypeEnum;
import com.nari.slsd.msrv.waterdiversion.commons.WrBuildingEnum;
import com.nari.slsd.msrv.waterdiversion.config.RedisUtil;
import com.nari.slsd.msrv.waterdiversion.interfaces.IDataService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWaterBuildingManagerService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrDispatchSchemeService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrRealTimeSchedulingService;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrPlanInterDayMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.*;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrPlanInterDay;
import com.nari.slsd.msrv.waterdiversion.model.vo.GisRealtimeDispatchListVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.GisRealtimeDispatchVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.MngUnitAndBuilding;
import com.nari.slsd.msrv.waterdiversion.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * GIS实时调度服务类
 * </p>
 *
 * @author bigb
 * @since 2021-08-10
 */
@Service
@Slf4j
public class WrRealTimeSchedulingServiceImpl implements IWrRealTimeSchedulingService {

    private static final String CURRENT_DAY_WATER = "today_water";

    //private static final String CURRENT_DAY_FLOW = "today_flow";

    private static final String RESIZE = "resize";

    private static final String RESIZE_DATE = "resize_date";

    private static final String DEFAULT_NUMBER_STR = "0.00";

    private static final String REDIS_KEY_GIS_DISPATCH = "wr_gis_dispatch";

    @Autowired
    private IWrDispatchSchemeService wrDispatchSchemeService;

    @Autowired
    private IDataService dataService;

    @Autowired
    private IWaterBuildingManagerService waterBuildingManagerService;

    @Autowired
    private WrPlanInterDayMapper wrPlanInterDayMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Value("scheduler.realtime.plan.future")
    private String futurePlanDays;

    @Override
    public GisRealtimeDispatchListVO realTimeSchedulingAndCaching(){
        log.info("=======引水口近期用水计划缓存开始=======");
        //获取所有引水口今日计划流量
        QueryWrapper<WrPlanInterDay> iteratorForDayQueryWrapper = new QueryWrapper<>();
        iteratorForDayQueryWrapper.eq("SUPPLY_TIME", getToday());
        List<WrPlanInterDay> WrPlanInterDayList = wrPlanInterDayMapper.selectList(iteratorForDayQueryWrapper);
        if(CollectionUtils.isEmpty(WrPlanInterDayList)){
            log.info("未查询到任何当日计划！");
            return null;
        }
        Map<String,GisRealtimeDispatchVO> voMap = new HashMap<>();
        //当日计划水量或流量
        WrPlanInterDayList.forEach(plan -> {
            GisRealtimeDispatchVO vo = new GisRealtimeDispatchVO();
            vo.setBuildingId(plan.getBuildingId());
            vo.setWaterQuantity(numberToString(plan.getWaterQuantity()));
            vo.setWaterFlow(numberToString(plan.getWaterFlow()));
            voMap.put(vo.getBuildingId(),vo);
        });
        GisRealtimeDispatchListVO listVO = GisRealtimeDispatchListVO.builder().build();
        List<WrPlanInterDay> changeList = new ArrayList<>();
        int loop = 0;
        //待调整水量或者流量
        String planDays = StringUtils.defaultString(futurePlanDays,"7");
        while(loop < Integer.parseInt(planDays)){
            //未来n天，找到第一时间出现引水口流量变化（300+水口任意一个引水口）
            List<WrPlanInterDay> localChangeList = wrPlanInterDayMapper.getChangeFlowForAllStations(getNextDay(loop), getNextDay(loop+1));
            if(CollectionUtils.isNotEmpty(localChangeList)){
                listVO.setResizeDateStr(getTimeFormatForMMDD(localChangeList.get(0).getSupplyTime()));
                listVO.setResizeDate(localChangeList.get(0).getSupplyTime());
                changeList.addAll(localChangeList);
                break;
            }
            loop++;
        }
        changeList.forEach(change -> {
            GisRealtimeDispatchVO localVO = voMap.get(change.getBuildingId());
            if(null != localVO){
                localVO.setResizeWaterQuantity(numberToString(change.getWaterQuantity()));
                localVO.setResizeWaterFlow(numberToString(change.getWaterFlow()));
            }
        });

        //缓存
        if(voMap.size() > 0){
            listVO.getVoList().addAll(voMap.values());
            String json = JSON.toJSONString(listVO);
            redisUtil.set(REDIS_KEY_GIS_DISPATCH, json);
            log.debug("近期用水计划缓存：{}",json);
        }
        log.info("=======引水口近期用水计划缓存结束=======");
        return listVO;
    }

    /**
     * 近期计划填报更新实时调度缓存
     * @param dtoList
     */
    @Override
    public void recentPlanResizeForRedis(List<RecentPlanResizeDTO> dtoList){
        if(CollectionUtils.isEmpty(dtoList)){
            return;
        }
        //近期用水计划按照时间正序排序
        dtoList.sort(Comparator.comparing(RecentPlanResizeDTO::getResizeDate));
        GisRealtimeDispatchListVO listVO = CommonUtil.getInstanceForRedis(redisUtil.get(REDIS_KEY_GIS_DISPATCH), GisRealtimeDispatchListVO.class);
        if(null == listVO || null == listVO.getResizeDate() || listVO.getVoList().size() == 0){
            return;
        }
        List<GisRealtimeDispatchVO> voList = listVO.getVoList();
        Map<String, GisRealtimeDispatchVO> voMap = voList.stream().collect(Collectors.toMap(GisRealtimeDispatchVO::getBuildingId, vo -> vo));
        //调度计划调整时间
        Date resizeDate = listVO.getResizeDate();
        //近期计划最早调整时间
        Date resizeDateForRecentPlan = dtoList.get(0).getResizeDate();
        //近期计划时间早于调度计划调整时间，则需更新缓存
        if(resizeDate.after(resizeDateForRecentPlan) || resizeDate.equals(resizeDateForRecentPlan)){
            dtoList.stream().filter(dto -> dto.getResizeDate().equals(resizeDateForRecentPlan)).forEach(dto -> {
                GisRealtimeDispatchVO vo = voMap.get(dto.getBuildingId());
                if(null != vo){
                    //水量
                    vo.setResizeWaterQuantity(dto.getResizeWater());
                    //流量=水量/86400
                    BigDecimal resizeWater = new BigDecimal(dto.getResizeWater());
                    //流量
                    BigDecimal resizeFlow = NumberUtil.div(resizeWater, 8.64);
                    //流量
                    vo.setResizeWaterFlow(resizeFlow.toPlainString());
                }
            });
            listVO.setResizeDate(resizeDateForRecentPlan);
            redisUtil.set(REDIS_KEY_GIS_DISPATCH, JSON.toJSONString(listVO));
        }
    }

    /**
     * 人工选择调度
     * @param buildingIdList
     * @param selectDateLong
     */
    @Override
    public List<GisRealtimeDispatchListVO> artificialScheduling(List<String> buildingIdList , Long selectDateLong , List<String> pointTypeList){
        log.info("=======人工选择调度执行开始=======");
        if(CollectionUtils.isEmpty(buildingIdList)){
            throw new TransactionException(CodeEnum.NO_PARAM,"未传入任何引水口信息！");
        } else if(CollectionUtils.isEmpty(pointTypeList)){
            throw new TransactionException(CodeEnum.NO_PARAM,"未传入任何测点类别信息！");
        }
        //管理站-引水口关系
        List<WrBuildingAndDiversion> diversionList = waterBuildingManagerService.getWrBuildingAndDiversionList(buildingIdList);
        if(CollectionUtils.isEmpty(diversionList)){
            throw new TransactionException(CodeEnum.NO_DATA,"未查询到任何引水口信息，buildingIds is " + buildingIdList.toString());
        }
        //获取给定引水口所有实测信息
        List<DataBuildingDto> buildingDtoList = dataService.getSpecialDataRunRtreal(buildingIdList, pointTypeList);
        if(CollectionUtils.isEmpty(buildingDtoList)){
            throw new TransactionException(CodeEnum.NO_DATA,"未查询到任何实测信息，buildingIds is " + buildingIdList.toString());
        }
        Date selectDate = selectDateLong == null ? new Date() : new Date(selectDateLong);
        //查询给定引水口列表当日计划水量
        QueryWrapper<WrPlanInterDay> iteratorForDayQueryWrapper = new QueryWrapper<>();
        iteratorForDayQueryWrapper.eq("SUPPLY_TIME", selectDate);
        iteratorForDayQueryWrapper.in("BUILDING_ID",buildingIdList);
        List<WrPlanInterDay> queryList = wrPlanInterDayMapper.selectList(iteratorForDayQueryWrapper);
        if(CollectionUtils.isEmpty(queryList)){
            throw new TransactionException(CodeEnum.NO_DATA,"未查询到任何近期用水计划！");
        }
        Map<String, WrBuildingAndDiversion> buildingMap = diversionList.stream().collect(Collectors.toMap(WrBuildingAndDiversion::getId, building -> building));
        Map<String, DataBuildingDto> buildingDtoMap = buildingDtoList.stream().collect(Collectors.toMap(DataBuildingDto::getId, dto -> dto));
        Map<String, WrPlanInterDay> planDayMap = queryList.stream().collect(Collectors.toMap(WrPlanInterDay::getBuildingId, day -> day));
        //构建GIS实时调度显示
        GisRealtimeDispatchListVO listVO = GisRealtimeDispatchListVO.builder().build();
        listVO.setResizeDate(selectDate);
        buildingIdList.stream().forEach(buildingId -> {
            GisRealtimeDispatchVO vo = new GisRealtimeDispatchVO();
            WrBuildingAndDiversion version = buildingMap.get(buildingId);
            //引水口id
            vo.setBuildingId(buildingId);
            if(null != version){
                //引水口名称
                vo.setBuildingName(version.getBuildingName());
                //GIS经纬度
                vo.setLatlng_f(version.getLatlngF());
                //拓扑经纬度
                vo.setLatlng_s(version.getLatlngS());
            }
            DataBuildingDto buildingDto = buildingDtoMap.get(buildingId);
            setRealWaterQuantityAndFlow(vo, buildingDto);
            WrPlanInterDay wrPlanInterDay = planDayMap.get(buildingId);
            if(null != wrPlanInterDay){
                //水量
                vo.setWaterQuantity(numberToString(wrPlanInterDay.getWaterQuantity()));
                //流量
                vo.setWaterFlow(numberToString(wrPlanInterDay.getWaterFlow()));
            }
            listVO.getVoList().add(vo);
        });
        log.info("=======人工选择调度执行结束=======");
        return Arrays.asList(listVO);
    }

    private void setRealWaterQuantityAndFlow(GisRealtimeDispatchVO vo, DataBuildingDto buildingDto) {
        if (null != buildingDto && CollectionUtils.isNotEmpty(buildingDto.getDataPointDtos())) {
            List<DataPointDto> dtoList = buildingDto.getDataPointDtos();
            dtoList.stream().forEach(e -> {
                if (PointTypeEnum.WATER_VOLUME.getId().equals(e.getPointType())) {
                    //实测水量
                    vo.setRealTimeWaterQuantity(numberToString(e.getV()));
                } else if (PointTypeEnum.FLOW.getId().equals(e.getPointType())) {
                    //实测流量
                    vo.setRealTimeWaterFlow(numberToString(e.getV()));
                }
            });
        }
    }

    private List<GisRealtimeDispatchListVO> convert2TreeForGisDispatch(GisRealtimeDispatchListVO listVO){
        if(null == listVO || CollectionUtils.isEmpty(listVO.getVoList())){
            return null;
        }
        List<GisRealtimeDispatchVO> voList = listVO.getVoList();
        Map<String, GisRealtimeDispatchVO> voMap = voList.stream().collect(Collectors.toMap(vo -> vo.getMngUnitId() + "-" + vo.getMngUnitName(), vo -> vo));
        List<GisRealtimeDispatchListVO> resultList = new ArrayList<>(voMap.size());
        voMap.entrySet().stream().forEach(entry -> {
            String key = entry.getKey();
            GisRealtimeDispatchListVO vo = new GisRealtimeDispatchListVO();
            String[] split = StringUtils.split(key, "-");
            //暂不做数组越界判断
            if(null != split){
                vo.setMngUnitName(split[0]);
                vo.setMngUnitId(split[1]);
            }
            vo.setResizeDateLong(listVO.getResizeDateLong());
            vo.setResizeDateStr(listVO.getResizeDateStr());
            resultList.add(vo);
        });
        return resultList;
    }

    /**
     * @title planScheduling
     * @description 计划调度
     * @author bigb
     * @param: buildingTypeList
     * @param: pointType 1-水量 2-流量
     * @updateTime 2021/8/20 16:23
     * @throws
     */
    @Override
    public List<GisRealtimeDispatchListVO> planScheduling(List<String> buildingTypeList , List<String> pointTypeList){
        log.info("=======计划调度执行开始=======");
        if(CollectionUtils.isEmpty(pointTypeList)){
            throw new TransactionException(CodeEnum.NO_PARAM,"请传入测点类型！");
        }
        //获取所有引水口信息
        //已进行是否填报的过滤
        List<MngUnitAndBuilding> buildingList = waterBuildingManagerService.getMngAndBuildingsByMng(null, buildingTypeList, 1 ,
                Arrays.asList(WrBuildingEnum.BUILDING_LEVEL_1_2,WrBuildingEnum.BUILDING_LEVEL_2));
        if(CollectionUtils.isEmpty(buildingList)){
            throw new TransactionException(CodeEnum.NO_DATA,"未查询到任何引水口信息！");
        }
        //引水口id
        List<String> buildingIdList = buildingList.stream().map(MngUnitAndBuilding::getBuildingId).collect(Collectors.toList());
        //获取给定引水口所有实测信息
        List<DataBuildingDto> buildingDtoList = dataService.getSpecialDataRunRtreal(buildingIdList, pointTypeList);
        if(CollectionUtils.isEmpty(buildingDtoList)){
            throw new TransactionException(CodeEnum.NO_DATA,"未查询到任何实测信息！");
        }
        //查询给定引水口列表当日及给定日期计划水量
        GisRealtimeDispatchListVO resultVO = CommonUtil.getInstanceForRedis(redisUtil.get(REDIS_KEY_GIS_DISPATCH), GisRealtimeDispatchListVO.class);
        //未查询到缓存信息，数据库查询
        if(null == resultVO){
            resultVO = this.realTimeSchedulingAndCaching();
            if(null == resultVO || CollectionUtils.isEmpty(resultVO.getVoList())){
                throw new TransactionException(CodeEnum.NO_DATA,"未查询到任何近期用水计划缓存信息！");
            }
        }
        Map<String, GisRealtimeDispatchVO> voMap = resultVO.getVoList().stream().collect(Collectors.toMap(GisRealtimeDispatchVO::getBuildingId, vo -> vo));
        Map<String, DataBuildingDto> buildingDtoMap = buildingDtoList.stream().collect(Collectors.toMap(DataBuildingDto::getId, dto -> dto));
        List<GisRealtimeDispatchVO> dispatchVOList = new ArrayList<>(buildingList.size());
        //不使用反射
        buildingList.stream().forEach(building -> {
            GisRealtimeDispatchVO vo = new GisRealtimeDispatchVO();
            dispatchVOList.add(vo);
            //管理单位id
            vo.setMngUnitId(building.getMngUnitId());
            //管理单位名称
            vo.setMngUnitName(building.getMngUnitName());
            //引水口id
            vo.setBuildingId(building.getBuildingId());
            //引水口名称
            vo.setBuildingName(building.getBuildingName());
            DataBuildingDto dto = buildingDtoMap.get(building.getBuildingId());
            setRealWaterQuantityAndFlow(vo, dto);
            GisRealtimeDispatchVO gisVo = Optional.ofNullable(voMap.get(building.getBuildingId())).orElse(new GisRealtimeDispatchVO());
            //今日水量
            vo.setWaterQuantity(StringUtils.defaultString(gisVo.getWaterQuantity(),DEFAULT_NUMBER_STR));
            //今日流量
            vo.setWaterFlow(StringUtils.defaultString(gisVo.getWaterFlow(),DEFAULT_NUMBER_STR));
            //待调整水量
            vo.setResizeWaterQuantity(StringUtils.defaultString(gisVo.getResizeWaterQuantity(),DEFAULT_NUMBER_STR));
            //待调整流量
            vo.setResizeWaterFlow(StringUtils.defaultString(gisVo.getResizeWaterFlow(),DEFAULT_NUMBER_STR));
            //今日水量与待调整流量是否差异
            vo.setDiff(StringUtils.compare(vo.getWaterQuantity(),vo.getResizeWaterQuantity()));
            //GIS经纬度
            vo.setLatlng_f(building.getLatlngF());
            //拓扑经纬度
            vo.setLatlng_s(building.getLatlngS());
        });
        resultVO.setVoList(dispatchVOList);
        //业务需要，ResizeDate不可为空
        resultVO.setResizeDateLong(resultVO.getResizeDate().getTime());
        log.info("=======计划调度执行结束=======");
        return convert2TreeForGisDispatch(resultVO);
    }

    /**
     * @title generateDispatchOrder
     * @description 调用模型服务，生成实时调度指令集
     * @author bigb
     * @param: modelRequestDto
     * @updateTime 2021/8/21 10:20
     * @return: 方案id
     * @throws
     */
    @Transactional
    public String generateDispatchOrder(ModelRequestDto modelRequestDto){
        if(StringUtils.isEmpty(modelRequestDto.getDispatchType())){
            throw new TransactionException(CodeEnum.NO_PARAM,"未传入任何实时调度模式！");
        }
        if(null == modelRequestDto.getResizeDate()){
            throw new TransactionException(CodeEnum.NO_PARAM,"未传入调度日期！");
        }
        //TODO 预留调用模型服务
        //构建配水调度方案表
        String schemeId = wrDispatchSchemeService.generateWrDispatchScheme(modelRequestDto);
        //根据模型返回结果，构建方案结果表
        return schemeId;
    }

    private Date getToday(){
        return Convert.toDate(DateUtil.today());
    }

    private Date getNextDay(int day){
        return DateUtils.addDays(new Date() , day);
    }

    private String getTimeFormatForMMDD(Date time){
        return time == null ? "" : DateUtil.format(time,"MM/dd");
    }

    private String numberToString(Number bi){
        if(bi instanceof Double){
            return String.valueOf(bi.doubleValue());
        }else if(bi instanceof BigDecimal){
            return ((BigDecimal) bi).toPlainString();
        }
        return DEFAULT_NUMBER_STR;
    }
}
