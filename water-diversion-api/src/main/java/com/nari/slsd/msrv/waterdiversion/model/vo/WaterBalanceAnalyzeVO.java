package com.nari.slsd.msrv.waterdiversion.model.vo;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @title
 * @description 水量平衡结果展示
 * @author bigb
 * @updateTime 2021/9/18 11:25
 * @throws
 */
@Data
public class WaterBalanceAnalyzeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *数据比对
     */
    private List<WrPlanDataCompareVO> dataCompareAnalyze;

    /**
     * 计划调整
     */
    private PlanAdjustVO planAdjustVO;

    private Boolean balanced;

}
