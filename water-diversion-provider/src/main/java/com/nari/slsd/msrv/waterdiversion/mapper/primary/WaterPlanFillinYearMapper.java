package com.nari.slsd.msrv.waterdiversion.mapper.primary;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.nari.slsd.msrv.waterdiversion.model.dto.WaterPlanFillinYearAndPId;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WaterPlanFillinYear;
import com.nari.slsd.msrv.waterdiversion.model.vo.SimpleWaterPlanFillInYearVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WaterPlanFillInYearSimpleVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 月度用水计划填报 Mapper 接口
 * </p>
 *
 * @author zhs
 * @since 2021-08-13
 */
@Repository
public interface WaterPlanFillinYearMapper extends BaseMapper<WaterPlanFillinYear> {
    /**
     * @param wrapper 条件
     * @return List<WaterPlanFillInYearSimpleVO>
     */
    @Select("SELECT BUILDING_ID  , DEMAND_WATER_QUANTITY AS WATER_QUANTITY FROM  WR_PLAN_FILLIN_Y " +
            "${ew.customSqlSegment}")
    List<WaterPlanFillInYearSimpleVO> getYearPlan(@Param(Constants.WRAPPER) QueryWrapper<WaterPlanFillinYear> wrapper);

    /**
     *
     * @param wrapper
     * @return
     */
    @Select("select sum(DEMAND_WATER_QUANTITY) as " +
            "demadWaterQuantity," +
            "YEAR as year," +
            "MONTH as month," +
            "TDAY as tday," +
            "PID as buildingId " +
            "from (select a.*,b.PID " +
            "from PROMNG.WR_PLAN_FILLIN_Y a " +
            "left join PROMNG.WR_DIVERSION_PORT b " +
            "on a.BUILDING_ID = b.ID ${ew.customSqlSegment} ) " +
            "group by MONTH,YEAR,TDAY,PID")
    List<WaterPlanFillinYearAndPId> getWaterPlanFillinYearAndPId(@Param(Constants.WRAPPER) QueryWrapper<WaterPlanFillinYearAndPId> wrapper);
    //a.YEAR = '2021' and b.PID IS NOT NULL and b.PID != ''

    @Select("select " +
            "a.BUILDING_ID as buildingId," +
            "a.DEMAND_WATER_QUANTITY as demadWaterQuantity," +
            "a.YEAR as year," +
            "a.MONTH as month," +
            "a.TDAY as tday," +
            "b.PID as pid " +
            "from " +
            "PROMNG.WR_PLAN_FILLIN_Y a " +
            "left join PROMNG.WR_DIVERSION_PORT b " +
            "on a.BUILDING_ID=b.ID " +
            "${ew.customSqlSegment}")
    List<WaterPlanFillinYearAndPId> getWaterPlanFillinYearAndPIdisNull(@Param(Constants.WRAPPER) QueryWrapper<WaterPlanFillinYearAndPId> wrapper);
    //where b.PID is null or b.PID = '' and a.YEAR = '2021'

    @Select("select BUILDING_ID,sum(DEMAND_WATER_QUANTITY) as waterQuantity from WR_PLAN_FILLIN_Y ${ew.customSqlSegment}")
    List<SimpleWaterPlanFillInYearVO> getSumWaterQuantity(@Param(Constants.WRAPPER) LambdaQueryWrapper<WaterPlanFillinYear> wrapper);
}
