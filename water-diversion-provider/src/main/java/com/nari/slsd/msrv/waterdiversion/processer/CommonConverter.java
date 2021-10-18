package com.nari.slsd.msrv.waterdiversion.processer;

/**
 * @title
 * @author bigb
 * @updateTime 2021/3/2 20:08
 * @throws
 */
public class CommonConverter extends MyAbstractConverter{
    @Override
    public Object convert(Object source, Class type) {
        if(null == source || null == type){
            return null;
        }
        return super.convert(source, type);
    }
}