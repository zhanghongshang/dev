package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用水计划任务
 * </p>
 *
 * @author zhs
 * @since 2021-08-13
 */
@Data
@TableName("WR_PLAN_TASK")
public class WrPlanTask implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private String id;

    /**
     * 任务名称
     */
    @TableField("TASK_NAME")
    private String taskName;
    /**
     * 计划类型
     */
    @TableField("PLAN_TYPE")
    private String planType;
    /**
     * 计划开始日期
     */
    @TableField("START_DATE")
    private Date startDate;
    /**
     * 计划结束日期
     */
    @TableField("END_DATE")
    private Date endDate;
    /**
     * 内容
     */
    @TableField("CONTENT")
    private String content;
    /**
     * 操作人
     */
    @TableField("PERSON_ID")
    private String personId;
    /**
     * 发起时间
     */
    @TableField("CREATE_DATE")
    private Date createDate;
    /**
     * 流程实例ID
     */
    @TableField("WATER_PLAN_FILL_IN")
    private String waterPlanFillIn;
    /**
     * 状态
     */
    @TableField("STATE")
    private String state;
    /**
     * 总水量(m³)
     */
    @TableField("TOTAL_WATER_QUANTITY")
    private BigDecimal totalWaterQuantity;
    /**
     * 年份
     */
    @TableField("YEAR")
    private String year;
    /**
     * 月份
     */
    @TableField("MONTH")
    private String month;

    @TableField("FILL_TYPE")
    private String fillType;
    /**
     * 近期填报表子类型 0.月内 1月内（跨引水口） 2 跨月 3超年
     */
    @TableField("SUB_TYPE")
    private String subType;

    @TableField(exist = false)
    private List<WrPlanTaskSub> wrPlanTaskSubList;

}
