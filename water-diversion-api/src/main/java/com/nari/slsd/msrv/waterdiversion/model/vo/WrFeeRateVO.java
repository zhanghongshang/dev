package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

/**
 * @title
 * @description 费率vo
 * @author bigb
 * @updateTime 2021/8/22 10:10
 * @throws
 */
@Data
public class WrFeeRateVO {

    /**
     * ID
     */
    private String id;

    /**
     * 超水比率
     */
    private Double surpassRate;

    /**
     * 费率
     */
    private Double feeRate;

}
