package com.nari.slsd.msrv.waterdiversion.processer;

import com.nari.slsd.msrv.common.utils.DateUtils;

import java.util.Date;

/**
 * @title
 * @description Long-Date
 * @author bigb
 * @updateTime 2021/3/2 20:08
 * @throws
 */
public class LongToDateConverter extends MyAbstractConverter<Long,Date>{
    @Override
    public Date convert(Long source) {
        if(null == source){
            return null;
        }
        return DateUtils.convertTimeToDate(source);
    }
}