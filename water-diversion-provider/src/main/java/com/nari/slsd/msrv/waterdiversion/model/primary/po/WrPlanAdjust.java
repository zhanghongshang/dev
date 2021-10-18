package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhs
 * @program: WR_DAY_INPUT
 * @description: 调整计划
 * @date: 2021/8/20
 */
@Data
@TableName("WR_PLAN_ADJUST")
public class WrPlanAdjust implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID")
    private String id;//主键id

    @TableField("CREATE_TIME")
    private Date createTime;//发起时间

    @TableField("MNG_UNIT_ID")
    private String mngUnitId;//用水单位ID

    @TableField("MNG_UNIT_NAME")
    private String mngUnitName;//用水单位名称

    @TableField("PERSON_ID")
    private String personId;//发起人ID

    @TableField("PERSON_NAME")
    private String personName;//发起人名称

    @TableField("ADJUST_TYPE")
    private String adjustType;//调整类别

    @TableField("START_TIME")
    private Date startTime;//调整开始时间

    @TableField("END_TIME")
    private Date endTime;//调整结束时间

    @TableField("CONTENT")
    private String content;//内容

    @TableField("STATE")
    private String state;//状态

    @TableField("RES")
    private String res;//附件资源

}


