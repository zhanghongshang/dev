package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

/**
 * @title
 * @description 用水性质dto
 * @author bigb
 * @updateTime 2021/8/23 10:10
 * @throws
 */
@Data
public class WrCategoryDto {

    /**
     * 用水性质名称
     */
    private String categoryName;

    /**
     * 用水性质唯一编码
     */
    private String categoryCode;

    /**
     * 用水类型编码
     */
    private String waterTypeCode;

    /**
     * 用水类型名称
     */
    private String waterTypeName;

    /**
     * 用户id
     */
    private String personId;

    /**
     * 用户名称
     */
    private String personName;

    /**
     * 开始
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;
}
