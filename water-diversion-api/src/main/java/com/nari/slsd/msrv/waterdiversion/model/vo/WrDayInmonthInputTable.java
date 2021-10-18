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
public class WrDayInmonthInputTable {

    /**
     * 未校核计数
     */
    private Integer uncheckedCount = 0;
    /**
     * 已校核计数
     */
    private Integer checkedCount = 0;
    /**
     * 总计数
     */
    private Integer totalCount = 0;
    /**
     * 表格数据
     */
    private List<MngUnitAndBuilding> data;

    public WrDayInmonthInputTable() {
        data = new ArrayList<>();
    }

}
