package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
/**
 * @Description 流程提交 dto类
 * @Author ZHS
 * @Date 2021/9/2 10:53
 */
@Data
public class ActivitiHandle {

    //流程审批：positive通过 negative拒绝
    private String handleType;

    private String expression;

    //流程id
    private String flowKey;

    //操作人信息
    private Map<String,Object> userInfo;

    //固定传promng
    private String src;

    //管理站信息
    private Map<String,Object> param;

    //排他网关 "flag": 1//  "flag": 2
    private Map<String,Object> variables;
}
