package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @title
 * @description 水量使用情况
 * @author bigb
 * @updateTime 2021/9/26 17:46
 * @throws
 */
@Data
public class WrUseDetailVO {
    /**
     * 时间
     */
    private Date everyDay;
    /**
     * 计划水量
     */
    private BigDecimal plan;
    /**
     * 调整后计划水量
     */
    private BigDecimal planAfter;
    /**
     * 实际使用水量
     */
    private BigDecimal realUse;
    /**
     * 结余水量
     */
    private BigDecimal difference;
}
