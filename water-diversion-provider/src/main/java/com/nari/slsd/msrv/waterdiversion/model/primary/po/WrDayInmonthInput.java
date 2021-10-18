package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 逐日水情输入表
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-21
 */
@Data
@TableName("WR_DAY_INMONTH_INPUT")
public class WrDayInmonthInput implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "ID")
    private String id;

    /**
     * 引水口编码
     */
    @TableField("STATION_ID")
    private String stationId;

    /**
     * 时间类型 1:年，2:月,3:旬，4:日
     */
    @TableField("TIME_TYPE")
    private Integer timeType;

    /**
     * 数据时间
     */
    @TableField("TIME")
    private Date time;

    /**
     * 水位
     */
    @TableField("WATER_LEVEL")
    private Double waterLevel;

    /**
     * 流量(m³/s)
     */
    @TableField("WATER_FLOW")
    private Double waterFlow;

    /**
     * 水量
     */
    @TableField("WATER_QUANTITY")
    private Double waterQuantity;

    /**
     * 人工0/自动1
     */
    @TableField("AUTO")
    private Integer auto;

    /**
     * 输入时间
     */
    @TableField("OPERATE_TIME")
    private Date operateTime;

    /**
     * 输入人
     */
    @TableField("OPERATOR_ID")
    private String operatorId;

    /**
     * 校核时间
     */
    @TableField("APPROVE_TIME")
    private Date approveTime;

    /**
     * 校核人
     */
    @TableField("APPROVE_ID")
    private String approveId;

    /**
     * 状态  2已审核未校核 3已校核
     */
    @TableField("STATUS")
    private Integer status;

}


