package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 任务流程 VO类
 * @Author ZHS
 * @Date 2021/8/30 10:27
 */
@Data
public class WrProcessInfoVO {

    private String processInstanceId;//流程实例ID

    private Long peceiveTime;//接收时间

    private Long processTime;//处理时间

    private String processDetail;//处理详情

    private String ProcessResult;//处理意见

    private String PersonId;//处理人ID

    private String PersonName;//处理人姓名

    private String agree;//是否同意

    private String nodeName;//节点名称

    private String TaskId;//任务id

    private String ext;//扩展字段

    private String takeUpTime;//操作耗时

}
