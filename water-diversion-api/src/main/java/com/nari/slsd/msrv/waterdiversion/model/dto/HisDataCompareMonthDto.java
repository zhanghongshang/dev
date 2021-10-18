package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Created by ZHD
 * @program: HisDataCompareMonth
 * @description:历史同期比对月数据返回
 * @date: 2021/8/26 16:28
 */
@Data
public class HisDataCompareMonthDto {

    private String pointType;//测点类型

    private String correlationCode;//关联测点号

    private List data;
}
