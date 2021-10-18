package com.nari.slsd.msrv.waterdiversion.mapper.primary;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrBuildingAndDiversion;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WaterBuildingManager;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * <p>
 * 水工建筑物管理 Mapper 接口
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-03
 */
@Repository
public interface WaterBuildingManagerMapper extends BaseMapper<WaterBuildingManager> {

    /**
     * 联表查询测站引水口
     *
     * @param wrapper 条件
     * @return List<WrBuildingAndDiversion>
     */
    @Select("SELECT " +
            "wb.ID," +
            "wb.BUILDING_NAME," +
            "wb.BUILDING_TYPE," +
            "wb.FILL_REPORT," +
            "wb.LATLNG_F," +
            "wb.LATLNG_S," +
            "wd.BUILDING_CODE," +
            "wd.SITE_TYPE," +
            "wd.COLLECT_TYPE," +
            "wd.IF_ECOLOGICAL," +
            "wd.REMARK," +
            "wd.WATER_UNIT_ID," +
            "wd.MNG_UNIT_ID," +
            "wd.SHARE_FACTOR," +
            "wd.WATER_NATURE," +
            "wd.IF_LONG_TERM," +
            "wd.IF_REMOTE_CONTROL," +
            "wd.IF_REMOTE_CONTROL," +
            "wd.RATE_MANAGER," +
            "wd.BUILDING_LEVEL," +
            "wd.IF_PUBLIC," +
            "wd.PID " +
            "FROM WATER_BUILDING_MANAGER wb " +
            "LEFT JOIN " +
            "WR_DIVERSION_PORT wd " +
            "ON wb.ID=wd.ID " +
            "${ew.customSqlSegment}")
    List<WrBuildingAndDiversion> getBuildingAndDiversionList(@Param(Constants.WRAPPER) QueryWrapper<WaterBuildingManager> wrapper);

    /**
     * 联表查询测站引水口(包含上级引水口name和code)
     *
     * @param wrapper 条件
     * @return List<WrBuildingAndDiversion>
     */
    @Select("SELECT " +
            "wb.ID," +
            "wb.BUILDING_NAME," +
            "wb.BUILDING_TYPE," +
            "wb.FILL_REPORT," +
            "wb.LATLNG_F," +
            "wb.LATLNG_S," +
            "wd.BUILDING_CODE," +
            "wd.SITE_TYPE," +
            "wd.COLLECT_TYPE," +
            "wd.IF_ECOLOGICAL," +
            "wd.REMARK," +
            "wd.WATER_UNIT_ID," +
            "wd.MNG_UNIT_ID," +
            "wd.SHARE_FACTOR," +
            "wd.WATER_NATURE," +
            "wd.IF_LONG_TERM," +
            "wd.IF_REMOTE_CONTROL," +
            "wd.RATE_MANAGER," +
            "wd.BUILDING_LEVEL," +
            "wd.IF_PUBLIC," +
            "wd.PID," +
            "wbm.BUILDING_NAME AS P_NAME," +
            "wdm.BUILDING_CODE AS P_CODE " +
            "FROM WATER_BUILDING_MANAGER wb " +
            "LEFT JOIN " +
            "WR_DIVERSION_PORT wd " +
            "ON wb.ID=wd.ID " +
            "LEFT JOIN " +
            "WATER_BUILDING_MANAGER wbm " +
            "ON wd.PID=wbm.ID " +
            "LEFT JOIN " +
            "WR_DIVERSION_PORT wdm " +
            "ON wbm.ID=wdm.ID " +
            "${ew.customSqlSegment}")
    List<WrBuildingAndDiversion> getPBuildingAndDiversionList(@Param(Constants.WRAPPER) QueryWrapper<WaterBuildingManager> wrapper);

    /**
     * 查询测站-引水口以及所属用水单位、所属管理单位分页
     *
     * @param page
     * @param wrapper eg: wrapper.eq("wb.ID", "SGJZ00000169");
     * @return
     */
    @Select("SELECT " +
            "wb.ID," +
            "wb.BUILDING_NAME," +
            "wb.BUILDING_TYPE," +
            "wb.FILL_REPORT," +
            "wb.LATLNG_F," +
            "wb.LATLNG_S," +
            "wd.BUILDING_CODE," +
            "wd.SITE_TYPE," +
            "wd.COLLECT_TYPE," +
            "wd.IF_ECOLOGICAL," +
            "wd.REMARK," +
            "wd.WATER_UNIT_ID," +
            "wd.MNG_UNIT_ID," +
            "wd.SHARE_FACTOR," +
            "wd.WATER_NATURE," +
            "wd.IF_LONG_TERM," +
            "wd.RATE_MANAGER," +
            "wd.BUILDING_LEVEL," +
            "wd.IF_PUBLIC," +
            "wd.PID," +
            "wd.IF_REMOTE_CONTROL " +
            "FROM WATER_BUILDING_MANAGER wb " +
            "LEFT JOIN " +
            "WR_DIVERSION_PORT wd " +
            "ON wb.ID=wd.ID " +
            "${ew.customSqlSegment}")
    IPage<WrBuildingAndDiversion> getBuildingAndDiversionPage(IPage page, @Param(Constants.WRAPPER) QueryWrapper<WaterBuildingManager> wrapper);


}
