package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

/**
 * @author Created by ZHD
 * @program: WrRightTradeDTO
 * @description:水权交易交互实体类
 * @date: 2021/8/17 10:51
 */
@Data
public class WrRightTradeDTO {
    private String id;//主键id

    private String uniqueCode;//水权交易编码

    private String buyer;//买入方

    private String buyerName;//买入方名称

    private String saler;//卖出方

    private String salerName;//卖出方名称

    private Long tradeTime;//交易时间

    private String year;//年份

    private Double waterAmount;//交易水量

    private Double waterMoney;//交易金额

    private String recorder;//录入人

    private String recorderName;//录入人名称

    private Long recorderTime;//录入时间

    private String res;//附件资源

    private Integer status=1;//0无效 1有效
}
