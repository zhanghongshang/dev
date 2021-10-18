package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @title
 * @description 水量借出详情
 * @author bigb
 * @updateTime 2021/9/23 10:39
 * @throws
 */
@Data
public class LendOutDetailVO {
    /**
     * 时间
     */
    private Long day;
    /**
     * 时间
     */
    private Date dayDate;
    /**
     * 迭代原计划
     */
    private BigDecimal plan;
    /**
     * 调整后
     */
    private BigDecimal after;
    /**
     * 实引水量
     */
    private BigDecimal real;
    /**
     * 借出部分
     */
    private BigDecimal lend;

}
