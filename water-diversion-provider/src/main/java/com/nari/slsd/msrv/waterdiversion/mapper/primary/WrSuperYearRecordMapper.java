package com.nari.slsd.msrv.waterdiversion.mapper.primary;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrPlanInterDay;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrSuperYearRecord;
import com.nari.slsd.msrv.waterdiversion.model.vo.SimpleWrPlanInterDayVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.SimpleWrSuperYearRecordVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description 超年填报记录  Mapper 接口
 * @Author ZHS
 * @Date 2021/9/27 1:32
 */
@Repository
public interface WrSuperYearRecordMapper extends BaseMapper<WrSuperYearRecord> {
    /**
     * 累计分配水量
     * @return
     */
    @Select("select WATER_REGIME_CODE,sum(TOTAL_WATER) as totalWater from WR_SUPER_YEAR_RECORD ${ew.customSqlSegment}")
    List<SimpleWrSuperYearRecordVO> getSumTradeWater(@Param(Constants.WRAPPER) LambdaQueryWrapper<WrSuperYearRecord> wrapper);
}
