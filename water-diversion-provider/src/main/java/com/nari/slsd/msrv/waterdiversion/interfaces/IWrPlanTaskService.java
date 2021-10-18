package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WaterBuildingManager;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrPlanTask;
import com.nari.slsd.msrv.waterdiversion.model.vo.BuildingExt;
import com.nari.slsd.msrv.waterdiversion.model.vo.BuildingTask;
import com.nari.slsd.msrv.waterdiversion.model.vo.MngUnitAndBuilding;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrPlanTaskVO;

import java.util.List;

/**
 * <p>
 * 用水计划任务 服务类
 * </p>
 *
 * @author zhs
 * @since 2021-08-13
 */
public interface IWrPlanTaskService extends IService<WrPlanTask>{
    /**
     *   条件查询用水计划任务数据
     *
     * @param statrTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    public DataTableVO findWrPlanTask(String userId,List<String> mngUnitId,List<Integer> unitLevel,List<String> buildType ,Integer fillReport ,Long statrTime, Long endTime, String planType,String state,List<Integer> levels,Integer pageIndex, Integer pageSize);

    /**
     * 填报任务总览（用水户/管理单位）
     */
    List<BuildingTask> findYearTasks(String time, String planType,String unitId,List<String> buildingTypes,List<String> mngUnitId,Integer fillReport, List<Integer> unitLevels,List<Integer> levels);

    boolean insert(WrPlanTask wrPlanTask);
    
}
