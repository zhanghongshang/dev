package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 测站-引水口管理
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-03
 */
@Data
@TableName("WR_DIVERSION_PORT")
public class WrDiversionPort implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 水工建筑物编号
     */
    @TableId(value = "ID", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 测站引水口编码
     */
    @TableField("BUILDING_CODE")
    private String buildingCode;

    /**
     * 站点属性
     * 0 水情点
     * 1 工情点
     */
    @TableField("SITE_TYPE")
    private Integer siteType;

    /**
     * 采集方式
     * 0.人工
     * 1.自动
     */
    @TableField("COLLECT_TYPE")
    private Integer collectType;

//    /**
//     * 经度（不使用）
//     */
//    @TableField("LONGITUDE")
//    private String longitude;
//
//    /**
//     * 纬度（不使用）
//     */
//    @TableField("LATITUDE")
//    private String latitude;

    /**
     * 是否为生态用水
     * 1 是
     * 0 否
     */
    @TableField("IF_ECOLOGICAL")
    private Integer ifEcological;

    /**
     * 备注
     */
    @TableField("REMARK")
    private String remark;

    /**
     * 所属用水单位编码
     */
    @TableField("WATER_UNIT_ID")
    private String waterUnitId;

    /**
     * 所属管理单位id
     */
    @TableField("MNG_UNIT_ID")
    private String mngUnitId;

    /**
     * 公摊系数
     */
    @TableField("SHARE_FACTOR")
    private Double shareFactor;

    /**
     * 用水性质
     * 1.工业
     * 2.农业
     */
    @TableField("WATER_NATURE")
    private Integer waterNature;

    /**
     * 是否长期
     * 0否
     * 1是
     */
    @TableField("IF_LONG_TERM")
    private Integer ifLongTerm;

    /**
     * 是否可远控
     * 0否(默认)
     * 1是
     */
    @TableField("IF_REMOTE_CONTROL")
    private Integer ifRemoteControl;

    /**
     * 费率管理
     */
    @TableField("RATE_MANAGER")
    private Double rateManager;

    /**
     * 引水口层级
     * 1 一级
     * 2 二级
     * 0 一二级共用
     */
    @TableField("BUILDING_LEVEL")
    private Integer buildingLevel;

    /**
     * 是否为公共引水口
     * 0 否
     * 1 是
     */
    @TableField("IF_PUBLIC")
    private Integer ifPublic;

    /**
     * 上级引水口
     */
    @TableField("PID")
    private String pid;

}
