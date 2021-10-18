package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

/**
 * @author Created by ZHD
 * @program: WaterPoiniTypeDTO
 * @description:测站下测点对象信息
 * @date: 2021/8/16 15:33
 */
@Data
public class WaterPointDTO {
    private String buildingId;//测站、引水口id
    private String pointName;//测点名称
    private String correlationCode;//关联测点号
    private String correlationSource;//关联应用来源
    private String pointType;//测点类型
}
