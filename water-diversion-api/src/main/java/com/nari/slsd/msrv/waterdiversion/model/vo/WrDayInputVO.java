package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Program: water-diversion
 * @Description:
 * @Author: reset kalar
 * @Date: 2021-08-19 16:33
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrDayInputVO {
    /**
     * 主键id
     */
    private String id;

    /**
     * 引水口编码
     */
    private String stationId;

    /**
     * 时间类型
     */
    private Integer timeType;

    /**
     * 数据时间
     */
    private Long time;

    /**
     * 自动水位
     */
    private Double waterLevel;

    /**
     * 自动流量(m³/s)
     */
    private Double waterFlow;

    /**
     * 上级引水口水位
     */
    private Double pWaterLevel;

    /**
     * 上级引水口流量
     */
    private Double pWaterFlow;

    /**
     * 手动水位
     */
    private Double manualWaterLevel;

    /**
     * 手动流量
     */
    private Double manualWaterFlow;

    /**
     * 水量
     */
    private Double waterQuantity;

    /**
     * 公摊系数
     */
    private Double shareFactor;

    /**
     * 人工/自动
     */
    private Integer auto;

    /**
     * 输入时间
     */
    private Long operateTime;

    /**
     * 输入人
     */
    private String operatorId;


    /**
     * 输入人名称
     */
    private String operatorName;

    /**
     * 输入备注
     */
    private String operatorRemark;

    /**
     * 审批时间
     */
    private Long approveTime;

    /**
     * 审批人
     */
    private String approveId;

    /**
     * 审批人名称
     */
    private String approveName;

    /**
     * 审批备注
     */
    private String approveRemark;

    /**
     * 状态
     */
    private Integer status;
}
