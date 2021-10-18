package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

import java.math.BigDecimal;
/**
 * @Description 计划对比
 * @Author ZHS
 * @Date 2021/9/10 11:16
 */
@Data
public class PlanContrast {
    //本年计划
    private BigDecimal yearPlan;
    //本月滚存计划
    private BigDecimal rollMonthPlan;
    //年初月计划
    private BigDecimal originalMonthPlan;
    //本年截止累计（计划）
    private BigDecimal planYtd;
    //本月截止累计（计划）
    private BigDecimal planMtd;
    //年同期（计划）
    private BigDecimal planStly;
    //月同期（计划）
    private BigDecimal planStlm;
    //本年截止累计（实际）
    private BigDecimal actYtd;
    //本月截止累计（实际）
    private BigDecimal actMtd;
    //年同期（实际）
    private BigDecimal actStly;
    //月同期（实际）
    private BigDecimal actStlm;
}
