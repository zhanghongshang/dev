package com.nari.slsd.msrv.waterdiversion.mapper.primary;

import com.nari.slsd.msrv.waterdiversion.model.dto.WrBuildingAndDiversion;
import com.nari.slsd.msrv.waterdiversion.model.po.WaterBuildingManager;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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

//    /**
//     * 关联查询
//     * @return
//     */
//    @Select("select * from ")
//    List<WrBuildingAndDiversion> selectWrBuildingAndDiversion();

}
