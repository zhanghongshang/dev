package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.nari.slsd.msrv.waterdiversion.model.dto.WrBuildingAndDiversion;

import java.util.List;

/**
 * 月计划生成服务类
 *
 * @author reset kalar
 * @since 2021-08-08
 */
public interface IWrPlanGenerateMonthService {

    void autoGenerateMonthPlan();

    List<WrBuildingAndDiversion> getAllWaterBuildingForAppointUseUnit(String useUnitId);
}
