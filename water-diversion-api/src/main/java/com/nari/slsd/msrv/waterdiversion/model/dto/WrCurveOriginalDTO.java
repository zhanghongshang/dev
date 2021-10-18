package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 原始率定数据表
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrCurveOriginalDTO {

    /**
     * ID
     */
    private String id;

    /**
     * 实测时间
     */
    private Long time;

    /**
     * 曲线ID
     */
    private String curveId;

    /**
     * 实测0维数值
     */
    private String v0;

    /**
     * 实测1维数值
     */
    private String v1;

    /**
     * 实测2维数值
     */
    private String v2;

    /**
     * 实测3维数值
     */
    private String v3;

    /**
     * 曲线数值
     */
    private String curveV;

    /**
     * 曲线数值维数
     */
    private Integer curveDimension;

    /**
     * 误差
     */
    private String curveError;


}
