package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 配水方案入参表
 * </p>
 *
 * @author bigb
 * @since 2021-08-10
 */
@Data
@TableName("WR_DISPATCH_CALC_IN")
public class WrDispatchSchemeParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private String id;

    /**
     * 方案id
     */
    @TableField("SCHEME_ID")
    private String schemeId;

    /**
     * 引水口id
     */
    @TableField("BUILDING_ID")
    private String buildingId;

    /**
     * 测点号
     */
    @TableField("SENID")
    private String senID;

    /**
     * 测点值
     */
    @TableField("VALUE")
    private String pointValue;

    /**
     * 时间
     */
    @TableField(value = "TIME",fill = FieldFill.INSERT)
    private Date saveTime;

    /**
     * 值类型
     */
    @TableField("VTYPE")
    private Integer valueType;

    /**
     * 备用
     */
    @TableField("RESERVE")
    private String reserve;

}
