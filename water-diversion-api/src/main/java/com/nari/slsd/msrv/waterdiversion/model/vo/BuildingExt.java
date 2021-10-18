package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Program: water-diversion
 * @Description:
 * @Author: reset kalar
 * @Date: 2021-08-10 13:52
 **/
@Data
public class BuildingExt {

    /**
     * 测站-引水口ID
     */
    private String buildingId;

    /**
     * 测站引水口编码
     */
    private String buildingCode;

    /**
     * 测站-引水口名称
     */
    private String buildingName;

    /**
     * 用水单位
     */
    private List<WrUseUnitSimpleVO> waterUnits;

    /**
     * 管理单位
     */
    private String mngUnitId;
    /**
     * 管理单位
     */
    private String mngUnitName;

    /**
     * 预留存数据
     */
    private Object data;

    /**
     * 填报状态
     */
    private String state;

    public BuildingExt() {
        /**
         * 初始化用水单位list
         */
        waterUnits = new ArrayList<>();
    }
}
