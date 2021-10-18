package com.nari.slsd.msrv.waterdiversion.processer;

import cn.hutool.core.convert.ConverterRegistry;
import com.nari.slsd.msrv.waterdiversion.processer.interfaces.Converter;

/**
 * @title
 * @author bigb
 * @updateTime 2021/3/2 20:08
 * @throws
 */
public abstract class MyAbstractConverter<S,T> implements Converter<S,T> {

    private static final ConverterRegistry REGISTRY = ConverterRegistry.getInstance();

    @Override
    public T convert(S source, Class<T> type) {
        return REGISTRY.convert(type,source);
    }
}