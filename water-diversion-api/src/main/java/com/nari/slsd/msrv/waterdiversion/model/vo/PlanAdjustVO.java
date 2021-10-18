package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @title
 * @description 计划调整
 * @author bigb
 * @updateTime 2021/9/18 11:49
 * @throws
 */
@Data
public class PlanAdjustVO {
    /**借调方
     */
    private List<PlanAdjustDetailVO> lendInList;
    /**借出方
     */
    private List<PlanAdjustDetailVO> lendOutList;
}
