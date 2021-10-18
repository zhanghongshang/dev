package com.nari.slsd.msrv.waterdiversion.config.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.nari.slsd.msrv.waterdiversion.config.annotations.ExcelField;
import lombok.Data;

/**
 * @author bigb
 * @version 1.0.0
 * @ClassName WaterPlanYearModel
 * @Description 日水情录入月导入模板
 * @createTime 2021年08月31日
 */
@Data
public class WrDayInputInMonthModel extends BaseRowModel {
    @ExcelProperty(value = {"管理单位","管理单位"},index = 0)
    @ExcelField(ignore = true)
    private String manager;

    @ExcelProperty(value = {"用水单位","市/县/师团"},index = 1)
    @ExcelField(ignore = true)
    private String waterUserUnitParent;

    @ExcelProperty(value = {"用水单位","县/乡/其他/团连"},index = 2)
    @ExcelField(ignore = true)
    private String waterUserUnitChild;

    @ExcelProperty(value = {"引水口","引水口"},index = 3)
    private String buildingName;

    @ExcelProperty(value = {"引水口编码","引水口编码"},index = 4)
    private String buildingCode;

    private String buildingId;

    @ExcelProperty(value = {"流量","1月"},index = 5)
    @ExcelField
    private Double month1;

    @ExcelField
    @ExcelProperty(value = {"流量","2月"},index = 6)
    private Double month2;

    @ExcelField
    @ExcelProperty(value = {"流量","3月"},index = 7)
    private Double month3;

    @ExcelField
    @ExcelProperty(value = {"流量","4月"},index = 8)
    private Double month4;

    @ExcelField
    @ExcelProperty(value = {"流量","5月"},index = 9)
    private Double month5;

    @ExcelField
    @ExcelProperty(value = {"流量","6月"},index = 10)
    private Double month6;

    @ExcelField
    @ExcelProperty(value = {"流量","7月"},index = 11)
    private Double month7;

    @ExcelField
    @ExcelProperty(value = {"流量","8月"},index = 12)
    private Double month8;

    @ExcelField
    @ExcelProperty(value = {"流量","9月"},index = 13)
    private Double month9;

    @ExcelField
    @ExcelProperty(value = {"流量","10月"},index = 14)
    private Double month10;

    @ExcelField
    @ExcelProperty(value = {"流量","11月"},index = 15)
    private Double month11;

    @ExcelField
    @ExcelProperty(value = {"流量","12月"},index = 16)
    private Double month12;


}
