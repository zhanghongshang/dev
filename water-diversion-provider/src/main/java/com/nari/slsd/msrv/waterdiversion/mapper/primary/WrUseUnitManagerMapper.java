package com.nari.slsd.msrv.waterdiversion.mapper.primary;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrUseUnitManager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 用水单位管理 Mapper 接口
 * </p>
 *
 * @author reset kalar
 * @since 2021-07-29
 */
@Mapper
public interface WrUseUnitManagerMapper extends BaseMapper<WrUseUnitManager> {
    /**TODO 存在问题
     * 查询最后一级用水单位信息
     * @return
     */
    @Select("select id,code,unit_name from WR_USE_UNIT_MANAGER " +
            "where UNIT_LEVEL = (select max(UNIT_LEVEL) from WR_USE_UNIT_MANAGER) and state =1")
    List<WrUseUnitManager> getAllWrUseUnitManager();
}
