package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterPlanFillinYearVO {

    /**
     * 用水单位编码
     */
    private String waterUnitId;

    /**
     * 管理单位编码
     */
    private String manageUnitId;
    /**
     * 引水口编码
     */
    private String buildingId;
    /**
     * 年份
     */
    private String year;

    /**
     * 需求水量(m³)
     */
    private List<BigDecimal> demadWaterQuantity;

    /**
     * 状态
     */
    private String state;

}
