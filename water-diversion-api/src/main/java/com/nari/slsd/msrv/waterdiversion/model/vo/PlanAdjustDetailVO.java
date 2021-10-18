package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @title
 * @description 计划调整
 * @author bigb
 * @updateTime 2021/9/18 11:49
 * @throws
 */
@Data
public class PlanAdjustDetailVO {
    /**管理站名称
     */
    private String mngUnitName;
    /**用水单位名称（县市师团）
     */
    private String waterUnitName;
    /**引水口名称
     */
    private String buildingName;
    /**时间
     */
    private String timeStr;
    /**原计划
     */
    private BigDecimal plan;
    /**调整后
     */
    private BigDecimal resize;
    /**差值
     */
    private BigDecimal diff;
    /**借调来源 0-结余 1-月指标 2-年指标
     */
    private String lendOutSource;
}
