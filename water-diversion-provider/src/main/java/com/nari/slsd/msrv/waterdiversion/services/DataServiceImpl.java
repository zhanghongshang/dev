package com.nari.slsd.msrv.waterdiversion.services;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nari.slsd.hu.mplat.imc.client.Param.Param;
import com.nari.slsd.hu.mplat.imc.client.dto.Chardata;
import com.nari.slsd.hu.mplat.imc.client.dto.FetchParam;
import com.nari.slsd.hu.mplat.imc.client.dto.TimeCond;
import com.nari.slsd.hu.mplat.imc.client.service.ImcDataProxy;
import com.nari.slsd.msrv.common.utils.DateUtils;
import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.msrv.waterdiversion.commons.PointTypeEnum;
import com.nari.slsd.msrv.waterdiversion.interfaces.IDataService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWaterPointService;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrDayInputMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.*;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrDayInput;
import com.nari.slsd.msrv.waterdiversion.model.vo.SimpleWrDayInputVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.nari.slsd.msrv.waterdiversion.utils.CommonUtil.Convert2Quantity;

/**
 * @author Created by ZHD
 * @program: DataServiceImpl
 * @description:
 * @date: 2021/8/16 17:41
 */
@Slf4j
@Service
public class DataServiceImpl implements IDataService {

    @Autowired
    IWaterPointService iWaterPointService;

    @Autowired
    ImcDataProxy imcDataProxy;

    @Autowired
    private WrDayInputMapper wrDayInputMapper;

    @Override
    public List<DataBuildingDto> getSpecialDataRunRtreal(List<String> buildings, List<String> pointType) {
        return getSpecialData(buildings, pointType, null, null, Param.ValType.Special_V, Param.RunDataType.RUN_RTREAL, null);
    }

    /**
     * TODO 方法改造成直接读取日水情数据
     * @param buildings
     * @param pointType
     * @param sdt
     * @param edt
     * @param valType
     * @param runDataType
     * @param calcType
     * @return
     */
    @Override
    public List<DataBuildingDto> getSpecialDataRunDataType(List<String> buildings, List<String> pointType, Long sdt, Long edt,
                                                           String valType, String runDataType,String calcType ) {
////        String calcType = Param.CalcType.CALC_SPANREAL;
//        String calcType = null;
        if(!PointTypeEnum.WATER_VOLUME.getId().equals(pointType.get(0))){
            return getSpecialData(buildings, pointType, sdt, edt, valType, runDataType, calcType);
        }
        return getSpecialDataExt(buildings, sdt, edt, calcType);
    }


    @Override
    public List<DataSetBuildingDto> getSpecialDataSet(List<String> buildings, List<String> pointType,
                                  Long sdt, Long edt,
                                  String valType, String runDataType) {
        String calcType = null;
        List<DataSetBuildingDto> result = new ArrayList<>();
        List<WaterPointDTO> waterPointDTOList = iWaterPointService.getWaterPointId(buildings, pointType);
        //按照测点PM、WDS来源分组
        Map<String, List<WaterPointDTO>> sourceMap = waterPointDTOList.stream().collect(Collectors.groupingBy(WaterPointDTO::getCorrelationSource));
        FetchParam[] fetchParams = getSpecialDataPoint(sourceMap, valType, runDataType, calcType, sdt, edt);
        HashMap<Long, List<Chardata>>[] dataSet = new HashMap[]{};
        if (fetchParams.length != 0) {
            dataSet = imcDataProxy.getSpecialDataSet(fetchParams);
        }
        if (StringUtils.isNotEmpty((dataSet.length == 0) ? null : dataSet[0])) {
            HashMap<Long, List<Chardata>> chardataSetHashMap = new HashMap<>();
            Arrays.stream(dataSet).forEach((mapSetData) -> chardataSetHashMap.putAll(mapSetData));
            return chardatasToDataSetBuildingDtos(chardataSetHashMap, waterPointDTOList,
                    valType, runDataType);
        }
        return result;
    }

    private List<DataBuildingDto> getSpecialData(List<String> buildings, List<String> pointType, Long sdt, Long edt,
                                                 String valType, String runDataType, String calcType) {
        List<DataBuildingDto> result = new ArrayList<>();
        List<WaterPointDTO> waterPointDTOList = iWaterPointService.getWaterPointId(buildings, pointType);
        //按照测点PM、WDS来源分组
        Map<String, List<WaterPointDTO>> sourceMap = waterPointDTOList.stream().collect(Collectors.groupingBy(WaterPointDTO::getCorrelationSource));
        FetchParam[] fetchParams = getSpecialDataPoint(sourceMap, valType, runDataType, calcType, sdt, edt);
        HashMap<Long, Chardata>[] data = new HashMap[]{};
        if (fetchParams.length != 0) {
            data = imcDataProxy.getSpecialData(fetchParams);
        }

        if (StringUtils.isNotEmpty((data.length == 0) ? null : data[0])) {
            HashMap<Long, Chardata> chardataHashMap = new HashMap<>();
            Arrays.stream(data).forEach((mapData) -> chardataHashMap.putAll(mapData));
            return chardataToDataBuildingDtos(chardataHashMap, waterPointDTOList,
                    valType, runDataType);
        }
        return result;
    }

    /**
     * TODO 对于可能存在的空指针未进行处理
     * @param buildings
     * @param sdt
     * @param edt
     * @param calcType
     * @return
     */
    private List<DataBuildingDto> getSpecialDataExt(List<String> buildings, Long sdt, Long edt, String calcType) {
        List<DataBuildingDto> result = new ArrayList<>();
        Date startDate = DateUtils.convertTimeToDate(sdt);
        Date endDate = DateUtils.convertTimeToDate(edt);
        if(Param.CalcType.CALC_SUM.equals(calcType)){
            //累计
            List<SimpleWrDayInputVO> dayInputVOList = getDayInputVOList(buildings, startDate, endDate);
            if(CollectionUtils.isNotEmpty(dayInputVOList)){
                for (SimpleWrDayInputVO simpleWrDayInputVO : dayInputVOList) {
                    DataBuildingDto dataBuildingDto = new DataBuildingDto();
                    //引水口id
                    dataBuildingDto.setId(simpleWrDayInputVO.getStationId());
                    List<DataPointDto> dataPointList = new ArrayList<>();
                    dataBuildingDto.setDataPointDtos(dataPointList);
                    DataPointDto dto = new DataPointDto();
                    dataPointList.add(dto);
                    //流量转为水量
                    dto.setV(Convert2Quantity(simpleWrDayInputVO.getWaterFlow(),1).doubleValue());
                    result.add(dataBuildingDto);
                }
            }
        }else{
            //获取每天实引数据
            List<WrDayInput> wrDayInputList = getWrDayInputList(buildings, startDate, endDate);
            if(CollectionUtils.isNotEmpty(wrDayInputList)){
                Map<String, List<WrDayInput>> inputListMap = wrDayInputList.stream().collect(Collectors.groupingBy(WrDayInput::getStationId));
                inputListMap.entrySet().forEach(en -> {
                    List<WrDayInput> inputListForBuilding = en.getValue();
                    DataBuildingDto buildingDto = new DataBuildingDto();
                    //引水口id
                    buildingDto.setId(en.getKey());
                    List<DataPointDto> dataPointList = new ArrayList<>();
                    buildingDto.setDataPointDtos(dataPointList);
                    for (WrDayInput input : inputListForBuilding) {
                        DataPointDto dto = new DataPointDto();
                        dataPointList.add(dto);
                        //流量转为水量
                        dto.setV(Convert2Quantity(input.getManualWaterFlow(),1).doubleValue());
                        dto.setTime(input.getTime().getTime());
                    }
                    result.add(buildingDto);
                });
            }
        }
        return result;
    }

    private List<SimpleWrDayInputVO> getDayInputVOList(List<String> buildingIdList , Date starTime, Date endTime) {
        LambdaQueryWrapper<WrDayInput> wrapper = new QueryWrapper().lambda();
        wrapper.between(WrDayInput::getTime,starTime,endTime);
        if(CollectionUtils.isNotEmpty(buildingIdList)){
            if(buildingIdList.size() > 1){
                wrapper.in(WrDayInput::getStationId,buildingIdList);
            }else{
                wrapper.eq(WrDayInput::getStationId,buildingIdList.get(0));
            }
        }
        wrapper.groupBy(WrDayInput::getStationId);
        return wrDayInputMapper.getSumWaterFlow(wrapper);
    }

    private List<WrDayInput> getWrDayInputList(List<String> buildingIdList , Date starTime, Date endTime) {
        LambdaQueryWrapper<WrDayInput> wrapper = new QueryWrapper().lambda();
        wrapper.between(WrDayInput::getTime,starTime,endTime);
        if(CollectionUtils.isNotEmpty(buildingIdList)){
            if(buildingIdList.size() == 1){
                wrapper.eq(WrDayInput::getStationId,buildingIdList.get(0));
            }else{
                wrapper.in(WrDayInput::getStationId,buildingIdList);
            }
        }
        return wrDayInputMapper.selectList(wrapper);
    }

    //FetchParam[] 封装 sourceMap key为pm wds等分类
    private FetchParam[] getSpecialDataPoint(Map<String, List<WaterPointDTO>> sourceMap,
                                             String valType, String runDataType, String calcType, Long sdt, Long edt) {
        List<FetchParam> fetchParamList = new ArrayList<>();
        for (String sourceKey : sourceMap.keySet()) {
            FetchParam fetchParam = new FetchParam(sourceKey);
            List<Long> pointIdLongs = new ArrayList<>();
            sourceMap.get(sourceKey).stream().forEach((waterPointDTO) -> {
                if (StringUtils.isNotEmpty(waterPointDTO.getCorrelationCode())) {
                    pointIdLongs.add(Long.parseLong(waterPointDTO.getCorrelationCode()));
                }
            });
            fetchParam.idarrayLongs = pointIdLongs.toArray(new Long[pointIdLongs.size()]);
            fetchParam.valtype = valType;
            fetchParam.rundatatype = runDataType;
            fetchParam.calctype = calcType;
            if (StringUtils.isNotEmpty(sdt) && StringUtils.isNotEmpty(edt)) {
                Calendar bt = Calendar.getInstance();
                bt.setTimeInMillis(sdt);
                Calendar et = Calendar.getInstance();
                et.setTimeInMillis(edt);
                fetchParam.timeCond = new TimeCond(">=", bt, "<=", et);
            }
            if (!pointIdLongs.isEmpty()) {
                fetchParamList.add(fetchParam);
            }
        }

        return fetchParamList.toArray(new FetchParam[fetchParamList.size()]);
    }

    private List<DataBuildingDto> chardataToDataBuildingDtos(HashMap<Long, Chardata> data,
                                                             List<WaterPointDTO> waterPointDTOList,
                                                             String valType,
                                                             String runDataType) {
        List<DataBuildingDto> dataBuildingDtos = new ArrayList<>();
        //按照测站分组
        Map<String, List<WaterPointDTO>> buildingIdMap = waterPointDTOList.stream().collect(Collectors.groupingBy(WaterPointDTO::getBuildingId));
        for (String buildingKey : buildingIdMap.keySet()) {
            DataBuildingDto dataBuildingDto = new DataBuildingDto();
            dataBuildingDto.setId(buildingKey);//测站id
            dataBuildingDto.setBuildingName(null);//TODO 测站名称
            List<WaterPointDTO> waterPointDTOS = buildingIdMap.get(buildingKey);//根据测站获取测站下测点信息
            waterPointDTOS = waterPointDTOS.stream().filter(item -> StringUtils.isNotEmpty(item.getCorrelationCode())).collect(Collectors.toList());
            List<DataPointDto> dataPointDtos = new ArrayList<>();
            waterPointDTOS.stream().forEach(waterPointDTO -> {
                Chardata chardata = data.get(Long.parseLong(waterPointDTO.getCorrelationCode()));
                DataPointDto dataPointDto = new DataPointDto();
                dataPointDto.setCorrelationCode(waterPointDTO.getCorrelationCode());
                dataPointDto.setPointType(waterPointDTO.getPointType());
                dataPointDto.setTime(chardata.getTime().getTime().getTime());
                dataPointDto.setV(chardata.getV());
                dataPointDto.setValType(valType);
                dataPointDto.setRunDataType(runDataType);
                dataPointDtos.add(dataPointDto);
            });
            dataBuildingDto.setDataPointDtos(dataPointDtos);
            dataBuildingDtos.add(dataBuildingDto);
        }
        log.info(dataBuildingDtos.toString());
        return dataBuildingDtos;
    }

    private List<DataSetBuildingDto> chardatasToDataSetBuildingDtos(HashMap<Long, List<Chardata>> data,
                                                                    List<WaterPointDTO> waterPointDTOList,
                                                                    String valType,
                                                                    String runDataType) {
        List<DataSetBuildingDto> dataSetBuildingDtos = new ArrayList<>();
        //按照测站分组
        Map<String, List<WaterPointDTO>> buildingIdMap = waterPointDTOList.stream().collect(Collectors.groupingBy(WaterPointDTO::getBuildingId));
        for (String buildingKey : buildingIdMap.keySet()) {
            DataSetBuildingDto dataSetBuildingDto = new DataSetBuildingDto();
            dataSetBuildingDto.setId(buildingKey);//测站id
            dataSetBuildingDto.setBuildingName(null);//TODO 测站名称
            List<WaterPointDTO> waterPointDTOS = buildingIdMap.get(buildingKey);//根据测站获取测站下测点信息
            waterPointDTOS = waterPointDTOS.stream().filter(item -> StringUtils.isNotEmpty(item.getCorrelationCode())).collect(Collectors.toList());
            List<DataSetPointDto> dataSetPointDtos = new ArrayList<>();
            waterPointDTOS.stream().forEach( waterPointDTO -> {
                List<Chardata> chardatas = data.get(Long.parseLong(waterPointDTO.getCorrelationCode()));
                DataSetPointDto dataSetPointDto = new DataSetPointDto();
                dataSetPointDto.setCorrelationCode(waterPointDTO.getCorrelationCode());
                dataSetPointDto.setPointType(waterPointDTO.getPointType());
                dataSetPointDto.setValType(valType);
                dataSetPointDto.setRunDataType(runDataType);
                dataSetPointDto.setDataVDTOS(chardatasToDataVDTO(chardatas));
                dataSetPointDtos.add(dataSetPointDto);
            });
            dataSetBuildingDto.setDataPointDtos(dataSetPointDtos);
            dataSetBuildingDtos.add(dataSetBuildingDto);
        }
        log.info(dataSetBuildingDtos.toString());
        return dataSetBuildingDtos;
    }
   private List<DataVDTO> chardatasToDataVDTO(List<Chardata> chardatas){
        List<DataVDTO> dataVDTOS = new ArrayList<>();
        chardatas.stream().forEach( chardata -> {
            DataVDTO dataVDTO = new DataVDTO();
            dataVDTO.setV(chardata.getV());
            dataVDTO.setTime(chardata.getTime().getTime().getTime());
            dataVDTOS.add(dataVDTO);
        });
        return dataVDTOS;
    }

}
