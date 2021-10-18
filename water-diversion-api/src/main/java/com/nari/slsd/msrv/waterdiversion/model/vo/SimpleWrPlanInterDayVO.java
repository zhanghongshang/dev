package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @title
 * @description 截至到当前时间日迭代计划
 * @author bigb
 * @updateTime 2021/9/25 1:00
 * @throws
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleWrPlanInterDayVO {
    /**
     * 引水口编码
     */
    private String buildingId;

    /**
     * 水量
     */
    private Double waterQuantity;
}
