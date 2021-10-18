package com.nari.slsd.msrv.waterdiversion.model.secondary.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 曲线变量
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-08
 */
@Data
//@EqualsAndHashCode(callSuper = false)
@TableName("SL_WATER_CURVE_VARIATE")
public class WrCurveVariate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 曲线id
     */
    @TableField("CURVE_ID")
    private String curveId;

    /**
     * 维度标识
     */
    @TableField("DIM_KEY")
    private Integer dimKey;

    /**
     * 测点id
     */
    @TableField("SENID")
    private String senId;

    /**
     * 数值类型
     */
    @TableField("APP_TYPE")
    private String appType;

    /**
     * XPARAM
     */
    @TableField("XPARAM")
    private String xparam;

}
