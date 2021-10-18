package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.waterdiversion.model.dto.PlanFillinExamineDTO;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrPlanFillInValue;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WaterPlanFillinYear;
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
public interface IWrPlanFillInYearService extends IService<WaterPlanFillinYear>{
    /**
     * 添加年计划水量
     */
    ResultModel addWaterPlanValue(WrPlanFillInValue wrPlanFileYearValue);

    ResultModel updateWaterPlanValue(WrPlanFillInValue wrPlanFillInValue);
    /**
     * 用水计划填报单位查询
     */
    PlanFillinStateExt findUnitByCodAndTime(String time,List<String> mngUnitId,List<Integer> unitLevels, List<String> buildingType, Integer fillReport, List<Integer> buildingLevels);

    /**
     * 年计划汇总（管理单位）
     */
    List<BuildingExt> findPlanAllValueByCodAndTime(String year, String userId, List<String> mngUnitId, List<Integer> unitLevels, List<String> buildingType, Integer fillReport, List<Integer> levels, String type);

    /**
     * 获取一级引水口数据
     */
    List<BuildingExt>  findPlanAllBybuildId(String year, List<String> mngUnitId, List<BuildingExt> buildingExts);
}