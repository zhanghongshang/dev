package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 收费信息
 * </p>
 * @author bigb
 * @since 2021-08-21
 */
@Data
@TableName("WR_CHARGE_MANAGE")
public class WrCharge implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private String id;

    /**
     * 缴费记录编号
     * 2021082200000001
     */
    @TableField(value = "RECORD_CODE")
    private String recordCode;

    /**
     *用水单位id
     */
    @TableField("WATER_UNIT_ID")
    private String waterUnitId;

    /**
     * 年份
     */
    @TableField("YEAR")
    private String year;

    /**
     * 缴费类型
     */
    @TableField("FEE_TYPE")
    private String feeType;

    /**
     * 实收金额
     */
    @TableField("REAL_AMOUNT")
    private Double realAmount;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME",fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 收费人
     */
    @TableField("PERSON_ID")
    private String personId;

    /**
     * 备注
     */
    @TableField(value = "REMARK")
    private String remark;
}
