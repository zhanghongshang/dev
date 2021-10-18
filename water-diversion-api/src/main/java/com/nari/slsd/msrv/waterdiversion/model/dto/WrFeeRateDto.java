package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

/**
 * @title
 * @description 费率dto
 * @author bigb
 * @updateTime 2021/8/22 10:10
 * @throws
 */
@Data
public class WrFeeRateDto {
    /**
     * ID
     */
    private String id;

    /**
     * 用水性质ID
     */
    private String categoryId;

    /**
     * 超水比率
     */
    private Double surpassRate;

    /**
     * 费率
     */
    private Double feeRate;

    /**
     * 操作人id
     */
    private String personId;

    /**
     * 操作人姓名
     */
    private String personName;

}
