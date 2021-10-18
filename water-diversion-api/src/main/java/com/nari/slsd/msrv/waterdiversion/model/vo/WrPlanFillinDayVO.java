package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 近期计划调整
 * </p>
 *
 * @author zhs
 * @since 2021-08-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrPlanFillinDayVO {
    /**
     * 调整类型（0：跨月 1：跨年 2：超年）
     */
    private String adjustType;
    /**
     * 引水口名称
     */
    private String buildingName;
    /**
     * 管理站名称
     */
    private String mngUnitName;
    /**
     * 用水单位名称
     */
    private String useUnitName;
    /**
     * 引水口id
     */
    private String buildingId;
    /**
     * 近期调整数据
     */
    private Object oldWaterValue;
    /**
     * 实引水量数据
     */
    private Object realWaterValue;

    /**
     * 近期调整数据
     */
    private Object newWaterValue;
    /**
     * 差值
     */
    private Object diffWaterValue;
    /**
     * 最终差值
     */
    private Object lastDiffWaterValue;

    /**
     * 差值百分比
     */
    private Object percentWaterValue;

    private Object adjustDifference;

    private String name;

    /**
     * 差值
     */
    private Object difference;

    /**
     * 差值百分比
     */
    private Object differencePercentage;
    /**
     * 编辑
     */
    private Object differenceEdit;

    /**
     * 输入数字
     */
    private Object inputType;
    /**
     * 不能编辑
     */
    private Object notEdit;

}
