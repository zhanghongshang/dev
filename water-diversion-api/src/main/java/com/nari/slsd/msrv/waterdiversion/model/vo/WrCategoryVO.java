package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @title
 * @description 用水性质vo
 * @author bigb
 * @updateTime 2021/8/23 10:10
 * @throws
 */
@Data
public class WrCategoryVO {

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
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 用户id
     */
    private String personId;

    /**
     * 用户名称
     */
    private String personName;

    /**
     * 费率集
     */
    private List<WrFeeRateVO> rateVOList = new ArrayList<>();
}
