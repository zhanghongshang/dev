package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 用水计划任务关联表
 * </p>
 *
 * @author zhs
 * @since 2021-08-16
 */
@Data
@TableName("WR_PLAN_TASK_SUB")
public class WrPlanTaskSub implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private String id;

    /**
     * 任务id
     */
    @TableField("TASK_ID")
    private String taskId;
    /**
     * 单位类型
     */
    @TableField("UNIT_TYPE")
    private String unitType;
    /**
     * 管理单位ID
     */
    @TableField("UNIT_ID")
    private String unitId;
    /**
     * 单位名称
     */
    @TableField("UNIT_NAME")
    private String unitName;

}
