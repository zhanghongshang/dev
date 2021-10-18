package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Program: water-diversion
 * @Description: 管理单位和测站-引水口返回体
 * @Author: reset kalar
 * @Date: 2021-08-10 16:17
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MngUnitAndBuildings extends MngUnitAndBuilding {

    /**
     * 一级引水口ID
     */
    private String pBuildingId;

    /**
     * 一级引水口编码
     */
    private String pBuildingCode;

    /**
     * 一级引水口名称
     */
    private String pBuildingName;

    /**
     * 引水口层级
     */
    private Integer buildingLevel;

}
