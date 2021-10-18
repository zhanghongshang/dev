package com.nari.slsd.msrv.waterdiversion.model.vo;


import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * gis实时调度
 * </p>
 *
 * @author bigb
 * @since 2021-08-11
 */
@Data
public class GisRealtimeDispatchVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 管理单位ID
     */
    private String mngUnitId;
    /**
     * 管理单位名称
     */
    private String mngUnitName;
    /**
     * 引水口id
     */
    private String buildingId;
    /**
     * 测站引水口名称
     */
    private String buildingName;
    /**
     * 实测水量
     */
    private String realTimeWaterQuantity;
    /**
     * 实测流量
     */
    private String realTimeWaterFlow;
    /**
     * 当日水量
     */
    private String waterQuantity;
    /**
     * 当日流量
     */
    private String waterFlow;
    /**
     * 调整水量
     */
    private String resizeWaterQuantity;
    /**
     * 调整流量
     */
    private String resizeWaterFlow;
    /**
     * 经纬度
     */
    private String latlng_f;
    /**
     * 拓扑经纬度
     */
    private String latlng_s;
    /**
     * 是否差异
     */
    private int diff;

}
