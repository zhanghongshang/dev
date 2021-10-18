package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * 年度用水计划填报
 * </p>
 *
 * @author zhs
 * @since 2021-08-04
 */
@Data
public class WaterPlanFillinYearDTO {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private String id;
    /**
     * 计划任务ID
     */
    private String planTaskId;

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
     * 年份
     */
    private String year;
    /**
     * 月份
     */
    private String month;
    /**
     * 旬别(1：上旬，2：中旬，3：下旬，4：全月，5：全年)
     */
    private String tday;
    /**
     * 建议水量(m³)
     */
    private BigDecimal propalWaterQuantity;
    /**
     * 需求水量(m³)
     */
    private BigDecimal demadWaterQuantity;
    /**
     * 建议流量(m³/s)
     */
    private BigDecimal proposalWaterFlow;
    /**
     * 需求流量(m³/s)
     */
    private BigDecimal demadWaterFlow;
    /**
     * 内容
     */
    private String content;
    /**
     * 状态
     */
    private String state;

}
