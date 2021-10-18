package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhs
 * @program: WR_DAY_INPUT
 * @description: 调整计划
 * @date: 2021/8/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrPlanAdjustVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;//主键id

    private Long createTime;//用水单位ID

    private String mngUnitId;//用水单位ID

    private String mngUnitName;//用水单位名称

    private String personId;//发起人ID

    private String personName;//发起人名称

    private String adjustType;//调整类别

    private Long startTime;//调整开始时间

    private Long endTime;//调整结束时间

    private String content;//内容

    private String state;//状态

}


