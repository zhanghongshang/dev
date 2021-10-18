package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Program: water-diversion
 * @Description: 日水情表格
 * @Author: reset kalar
 * @Date: 2021-08-24 16:51
 **/
@Data
public class WrDayInputTable {

    /**
     * 未录入计数
     */
    private Integer unenteredCount = 0;
    /**
     * 未审核计数
     */
    private Integer unreviewedCount = 0;
    /**
     * 已审核计数
     */
    private Integer reviewedCount = 0;
    /**
     * 总计数
     */
    private Integer totalCount = 0;
    /**
     * 表格数据
     */
    private List<MngUnitAndBuildings> data;

    public WrDayInputTable() {
        data = new ArrayList<>();
    }

}
