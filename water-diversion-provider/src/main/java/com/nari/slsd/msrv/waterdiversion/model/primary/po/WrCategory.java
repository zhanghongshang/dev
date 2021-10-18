package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用水性质管理
 * </p>
 *
 * @author bigb
 * @since 2021-08-10
 */
@Data
@TableName("WR_CATEGORY_MANAGE")
public class WrCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private String id;

    /**
     * 用水性质唯一编码
     */
    @TableField(value = "CATEGORY_CODE")
    private String categoryCode;

    /**
     * 用水性质名称
     */
    @TableField(value = "CATEGORY_NAME")
    private String categoryName;

    /**
     * 用水类型编码
     */
    @TableField("WATER_TYPE")
    private String waterTypeCode;

    /**
     * 用水类型名称
     */
    @TableField("WATER_TYPE_NAME")
    private String waterTypeName;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME",fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "UPDATE_TIME",fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 用户id
     */
    @TableField("PERSON_ID")
    private String personId;

    /**
     * 用户名称
     */
    @TableField("PERSON_NAME")
    private String personName;

    /**
     * 有效标识
     */
    @TableLogic
    @TableField(value = "ACTIVE_FLAG",fill = FieldFill.INSERT)
    private Integer activeFlag;
}
