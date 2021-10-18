package com.nari.slsd.msrv.waterdiversion.config.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.nari.slsd.msrv.waterdiversion.config.annotations.ExcelField;
import lombok.Data;

/**
 * @author bigb
 * @version 1.0.0
 * @ClassName WaterPlanYearModel
 * @Description 日水情录入日导入模板
 * @createTime 2021年08月31日
 */
@Data
public class WrDayInputInDayModel extends BaseRowModel {
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

    @ExcelProperty(value = {"流量","1日"},index = 5)
    @ExcelField
    private Double day1;

    @ExcelField
    @ExcelProperty(value = {"流量","2日"},index = 6)
    private Double day2;

    @ExcelField
    @ExcelProperty(value = {"流量","3日"},index = 7)
    private Double day3;

    @ExcelField
    @ExcelProperty(value = {"流量","4日"},index = 8)
    private Double day4;

    @ExcelField
    @ExcelProperty(value = {"流量","5日"},index = 9)
    private Double day5;

    @ExcelField
    @ExcelProperty(value = {"流量","6日"},index = 10)
    private Double day6;

    @ExcelField
    @ExcelProperty(value = {"流量","7日"},index = 11)
    private Double day7;

    @ExcelField
    @ExcelProperty(value = {"流量","8日"},index = 12)
    private Double day8;

    @ExcelField
    @ExcelProperty(value = {"流量","9日"},index = 13)
    private Double day9;

    @ExcelField
    @ExcelProperty(value = {"流量","10日"},index = 14)
    private Double day10;

    @ExcelField
    @ExcelProperty(value = {"流量","11日"},index = 15)
    private Double day11;

    @ExcelField
    @ExcelProperty(value = {"流量","12日"},index = 16)
    private Double day12;

    @ExcelField
    @ExcelProperty(value = {"流量","13日"},index = 17)
    private Double day13;

    @ExcelField
    @ExcelProperty(value = {"流量","14日"},index = 18)
    private Double day14;

    @ExcelField
    @ExcelProperty(value = {"流量","15日"},index = 19)
    private Double day15;

    @ExcelField
    @ExcelProperty(value = {"流量","16日"},index = 20)
    private Double day16;

    @ExcelField
    @ExcelProperty(value = {"流量","17日"},index = 21)
    private Double day17;

    @ExcelField
    @ExcelProperty(value = {"流量","18日"},index = 22)
    private Double day18;

    @ExcelField
    @ExcelProperty(value = {"流量","19日"},index = 23)
    private Double day19;

    @ExcelField
    @ExcelProperty(value = {"流量","20日"},index = 24)
    private Double day20;

    @ExcelField
    @ExcelProperty(value = {"流量","21日"},index = 25)
    private Double day21;

    @ExcelField
    @ExcelProperty(value = {"流量","22日"},index = 26)
    private Double day22;

    @ExcelField
    @ExcelProperty(value = {"流量","23日"},index = 27)
    private Double day23;

    @ExcelField
    @ExcelProperty(value = {"流量","24日"},index = 28)
    private Double day24;

    @ExcelField
    @ExcelProperty(value = {"流量","25日"},index = 29)
    private Double day25;

    @ExcelField
    @ExcelProperty(value = {"流量","26日"},index = 30)
    private Double day26;

    @ExcelField
    @ExcelProperty(value = {"流量","27日"},index = 31)
    private Double day27;

    @ExcelField
    @ExcelProperty(value = {"流量","28日"},index = 32)
    private Double day28;

    @ExcelField
    @ExcelProperty(value = {"流量","29日"},index = 33)
    private Double day29;

    @ExcelField
    @ExcelProperty(value = {"流量","30日"},index = 34)
    private Double day30;

    @ExcelField
    @ExcelProperty(value = {"流量","31日"},index = 35)
    private Double day31;


}
