package com.nari.slsd.msrv.waterdiversion.mapper.primary;

import com.nari.slsd.msrv.waterdiversion.model.po.WrUseUnitPerson;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 用水单位人员表 Mapper 接口
 * </p>
 *
 * @author reset kalar
 * @since 2021-07-30
 */
@Repository
public interface WrUseUnitPersonMapper extends BaseMapper<WrUseUnitPerson> {
 /*   @Select("select e2.UNIT_NAME,e1.UNIT_CODE from WATER_USE_UNIT_PERSON e1,WATER_USE_UNIT_MANAGER e2 where e1.id=e2.pid and e1.id = #{id}")
    List<WaterUserDemo> list(@Param("id") String id, IPage page);*/
}
