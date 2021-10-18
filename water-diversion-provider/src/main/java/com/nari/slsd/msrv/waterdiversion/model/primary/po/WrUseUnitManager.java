package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import lombok.Data;

/**
 * <p>
 * 用水单位管理
 * </p>
 *
 * @author reset kalar
 * @since 2021-07-29
 */
@Data
@TableName("WR_USE_UNIT_MANAGER")
public class WrUseUnitManager implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用水单位主键
     */
    @TableId(value = "ID", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 用水单位名称
     */
    @TableField("UNIT_NAME")
    private String unitName;

    /**
     * 建户时间
     */
    @TableField("HOUSES_TIME")
    private Date housesTime;

    /**
     * 状态 0：无效，1：有效
     */
    @TableField("STATE")
    private Integer state;

    /**
     * 父级用水单位
     */
    @TableField("PID")
    private String pid;

    /**
     * 编码 保证唯一性
     */
    @TableField("CODE")
    private String code;

    /**
     * 层级
     */
    @TableField("UNIT_LEVEL")
    private Integer unitLevel;

    /**
     * 全路径
     */
    @TableField("PATH")
    private String path;

}
