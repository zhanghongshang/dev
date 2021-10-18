package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description 计划水引数据对比
 * @Author ZHS
 * @Date 2021/9/10 10:50
 */
@Data
public class WrPlanDataCompareVO {
    /**管理站名称
     */
    private String mngUnitName;
    /**用水单位名称（县市师团）
     */
    private String waterUnitName;
    /**引水口名称
     */
    private String buildingName;
    /**本月截止累计（实引）
     */
    private BigDecimal actMtd;
    /**月截止计划
     */
    private BigDecimal planMtd;
    /**滚存月计划
     */
    private BigDecimal rollMonthPlan;
    /**年初月计划
     */
    private BigDecimal originalMonthPlan;
    /**本年截止累计（实引）
     */
    private BigDecimal actYtd;
    /**本年截止累计（计划）
     */
    private BigDecimal planYtd;
    /**年度总计划
     */
    private BigDecimal planYear;
}
