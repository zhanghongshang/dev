package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 逐日水情输入表 DTO
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrDayInmonthInputDTO {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 引水口ID
     */
    private String stationId;

    /**
     * 时间类型 1:年，2:月,3:旬，4:日
     */
    private Integer timeType;

    /**
     * 数据时间
     */
    private Long time;

    /**
     * 水位
     */
    private Double waterLevel;

    /**
     * 流量(m³/s)
     */
    private Double waterFlow;

    /**
     * 水量
     */
    private Double waterQuantity;

    /**
     * 人工0/自动1
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
     * 审批时间
     */
    private Long approveTime;

    /**
     * 审批人
     */
    private String approveId;

    /**
     * 状态 2已审核未校核 3已校核
     */
    private Integer status;

}


