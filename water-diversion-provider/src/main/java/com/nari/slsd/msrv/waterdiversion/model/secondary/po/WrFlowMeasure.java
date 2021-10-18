package com.nari.slsd.msrv.waterdiversion.model.secondary.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 断面实测数据表
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-05
 */
@Data
//@EqualsAndHashCode(callSuper = false)
@TableName("WR_FLOW_MEASURE")
public class WrFlowMeasure implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 测站ID
     */
    @TableField("STATIONID")
    private String stationId;

    /**
     * 实测时间
     */
    @TableField("TIME")
    private Date time;

    /**
     * 水位
     */
    @TableField("WATER_LEVEL")
    private Double waterLevel;

    /**
     * 流量
     */
    @TableField("FLOW")
    private Double flow;

    /**
     * 录入时间
     */
    @TableField("UPDATE_TIME")
    private Date updateTime;

    /**
     * 操作人员id
     */
    @TableField("PERSON_ID")
    private String personId;

    /**
     * 备注
     */
    @TableField("REMARK")
    private String remark;

}
