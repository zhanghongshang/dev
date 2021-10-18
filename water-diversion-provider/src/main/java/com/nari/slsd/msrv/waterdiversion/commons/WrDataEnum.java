package com.nari.slsd.msrv.waterdiversion.commons;

/**
 * @Program: water-diversion
 * @Description:
 * @Author: reset kalar
 * @Date: 2021-09-01 10:38
 **/
public class WrDataEnum {

    /**
     * 数据表枚举
     */
    /**
     * 月数据
     */
    public static final String TABLE_MINW_S = "month";
    /**
     * 日数据（月审核后）
     */
    public static final String TABLE_DINW_S = "day";
    /**
     * 日数据
     */
    public static final String TABLE_DINW_S_CHECKED = "daychecked";
    /**
     * 小时数据
     */
    public static final String TABLE_HINW_S = "hour";

    /**
     * 值类型枚举
     */
    /**
     * 水位
     */
    public static final String VAL_TYPE_Z = "z";
    /**
     * 流量
     */
    public static final String VAL_TYPE_Q = "q";
    /**
     * 水量（含公摊系数）
     */
    public static final String VAL_TYPE_W = "w";

    /**
     * 水量（不含公摊系数）
     */
    public static final String VAL_TYPE_WT = "wt";

}
