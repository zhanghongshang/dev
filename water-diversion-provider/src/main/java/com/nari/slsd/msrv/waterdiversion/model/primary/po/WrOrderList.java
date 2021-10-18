package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 配水调度方案表
 * </p>
 *
 * @author bigb
 * @since 2021-08-10
 */
@Data
@TableName("ORDER_LIST")
public class WrOrderList implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 指令唯一识别码
     */
    @TableId(value = "UUID", type = IdType.INPUT)
    private String id;

    /**
     * 指令生成时间
     */
    @TableField("CREATETIME")
    private Date createTime;

    /**
     * 指令类型
     */
    @TableField("ORDERTYPE")
    private Integer orderType;

    /**
     * 开始时间
     */
    @TableField("STARTTIME")
    private Date startTime;

    /**
     * 时间测点id
     */
    @TableField("TIMEID")
    private Long timeId;

    /**
     * 控制测点id
     */
    @TableField("CTRLID")
    private Long ctrlId;

    /**
     * 控制测值
     */
    @TableField(value = "CTRLVAL",numericScale = "3")
    private Double ctrlVal;

    /**
     * 消息类型
     */
    @TableField("MSGTYPE")
    private String msgType;

    /**
     * 自定义提示语
     */
    @TableField("CTRLMSG")
    private String ctrlMsg;

    /**
     * 自定义提示语单位
     */
    @TableField("CTRLMSGUNIT")
    private String ctrlMsgUnit;

    /**
     * 指令状态
     */
    @TableField("PROCESSNUBER")
    private Integer processNuber;

    /**
     * 人工确认时间
     */
    @TableField("EXETIME")
    private Date exeTime;

    /**
     * 控制确认点号
     */
    @TableField("CTRLCONFORMID")
    private Long ctrlConformId;

    /**
     * 控制确认值
     */
    @TableField(value = "CTRLCONFORMVAL",numericScale = "3")
    private Double ctrlConformVal;

    /**
     * 控制对象点号
     */
    @TableField("CTRLOBJID")
    private Long ctrlObjId;

    /**
     * 控制对象号值
     */
    @TableField(value = "CTRLOBJVAL",numericScale = "3")
    private Double ctrlObjVal;

    /**
     * 控制性质点号
     */
    @TableField("CTRLTYPEID")
    private Long ctrlTypeId;

    /**
     * 控制性质值
     */
    @TableField(value = "CTRLTYPEVAL",numericScale = "3")
    private Double ctrlTypeVal;

    /**
     * 备份
     */
    @TableField("BAK")
    private String bak;

}
