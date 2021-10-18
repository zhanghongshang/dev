package com.nari.slsd.msrv.waterdiversion.mapper.fourth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import com.nari.slsd.msrv.waterdiversion.model.fourth.po.WmProjectLastR;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 测站采集最新水情表 Mapper 接口
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-30
 */
@Mapper
public interface WmProjectLastRMapper extends MppBaseMapper<WmProjectLastR> {

//    @Select("SELECT " +
//            "A.* " +
//            "FROM WM_PROJECT_LAST_R A " +
//            "RIGHT JOIN " +
//            "(SELECT MAX(B.TM) as mtm,B.STCD as mstcd  from WM_PROJECT_LAST_R B GROUP BY B.STCD) C " +
//            "ON A.STCD=C.mstcd " +
//            "AND A.TM=C.mtm " +
//            "${ew.customSqlSegment}")
//    IPage<WmProjectLastR> getNewestWmProjectLastRPageGroupByStcd(IPage page, @Param(Constants.WRAPPER) QueryWrapper<WmProjectLastR> wrapper);

    /**
     * TODO 模式写死
     * @param wrapper
     * @return
     */

    @Select("SELECT " +
            "A.* " +
            "FROM USER_WM.WM_PROJECT_LAST_R A " +
            "RIGHT JOIN " +
            "(SELECT MAX(B.TM) as mtm,B.STCD as mstcd  from USER_WM.WM_PROJECT_LAST_R B GROUP BY B.STCD) C " +
            "ON A.STCD=C.mstcd " +
            "AND A.TM=C.mtm " +
            "${ew.customSqlSegment}")
    List<WmProjectLastR> getNewestWmProjectLastRListGroupByStcd(@Param(Constants.WRAPPER) QueryWrapper<WmProjectLastR> wrapper);

}
