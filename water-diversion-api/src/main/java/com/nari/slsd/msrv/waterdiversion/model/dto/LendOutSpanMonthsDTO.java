package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description 跨月借出
 * @Author ZHS
 * @Date 2021/9/24 11:32
 */
@Data
public class LendOutSpanMonthsDTO {


    //测试id
    private String buildingId;

    //所属管理单位
    private  String mngUnitId;
    //所属管理单位
    private  String waterUnitId;
    //所属管理单位
    private  String waterUnitName;

    //调整后
    private List<BigDecimal> newPlanValue;

    //调整前
    private List<BigDecimal> oldPlanValue;
}
