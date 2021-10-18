package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
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
@TableName("WR_PLAN_FILLIN_Y")
public class WaterPlanFillinYear extends Model<WaterPlanFillinYear> {

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
     * 用水单位编码
     */
    @TableField("WATER_UNIT_ID")
    private String waterUnitId;

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
     * 旬别(1：上旬，2：中旬，3：下旬，4：全月，5：全年)
     */
    @TableField("TDAY")
    private String tday;
    /**
     * 建议水量(m³)
     */
    @TableField("PROPOSAL_WATER_QUANTITY")
    private BigDecimal propalWaterQuantity;
    /**
     * 需求水量(m³)
     */
    @TableField("DEMAND_WATER_QUANTITY")
    private BigDecimal demadWaterQuantity;
    /**
     * 建议流量(m³/s)
     */
    @TableField("PROPOSAL_WATER_FLOW")
    private BigDecimal proposalWaterFlow;
    /**
     * 需求流量(m³/s)
     */
    @TableField("DEMAND_WATER_FLOW")
    private BigDecimal demadWaterFlow;
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

    // 该属性不为数据库表字段，但又是必须使用的
    @TableField(exist = false)
    private WrPlanInterTday newUserItems;


}
