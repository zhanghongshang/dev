package com.nari.slsd.msrv.waterdiversion.processer;


import com.nari.slsd.msrv.waterdiversion.processer.interfaces.Converter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bigb
 * @version 1.0.0
 * @ClassName ProcessorFactory
 * @createTime 2020年12月10日
 */
public class ProcessorFactory {
    public static final String CONVERTER_DATETIME_TO_LONG = "time-to-long-converter";

    public static final String CONVERTER_LONG_TO_DATETIME = "long-to-time-converter";

    /**
     * 类型转换器
     */
    private static final Map<String, Converter> CONVERTER_MAP = new HashMap<>();

    static{
        CONVERTER_MAP.put(CONVERTER_DATETIME_TO_LONG,new LocalDateTimeToLongConverter());
        CONVERTER_MAP.put(CONVERTER_LONG_TO_DATETIME,new LongToDateConverter());
    }

    /**
     * @param converter
     * @return
     */
    public static Converter getConverterInstance(String converter){
        return CONVERTER_MAP.get(converter) == null ? CONVERTER_MAP.get(CONVERTER_DATETIME_TO_LONG) : CONVERTER_MAP.get(converter);
    }
}
