package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author bigb
 * */
@Getter
@Setter
public class WrDispatchInstructionVO {

    /**
     * ID
     */
    private String id;

    /**
     * 方案ID
     */
    private String schemeId;

    /**
     * 指令管理ID
     */
    private String cmdManagerId;

    /**
     * 引水口id
     */
    private String buildingId;

    /**
     * 引水口名称
     */
    private String buildingName;

    /**
     * 设备id
     */
    private String objectId;

    /**
     * 执行开始时间
     */
    private Long startTime;

    /**
     * 模型号
     */
    private String modelId;

    /**
     * 测点号
     */
    private String senId;

    /**
     * 目标值
     */
    private Double setValue;

    /**
     * 调整时序
     */
    private Long adjustOrder;

    /**
     * 完成后等待时间
     */
    private Long waitTime;

    /**
     * 状态
     * 0：取消（包括重算取消）；1：待审批，2：已审批，3：无需审批，等待下发；4：已下发；5：结束操作
     */
    private String status;

    /**
     * 下发时间
     */
    private Long sendTime;

    /**
     * 结束时间
     */
    private Long finishTime;

    /**
     * 调整后目标值
     */
    private Double modifyValue;

    /**
     * 下发人
     */
    private String personId;

    /**
     * 指令类型
     * 0：人工，1：需水计划 ；2：事故 3：检修
     */
    private String commandType;

    /**
     * 流程id
     */
    private String processId;
}
