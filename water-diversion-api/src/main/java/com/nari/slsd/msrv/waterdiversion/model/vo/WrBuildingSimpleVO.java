package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Program: water-diversion
 * @Description:
 * @Author: reset kalar
 * @Date: 2021-08-13 09:34
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrBuildingSimpleVO {

    /**
     * 测站ID
     */
    private String buildingId;

    /**
     * 测站引水口编码
     */
    private String buildingCode;

    /**
     * 测站名称
     */
    private String buildingName;
}
