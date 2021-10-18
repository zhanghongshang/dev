//package com.nari.slsd.msrv.waterdiversion.interfaces;
//
//import com.alibaba.fastjson.JSONArray;
//import com.baomidou.mybatisplus.extension.service.IService;
//import com.nari.slsd.msrv.waterdiversion.model.primary.po.WaterPlanFillinMonth;
//import com.nari.slsd.msrv.waterdiversion.model.primary.po.WaterPlanFillinYear;
//import com.nari.slsd.msrv.waterdiversion.model.vo.BuildingExt;
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
//interface IWaterPlanFillInMonthService extends IService<WaterPlanFillinMonth> {
//    /**
//     * 添加月计划水量
//     */
//    void addWaterPlanValue(JSONArray jsonArray);
//
//    /**
//     * 根据条件查询审批数据
//     */
//    List<BuildingExt> findPlanValueByCodAndTime(String year, String month, String userId, List<Integer> unitLevels, List<String> buildingType, Integer fillReport,List<Integer> levels);
//
//    /**
//     * 用水计划填报单位查询
//     */
//    List<BuildingExt> findUnitByCodAndTime(String time, String month, String userId, List<Integer> unitLevels, List<String> buildingType, Integer fillReport,List<Integer> levels);
//
//    /**
//     * 月计划汇总（管理单位）
//     */
//    List<BuildingExt> findPlanAllValueByCodAndTime(String year, String month, String userId, List<String> mngUnitId, List<Integer> unitLevels, List<String> buildingType, Integer fillReport,List<Integer> levels);
//
//}
