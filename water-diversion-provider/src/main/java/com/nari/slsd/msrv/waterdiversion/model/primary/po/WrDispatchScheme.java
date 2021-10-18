package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 配水调度方案表
 * </p>
 *
 * @author bigb
 * @since 2021-08-10
 */
@Data
@TableName("WR_DISPATCH_CALC")
public class WrDispatchScheme implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 方案ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private String id;

    /**
     * 方案编号（yyyyMMdd+4位数字递增）
     */
    @TableField("CODE")
    private String schemeCode;

    /**
     * 方案名称
     */
    @TableField("NAME")
    private String schemeName;

    /**
     * 执行开始时间
     */
    @TableField("START_TIME")
    private Date executeStartTime;

    /**
     * 执行结束时间
     */
    @TableField("END_TIME")
    private Date executeEndTime;

    /**
     * 操作人
     */
    @TableField("PERSON_ID")
    private String personId;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_DATE",fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 关联计划id
     */
    @TableField("PLAN_ID")
    private String plantId;

    /**
     * 扩展参数（json格式）
     */
    @TableField("PARAM")
    private String extParam;

}
