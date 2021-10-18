package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrBuildingAndDiversion;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrPlanFillInValue;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WaterPlanFillinMonth;
import com.nari.slsd.msrv.waterdiversion.model.vo.BuildingExt;
import com.nari.slsd.msrv.waterdiversion.model.vo.PlanFillinStateExt;

import java.util.List;

/**
 * <p>
 * 年度用水计划填报 服务类
 * </p>
 *
 * @author zhs
 * @since 2021-08-04
 */
public interface IWrPlanFillInMonthService extends IService<WaterPlanFillinMonth> {
    /**
     * 添加月计划水量
     */
    ResultModel addWaterPlanValue(WrPlanFillInValue wrPlanFillInValue);

    ResultModel updateWaterPlanValue(WrPlanFillInValue wrPlanFillInValue);

    /**
     * 根据条件查询审批数据
     */
    //List<BuildingExt> findPlanValueByCodAndTime(String year, String month, String userId, List<Integer> unitLevels, List<String> buildingType, Integer fillReport, List<Integer> levels);

    /**
     * 用水计划填报单位查询
     */
    PlanFillinStateExt findUnitByCodAndTime(String year, List<String> mngUnitId, List<Integer> unitLevels, List<String> buildingType, Integer fillReport, List<Integer> buildingLevels);

    /**
     * 月计划汇总（管理单位）
     */
    List<BuildingExt> findPlanAllValueByCodAndTime(String year,String userId, List<String> mngUnitId, List<Integer> unitLevels, List<String> buildingType, Integer fillReport, List<Integer> levels);

}
