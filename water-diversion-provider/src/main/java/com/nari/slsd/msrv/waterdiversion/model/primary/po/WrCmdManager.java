package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 调度指令管理表
 * </p>
 *
 * @author bigb
 * @since 2021-08-27
 */
@Data
@TableName("WR_CMD_MANAGER")
public class WrCmdManager implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private String id;

    /**
     * 方案id
     */
    @TableField(value = "SCHEME_ID")
    private String schemeId;

    /**
     * 调令编号
     */
    @TableField(value = "ORDER_CODE")
    private String orderCode;

    /**
     * 调令名称
     */
    @TableField(value = "ORDER_NAME")
    private String orderName;

    /**
     * 调令类型
     */
    @TableField(value = "ORDER_TYPE")
    private String orderType;

    /**
     * 调令状态
     */
    @TableField(value = "ORDER_STATUS")
    private String orderStatus;

    /**
     * 调令内容
     */
    @TableField(value = "ORDER_CONTENT")
    private String orderContent;

    /**
     * 年份
     */
    @TableField(value = "YEAR")
    private String year;

    /**
     * 编制人
     */
    @TableField(value = "LAUNCH_NAME")
    private String launchName;

    /**
     * 审批人
     */
    @TableField(value = "APPROVE_NAME")
    private String approveName;
    /**
     * 审批意见
     */
    @TableField(value = "APPROVE_CONTENT")
    private String approveContent;


    /**
     * 调令下审批时间
     */
    @TableField(value = "ORDER_TIME")
    private Date orderTime;

    /**
     * 管理站id
     */
    @TableField(value = "MANAGE_UNIT_ID")
    private String manageUnitId;

    /**
     * 附件
     */
    @TableField(value = "ATTACH")
    private String attach;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME",fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "UPDATE_TIME",fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 有效标识
     */
    @TableLogic(value = "1", delval = "0")
    private Integer activeFlag;

}
