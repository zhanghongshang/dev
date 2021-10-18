package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WaterPlanFillinYearAndPId {
    /**
     * 主键id
     */
    private String id;

    /**
     * 引水口编码
     */
    private String buildingId;
    /**
     * 年份
     */
    private String year;
    /**
     * 月份
     */
    private String month;
    /**
     * 旬别(1：上旬，2：中旬，3：下旬，4：全月)
     */
    private String tday;
    /**
     * 需求水量(m³)
     */
    private BigDecimal demadWaterQuantity;
    /**
     * 上级引水口id
     */
    private String pid;
    /**
     *  上级引水口编码
     */
    private String pidCode;

}
