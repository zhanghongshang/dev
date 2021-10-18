package com.nari.slsd.msrv.waterdiversion.mapper.primary;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrCmdManager;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrCmdManagerAndInstruction;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @title
 * @description 调度指令管理 Mapper 接口
 * @author bigb
 * @updateTime 2021/8/23 9:58
 * @throws
 */
@Repository
public interface WrCmdManagerMapper extends BaseMapper<WrCmdManager> {
    /**
     * 查询调度指令管理信息分页
     * @param page
     * @param wrapper
     * @return
     */
    @Select("SELECT " +
            "wb.ID," +
            "wb.SCHEME_ID," +
            "wb.ORDER_CODE," +
            "wb.ORDER_NAME," +
            "wb.ORDER_TYPE," +
            "wb.ORDER_STATUS," +
            "wb.ORDER_CONTENT," +
            "wb.YEAR," +
            "wb.LAUNCH_NAME," +
            "wb.APPROVE_NAME," +
            "wb.APPROVE_CONTENT," +
            "wb.ORDER_TIME," +
            "wb.MANAGE_UNIT_NAME," +
            "wb.ATTACH," +
            "wb.UPDATE_TIME," +
            "wd.ID INSTRUCTION_ID," +
            "wd.BUILDING_ID," +
            "wd.BUILDING_NAME," +
            "wd.START_TIME," +
            "wd.SET_VALUE," +
            "wd.MODIFY_VALUE " +
            "FROM WR_CMD_MANAGER wb " +
            "LEFT JOIN " +
            "WR_DISPATCH_CMD wd " +
            "ON wb.ID = wd.ORDER_ID " +
            "${ew.customSqlSegment}")
    IPage<WrCmdManagerAndInstruction> getBWrCmdManagerPage(IPage page, @Param(Constants.WRAPPER) QueryWrapper<WrCmdManager> wrapper);
}
