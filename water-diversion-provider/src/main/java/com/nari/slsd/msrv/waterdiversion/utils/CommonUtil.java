package com.nari.slsd.msrv.waterdiversion.utils;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.nari.slsd.msrv.waterdiversion.processer.interfaces.Converter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.springframework.util.ReflectionUtils.findMethod;
import static org.springframework.util.ReflectionUtils.makeAccessible;

/**
 * @author bigb
 * @version 1.0.0
 * @ClassName CommonUtil
 * @Description bigb
 * @createTime 2021年08月20日
 */
public class CommonUtil {

    public static final String FORMAT_SECOND = "yyyy-MM-dd HH:mm:ss";

    public static final String FORMAT_DAY = "yyyy-MM-dd";

    /**
     * mapping:sourceName:targetName
     */
    public static final String MAPPING_START = "mapping:";

    /**
     * 保留4位有效小数位数
     * @param
     * @return
     */
    public static BigDecimal number(Double d){
        if (null == d){
            return new BigDecimal(0);
        }
       BigDecimal b =  new BigDecimal(d);
       b = b.setScale(4,BigDecimal.ROUND_HALF_DOWN);
    return b;
    }

    /**
     * 保留4位有效小数位数
     * @param
     * @return
     */
    public static BigDecimal nonNullOfBigDecimal(BigDecimal big){
        if(null == big){
            return new BigDecimal(0D);
        }
        return big;
    }
    /**
     * 将redis中字符串转为实体bean
     * @param instance
     * @param clazz
     * @param <T>
     * @return
     */
    public static<T> T getInstanceForRedis(Object instance , Class<T> clazz) {
        if (null == instance) {
            return null;
        }
        return JSON.toJavaObject(JSON.parseObject(instance.toString()), clazz);
    }

    /**
     * dto->实体bean
     * @param doList
     * @param setMap 替换目标对象字段值
     * @param <T>
     * @return
     */
    public static <T> List<T> convert2EntityList(List<?> doList , Class<T> clazz , Map<String,Object> setMap) {
        if (CollectionUtils.isEmpty(doList)) {
            return new ArrayList();
        } else {
            List<T> returnList = new ArrayList();
            doList.stream().filter((data) -> data != null).forEach((data) -> {
                T target = convert2Entity(data, clazz , setMap);
                returnList.add(target);
            });
            return returnList;
        }
    }

    /**
     * id所在字段名可以解析@Id注解获取到，考虑到性能，暂时只支持指定
     * @param source
     * @param clazz
     * @param setMap
     * @param <T>
     * @return
     */
    public static <T>T convert2Entity(Object source , Class<T> clazz , Map<String,Object> setMap) {
        Assert.notNull(source,"CommonUtil#convert2Entity source can not null");
        Assert.notNull(clazz,"CommonUtil#convert2Entity clazz can not null");
        T target = BeanUtils.instantiateClass(clazz);
        String[] ignoreCopyFieldNames = getIgnoreCopyFieldNames(setMap);
        if(null != ignoreCopyFieldNames){
            BeanUtils.copyProperties(source,target,ignoreCopyFieldNames);
        }else{
            BeanUtils.copyProperties(source,target);
        }
        if(MapUtils.isNotEmpty(setMap)){
            setMap.entrySet().forEach(entry -> {
                String fieldName = entry.getKey();
                Object val = entry.getValue();
                Field targetField = null;
                Field sourceField = null;
                if(fieldName.startsWith(MAPPING_START)){
                    //mapping:sourceName:targetName
                    String[] nameArr = fieldName.substring(MAPPING_START.length()).split(":");
                    if(null != nameArr && nameArr.length == 2){
                        String sourceName = nameArr[0];
                        String targetName = nameArr[1];
                        sourceField = getAccessibleField(source.getClass(),sourceName);
                        targetField = getAccessibleField(clazz, targetName);
                    }
                }else{
                    sourceField = getAccessibleField(source.getClass(), entry.getKey());
                    targetField = getAccessibleField(clazz, entry.getKey());
                }
                if(null != targetField){
                    if(val instanceof Converter){
                        Converter converter = (Converter) val;
                        if(null != sourceField){
                            val = converter.convert(ReflectionUtils.getField(sourceField, source));
                        }
                    }
                    ReflectionUtils.setField(targetField,target,val);
                }
            });
        }
        return target;
    }

    private static Field getAccessibleField(Class<?> clazz , String fieldName){
        Field field = ReflectionUtils.findField(clazz, fieldName);
        if(null != field){
            ReflectionUtils.makeAccessible(field);
        }
        return field;
    }

    public static Method getAccessibleMethod(Class<?> clazz , String methodName , Class<?> ... clazzArr){
        Method mainMethod = findMethod(clazz,methodName,clazzArr);
        if (null != mainMethod){
            makeAccessible(mainMethod);
        }
        return mainMethod;
    }

    /**
     * BeanUtils.copy忽略的字段集
     * @param setMap
     * @return
     */
    private static String[] getIgnoreCopyFieldNames(Map<String,Object> setMap){
        if(MapUtils.isNotEmpty(setMap)){
            List<String> ignoreFieldList = new ArrayList<>();
            setMap.entrySet().stream().forEach(entry -> {
                String key = entry.getKey();
                if(key.startsWith(MAPPING_START)){
                    String[] nameArr = key.substring(MAPPING_START.length()).split(":");
                    if(null != nameArr && nameArr.length == 2){
                        ignoreFieldList.add(nameArr[1]);
                    }
                }else{
                    ignoreFieldList.add(key);
                }
            });
            return ignoreFieldList.toArray(new String[ignoreFieldList.size()]);
        }
        return null;
    }

    /**
     * 获取指定年月的天数
     * @param year
     * @param month
     * @return
     */
    public static int getDaysOfMonth(int year , int month){
        //获取当前时间
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,year);
        //下面可以设置月份，注：月份设置要减1，所以设置1月就是1-1，设置2月就是2-1，如此类推
        cal.set(Calendar.MONTH, month-1);
        //得到一个月最最后一天日期(31/30/29/28)
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取指定年月下旬的天数
     * @param year
     * @param month
     * @return
     */
    public static int getDaysOfTD3(int year , int month){
        int daysOfMonth = getDaysOfMonth(year, month);
        return daysOfMonth - 20;
    }

    /**
     * 获取指定年月下旬的秒数
     * @param year
     * @param month
     * @return
     */
    public static int getSecondsOfTD3(int year , int month){
        int daysOfMonth = getDaysOfTD3(year, month);
        return getSecondsOfDays(daysOfMonth);
    }

    /**
     * 获取指定年月的秒数
     * @param year
     * @param month
     * @return
     */
    public static int getSecondsOfMonth(int year , int month){
        int days = getDaysOfMonth(year, month);
        return getSecondsOfDays(days);
    }

    /**
     * 获取指定天数秒数
     * @param days
     * @return
     */
    public static int getSecondsOfDays(int days){
        return days * 24 * 60 * 60;
    }

    /**
     * 获取给定月天数
     * @return
     */
    public static int getDayNumOfCurrentMonth(int year , int month){
        month += 1;
        if(month > 12){
            month = (month + 1) % 12;
            year += 1;
        }
        LocalDate firstDayOfNextMonth = LocalDate.of(year, month, 1);
        return firstDayOfNextMonth.minusDays(1).getDayOfMonth();
    }

    /**
     * 本月第一天
     * @return
     */
    public static Date getFirstDayOfMonth(){
        return DateUtil.beginOfMonth(DateUtil.date());
    }

    /**
     * 前一天最后时间
     * @return
     */
    public static Date getEndTimeOfYesterday(){
        return DateUtil.endOfDay(DateUtil.yesterday());
    }

    /**
     * 本月最后一天
     * @return
     */
    public static Date getLastDayOfMonth(){
        return DateUtil.endOfMonth(DateUtil.date());
    }

    public static List<String> getDateStrArrBetweenSpecialDate(Date startDate, Date endDate) {
        if(null == startDate || null == endDate){
            return Collections.emptyList();
        }
        if(endDate.before(startDate)){
            return Collections.emptyList();
        }
        List<String> titleList = new ArrayList<>();
        long days = DateUtil.between(startDate, endDate, DateUnit.DAY);
        Date loopDate = startDate;
        for (int i = 1; i <= days + 1; i++) {
            titleList.add(DateUtil.format(loopDate, FORMAT_DAY));
            loopDate = DateUtils.addDays(loopDate, 1);
        }
        return titleList;
    }

    public static List<Date> getRemainMonth(){
        int month = LocalDate.now().getMonthValue();
        Date firstDayOfMonth = getFirstDayOfMonth();
        List<Date> dateList = new ArrayList<>();
        for(int i=1;i<=12-month;i++){
            Date nextDate = org.apache.commons.lang3.time.DateUtils.addMonths(firstDayOfMonth,i);
            dateList.add(nextDate);
        }
        return dateList;
    }

    public static List<Date> getRemainMonthContainsCurrent(){
        int month = LocalDate.now().getMonthValue();
        Date firstDayOfMonth = getFirstDayOfMonth();
        List<Date> dateList = new ArrayList<>();
        dateList.add(firstDayOfMonth);
        for(int i=1;i<=12-month;i++){
            Date nextDate = org.apache.commons.lang3.time.DateUtils.addMonths(firstDayOfMonth,i);
            dateList.add(nextDate);
        }
        return dateList;
    }

    /**
     * 水量转流量
     * @param quantity
     * @param days
     * @return
     */
    public static BigDecimal Convert2Flow(BigDecimal quantity , int days){
        long seconds = days * 86400;
        return NumberUtil.div(NumberUtil.mul(quantity,10000),seconds,4);
    }

    /**
     * 流量转水量
     * @param flow
     * @param days
     * @return
     */
    public static BigDecimal Convert2Quantity(Number flow , int days){
        long seconds = days * 86400;
        return NumberUtil.div(NumberUtil.mul(flow,seconds),10000,4);
    }

    /**
     * TODO
     * map反向按值进行分组
     * @param map
     * @return
     */
    public static Map<String,List<String>> reverseMap(Map<String,List<String>> map){
        Map<String,List<String>> result = new HashMap<>();
        if(MapUtils.isEmpty(map)){
            return result;
        }
        List<Map<String,String>> mapList = new ArrayList<>();
        map.entrySet().forEach(entry -> {
            List<String> localList = entry.getValue();
            if(CollectionUtils.isNotEmpty(localList)){
                for (String managerId: localList) {
                    Map<String,String> localMap = new HashMap<>();
                    localMap.put(entry.getKey(),managerId);
                    mapList.add(localMap);
                }
            }
        });

        for (Map<String, String> localMap : mapList) {
            String key = localMap.keySet().stream().findFirst().get();
            String val = localMap.get(key);
            if(!result.containsKey(val)){
                result.put(val,new ArrayList<>());
            }
            result.get(val).add(key);
        }
        return result;
    }

    public static void main(String[] args) {
        int daysOfMonth = getDaysOfMonth(2021, 8);
        System.out.println(daysOfMonth);
    }
}
