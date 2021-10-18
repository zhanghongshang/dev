package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

import java.util.List;

/**
 * 计划填报保存数据
 */
@Data
public class WrPlanFillInDayAllDTO {
    //申请人
    private String userId;

    //申请人名称
    private String userName;

    //所属管理单位
    private  String mngUnitId;

    //所属管理单位名称
    private  String mngUnitName;

    //所属用水单位
    private String waterUnitId;

    //所属县市师团用水单位
    private String xsUseId;

    //所属县市师团用水单位名称
    private String xsUseName;

    //所属县市师团用水单位（借出）
    private String xsUseIdOut;

    //所属县市师团用水单位（借出）名称
    private String xsUseIdOutName;

    //所属用水单位名称
    private String waterUnitName;

    //内容
    private String content;

    //借出月份（，分割）
    private String months;

    //0 月内 1 月内（多引水口） 2跨月 3超年
    private String type;

    //附件
    private String res;

    //水权交易id
    private String tradeId;

    //借调
    private List<LendInDTO> lendIns;

    //借出（月内借出、超年借出）
    private List<LendOutDTO> lendOuts;

    //跨月借出
    private List<LendOutSpanMonthsDTO> spanMonthLendOuts;

}
