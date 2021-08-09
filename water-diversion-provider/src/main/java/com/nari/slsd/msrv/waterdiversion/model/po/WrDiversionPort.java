package com.nari.slsd.msrv.waterdiversion.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 测站-引水口管理
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-03
 */
@Data
//@EqualsAndHashCode(callSuper = false)
@TableName("WR_DIVERSION_PORT")
public class WrDiversionPort implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 水工建筑物编号
     */
    @TableId(value = "ID", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 站点属性
     * TODO
     */
    @TableField("SITE_TYPE")
    private Integer siteType;

    /**
     * 采集方式
     * 1.人工
     * 2.自动
     */
    @TableField("COLLECT_TYPE")
    private Integer collectType;

    /**
     * 经度
     */
    @TableField("LONGITUDE")
    private String longitude;

    /**
     * 纬度
     */
    @TableField("LATITUDE")
    private String latitude;

    /**
     * 是否为生态用水
     */
    @TableField("IF_ECOLOGICAL")
    private Integer ifEcological;
    /**
     * 备注
     */
    @TableField("REMARK")
    private String remark;


}
