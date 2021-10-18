package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Program: water-diversion
 * @Description: 测站一个月的流量数据
 * @Author: reset kalar
 * @Date: 2021-08-26 10:21
 **/
@Data
public class WrFlowDayInmonthRow {

    /**
     * 测站编码
     */
    private String stationId;

    /**
     * 月平均流量(m³/s)
     */
    private Double avgFlow;

    /**
     * 月水量(万方)
     */
    private Double monthlyFlow;

    /**
     * 校核时间
     */
    private Long approveTime;

    /**
     * 校核人
     */
    private String approveId;

    /**
     * 校核人
     */
    private String approveName;

    /**
     * 状态 1审核未完成 2未校核 3已校核
     */
    private Integer status;

    /**
     * 流量集合(m³/s)
     */
    private List<WrDayInmonthInputVO> flow;

    public WrFlowDayInmonthRow() {
        this.flow = new ArrayList<>();
    }

}
