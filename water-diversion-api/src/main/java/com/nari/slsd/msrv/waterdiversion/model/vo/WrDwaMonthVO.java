package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description 指标查询vo类
 * @Author ZHS
 * @Date 2021/9/20 20:51
 */
@Data
public class WrDwaMonthVO {

    private String Id;//ID

    private String buildingId;//引水口id

    private String buildingName;//引水口名称

    private String year;//年份

    private String month;//月份

    //时间
    //private String time;

    private String proportion;//占比

    private BigDecimal targer;//指标（水量）
}
