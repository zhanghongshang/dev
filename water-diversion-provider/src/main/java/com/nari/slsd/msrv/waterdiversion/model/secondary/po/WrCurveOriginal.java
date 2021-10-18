package com.nari.slsd.msrv.waterdiversion.model.secondary.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 原始率定数据表
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-16
 */
@Data
//@EqualsAndHashCode(callSuper = false)
@TableName("SL_WATER_CURVE_ORIGIAL")
public class WrCurveOriginal implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 时间
     */
    @TableField("TIME")
    private Date time;

    /**
     * 曲线ID
     */
    @TableField("CURVE_ID")
    private String curveId;

    /**
     * 实测0维数值
     */
    @TableField("V0")
    private String v0;

    /**
     * 实测1维数值
     */
    @TableField("V1")
    private String v1;

    /**
     * 实测2维数值
     */
    @TableField("V2")
    private String v2;

    /**
     * 实测3维数值
     */
    @TableField("V3")
    private String v3;

    /**
     * 曲线数值
     */
    @TableField("CURVE_V")
    private String curveV;

    /**
     * 曲线数值维数
     */
    @TableField("CURVE_DIMENSION")
    private Integer curveDimension;

    /**
     * 误差
     */
    @TableField("CURVE_ERROR")
    private String curveError;


}
