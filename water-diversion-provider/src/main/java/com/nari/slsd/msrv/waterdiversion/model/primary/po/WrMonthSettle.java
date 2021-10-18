package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 月度结算表
 * </p>
 *
 * @author bigb
 * @since 2021-08-23
 */
@Data
@TableName("WR_MONTH_SETTLE")
public class WrMonthSettle implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private String id;

    /**
     * 引水口id
     */
    @TableField(value = "BUILDING_ID")
    private String buildingId;

    /**
     * 年份
     */
    @TableField(value = "YEAR")
    private String year;

    /**
     * 月份
     */
    @TableField(value = "MONTH")
    private String month;

    /**
     * 月均流量
     */
    @TableField(value = "AVG_FLOW")
    private Double avgFlow;

    /**
     * 月累计引水量（含公摊）
     */
    @TableField(value = "WR_TOTAL_SHARE")
    private Double wrTotalShare;

    /**
     * 月累计引水量（不含公摊）
     */
    @TableField(value = "WR_TOTAL")
    private Double wrTotal;

    /**
     * 审核月累计引水量（含公摊）
     */
    @TableField(value = "WR_TOTAL_SHARE_AD")
    private Double wrTotalShareAdjust;

    /**
     * 审核月累计引水量（不含公摊）
     */
    @TableField(value = "WR_TOTAL_AD")
    private Double wrTotalAdjust;

    /**
     * 平均水位
     */
    @TableField(value = "AVG_WATER_LEVEL")
    private Double avgWaterLevel;

    /**
     * 取退口水量（断面）
     */
    @TableField("CROSS_SECTION")
    private Double crossSection;

    /**
     * 上报状态
     */
    @TableField("REPORT_STATUS")
    private String reportStatus;

    /**
     * 上报人
     */
    @TableField("PERSON_ID")
    private String personId;

    /**
     * 创建时间
     */
    @TableField(value = "REPORT_TIME",fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 用户名称
     */
    @TableField("REMARK")
    private String remark;

}
