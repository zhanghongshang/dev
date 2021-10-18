package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 配水调度指令表
 * </p>
 *
 * @author bigb
 * @since 2021-08-10
 */
@Data
@TableName("WR_DISPATCH_CMD")
public class WrDispatchInstruction implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private String id;

    /**
     * 方案ID
     */
    @TableField("SCHEME_ID")
    private String schemeId;

    /**
     * 指令管理ID
     */
    @TableField("ORDER_ID")
    private String cmdManagerId;

    /**
     * 引水口id
     */
    @TableField("BUILDING_ID")
    private String buildingId;

    /**
     * 引水口名称
     */
    @TableField("BUILDING_NAME")
    private String buildingName;

    /**
     * 设备id
     */
    @TableField("OBJECT_ID")
    private String objectId;

    /**
     * 执行开始时间
     */
    @TableField("START_TIME")
    private Date startTime;

    /**
     * 执行结束时间
     */
    @TableField("END_TIME")
    private Date endTime;

    /**
     * 模型号
     */
    @TableField("MODEL_ID")
    private String modelId;

    /**
     * 测点号
     */
    @TableField("SENID")
    private String senId;

    /**
     * 原值
     */
    @TableField(value = "SOURCE_VALUE",numericScale = "4")
    private Double sourceValue;

    /**
     * 目标值
     */
    @TableField(value = "SET_VALUE",numericScale = "4")
    private Double setValue;

    /**
     * 调整时序
     */
    @TableField("ADJUST_ORDER")
    private Long adjustOrder;

    /**
     * 完成后等待时间
     */
    @TableField("WAIT_TIME")
    private Long waitTime;

    /**
     * 状态
     * 0：取消（包括重算取消）；1：待审批，2：已审批，3：无需审批，等待下发；4：已下发；5：结束操作
     */
    @TableField("STATUS")
    private String status;

    /**
     * 下发时间
     */
    @TableField("SEND_TIME")
    private Date sendTime;

    /**
     * 结束时间
     */
    @TableField("FINISH_TIME")
    private Date finishTime;

    /**
     * 调整后目标值
     */
    @TableField(value = "MODIFY_VALUE",numericScale = "4")
    private Double modifyValue;

    /**
     * 下发人
     */
    @TableField("PERSON_ID")
    private String personId;

    /**
     * 指令类型
     * 0：人工，1：需水计划 ；2：事故 3：检修
     */
    @TableField("COMMAND_TYPE")
    private String commandType;

    /**
     * 流程id
     */
    @TableField("PROCESS_ID")
    private String processId;

    /**
     * 有效标识
     */
    @TableLogic(value = "1", delval = "0")
    @TableField(value = "ACTIVE_FLAG",fill = FieldFill.INSERT)
    private Integer activeFlag;
}
