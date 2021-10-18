package com.nari.slsd.msrv.waterdiversion.mapper.primary;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrBuildingAndDiversion;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrCategory;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrCategoryTempVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @title
 * @description 用水性质 Mapper 接口
 * @author bigb
 * @updateTime 2021/8/23 9:58
 * @throws
 */
@Repository
public interface WrCategoryMapper extends BaseMapper<WrCategory> {
    /**
     * 联表查询所有用水性质信息
     * @param wrapper
     * @return
     */
    @Select("SELECT " +
            "wb.ID," +
            "wb.CATEGORY_CODE," +
            "wb.CATEGORY_NAME," +
            "wb.WATER_TYPE WATER_TYPE_CODE," +
            "wb.WATER_TYPE_NAME," +
            "wb.CREATE_TIME," +
            "wb.UPDATE_TIME," +
            "wb.PERSON_NAME," +
            "wd.ID FEE_ID," +
            "wd.SURPASS_RATE," +
            "wd.FEE_RATE" +
            "FROM WR_CATEGORY_MANAGE wb " +
            "LEFT JOIN " +
            "WR_FEE_RATE wd " +
            "ON wb.ID = wd.CATEGORY_ID " +
            "${ew.customSqlSegment}")
    List<WrCategoryTempVO> getWrCategoryAndFeeList(@Param(Constants.WRAPPER) QueryWrapper<WrCategory> wrapper);
}
