package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @title
 * @description 水量借出详情
 * @author bigb
 * @updateTime 2021/9/23 10:39
 * @throws
 */
@Data
public class LendOutVO {
    /**
     * 引水口id
     */
    private String buildingId;
    /**
     * 结余水量
     */
    private BigDecimal remainWater;
    /**
     * 借出水量
     */
    private BigDecimal lendOutWater;
    /**
     * 借出详情
     */
    private List<LendOutDetailVO> detailVOList;
}
