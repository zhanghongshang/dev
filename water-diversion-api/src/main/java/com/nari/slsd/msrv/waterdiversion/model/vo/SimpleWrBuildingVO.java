package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @title
 * @description 测站信息
 * @author bigb
 * @updateTime 2021/9/13 22:00
 * @throws
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleWrBuildingVO {

    /**
     * 引水口ID
     */
    private String buildingId;

    /**
     * 引水口编码
     */
    private String buildingCode;

    /**
     * 引水口名称
     */
    private String buildingName;

    /**
     * 所属用水单位ID
     */
    private String waterUnitId;

    /**
     * 所属县市师团用水单位ID
     */
    private String xsUnitId;

    /**
     * 所属县市师团用水单位名称
     */
    private String xsUnitName;

    /**
     * 所属用水单位名称
     */
    private String waterUnitName;

    /**
     * 所属管理单位id
     */
    private String mngUnitId;

    /**
     * 所属管理单位名称
     */
    private String mngUnitName;

    /**
     * 月剩余水量（月结余+月剩余指标）
     */
    private BigDecimal remainOfMonth;

}
