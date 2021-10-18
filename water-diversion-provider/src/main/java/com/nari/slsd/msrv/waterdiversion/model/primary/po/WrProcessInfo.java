package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
/**
 * @Description 任务流程实体类
 * @Author ZHS
 * @Date 2021/10/8 16:26
 */
@Data
@TableName("WR_PROCESS_INFO")
public class WrProcessInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.INPUT)
    private String Id;//主键ID

    @TableField(value = "PROCESS_INSTANCE_ID")
    private String processInstanceId;//流程实例ID

    @TableField(value = "RECEIVE_TIME")
    private Date peceiveTime;//接收时间

    @TableField(value = "PROCESS_TIME")
    private Date processTime;//处理时间

    @TableField(value = "PROCESS_DETAIL")
    private String processDetail;//处理详情

    @TableField(value = "PROCESS_RESULT")
    private String processResult;//处理意见

    @TableField(value = "PERSON_ID")
    private String personId;//处理人ID

    @TableField(value = "PERSON_NAME")
    private String personName;//处理人姓名

    @TableField(value = "AGREE")
    private String agree;//是否同意

    @TableField(value = "NODE_NAME")
    private String nodeName;//节点名称

    @TableField(value = "TASK_ID")
    private String taskId;//任务id

    @TableField(value = "EXT")
    private String ext;//扩展字段

}
