package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Program: water-diversion
 * @Description: 测站-引水口管理类
 * @Author: reset kalar
 * @Date: 2021-08-04 09:24
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleWrBuildingAndDiversion {

    /**
     * 水工建筑物ID
     */
    private String id;

    /**
     * 测站编码
     */
    private String buildingCode;

    /**
     * 水工建筑物名称
     */
    private String buildingName;

    /**
     * 年剩余水量（月结余+年剩余指标）
     */
    private BigDecimal remainOfYear;
}
