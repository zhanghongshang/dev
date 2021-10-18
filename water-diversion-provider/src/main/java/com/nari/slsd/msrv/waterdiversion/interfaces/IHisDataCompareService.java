package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.nari.slsd.msrv.waterdiversion.model.dto.HisDataCompareMonthDto;

import java.util.List;

/**
 * Created by 张洪迪 on 2021/8/22.
 */
public interface IHisDataCompareService {
    List<HisDataCompareMonthDto> getHisDataCompareMonth(String building, List<String> pointType, String runDataType, String valType, List<Integer> years);
    List<HisDataCompareMonthDto> getHisDataCompareDay(String building,List<String> pointType,String runDataType,String valType,List<Long>[] date, List<Integer> years);
}
