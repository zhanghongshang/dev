package com.nari.slsd.msrv.waterdiversion.utils;

import java.util.Calendar;

/**
 * @Description 日期转换工具类
 * @Author ZHS
 * @Date 2021/10/13 11:02
 */
public class DateUtil {
    /**
     * 获取月份的天数
     * @param year
     * @param month
     * @return
     */
    public static int getDaysOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
}
