package com.nari.slsd.msrv.waterdiversion.config.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.nari.slsd.msrv.waterdiversion.config.annotations.ExcelField;
import lombok.Data;

/**
 * @author bigb
 * @version 1.0.0
 * @ClassName WaterPlanYearModel
 * @Description 年计划导入模板
 * @createTime 2021年08月31日
 */
@Data
public class WaterPlanYearModel extends BaseRowModel {
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

    @ExcelProperty(value = {"1月","上旬"},index = 5)
    @ExcelField
    private Double month1_1;

    @ExcelField
    @ExcelProperty(value = {"1月","中旬"},index = 6)
    private Double month1_2;

    @ExcelField
    @ExcelProperty(value = {"1月","下旬"},index = 7)
    private Double month1_3;

    @ExcelField
    @ExcelProperty(value = {"2月","上旬"},index = 8)
    private Double month2_1;

    @ExcelField
    @ExcelProperty(value = {"2月","中旬"},index = 9)
    private Double month2_2;

    @ExcelField
    @ExcelProperty(value = {"2月","下旬"},index = 10)
    private Double month2_3;

    @ExcelField
    @ExcelProperty(value = {"3月","上旬"},index = 11)
    private Double month3_1;

    @ExcelField
    @ExcelProperty(value = {"3月","中旬"},index = 12)
    private Double month3_2;

    @ExcelField
    @ExcelProperty(value = {"3月","下旬"},index = 13)
    private Double month3_3;

    @ExcelField
    @ExcelProperty(value = {"4月","上旬"},index = 14)
    private Double month4_1;

    @ExcelField
    @ExcelProperty(value = {"4月","中旬"},index = 15)
    private Double month4_2;

    @ExcelField
    @ExcelProperty(value = {"4月","下旬"},index = 16)
    private Double month4_3;

    @ExcelField
    @ExcelProperty(value = {"5月","上旬"},index = 17)
    private Double month5_1;

    @ExcelField
    @ExcelProperty(value = {"5月","中旬"},index = 18)
    private Double month5_2;

    @ExcelField
    @ExcelProperty(value = {"5月","下旬"},index = 19)
    private Double month5_3;

    @ExcelField
    @ExcelProperty(value = {"6月","上旬"},index = 20)
    private Double month6_1;

    @ExcelField
    @ExcelProperty(value = {"6月","中旬"},index = 21)
    private Double month6_2;

    @ExcelField
    @ExcelProperty(value = {"6月","下旬"},index = 22)
    private Double month6_3;

    @ExcelField
    @ExcelProperty(value = {"7月","上旬"},index = 23)
    private Double month7_1;

    @ExcelField
    @ExcelProperty(value = {"7月","中旬"},index = 24)
    private Double month7_2;

    @ExcelField
    @ExcelProperty(value = {"7月","下旬"},index = 25)
    private Double month7_3;

    @ExcelField
    @ExcelProperty(value = {"8月","上旬"},index = 26)
    private Double month8_1;

    @ExcelField
    @ExcelProperty(value = {"8月","中旬"},index = 27)
    private Double month8_2;

    @ExcelField
    @ExcelProperty(value = {"8月","下旬"},index = 28)
    private Double month8_3;

    @ExcelField
    @ExcelProperty(value = {"9月","上旬"},index = 29)
    private Double month9_1;

    @ExcelField
    @ExcelProperty(value = {"9月","中旬"},index = 30)
    private Double month9_2;

    @ExcelField
    @ExcelProperty(value = {"9月","下旬"},index = 31)
    private Double month9_3;

    @ExcelField
    @ExcelProperty(value = {"10月","上旬"},index = 32)
    private Double month10_1;

    @ExcelField
    @ExcelProperty(value = {"10月","中旬"},index = 33)
    private Double month10_2;

    @ExcelField
    @ExcelProperty(value = {"10月","下旬"},index = 34)
    private Double month10_3;

    @ExcelField
    @ExcelProperty(value = {"11月","上旬"},index = 35)
    private Double month11_1;

    @ExcelField
    @ExcelProperty(value = {"11月","中旬"},index = 36)
    private Double month11_2;

    @ExcelField
    @ExcelProperty(value = {"11月","下旬"},index = 37)
    private Double month11_3;

    @ExcelField
    @ExcelProperty(value = {"12月","上旬"},index = 38)
    private Double month12_1;

    @ExcelField
    @ExcelProperty(value = {"12月","中旬"},index = 39)
    private Double month12_2;

    @ExcelField
    @ExcelProperty(value = {"12月","下旬"},index = 40)
    private Double month12_3;

}
