package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Created by ZHD
 * @program: DataBuildingDto
 * @description:数据结果-测站DTO
 * @date: 2021/8/18 15:06
 */
@Data
public class DataBuildingDto {

    private String id;//水工建筑物编号（测站、引水口id）

    private String buildingName;//水工建筑物名称 （测站、引水口名称）

    private List<DataPointDto> dataPointDtos;//测点信息以及数据
}
