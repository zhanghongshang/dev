package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @title
 * @description 超年填报记录
 * @author bigb
 * @updateTime 2021/9/25 1:00
 * @throws
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleWrSuperYearRecordVO {
    /**
     * 水权交易编码
     */
    private String waterRegimeCode;

    /**
     * 分配水量
     */
    private BigDecimal totalWater;
}
