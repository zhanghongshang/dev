package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @title
 * @description 调令管理dto
 * @author bigb
 * @updateTime 2021/8/29 11:18
 * @throws
 */
@Data
public class WrCmdManagerOperateDto {

    /**
     * id
     */
    private String id;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 调令编号
     */
    private String orderCode;

    /**
     * 调令名称
     */
    private String orderName;

    /**
     * 调令类型
     */
    private String orderType;

    /**
     * 调令状态
     */
    private String orderStatus;

    /**
     * 调令内容
     */
    private String orderContent;

    /**
     * 年份
     */
    private String year;

    /**
     * 编制人
     */
    private String launchName;

    /**
     * 审批人
     */
    private String approveName;

    /**
     * 审批意见
     */
    private String approveContent;

    /**
     * 查询开始时间
     */
    private Long orderStartTime;

    /**
     * 查询结束时间
     */
    private Long orderEndTime;

    /**
     * 管理站id
     */
    private List<String> manageUnitIdList;

    /**
     * 管理站名称
     */
    private String manageUnitName;

    /**
     * 附件
     */
    private String attach;

    /**
     * 个人待办
     */
    private boolean queryMine;

    /**
     * 调度指令集
     */
    private List<WrDispatchInstructionDto> instructionDtoList;

}
