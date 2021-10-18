package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Program: water-diversion
 * @Description: 测站-引水口管理类
 * @Author: reset kalar
 * @Date: 2021-08-04 09:24
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrBuildingAndDiversion {

    /**
     * 水工建筑物ID
     */
    private String id;

    /**
     * 测站编码
     */
    private String buildingCode;

    /**
     * 水工建筑物名称
     */
    private String buildingName;

    /**
     * 所属用水单位ID
     */
    private String waterUnitId;

    /**
     * 所属用水单位名称
     */
    private String waterUnitName;

    /**
     * 水工建筑物类型
     */
    private String buildingType;

    /**
     * 是否填报
     */
    private Integer fillReport;

    /**
     * 站点属性
     */
    private Integer siteType;

    /**
     * 采集方式
     * 0.人工
     * 1.自动
     */
    private Integer collectType;

    /**
     * GIS坐标 [经度,纬度]
     */
    private String latlngF;

    /**
     * 拓扑坐标 [经度,纬度]
     */
    private String latlngS;

    /**
     * 是否为生态用水
     */
    private Integer ifEcological;

    /**
     * 备注
     */
    private String remark;


    /**
     * 所属管理单位id
     */
    private String mngUnitId;

    /**
     * 所属管理单位名称
     */
    private String mngUnitName;

    /**
     * 公摊系数
     */
    private Double shareFactor;

    /**
     * 用水性质
     */
    private Integer waterNature;

    /**
     * 是否长期
     */
    private Integer ifLongTerm;

    /**
     * 是否可远控
     * 0否(默认)
     * 1是
     */
    private Integer ifRemoteControl;

    /**
     * 费率管理
     */
    private Double rateManager;

    /**
     * 引水口层级
     * 1 一级
     * 2 二级
     */
    private Integer buildingLevel;

    /**
     * 是否为公共引水口
     * 0 否
     * 1 是
     */
    private Integer ifPublic;

    /**
     * 上级引水口
     */
    private String pid;


    /**
     * 上级引水口code
     */
    private String pCode;

    /**
     *上级引水口名称
     */
    private String pName;
}
