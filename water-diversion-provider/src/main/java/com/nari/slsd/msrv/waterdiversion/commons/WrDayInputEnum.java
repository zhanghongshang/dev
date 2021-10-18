package com.nari.slsd.msrv.waterdiversion.commons;

/**
 * @Program: water-diversion
 * @Description: 日水情表枚举
 * @Author: reset kalar
 * @Date: 2021-08-21 13:49
 **/
public class WrDayInputEnum {
    /**
     * 状态
     * 0 未录入
     * 1 未审核
     * 2 已审核
     */
    public static final Integer DAY_STATUS_UNENTERED = 0;
    public static final Integer DAY_STATUS_UNREVIEWED = 1;
    public static final Integer DAY_STATUS_REVIEWED = 2;

    /**
     * 1 未校核 数据未审核完成
     * 2 未校核
     * 3 已校核
     */
    public static final Integer DAY_INPUT_STATUS_LACKED = 1;
    public static final Integer DAY_INPUT_STATUS_UNCHECKED = 2;
    public static final Integer DAY_INPUT_STATUS_CHECKED = 3;

    /**
     * 时间类型
     * 1 年
     * 2 月
     * 3 旬
     * 4 日
     */
    public static final Integer TIME_TYPE_YEAR = 1;
    public static final Integer TIME_TYPE_MONTH = 2;
    public static final Integer TIME_TYPE_TEN_DAYS = 3;
    public static final Integer TIME_TYPE_DAY = 4;

    /**
     * 人工/自动
     */
    public static final Integer DATA_TYPE_AUTO = 1;
    public static final Integer DATA_TYPE_MANUAL = 0;


}
