package com.nari.slsd.msrv.waterdiversion.commons;

/**
 * @Program: water-diversion
 * @Description: 曲线枚举类
 * @Author: reset kalar
 * @Date: 2021-08-16 11:41
 **/
public class WrCurveEnum {
    /**
     * 曲线审核状态
     */
    /**
     * 未审核
     */
    public static final Integer CURVE_STATE_UNREVIEWED = 0;
    /**
     * 启用
     */
    public static final Integer CURVE_STATE_ENABLED = 1;
    /**
     * 停用
     */
    public static final Integer CURVE_STATE_DISABLED = 2;

    /**
     * 曲线类型
     * 水位-流量曲线
     */
    public static final String CURVE_TYPE_WATERLV_FLOW = "1";

    /**
     * 维数
     */
    public static final Integer CURVE_DIMENSIONAL_TWO = 2;


}
