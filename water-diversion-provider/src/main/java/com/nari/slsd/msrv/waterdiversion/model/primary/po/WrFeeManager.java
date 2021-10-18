package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 水费管理表
 * </p>
 *
 * @author bigb
 * @since 2021-08-23
 */
@Data
@TableName("WR_FEE_MANAGER")
public class WrFeeManager implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private String id;

    /**
     * 用水单位ID
     */
    @TableField(value = "WATER_UNIT_ID")
    private String waterUnitId;

    /**
     * 年份
     */
    @TableField(value = "YEAR")
    private String year;

    /**
     * 年计划水量
     */
    @TableField(value = "WR_PLAN_YEAR")
    private Double waterPlanYear;

    /**
     * 实引水量
     */
    @TableField(value = "WATER_CONSUME")
    private Double waterConsume;

    /**
     * 应预交水费
     */
    @TableField(value = "PRE_PAY")
    private Double prePay;

    /**
     * 实收预交水费
     */
    @TableField(value = "PRE_PAY_REAL")
    private Double prePayReal;

    /**
     * 应收水费
     */
    @TableField(value = "FEE_AMOUNT")
    private Double feeAmount;

    /**
     * 实收水费
     */
    @TableField(value = "FEE_AMOUNT_REAL")
    private Double feeAmountReal;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_DATE",fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 备注
     */
    @TableField(value = "REMARK")
    private String remark;

}
