package com.nari.slsd.msrv.waterdiversion.mapper.primary;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrPlanTaskDTO;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrPlanTask;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 用水计划任务 Mapper 接口
 * </p>
 *
 * @author zhs
 * @since 2021-08-13
 */
@Repository
public interface WrPlanTaskMapper extends BaseMapper<WrPlanTask> {

    //判断年填报依据
    @Select("SELECT DISTINCT fw.WATER_PLAN_FILL_IN,fw.PERSON_ID,fw.CREATE_DATE,fw.STATE,wp.UNIT_ID FROM WR_PLAN_TASK fw LEFT JOIN  WR_PLAN_TASK_SUB wp  ON fw.ID  = wp.TASK_ID ${ew.customSqlSegment}")
    List<WrPlanTaskDTO> orderPlanYearList(@Param(Constants.WRAPPER) QueryWrapper<WrPlanTask> wrapper);
}
