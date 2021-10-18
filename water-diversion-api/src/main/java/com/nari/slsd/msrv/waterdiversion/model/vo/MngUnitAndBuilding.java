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
public class MngUnitAndBuilding {
    /**
     * 管理单位ID
     */
    private String mngUnitId;
    /**
     * 管理单位名称
     */
    private String mngUnitName;

    /**
     * 测站引水口ID
     */
    private String buildingId;

    /**
     * 测站引水口编码
     */
    private String buildingCode;

    /**
     * 测站引水口名称
     */
    private String buildingName;

    /**
     * GIS经纬度
     */
    private String latlngF;

    /**
     * 拓扑经纬度
     */
    private String latlngS;

    /**
     * 预留 数据
     */
    private Object data;

}
