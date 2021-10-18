package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 用水计划任务
 * </p>
 *
 * @author zhs
 * @since 2021-08-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrPlanTaskVO{
    /**
     * ID
     */
    private String id;

    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 计划类型
     */
    private String planType;
    /**
     * 计划开始日期
     */
    private Long startDate;
    /**
     * 计划结束日期
     */
    private Long endDate;
    /**
     * 内容
     */
    private String waterFlow;
    /**
     * 操作人
     */
    private String personId;
    /**
     * 操作人名称
     */
    private String personName;
    /**
     * 发起时间
     */
    private Long createDate;
    /**
     * 流程实例ID
     */
    private String waterPlanFillIn;
    /**
     * 状态
     */
    private String state;
    /**
     * 子类型
     */
    private String subType;
    /**
     * 流程变量
     */
    private String batchState;
    /**
     * 总水量(m³)
     */
    private BigDecimal totalWaterQuantity;

    /**
     * 节点id
     */
    private String taskDefinitionKey;

}
