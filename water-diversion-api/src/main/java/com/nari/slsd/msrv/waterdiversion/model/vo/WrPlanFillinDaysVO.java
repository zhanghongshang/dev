package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 近期计划填报计划填报
 * </p>
 *
 * @author zhs
 * @since 2021-08-04
 */
@Data
public class WrPlanFillinDaysVO {

    /**
     * 主键id
     */
    private String id;
    /**
     * 计划任务ID
     */
    private String planTaskId;
    /**
     * 调整计划ID
     */
    private String adjustId;

    /**
     * 用水单位编码
     */
    private String waterUnitId;

    /**
     * 管理单位编码
     */
    private String manageUnitId;
    /**
     * 引水口编码
     */
    private String buildingId;
    /**
     * 计划名称
     */
    private String planName;
    /**
     * 调整类型（0：跨月 1：跨年 2：超年）
     */
    private String adjustType;
    /**
     * 年份
     */
    private String year;
    /**
     * 月份
     */
    private String month;
    /**
     * 月份
     */
    private String timeType;
    /**
     * 旬别(1：上旬，2：中旬，3：下旬，4：全月，5：全年)
     */
    private String tday;
    /**
     * 日
     */
    private String day;
    /**
     * 调整前需求水量(m³)
     */
    private BigDecimal demandWaterQuantuty;
    /**
     * 调整前需求流量(m³/s)
     */
    private BigDecimal demandWaterFlow;
    /**
     * 调整后需求水量(m³)
     */
    private BigDecimal demandWaterQuantityAfter;
    /**
     * 调整后需求流量(m³/s)
     */
    private BigDecimal demandWaterFlowAfter;
    /**
     * 内容
     */
    private String content;
    /**
     * 状态
     */
    private String state;

}
