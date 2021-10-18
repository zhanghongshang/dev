package com.nari.slsd.msrv.waterdiversion.config.listener;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.nari.slsd.msrv.waterdiversion.config.annotations.ExcelField;
import com.nari.slsd.msrv.waterdiversion.config.excel.WaterPlanYearModel;
import com.nari.slsd.msrv.waterdiversion.processer.interfaces.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author bigb
 * @title
 * @description 年填报数据导入监听
 * @updateTime 2021/8/31 9:37
 * @throws
 */
@Slf4j
public class WaterPlanYearDataListener extends AnalysisEventListener<Object> {

    /**
     * 私有构造函数
     */
    private WaterPlanYearDataListener(){

    }

    private static Map<Integer, Field> fieldMap = new HashMap<>();

    private static Map<String, Field> name_field_map = new HashMap<>();

    /**
     * excel所有数据
     */
    private List<WaterPlanYearModel> modelList = new ArrayList<>();

    static {
        ReflectionUtils.doWithLocalFields(WaterPlanYearModel.class, field -> {
            ExcelField excelField = field.getDeclaredAnnotation(ExcelField.class);
            ExcelProperty excelProperty = field.getDeclaredAnnotation(ExcelProperty.class);
            //ignore字段忽略
            boolean put = (null == excelField || !excelField.ignore()) && null != excelProperty;
            if (put) {
                fieldMap.put(excelProperty.index(), field);
                name_field_map.put(field.getName(), field);
            }
        });
    }

    public static Map<String, Field> getNameFieldMap(){
        return name_field_map;
    }

    public static WaterPlanYearDataListener getInstance(){
        return new WaterPlanYearDataListener();
    }

    /**
     * 获取解析结果
     * @return
     */
    public List<WaterPlanYearModel> getParseResult(){
        //线程阻塞,直至文件读取完成
        return this.modelList;
    }

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(Object data, AnalysisContext context) {
        log.debug("解析到一条数据:{}", JSON.toJSONString(data));
        if (data instanceof LinkedHashMap) {
            WaterPlanYearModel target = new WaterPlanYearModel();
            LinkedHashMap dataMap = (LinkedHashMap) data;
            fieldMap.entrySet().forEach(entry -> {
                Integer index = entry.getKey();
                Field targetField = fieldMap.get(index);
                ReflectionUtils.makeAccessible(targetField);
                Object val = dataMap.get(index);
                //值是否需要转换
                ExcelField annotation = targetField.getAnnotation(ExcelField.class);
                if (null != annotation && null != val) {
                    //基础类型或者基础类型包装类
                    Class<?> type = targetField.getType();
                    if(!type.isInstance(val)){
                        Class<? extends Converter> converterClass = annotation.converter();
                        Converter converter = BeanUtils.instantiateClass(converterClass);
                        val = converter.convert(val, type);
                    }
                }
                ReflectionUtils.setField(targetField, target, val);
            });
            modelList.add(target);
        }
    }

    /**
     * 所有数据解析完成,分批存储数据库
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("excel读取完毕!");
    }
}