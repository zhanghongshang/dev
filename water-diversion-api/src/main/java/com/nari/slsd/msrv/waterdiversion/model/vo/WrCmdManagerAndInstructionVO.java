package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @title
 * @description 调令管理VO
 * @author bigb
 * @updateTime 2021/8/29 11:18
 * @throws
 */
@Data
public class WrCmdManagerAndInstructionVO {

    /**
     * ID
     */
    private String id;

    /**
     * SCHEME_ID
     */
    private String schemeId;

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
     * 调令下达时间
     */
    private Long orderTime;

    /**
     * 管理站名称
     */
    private String manageUnitName;

    /**
     * 附件
     */
    private String attach;

    /**
     * 调令下达时间
     */
    private Long updateTime;

    private List<WrDispatchInstructionVO> voList;

}
