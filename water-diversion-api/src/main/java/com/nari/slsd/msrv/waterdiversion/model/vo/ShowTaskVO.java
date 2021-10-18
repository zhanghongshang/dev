package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Program: water-diversion
 * @Description: 流程节点信息vo类
 * @Author: zhs
 * @Date: 2021-08-26
 **/
@Data
public class ShowTaskVO {

    private String id;
    private String taskDefinitionKey;
    private String name;
    private Long createTime;
    private String assignee;
    private String candidate;
    private String group;
    private String processInstanceId;
    private String processDefinitionId;
    private String description;
    private String category;
    private String formKey;
    private String userName;
    private String reason;
    private String urlpath;
}
