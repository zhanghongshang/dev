package com.nari.slsd.msrv.waterdiversion.mapper.primary;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrPlanInterDay;
import com.nari.slsd.msrv.waterdiversion.model.vo.SimpleWrPlanInterDayVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 年度用水计划填报 Mapper 接口
 * </p>
 *
 * @author zhs
 * @since 2021-08-04
 */
@Repository
public interface WrPlanInterDayMapper extends BaseMapper<WrPlanInterDay>{
    /**
     * 查询所有流量发生变化的引水口信息
     * @param
     * @return
     */
    @Select("select  b.* from\n" +
            "        (select * from WR_PLAN_ITER_DAY where SUPPLY_TIME = #{today}) a inner join\n" +
            "        (select * from WR_PLAN_ITER_DAY where SUPPLY_TIME = #{tomorrow}) b\n" +
            "        on a.BUILDING_ID = b.BUILDING_ID\n" +
            "        where a.WATER_FLOW <> b.WATER_FLOW")
    List<WrPlanInterDay> getChangeFlowForAllStations(@Param("today") Date today , @Param("tomorrow") Date tomorrow);

    /**
     * 时间段获取日迭代数据累计值
     * @return
     */
    @Select("select sum(WATER_QUANTITY) as waterQuantity, BUILDING_ID from WR_PLAN_ITER_DAY ${ew.customSqlSegment}")
    List<WrPlanInterDay>  getPlanDaySumForTime(@Param(Constants.WRAPPER) QueryWrapper<WrPlanInterDay> wrapper);

    /**
     * 时间段获取日迭代数据累计值
     * @return
     */
    @Select("select BUILDING_ID,sum(WATER_QUANTITY) as waterQuantity from WR_PLAN_ITER_DAY ${ew.customSqlSegment}")
    List<SimpleWrPlanInterDayVO> getSumWaterQuantity(@Param(Constants.WRAPPER) LambdaQueryWrapper<WrPlanInterDay> wrapper);

}
