//package com.nari.slsd.msrv.waterdiversion.interfaces;
//
//import com.baomidou.mybatisplus.extension.service.IService;
//import com.nari.slsd.msrv.waterdiversion.model.dto.PlanFillinExamineDTO;
//import com.nari.slsd.msrv.waterdiversion.model.dto.WrPlanFillInValue;
//import com.nari.slsd.msrv.waterdiversion.model.primary.po.WaterPlanFillinYear;
//import com.nari.slsd.msrv.waterdiversion.model.vo.BuildingExt;
//import com.nari.slsd.msrv.waterdiversion.model.vo.PlanFillinStateExt;
//
//import java.util.List;
//
///**
// * <p>
// * 年度用水计划填报 服务类
// * </p>
// *
// * @author zhs
// * @since 2021-08-04
// */
//interface IWaterPlanFillInYearService extends IService<WaterPlanFillinYear>{
//    /**
//     * 添加年计划水量
//     */
//    void addWaterPlanValue(WrPlanFillInValue wrPlanFillInValue);
//
//    /**
//     * 根据条件查询年汇总数据
//     */
//    List<BuildingExt> findPlanValueByCodAndTime(PlanFillinExamineDTO planFillinExamineDTO);
//
//    /**
//     * 用水计划填报单位查询
//     */
//    PlanFillinStateExt findUnitByCodAndTime(String time, String userId, List<Integer> unitLevels, List<String> buildingType, Integer fillReport,List<Integer> levels);
//
//    /**
//     * 年计划汇总（管理单位）
//     */
//    List<BuildingExt> findPlanAllValueByCodAndTime(String year,String userId,List<String> mngUnitId, List<Integer> unitLevels, List<String> buildingType,Integer fillReport,List<Integer> levels);
//
//
//}