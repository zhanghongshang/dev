package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

/**
 * @author Created by bigb
 * @program: ModelRequestInnerDto
 * @description:模型请求DTO
 * @date: 2021/8/21 15:06
 */
@Data
public class ModelRequestInnerDto {

    /**
     * 引水口id
     */
    private String buildingId;
    /**
     * 实测水量
     */
    private String realTimeWaterQuantity;
    /**
     * 实测流量
     */
    private String realTimeWaterFlow;
    /**
     * 调整水量
     */
    private String waterQuantity;
    /**
     * 调整流量
     */
    private String waterFlow;
}
