package com.nari.slsd.msrv.waterdiversion.model.dto;

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
public class WrDayInputDTO {
    /**
     * 主键id
     */
    private String id;

    /**
     * 引水口主键
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
     * 人工(0)/自动(1)
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
     * 审批备注
     */
    private String approveRemark;

    /**
     * 状态
     */
    private Integer status;
}
