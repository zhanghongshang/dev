package com.nari.slsd.msrv.waterdiversion.model.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @title
 * @description 水权交易vo
 * @author bigb
 * @updateTime 2021/9/15 16:18
 * @throws
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WrRightTradeVO implements Serializable {
    /**
     * 主键id
     */
    private String id;

    /**
     * 水权交易编码
     */
    private String uniqueCode;

    /**
     * 买入方（用水单位）
     */
    private String buyer;

    /**
     * 卖出方（用水单位）
     */
    private String saler;

    /**
     * 交易水量
     */
    private Double waterAmount;

    /**
     * 买入方（用水单位名称）
     */
    private String buyerName;

    /**
     * 卖出方（用水单位名称）
     */
    private String salerName;


}
