package com.nari.slsd.msrv.waterdiversion.mapper.primary;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WaterPlanFillinMonth;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrPlanFillinDay;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 近期计划填报 Mapper 接口
 * </p>
 *
 * @author zhs
 * @since 2021-08-20
 */
@Repository
public interface WrPlanFillinDayMapper extends BaseMapper<WrPlanFillinDay> {

}
