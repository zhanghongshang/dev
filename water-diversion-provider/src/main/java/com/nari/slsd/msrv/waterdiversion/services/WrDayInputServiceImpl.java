package com.nari.slsd.msrv.waterdiversion.services;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.hu.mplat.imc.client.Param.Param;
import com.nari.slsd.hu.mplat.imc.client.dto.DataRetoreRequest;
import com.nari.slsd.hu.mplat.imc.client.dto.UpdateData;
import com.nari.slsd.hu.mplat.imc.client.service.ImcDataProxy;
import com.nari.slsd.msrv.common.exception.TransactionException;
import com.nari.slsd.msrv.common.ext.enums.CodeEnum;
import com.nari.slsd.msrv.common.utils.BeanUtils;
import com.nari.slsd.msrv.common.utils.DateUtils;
import com.nari.slsd.msrv.common.utils.IDGenerator;
import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.msrv.waterdiversion.cache.interfaces.IModelCacheService;
import com.nari.slsd.msrv.waterdiversion.commons.PointTypeEnum;
import com.nari.slsd.msrv.waterdiversion.commons.WrBuildingEnum;
import com.nari.slsd.msrv.waterdiversion.commons.WrDayInputEnum;
import com.nari.slsd.msrv.waterdiversion.config.excel.WrDayInputInDayModel;
import com.nari.slsd.msrv.waterdiversion.config.excel.WrDayInputInMonthModel;
import com.nari.slsd.msrv.waterdiversion.interfaces.*;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrDayInputMapper;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrDiversionPortMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.DataBuildingDto;
import com.nari.slsd.msrv.waterdiversion.model.dto.WaterPointDTO;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrBuildingAndDiversion;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrDayInputDTO;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrDayInmonthInput;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrDayInput;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrDiversionPort;
import com.nari.slsd.msrv.waterdiversion.model.third.po.WrDinwSChecked;
import com.nari.slsd.msrv.waterdiversion.model.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.nari.slsd.msrv.waterdiversion.commons.WrBuildingEnum.BUILDING_LEVEL_1_2;
import static com.nari.slsd.msrv.waterdiversion.utils.CommonUtil.getDayNumOfCurrentMonth;

/**
 * <p>
 * 日水情录入 服务实现类
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-19
 */
@Service
@Slf4j
public class WrDayInputServiceImpl extends ServiceImpl<WrDayInputMapper, WrDayInput> implements IWrDayInputService {

    private static final String DAY = "day";

    private static Map<Integer, Field> DAY_FIELD_MAP = new HashMap<>();

    @Resource
    TransactionTemplate transactionTemplate;

    @Resource
    IWaterBuildingManagerService waterBuildingManagerService;

    @Resource
    IDataService dataService;

    @Resource
    IWrDayInMonthInputService wrDayInmonthInputService;

    @Resource
    IModelCacheService cacheService;

    @Resource
    IWrDinwSCheckedService wrDinwSCheckedService;

    @Resource
    IWaterPointService waterPointService;

    @Autowired
    private IWrDayInputService wrDayInputService;

    @Resource
    ImcDataProxy imcDataProxy;

    @Autowired
    private WrDiversionPortMapper wrDiversionPortMapper;

    static {
        ReflectionUtils.doWithLocalFields(WrDayInputInDayModel.class, field -> {
            String fieldName = field.getName();
            if (fieldName.startsWith(DAY)) {
                ReflectionUtils.makeAccessible(field);
                DAY_FIELD_MAP.put(Integer.parseInt(fieldName.substring(DAY.length())), field);
            }
        });
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void saveOrUpdateBatch(List<WrDayInputDTO> dtoList) {
        List<WrDayInput> poList = convert2DOList(dtoList);
        //生成操作时间
        poList.forEach(po -> po.setOperateTime(DateUtils.getDateTime()));
        transactionTemplate.executeWithoutResult(transactionStatus -> saveOrUpdateBatch(poList));
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void verifyBatch(List<WrDayInputDTO> dtoList) {
        List<WrDayInput> poList = convert2DOList(dtoList);

        transactionTemplate.executeWithoutResult(transactionStatus -> {
            //生成审核时间
            poList.forEach(po -> po.setApproveTime(DateUtils.getDateTime()));
            updateBatchById(poList);
            //审核后的数据加到逐日水情表
            List<String> idList = poList.stream().map(WrDayInput::getId).collect(Collectors.toList());
            List<WrDayInput> dataList = listByIds(idList);

            List<WrDayInmonthInput> transDataList = new ArrayList<>();
            dataList.forEach(dayInput -> {
                WrDayInmonthInput dayInmonthInput = new WrDayInmonthInput();
                BeanUtils.copyProperties(dayInput, dayInmonthInput);
                //日水情的操作人审核人不保存
                dayInmonthInput.setOperateTime(null);
                dayInmonthInput.setOperatorId(null);
                dayInmonthInput.setApproveTime(null);
                dayInmonthInput.setApproveId(null);
                //这里因为自动数据和逐日水情表字段一样 所以默认已经传了自动数据，当审核选择人工时再设置为人工数据
                if (dayInput.getAuto().equals(WrDayInputEnum.DATA_TYPE_MANUAL)) {
                    dayInmonthInput.setWaterLevel(dayInput.getManualWaterLevel());
                    dayInmonthInput.setWaterFlow(dayInput.getManualWaterFlow());
                }
                transDataList.add(dayInmonthInput);
            });

            wrDayInmonthInputService.saveBatch(transDataList);
            /**
             * TODO 调用接口入WDS 传参规则？
             */

            // 测站ID
            List<String> buildingIds = transDataList.stream().map(WrDayInmonthInput::getStationId).distinct().collect(Collectors.toList());
            if (buildingIds.size() != 0) {
                List<WaterPointDTO> waterPointDTOList = new ArrayList<>();
                try {
                    //根据引水口ID 测项类型查测点
                    waterPointDTOList = waterPointService.getWaterPointId(buildingIds, Arrays.asList(PointTypeEnum.WATER_LEVEL.getId(), PointTypeEnum.FLOW.getId()));
                } catch (RuntimeException e) {
                    log.error("调用测项信息失败");
                    e.printStackTrace();
                }
                //按测站ID+测项类型分组
                Map<String, WaterPointDTO> pointMap = waterPointDTOList.stream().collect(Collectors.toMap(o -> o.getBuildingId() + ":" + o.getPointType(), o -> o, (o1, o2) -> o1));
                List<DataRetoreRequest> requestList = new ArrayList<>();
                transDataList.forEach(data -> {
                    String waterLvKey = data.getStationId() + ":" + PointTypeEnum.WATER_LEVEL.getId();
                    String flowKey = data.getStationId() + ":" + PointTypeEnum.FLOW.getId();
                    if (pointMap.containsKey(waterLvKey)) {
                        WaterPointDTO pointDTO = pointMap.get(waterLvKey);
                        DataRetoreRequest dataRetoreRequest = new DataRetoreRequest();
                        dataRetoreRequest.setAppType(Param.AppType.APP_Type_WDS);
                        dataRetoreRequest.setValueType(Param.ValType.Special_V);
                        dataRetoreRequest.setTimeType(Param.RunDataType.RUN_DAY);
                        if (StringUtils.isNotEmpty(pointDTO.getCorrelationCode())) {
                            dataRetoreRequest.setSenid(Long.valueOf(pointDTO.getCorrelationCode()));
                        }
                        //数据
                        UpdateData updateData = new UpdateData();
                        updateData.setNewValue(data.getWaterLevel());
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(data.getTime());
                        updateData.setTime(calendar);
                        dataRetoreRequest.setDatalist(Collections.singletonList(updateData));
                        requestList.add(dataRetoreRequest);
                    }
                    if (pointMap.containsKey(flowKey)) {
                        WaterPointDTO pointDTO = pointMap.get(flowKey);
                        DataRetoreRequest dataRetoreRequest = new DataRetoreRequest();
                        dataRetoreRequest.setAppType(Param.AppType.APP_Type_WDS);
                        dataRetoreRequest.setValueType(Param.ValType.Special_V);
                        dataRetoreRequest.setTimeType(Param.RunDataType.RUN_DAY);
                        if (StringUtils.isNotEmpty(pointDTO.getCorrelationCode())) {
                            dataRetoreRequest.setSenid(Long.valueOf(pointDTO.getCorrelationCode()));
                        }
                        //数据
                        UpdateData updateData = new UpdateData();
                        updateData.setNewValue(data.getWaterFlow());
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(data.getTime());
                        updateData.setTime(calendar);
                        dataRetoreRequest.setDatalist(Collections.singletonList(updateData));
                        requestList.add(dataRetoreRequest);
                    }
                });
                try {
                    imcDataProxy.savePointData(requestList);
                } catch (RuntimeException e) {
                    log.error(e.getMessage());
                }
            }
        });
    }


    @Override
    public WrDayInputTable getDayInputDataTable(List<String> mngUnitIds, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels, Long time, Integer status) {
        WrDayInputTable table = new WrDayInputTable();

        Long sdt = time;
        Long edt = time + 1000 * 60 * 60 * 24 - 1;
        //首先通过管理单位和引水口类型获取结构体（包含了上级引水口信息）
        List<MngUnitAndBuildings> resultList = waterBuildingManagerService.getMngAndPBuildingsByMng(mngUnitIds, buildingTypes, fillReport, buildingLevels);
        //管理单位下无测站，直接返回
        if (resultList == null || resultList.size() == 0) {
            return table;
        }

        //根据上级引水口编码查询上级引水口的数据
        List<String> pCodes = resultList.stream().filter(o -> StringUtils.isNotEmpty(o.getPBuildingCode())).map(MngUnitAndBuildings::getPBuildingCode).distinct().collect(Collectors.toList());
        List<WrDinwSChecked> pDataList = wrDinwSCheckedService.getWrDinwSCheckedData(pCodes, sdt, edt);
        //每次查的是一天的数据 所以可以直接按引水口CODE分组 如果有重复 取第一条
        Map<String, WrDinwSChecked> pDataMap = pDataList.stream().collect(Collectors.toMap(WrDinwSChecked::getSwfcd, o -> o, (o1, o2) -> o1));

        //先查日水情表
        //获取测站ID，测站ID+time作为伪主键查数据
        //应该每个测站当日最多一条数据,如果有多条，按时间倒序排序后转map时取第一条
        List<String> stationIds = resultList.stream().map(MngUnitAndBuilding::getBuildingId).distinct().collect(Collectors.toList());

        List<WrDayInput> wrDayInputList = lambdaQuery().in(WrDayInput::getStationId, stationIds).between(WrDayInput::getTime, DateUtils.convertTimeToDate(sdt), DateUtils.convertTimeToDate(edt)).orderByDesc(WrDayInput::getOperateTime).list();
        //如果没有自动数据，再查一次表 组装数据
        List<String> lackAutoStationIds = wrDayInputList.stream().filter(data -> data.getWaterLevel() == null || data.getWaterFlow() == null).map(WrDayInput::getStationId).distinct().collect(Collectors.toList());
        if (lackAutoStationIds.size() != 0) {
            List<DataBuildingDto> lackAutoBuildingDtoList = new ArrayList<>();
            try {
                lackAutoBuildingDtoList = dataService.getSpecialDataRunDataType(lackAutoStationIds, Arrays.asList(PointTypeEnum.WATER_LEVEL.getId(), PointTypeEnum.FLOW.getId()), sdt, edt, Param.ValType.Special_AVGV, Param.RunDataType.RUN_DAY, null);
            } catch (RuntimeException e) {
                log.error("调用自动监测数据失败");
                e.printStackTrace();
            }
            if (lackAutoBuildingDtoList.size() != 0) {
                Map<String, DataBuildingDto> lacAutoBuildingDtoMap = lackAutoBuildingDtoList.stream().collect(Collectors.toMap(DataBuildingDto::getId, o -> o));
                wrDayInputList.forEach(data -> {
                    if (data.getWaterLevel() == null && data.getWaterFlow() == null) {
                        if (lacAutoBuildingDtoMap.containsKey(data.getStationId())) {
                            DataBuildingDto lackAuto = lacAutoBuildingDtoMap.get(data.getStationId());
                            lackAuto.getDataPointDtos().forEach(pointData -> {
                                if (pointData.getPointType().equals(PointTypeEnum.WATER_LEVEL.getId()) && data.getWaterLevel() == null) {
                                    data.setWaterLevel(pointData.getV());
                                }
                                if (pointData.getPointType().equals(PointTypeEnum.FLOW.getId()) && data.getWaterFlow() == null) {
                                    data.setWaterFlow(pointData.getV());
                                }
                            });
                        }
                    }
                });
            }
        }

        //测站为KEY转map
        Map<String, WrDayInput> wrDayInputMap = wrDayInputList.stream().collect(Collectors.toMap(WrDayInput::getStationId, o -> o, (v1, v2) -> v1));

        //去重后进行count计数未审核/已审核数据 防止同一测站一天之内有多条数据可能出现计数不准确问题
        Integer unreviewedCount = Math.toIntExact(wrDayInputMap.values().stream().filter(dayInput -> dayInput.getStatus().equals(WrDayInputEnum.DAY_STATUS_UNREVIEWED)).map(WrDayInput::getStationId).distinct().count());
        Integer reviewedCount = Math.toIntExact(wrDayInputMap.values().stream().filter(dayInput -> dayInput.getStatus().equals(WrDayInputEnum.DAY_STATUS_REVIEWED)).map(WrDayInput::getStationId).distinct().count());

        Set<String> wrDayInputStationIds = wrDayInputMap.keySet();

        //过滤出没有数据（未录入）的测站
        //去其他表查数据 1.自动检测数据  2，公摊系数
        List<String> otherStationIds = stationIds.stream().filter(s -> !wrDayInputStationIds.contains(s)).collect(Collectors.toList());
        if (otherStationIds.size() != 0) {
            List<WrDayInput> otherWrDayInputList = new ArrayList<>();
            List<DataBuildingDto> dataBuildingDtoList = new ArrayList<>();
            try {
                dataBuildingDtoList = dataService.getSpecialDataRunDataType(otherStationIds, Arrays.asList(PointTypeEnum.WATER_LEVEL.getId(), PointTypeEnum.FLOW.getId()), sdt, edt, Param.ValType.Special_AVGV, Param.RunDataType.RUN_DAY, null);
            } catch (RuntimeException e) {
                log.error("调用自动监测数据失败");
                e.printStackTrace();
            }

            Map<String, DataBuildingDto> dataBuildingDtoMap = dataBuildingDtoList.stream().collect(Collectors.toMap(DataBuildingDto::getId, o -> o, (v1, v2) -> v1));
            List<WrBuildingAndDiversion> buildingAndDiversionList = waterBuildingManagerService.getWrBuildingAndDiversionList(otherStationIds);
            Map<String, WrBuildingAndDiversion> buildingAndDiversionMap = buildingAndDiversionList.stream().collect(Collectors.toMap(WrBuildingAndDiversion::getId, o -> o));
            otherStationIds.forEach(id -> {
                WrDayInput dayInput = new WrDayInput();
                dayInput.setStationId(id);
                dayInput.setStatus(WrDayInputEnum.DAY_STATUS_UNENTERED);
                dayInput.setTimeType(WrDayInputEnum.TIME_TYPE_DAY);
                dayInput.setTime(DateUtils.convertStringTimeToDateExt(DateUtils.convertTimeToString(time)));
                if (dataBuildingDtoMap.containsKey(id)) {
                    DataBuildingDto dto = dataBuildingDtoMap.get(id);
                    dto.getDataPointDtos().forEach(pointData -> {
                        if (pointData.getPointType().equals(PointTypeEnum.WATER_LEVEL.getId())) {
                            dayInput.setWaterLevel(pointData.getV());
                        }
                        if (pointData.getPointType().equals(PointTypeEnum.FLOW.getId())) {
                            dayInput.setWaterFlow(pointData.getV());
                        }
                    });
                }
                if (buildingAndDiversionMap.containsKey(id)) {
                    WrBuildingAndDiversion station = buildingAndDiversionMap.get(id);
                    dayInput.setShareFactor(station.getShareFactor());
                }
                otherWrDayInputList.add(dayInput);
            });

            Map<String, WrDayInput> otherWrDayInputMap = otherWrDayInputList.stream().collect(Collectors.toMap(WrDayInput::getStationId, o -> o));
            wrDayInputMap.putAll(otherWrDayInputMap);
        }

        Iterator<MngUnitAndBuildings> it = resultList.iterator();
        while (it.hasNext()) {
            MngUnitAndBuildings result = it.next();
            if (wrDayInputMap.containsKey(result.getBuildingId())) {
                WrDayInput data = wrDayInputMap.get(result.getBuildingId());
                //根据状态过滤
                if (status == null || data.getStatus().equals(status)) {
                    //转VO
                    WrDayInputVO dataVO = convert2VO(data);
                    if (pDataMap.containsKey(result.getPBuildingCode())) {
                        WrDinwSChecked dayData = pDataMap.get(result.getPBuildingCode());
                        //如果有上级引水口数据 设置上级引水口水位流量数据
                        dataVO.setPWaterLevel(dayData.getAvz());
                        dataVO.setPWaterFlow(dayData.getDvq());
                        //如果引水口为1，2级共用 设置人工数据为上级引水口数据
                        if (WrBuildingEnum.BUILDING_LEVEL_1_2.equals(result.getBuildingLevel())) {
                            dataVO.setManualWaterLevel(dayData.getAvz());
                            dataVO.setManualWaterFlow(dayData.getDvq());
                        }
                    }
                    result.setData(dataVO);
                } else {
                    it.remove();
                }
            }
        }

        table.setTotalCount(stationIds.size());
        table.setUnenteredCount(otherStationIds.size());
        table.setUnreviewedCount(unreviewedCount);
        table.setReviewedCount(reviewedCount);
        table.setData(resultList);
        return table;
    }

    protected WrDayInputVO convert2VO(WrDayInput po) {
        WrDayInputVO vo = new WrDayInputVO();
        BeanUtils.copyProperties(po, vo);
        if (po.getTime() != null) {
            vo.setTime(DateUtils.convertDateToLong(po.getTime()));
        }
        if (po.getOperateTime() != null) {
            vo.setOperateTime(DateUtils.convertDateToLong(po.getOperateTime()));
        }
        if (po.getApproveTime() != null) {
            vo.setApproveTime(DateUtils.convertDateToLong(po.getApproveTime()));
        }
        vo.setOperatorName(cacheService.getUserName(vo.getOperatorId()));
        vo.setApproveName(cacheService.getUserName(vo.getApproveId()));
        return vo;
    }

    protected WrDayInput convert2DO(WrDayInputDTO dto) {
        WrDayInput po = new WrDayInput();
        BeanUtils.copyProperties(dto, po);
        if (StringUtils.isEmpty(dto.getId())) {
            po.setId(IDGenerator.getId());
        }
        po.setTimeType(WrDayInputEnum.TIME_TYPE_DAY);
        if (StringUtils.isNotEmpty(dto.getApproveId())) {
            po.setStatus(WrDayInputEnum.DAY_STATUS_REVIEWED);
        } else {
            po.setStatus(WrDayInputEnum.DAY_STATUS_UNREVIEWED);
        }
        if (dto.getTime() != null) {
            po.setTime(DateUtils.convertTimeToDate(dto.getTime()));
        }
        if (dto.getOperateTime() != null) {
            po.setOperateTime(DateUtils.convertTimeToDate(dto.getOperateTime()));
        }
        if (dto.getApproveTime() != null) {
            po.setApproveTime(DateUtils.convertTimeToDate(dto.getApproveTime()));
        }
        return po;
    }

    protected List<WrDayInput> convert2DOList(List<WrDayInputDTO> dtoList) {
        List<WrDayInput> poList = new ArrayList<>();
        dtoList.forEach(dto -> poList.add(convert2DO(dto)));
        return poList;
    }

    /**
     * 日水情录入日导入方式
     * @param inputList
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void importInDayForDayInput(String operator , String year , String month , List<WrDayInputInDayModel> inputList){
        //引水口id
        setBuildingId(inputList);
        //获取导入的月份
        WrDayInputInDayModel model = inputList.get(0);
        List<String> importDayList = new ArrayList<>();
        for(int index = 1;index <=12 ; index++){
            Field monthField = DAY_FIELD_MAP.get(index);
            if (null != monthField) {
                Object val = ReflectionUtils.getField(monthField, model);
                if (val instanceof Double) {
                    importDayList.add(String.valueOf(index));
                }
            }
        }
        if(importDayList.size() == 0){
            throw new TransactionException(CodeEnum.NO_PARAM,"日水情导入模板中不包含任何日数据");
        }
        generateAndSaveDayInput(operator, year ,month, importDayList, inputList);
    }

    private void generateAndSaveDayInput(String operator, String year,String month,List<String> importDayList, List<WrDayInputInDayModel> inputList) {
        //日水情录入
        List<WrDayInput> dayInputList = new ArrayList();
        for (WrDayInputInDayModel localModel : inputList) {
            if(StringUtils.isEmpty(localModel.getBuildingId())){
                log.error("当前引水口编码没有配置适合的引水口信息，其编码为：{}",localModel.getBuildingCode());
                continue;
            }
            for (String dayStr : importDayList) {
                //构建每一天
                String day = StringUtils.join(new String[]{year,
                        StringUtils.leftPad(month,2,'0'),
                        StringUtils.leftPad(dayStr,2,'0')},'-');
                Field monthField = DAY_FIELD_MAP.get(Integer.parseInt(dayStr));
                Object val = null;
                if (null != monthField) {
                    val = ReflectionUtils.getField(monthField, localModel);
                }
                if(!(val instanceof Double)){
                    log.error("当前天没有填报任何流量，时间为：{}，引水口名称为：{}", day,localModel.getBuildingName());
                    continue;
                }
                Double avgWaterFlow = (Double) val;
                WrDayInput wrDayInput = new WrDayInput();
                wrDayInput.setId(IDGenerator.getId());
                wrDayInput.setAuto(0);
                wrDayInput.setManualWaterFlow(avgWaterFlow);
                wrDayInput.setStationId(localModel.getBuildingId());
                wrDayInput.setTimeType(4);
                Date now = DateUtil.date();
                wrDayInput.setTime(DateUtils.convertStringTimeToDateExt(day));
                wrDayInput.setOperateTime(now);
                wrDayInput.setApproveTime(now);
                wrDayInput.setOperatorId(operator);
                wrDayInput.setApproveId(operator);
                wrDayInput.setStatus(1);
                dayInputList.add(wrDayInput);
            }
        }
        if(dayInputList.size() > 0){
            wrDayInputService.saveBatch(dayInputList);
        }
    }

    private void setBuildingId(List<WrDayInputInDayModel> inputList) {
        Map<String, WrDayInputInDayModel> inputMap = inputList.stream().collect(Collectors.toMap(WrDayInputInDayModel::getBuildingCode, e -> e));
        LambdaQueryWrapper<WrDiversionPort> wrapper = new QueryWrapper().lambda();
        wrapper.in(WrDiversionPort::getBuildingCode,inputMap.keySet());
        wrapper.in(WrDiversionPort::getBuildingLevel,Arrays.asList(WrBuildingEnum.BUILDING_LEVEL_2,BUILDING_LEVEL_1_2));
        List<WrDiversionPort> portList = wrDiversionPortMapper.selectList(wrapper);
        if(CollectionUtils.isNotEmpty(portList)){
            Map<String, WrDiversionPort> map = portList.stream()
                    .filter(e -> StringUtils.isNotEmpty(e.getBuildingCode()))
                    .collect(Collectors.toMap(WrDiversionPort::getBuildingCode, e -> e));
            inputMap.entrySet().forEach(entry -> {
                String code = entry.getKey();
                WrDayInputInDayModel model = entry.getValue();
                WrDiversionPort port = map.get(code);
                if(null != port){
                    model.setBuildingId(port.getId());
                }
            });
        }
    }
}


