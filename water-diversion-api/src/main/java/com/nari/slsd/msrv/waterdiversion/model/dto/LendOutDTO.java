package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description 近期调整借出方（月内借出、超年借出）
 * @Author ZHS
 * @Date 2021/9/23 21:50
 */
@Data
public class LendOutDTO {

    //调整时间
    private String year;

    //调整月份
    private String month;

    //引水口id
    private String buildingId;

    //引水口名称
    private String buildingName;

    //所属管理单位
    private  String mngUnitId;

    //剩余水量
    private BigDecimal surplusWater;

    //借出水量
    private BigDecimal lendOutWater;


}
