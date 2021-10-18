package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Created by ZHD
 * @program: SystemParamMeta
 * @description:系统参数字典表
 * @date: 2021/8/16 10:14
 */
@Data
@TableName("SYSTEM_PARAM_META")
public class SystemParamMeta implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "PARAM_ID")
    private String paramId;//参数ID

    @TableField(value = "PARAM_NAME")
    private String paramName;//参数名称

    @TableField(value = "PARAM_VALUE")
    private String paramValue;//参数值

    @TableField(value = "PARAM_GROUP")
    private String paramGroup;//参数分类

    @TableField(value = "PARAM_DESC")
    private String paramDesc;//参数描述

    @TableField(value = "SORTID")
    private Integer sortId;//排序

    @TableField(value = "IS_VALID")
    private Integer isValID;//1:有效 0：无效

    @TableField(value = "PARENT_ID")
    private String parentId;//

    @TableField(value = "IS_UPDATE")
    private Integer isUpdata;//是否可修改 0：否 1：是

    @TableField(value = "UPDATE_PERSON")
    private String updatePerson;//修改人

    @TableField(value = "UPDATE_TIME")
    private Date updateTime;//修改时间

    @TableField(value = "RELA_TEMPLATE")
    private String relaTemplate;//关联模板

}
