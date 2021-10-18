package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

/**
 * @title
 * @description 调令管理dto
 * @author bigb
 * @updateTime 2021/8/29 11:18
 * @throws
 */
@Data
public class WrCmdManagerDTO {
    /**
     * ID
     */
    private String id;
    /**
     * 引水口id
     */
    private String buildingId;
    /**
     * 引水口名称
     */
    private String buildingName;

    /**
     * 调令类型
     */
    private String orderType;

    /**
     * 调整前流量
     */
    private Double waterFlowBefore;

    /**
     * 调整后流量
     */
    private Double waterFlowAfter;

    /**
     * 调令执行开始时间
     */
    private Long executeStartTime;

    /**
     * 调令执行结束时间
     */
    private Long executeEndTime;

    /**
     * 调令内容
     */
    private String orderContent;

    /**
     * 编制人
     */
    private String launchName;

    /**
     * 管理站id
     */
    private String manageUnitId;

    /**
     * 管理站名称
     */
    private String manageUnitName;

    /**
     * 附件
     */
    private String attach;

}
