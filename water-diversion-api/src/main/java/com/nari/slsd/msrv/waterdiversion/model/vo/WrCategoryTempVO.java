package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * @title
 * @description 用水性质vo
 * @author bigb
 * @updateTime 2021/8/23 10:10
 * @throws
 */
@Data
public class WrCategoryTempVO {

    /**
     * ID
     */
    private String id;
    /**
     * 用水性质唯一编码
     */
    private String categoryCode;

    /**
     * 用水性质名称
     */
    private String categoryName;

    /**
     * 用水类型编码
     */
    private String waterTypeCode;

    /**
     * 用水类型名称
     */
    private String waterTypeName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户名称
     */
    private String personName;

    /**
     * fee ID
     */
    private String feeId;

    /**
     * 超水比率
     */
    private String surpassRate;

    /**
     * 费率
     */
    private String feeRate;


}
