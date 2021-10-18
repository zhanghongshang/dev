package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * @title
 * @description 引水口水量/流量调整信息
 * @author bigb
 * @updateTime 2021/8/26 10:19
 * @throws
 */
@Data
public class BuildingNode {

    private String buildingId;

    private Double water;

}
