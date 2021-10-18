package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Created by ZHD
 * @program: WR_DAY_INPUT
 * @description:日水情输入表
 * @date: 2021/8/17 9:21
 */
@Data
@TableName("WR_DAY_INPUT")
public class WrDayInput implements Serializable {

    private static final long serialVersionUID = 1L;


    @TableId(value = "ID")
    private String id;//主键id

    @TableField("STATION_ID")
    private String stationId;//引水口编码

    @TableField("TIME_TYPE")
    private Integer timeType;//时间类型 1:年，2:月,3:旬，4:日

    @TableField("TIME")
    private Date time;//数据时间

    @TableField("WATER_LEVEL")
    private Double waterLevel;//自动水位

    @TableField("WATER_FLOW")
    private Double waterFlow;//自动流量(m³/s)

    @TableField("MANUAL_WATER_LEVEL")
    private Double manualWaterLevel;//手动水位

    @TableField("MANUAL_WATER_FLOW")
    private Double manualWaterFlow;//手动流量

    @TableField("WATER_QUANTITY")
    private Double waterQuantity;//水量

    @TableField("SHARE_FACTOR")
    private Double shareFactor;//公摊系数

    @TableField("AUTO")
    private Integer auto;//人工0/自动1

    @TableField("OPERATE_TIME")
    private Date operateTime;//输入时间

    @TableField("OPERATOR_ID")
    private String operatorId;//输入人

    @TableField("OPERATOR_REMARK")
    private String operatorRemark;//输入备注

    @TableField("APPROVE_TIME")
    private Date approveTime;//审批时间

    @TableField("APPROVE_ID")
    private String approveId;//审批人

    @TableField("APPROVE_REMARK")
    private String approveRemark;//审批备注

    @TableField("STATUS")
    private Integer status;//状态

}


