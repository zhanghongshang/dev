package com.nari.slsd.msrv.waterdiversion.services;

import com.nari.slsd.hu.mplat.imc.client.Param.Param;
import com.nari.slsd.hu.mplat.imc.client.dto.Chardata;
import com.nari.slsd.hu.mplat.imc.client.dto.FetchParam;
import com.nari.slsd.hu.mplat.imc.client.dto.TimeCond;
import com.nari.slsd.hu.mplat.imc.client.service.ImcDataProxy;
import com.nari.slsd.msrv.common.utils.DateUtils;
import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.msrv.waterdiversion.interfaces.IHisDataCompareService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWaterPointService;
import com.nari.slsd.msrv.waterdiversion.model.dto.HisDataCompareMonthDto;
import com.nari.slsd.msrv.waterdiversion.model.dto.WaterPointDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

/**
 * @author Created by ZHD
 * @program: HisDataCompareServiceImpl
 * @description:
 * @date: 2021/8/22 16:58
 */
@Service
@Slf4j
public class HisDataCompareServiceImpl implements IHisDataCompareService {
    @Autowired
    IWaterPointService iWaterPointService;

    @Autowired
    ImcDataProxy imcDataProxy;

    @Autowired
    DataServiceImpl dataService;

    @Override
    public List<HisDataCompareMonthDto> getHisDataCompareMonth(String building, List<String> pointType, String runDataType, String valType, List<Integer> years)  {
        List<HisDataCompareMonthDto> result = new ArrayList<>();
        List<String> buildings = new ArrayList<>();
        buildings.add(building);
        List<WaterPointDTO> waterPointDTOList = iWaterPointService.getWaterPointId(buildings, pointType);
        List<FetchParam> fetchParamList = getFetchParamMonth(waterPointDTOList,runDataType,valType,years);
        HashMap<Long, List<Chardata>> chardataSetHashMap = getSpecialDataSetFetchParam(fetchParamList);
        if (chardataSetHashMap.size()==0){
            return result;
        }
        waterPointDTOList.stream().forEach(waterPointDTO -> {
            HisDataCompareMonthDto hisDataCompareMonth = new HisDataCompareMonthDto();
            hisDataCompareMonth.setPointType(waterPointDTO.getPointType());
            hisDataCompareMonth.setCorrelationCode(waterPointDTO.getCorrelationCode());
            List<Chardata> chardatas = chardataSetHashMap.get(Long.parseLong(waterPointDTO.getCorrelationCode()));
            Map<Integer, List<Chardata>> chardataMapYear = chardatas.stream().collect(Collectors.groupingBy(vo -> vo.getTime().get(Calendar.YEAR)));
            List data = new ArrayList();
            for (Integer year: years) {
                Map<String,Object> map = new HashMap<>();
                map.put("name",year);
                List monthData = new ArrayList();
                List<Chardata> chardatasYear = chardataMapYear.get(year);
                if (CollectionUtils.isNotEmpty(chardatasYear)){
                    Map<Integer, Chardata> chardataMapMonth = chardatasYear.stream().collect(Collectors.toMap((vo -> vo.getTime().get(Calendar.MONTH)), a -> a,(k1,k2)->k1));
                    for (int i = 0; i <12; i++) {
                        Chardata chardata = chardataMapMonth.get(i);//获取的时间从0开始 十二个月的数据
                        monthData.add(StringUtils.isEmpty(chardata)?null:chardata.getV());
                    }
                }
                map.put("data",monthData);
                data.add(map);
            }
            hisDataCompareMonth.setData(data);
            result.add(hisDataCompareMonth);
        });
        return result;
    }

    @Override
    public List<HisDataCompareMonthDto> getHisDataCompareDay(String building, List<String> pointType, String runDataType,String valType, List<Long>[] date, List<Integer> years) {
        List<HisDataCompareMonthDto> result = new ArrayList<>();
        List<String> buildings = new ArrayList<>();
        buildings.add(building);
        List<WaterPointDTO> waterPointDTOList = iWaterPointService.getWaterPointId(buildings, pointType);
        List<FetchParam> fetchParamList = getFetchParamDay(waterPointDTOList,runDataType,valType,date);
        HashMap<Long, List<Chardata>> chardataSetHashMap = getSpecialDataSetFetchParam(fetchParamList);
        if (chardataSetHashMap.size()==0){
            return result;
        }
        waterPointDTOList.stream().forEach(waterPointDTO -> {
            HisDataCompareMonthDto hisDataCompareMonth = new HisDataCompareMonthDto();
            hisDataCompareMonth.setPointType(waterPointDTO.getPointType());
            hisDataCompareMonth.setCorrelationCode(waterPointDTO.getCorrelationCode());
            List<Chardata> chardatas = chardataSetHashMap.get(Long.parseLong(waterPointDTO.getCorrelationCode()));
            Map<Integer, List<Chardata>> chardataMapYear = chardatas.stream().collect(Collectors.groupingBy(vo -> vo.getTime().get(Calendar.YEAR)));
            List data = new ArrayList();
            for (Integer year: years) {
                Map<String,Object> map = new HashMap<>();
                map.put("name",year);
                List monthData = new ArrayList();
                List<Chardata> chardatasYear = chardataMapYear.get(year);
                if (CollectionUtils.isNotEmpty(chardatasYear)){
                    Map<String, Chardata> chardataMapMonth = chardatasYear.stream().collect(Collectors.toMap(
                            (vo -> vo.getTime().get(Calendar.MONTH) +"-"+ vo.getTime().get(Calendar.DATE)), a -> a,(k1,k2)->k1));
                    List<Date> dates = findDates(DateUtils.convertTimeToDate(Long.parseLong(date[0].get(0).toString())),
                            DateUtils.convertTimeToDate(Long.parseLong(date[0].get(1).toString())));
                    dates.stream().forEach(dateD -> {
                        Calendar bt = Calendar.getInstance();
                        bt.setTimeInMillis(dateD.getTime());
                        String key  = bt.get(Calendar.MONTH)+"-"+bt.get(Calendar.DATE);
                        Chardata chardata = chardataMapMonth.get(key);
                        monthData.add(StringUtils.isEmpty(chardata)?null:chardata.getV());
                    });
                }
                map.put("data",monthData);
                data.add(map);
            }
            hisDataCompareMonth.setData(data);
            result.add(hisDataCompareMonth);
        });
        return result;
    }
    //获取月fetchparam参数
    private List<FetchParam> getFetchParamMonth(List<WaterPointDTO> waterPointDTOList,
                                               String runDataType,String valType,
                                               List<Integer> years){
        List<FetchParam> fetchParamList = new ArrayList<>();
        years.stream().forEach(year ->{
            FetchParam fetchParam = new FetchParam(Param.AppType.APP_Type_WDS);
            List<Long> pointIdLongs = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(waterPointDTOList)){
                waterPointDTOList.stream().forEach( waterPointDTO ->{
                    if (StringUtils.isNotEmpty(waterPointDTO.getCorrelationCode())){
                        pointIdLongs.add(Long.parseLong(waterPointDTO.getCorrelationCode()));
                    }
                }
                );
            }
            fetchParam.idarrayLongs = pointIdLongs.toArray(new Long[pointIdLongs.size()]);
            fetchParam.valtype = valType;
            fetchParam.rundatatype = runDataType;
//            fetchParam.calctype = calcType;
            Calendar bt = Calendar.getInstance();
            Calendar et = Calendar.getInstance();
            SimpleDateFormat simpleDateFormatBt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat simpleDateFormatEt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                bt.setTimeInMillis(simpleDateFormatBt.parse(year+"-01-01 00:00:00").getTime());
                et.setTimeInMillis(simpleDateFormatEt.parse(year+"-12-31 23:59:59").getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            fetchParam.timeCond = new TimeCond(">=", bt, "<=", et);
            fetchParamList.add(fetchParam);
        });
        return fetchParamList;
    }

    //获取日fetchparam参数
    private List<FetchParam> getFetchParamDay(List<WaterPointDTO> waterPointDTOList,
                                               String runDataType,String valType,
                                               List<Long>[] date){
        List<FetchParam> fetchParamList = new ArrayList<>();
        stream(date).forEach(month ->{
            FetchParam fetchParam = new FetchParam(Param.AppType.APP_Type_WDS);
            List<Long> pointIdLongs = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(waterPointDTOList)){
                waterPointDTOList.stream().forEach( waterPointDTO ->{
                    if (StringUtils.isNotEmpty(waterPointDTO.getCorrelationCode())){
                        pointIdLongs.add(Long.parseLong(waterPointDTO.getCorrelationCode()));
                    }
                });
            }
            fetchParam.idarrayLongs = pointIdLongs.toArray(new Long[pointIdLongs.size()]);
            fetchParam.valtype = valType;
            fetchParam.rundatatype = runDataType;
//            fetchParam.calctype = calcType;
            Calendar bt = Calendar.getInstance();
            Calendar et = Calendar.getInstance();
            bt.setTimeInMillis(Long.parseLong(month.get(0).toString()));
            et.setTimeInMillis(Long.parseLong(month.get(1).toString()));
            fetchParam.timeCond = new TimeCond(">=", bt, "<=", et);
            fetchParamList.add(fetchParam);
        });
        return fetchParamList;
    }


    private HashMap<Long, List<Chardata>> getSpecialDataSetFetchParam(List<FetchParam> fetchParamList){
        HashMap<Long, List<Chardata>> chardataSetHashMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(fetchParamList)) {
            HashMap<Long, List<Chardata>>[] hashMaps = imcDataProxy.getSpecialDataSet(fetchParamList.toArray(new FetchParam[fetchParamList.size()]));
            if (StringUtils.isNotEmpty((hashMaps.length == 0) ? null : hashMaps[0])){
                stream(hashMaps).forEach((mapSetData) ->  {
                    for (Long senId: mapSetData.keySet()) {
                        if (chardataSetHashMap.containsKey(senId)){
                            List<Chardata> old = chardataSetHashMap.get(senId);
                            old.addAll(mapSetData.get(senId));
                            chardataSetHashMap.put(senId,old);
                        }else {
                            chardataSetHashMap.put(senId,mapSetData.get(senId));
                        }
                    }
                });
            }
        }
        return chardataSetHashMap;
    }
    //获取两个时间内的天
    public static List<Date> findDates(Date dBegin, Date dEnd) {
        List lDate = new ArrayList();
        lDate.add(dBegin);
        Calendar calBegin = Calendar.getInstance();
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(dEnd);
        while (dEnd.after(calBegin.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            lDate.add(calBegin.getTime());
        }
        return lDate;
    }
}
