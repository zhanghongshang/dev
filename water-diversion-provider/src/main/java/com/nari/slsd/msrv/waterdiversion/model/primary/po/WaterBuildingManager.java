package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>
 * 水工建筑物管理
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-03
 */
@Data
@TableName("WATER_BUILDING_MANAGER")
public class WaterBuildingManager implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 水工建筑物编号
     */
    @TableId(value = "ID", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 水工建筑物名称
     */
    @TableField("BUILDING_NAME")
    private String buildingName;

    /**
     * 所属用水单位编码
     */
//    @TableField("WATER_UNIT_ID")
//    private String waterUnitId;

    /**
     * 上级
     */
//    @TableField("PID")
//    private String pid;

    /**
     * 水工建筑物类型
     */
    @TableField("BUILDING_TYPE")
    private String buildingType;

    /**
     * 是否填报
     * 0 否
     * 1 是
     */
    @TableField("FILL_REPORT")
    private Integer fillReport;

//    @TableField("SORT")
//    private Integer sort;

    /**
     * 所属管理单位id
     */
//    @TableField("MNG_UNIT_ID")
//    private String mngUnitId;

//    /**
//     *
//     */
//    @TableField("SOURCE_ID_F")
//    private String sourceIdF;
//
//    /**
//     *
//     */
//    @TableField("SOURCE_ID_S")
//    private String sourceIdS;

    /**
     * GIS坐标 [经度,纬度]
     */
    @TableField("LATLNG_F")
    private String latlngF;

    /**
     * 拓扑坐标 [经度,纬度]
     */
    @TableField("LATLNG_S")
    private String latlngS;

}
