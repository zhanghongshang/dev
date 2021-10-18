package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Created by ZHD
 * @program: DataSetPointDto
 * @description:过程数据结果-测点DTO
 * @date: 2021/8/21 10:39
 */
@Data
public class DataSetPointDto {
    private String pointType;//测点类型

    private String correlationCode;//关联测点号

    private List<DataVDTO> dataVDTOS;

    private  String valType;//值类型V、AVGV、MAXV等

    private  String runDataType;//数据表来源 实时表 月表 年表等 RUN_RTREAL 、RUN_HOUR
}
