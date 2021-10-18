package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author 86180
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterPlanFillInYearSimpleVO {

    /**
     * 引水口id
     */
    private String buildingId;

    /**
     * 需求水量(万m³)
     */
    private BigDecimal waterQuantity;

    /**
     * 滚存水量(万m³)
     */
    private BigDecimal accuWaterQuantity;

    /**
     * 占比
     */
    private BigDecimal rate;

}
