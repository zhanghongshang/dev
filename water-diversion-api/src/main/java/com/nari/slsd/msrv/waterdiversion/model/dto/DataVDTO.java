package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

/**
 * @author Created by ZHD
 * @program: DataVDTO
 * @description:
 * @date: 2021/8/21 10:31
 */
@Data
public class DataVDTO {

    private Long time;//数据时间

    private Double v;//数据值，可能是V、AVGV、MAXV等
}
