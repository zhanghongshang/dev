package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("WR_PLAN_FILLIN_D")
public class WrPlanFillinDay implements Serializable{
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private String id;
    /**
     * 计划任务ID
     */
    @TableField("PLAN_TASK_ID")
    private String planTaskId;
    /**
     * 调整计划ID
     */
    @TableField("ADJUST_ID")
    private String adjustId;

    /**
     * 用水单位编码
     */
    @TableField("WATER_UNIT_ID")
    private String waterUnitId;

    /**
     * 用水单位名称
     */
    @TableField("WATER_UNIT_NAME")
    private String waterUnitName;
    /**
     * 管理单位编码
     */
    @TableField("MANAGE_UNIT_ID")
    private String manageUnitId;
    /**
     * 引水口编码
     */
    @TableField("BUILDING_ID")
    private String buildingId;
    /**
     * 计划名称
     */
    @TableField("PLAN_NAME")
    private String planName;
    /**
     * 调整类型
     */
    @TableField("ADJUST_TYPE")
    private String adjustType;
    /**
     * 年份
     */
    @TableField("YEAR")
    private String year;
    /**
     * 月份
     */
    @TableField("MONTH")
    private String month;
    /**
     * 时间类别
     */
    @TableField("TIME_TYPE")
    private String timeType;
    /**
     * 旬别(1：上旬，2：中旬，3：下旬，4：全月，5：全年)
     */
    @TableField("TDAY")
    private String tday;
    /**
     * 日
     */
    @TableField("DAY")
    private String day;
    /**
     * 调整前需求水量(m³)
     */
    @TableField("DEMAND_WATER_QUANTITY")
    private BigDecimal demandWaterQuantuty;
    /**
     * 调整前需求流量(m³/s)
     */
    @TableField("DEMAND_WATER_FLOW")
    private BigDecimal demandWaterFlow;
    /**
     * 调整后需求水量(m³)
     */
    @TableField("DEMAND_WATER_QUANTITY_AFTER")
    private BigDecimal demandWaterQuantityAfter;
    /**
     * 调整后需求流量(m³/s)
     */
    @TableField("DEMAND_WATER_FLOW_AFTER")
    private BigDecimal demandWaterFlowAfter;
    /**
     * 内容
     */
    @TableField("CONTENT")
    private String content;
    /**
     * 状态
     */
    @TableField("STATE")
    private String state;
    /**
     * 实际引水量
     */
    @TableField("REAL_WATER_QUANTITY")
    private BigDecimal realWaterQuantity;
    /**
     * 状态（0：借调,1：借出）
     */
    @TableField("LEND_TYPE")
    private String lendType;

}
