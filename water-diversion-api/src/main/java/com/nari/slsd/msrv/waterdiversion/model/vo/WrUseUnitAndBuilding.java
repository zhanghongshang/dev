package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Program: water-diversion
 * @Description: 最后一层用水单位-测站引水口
 * @Author: reset kalar
 * @Date: 2021-08-23 11:36
 **/
@Data
public class WrUseUnitAndBuilding {

    private String waterUnitId;

    private String waterUnitName;

    private List<WrBuildingSimpleVO> buildings;

    public WrUseUnitAndBuilding() {
        buildings = new ArrayList<>();
    }

}
