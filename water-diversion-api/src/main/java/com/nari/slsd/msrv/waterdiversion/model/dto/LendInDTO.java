package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
/**
 * @Description 近期调整借调方
 * @Author ZHS
 * @Date 2021/9/24 11:33
 */
@Data
public class LendInDTO {

    //开始时间
    private String startTime;

    //结束时间
    private String endTime;

    //引水口id
    private String buildingId;

    //引水口名称
    private String buildingName;

    //所属管理单位
    private  String mngUnitId;

    //所属用水单位
    private  String waterUnitId;

    //所属管理单位
    private  String waterUnitName;

    //年计划分配水量
    private  Double distributionWater;

    //调整前
    private List<BigDecimal> newPlanValue;

    //调整后
    private List<BigDecimal> oldPlanValue;

}
