package com.nari.slsd.msrv.waterdiversion.processer.interfaces;

/**
 * @title
 * @description 类型转换
 * @author bigb
 * @updateTime 2021/3/2 20:05
 * @throws
 */
public interface Converter<S, T>{

    /**
     * @title
     * @description
     * @author bigb
     * @updateTime 2021/8/23 20:05
     * @throws
     */
    default T convert(S source){
        return null;
    };

    /**
     * @title
     * @description
     * @author bigb
     * @updateTime 2021/8/23 20:05
     * @throws
     */
    default T convert(S source , Class<T> type){
        return null;
    };

}