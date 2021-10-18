package com.nari.slsd.msrv.waterdiversion.model.secondary.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>
 * 曲线点值定义
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-08
 */
@Data
@TableName("SL_WATER_CURVE_POINT_VALUE")
public class WrCurvePointValue implements Serializable {

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
     * 0维数值
     */
    @TableField("V0")
    private Double v0;

    /**
     * 1维数值
     */
    @TableField("V1")
    private Double v1;

    /**
     * 2维数值
     */
    @TableField("V2")
    private Double v2;

    /**
     * 3维数值
     */
    @TableField("V3")
    private Double v3;

}
