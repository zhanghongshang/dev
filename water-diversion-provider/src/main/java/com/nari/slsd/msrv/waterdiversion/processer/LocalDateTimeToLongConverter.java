package com.nari.slsd.msrv.waterdiversion.processer;

import com.nari.slsd.msrv.common.utils.DateUtils;

import java.util.Date;

/**
 * @title
 * @description Date->Long
 * @author bigb
 * @updateTime 2021/3/2 20:08
 * @throws
 */
public class LocalDateTimeToLongConverter extends MyAbstractConverter<Date,Long> {
    @Override
    public Long convert(Date source) {
        return DateUtils.convertDateToLong(source);
    }
}