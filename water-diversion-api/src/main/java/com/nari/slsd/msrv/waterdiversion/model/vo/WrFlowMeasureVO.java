package com.nari.slsd.msrv.waterdiversion.model.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 人工率定数据录入
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrFlowMeasureVO {
    /**
     * ID
     */
    private String id;

    /**
     * 测站ID
     */
    private String stationId;

    /**
     * 测站ID
     */
    private String stationCode;

    /**
     * 测站名称
     */
    private String stationName;

    /**
     * 实测时间
     */
    private Long time;

    /**
     * 水位
     */
    private Double waterLevel;

    /**
     * 流量
     */
    private Double flow;

    /**
     * 录入时间
     */
    private Long updateTime;

    /**
     * 操作人员id
     */
    private String personId;

    /**
     * 操作人员id
     */
    private String personName;

    /**
     * 备注
     */
    private String remark;

}