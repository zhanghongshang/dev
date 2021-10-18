package com.nari.slsd.msrv.waterdiversion.mapper.primary;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrDayInput;
import com.nari.slsd.msrv.waterdiversion.model.vo.SimpleWrDayInputVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 日水情录入 Mapper 接口
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-19
 */
@Repository
public interface WrDayInputMapper extends BaseMapper<WrDayInput> {
    /**
     * 时间段获取日迭代数据累计值
     * @return
     */
    @Select("select STATION_ID,sum(MANUAL_WATER_FLOW) as waterFlow from WR_DAY_INPUT ${ew.customSqlSegment}")
    List<SimpleWrDayInputVO> getSumWaterFlow(@Param(Constants.WRAPPER) LambdaQueryWrapper<WrDayInput> wrapper);
}
