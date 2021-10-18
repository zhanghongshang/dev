package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description 引水口对应剩余水量 vo类
 * @Author ZHS
 * @Date 2021/9/23 19:36
 */
@Data
public class ResidualWaterByBuildingIdVO {

    private String buildingId;

    private String buildingName;

    private BigDecimal ResidualWater;

}
