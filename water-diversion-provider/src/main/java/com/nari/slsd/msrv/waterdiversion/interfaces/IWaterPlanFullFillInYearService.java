package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.nari.slsd.msrv.waterdiversion.config.excel.WaterPlanYearModel;

import java.util.List;

/**
 * <p>
 * 年度用水计划填报(全量填报)
 * </p>
 *
 * @author zhs
 * @since 2021-08-04
 */
public interface IWaterPlanFullFillInYearService {

    void planFullFill(List<WaterPlanYearModel> modelList, String personId , String year);
}
