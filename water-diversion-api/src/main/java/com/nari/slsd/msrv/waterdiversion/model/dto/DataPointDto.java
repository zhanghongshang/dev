package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

/**
 * @author Created by ZHD
 * @program: DataPointDto
 * @description:数据结果-测点DTO
 * @date: 2021/8/18 15:07
 */
@Data
public class DataPointDto {

    private String pointType;//测点类型

    private String correlationCode;//关联测点号

    private Long time;//数据时间

    private Double v;//数据值，可能是V、AVGV、MAXV等

    private  String valType;//值类型V、AVGV、MAXV等

    private  String runDataType;//数据表来源 实时表 月表 年表等 RUN_RTREAL 、RUN_HOUR
}
