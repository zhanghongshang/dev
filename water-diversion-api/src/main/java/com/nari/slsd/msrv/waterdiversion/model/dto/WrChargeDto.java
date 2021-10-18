package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

/**
 * @title
 * @description 水费收费dto
 * @author bigb
 * @updateTime 2021/8/22 10:10
 * @throws
 */
@Data
public class WrChargeDto {
    /**
     * ID
     */
    private String id;

    /**
     * 缴费记录编号
     * 2021082200000001
     */
    private String recordCode;

    /**
     *用水单位id
     */
    private String waterUnitId;

    /**
     * 年份
     */
    private String year;

    /**
     * 缴费类型
     */
    private String feeType;

    /**
     * 实收金额
     */
    private Double realAmount;


    /**
     * 收费人
     */
    private String personId;

    /**
     * 备注
     */
    private String remark;

}
