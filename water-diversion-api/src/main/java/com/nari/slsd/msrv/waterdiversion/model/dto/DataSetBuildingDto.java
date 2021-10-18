package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Created by ZHD
 * @program: DataSetBuildingDto
 * @description:过程数据结果-测站DTO
 * @date: 2021/8/21 10:42
 */
@Data
public class DataSetBuildingDto {

    private String id;//水工建筑物编号（测站、引水口id）

    private String buildingName;//水工建筑物名称 （测站、引水口名称）

    private List<DataSetPointDto> dataPointDtos;//测点信息以及数据
}
