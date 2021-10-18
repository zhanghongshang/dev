package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Description 审批人信息
 * @Author ZHS
 * @Date 2021/9/1 16:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudioProcLog {
    //操作人信息
    private String creator;

    //操作时间
    private Date createdAt;

    //节点名称
    private String handleAtNodeName;

    //操作人id
    private String creatorId;

}
