package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * @author Created by ZHD
 * @program: WrRightTrade
 * @description:水权交易表
 * @date: 2021/8/17 10:15
 */
@Data
@TableName("WR_RIGHT_TRADE")
public class WrRightTrade {

    @TableId(value = "ID")
    private String id;//主键id

    @TableField(value = "UNIQUE_CODE")
    private String uniqueCode;//水权交易编码

    @TableField("BUYER")
    private String buyer;//买入方（用水单位）

    @TableField("SALER")
    private String saler;//卖出方（用水单位）

    @TableField("TRADE_TIME")
    private Date tradeTime;//交易时间

    @TableField("YEAR")
    private String year;//年份

    @TableField("WATER_AMOUNT")
    private Double waterAmount;//交易水量

    @TableField("WATER_MONEY")
    private Double waterMoney;//交易金额

    @TableField("RECORDER")
    private String recorder;//录入人

    @TableField("RECORD_TIME")
    private Date recorderTime;//录入时间

    @TableField("RES")
    private String res;//附件资源

    @TableField(value = "STATUS")
    private Integer status;//0无效 1有效 2已填报

    @TableField(value = "BUYER_NAME")
    private String buyerName;//买入方（用水单位名称）

    @TableField(value = "SALER_NAME")
    private String salerName;//卖出方（用水单位名称）

}
