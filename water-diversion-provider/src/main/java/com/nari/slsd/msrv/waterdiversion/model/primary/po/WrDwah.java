package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 *  配水指标
 * </p>
 *
 * @author zhs
 * @since 2021-08-23
 */
@Data
@TableName("WR_DWA_H")
public class WrDwah implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private String id;
    /**
     * 用水单位编码
     */
    @TableField("WATER_UNIT_CODE")
    private String planTaskId;

    /**
     * 水量指标
     */
    @TableField("WATER_CONSUME")
    private String waterUnitId;

    /**
     * 年份
     */
    @TableField("YEAR")
    private String manageUnitId;
    /**
     * 月份
     */
    @TableField("MONTH")
    private String buildingId;
    /**
     * 时间类型
     */
    @TableField("TIME_TYPE")
    private String planName;
}
