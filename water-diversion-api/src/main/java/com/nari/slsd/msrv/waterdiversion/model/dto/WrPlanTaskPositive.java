package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

/**
 * @Description 流程审批dto类
 * @Author ZHS
 * @Date 2021/9/18 12:24
 */
@Data
public class WrPlanTaskPositive {

    private String processId;

    private String taskId;

    private String userId;

    private String userName;

    private String handleType;

    private String type;

    private String content;

    private String nodeFlag;

    private String batchState;

}
