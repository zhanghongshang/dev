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
     * 水工建筑物编号
     */
    private String id;

    /**
     * 水工建筑物名称
     */
    private String buildingName;

    /**
     * 水工建筑物类型
     */
    private String buildingType;

    /**
     * 站点属性
     */
    private Integer siteType;

    /**
     * 采集方式
     * 1.人工
     * 2.自动
     */
    private Integer collectType;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 是否为生态用水
     */
    private Integer ifEcological;

    /**
     * 所属用水单位编码
     */
    private String waterUnitId;

    /**
     * 所属管理单位id
     */
    private String mngUnitId;

    /**
     * 备注
     */
    private String remark;


}
