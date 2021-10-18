package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;
/**
 * @Description 流程审批dto类
 * @Author ZHS
 * @Date 2021/9/2 10:54
 */
/**
 * @Description 审批 dto类
 * @Author ZHS
 * @Date 2021/9/1 11:55
 */
@Data
public class ActivitiApproval {

    //流程审批：positive通过 negative拒绝
    private String handleType;

    private String expression;

    //流程id
    private String processId;

    //操作人信息
    private Map<String,Object> userInfo;

    //固定传promng
    private String src;

    //管理站信息
    private Map<String,Object> param;

    //任务id
    private String planId;

    //处理意见
    private String processResult;

    //排他网关  "flag": 1//  "flag": 2
    private Map<String,Object> variables;

}
