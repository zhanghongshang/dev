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
 * 配水方案结果表
 * </p>
 *
 * @author bigb
 * @since 2021-08-12
 */
@Data
@TableName("WR_DISPATCH_CALC_RESULT")
public class WrDispatchSchemeResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private String id;

    /**
     * 方案id
     */
    @TableField("SCHEME_ID")
    private String schemeId;

    /**
     * 引水口id
     */
    @TableField("BUILDING_ID")
    private String buildingId;

    /**
     * 设备id
     */
    @TableField("OBJECT_ID")
    private String objectId;

    /**
     * 执行开始时间
     */
    @TableField("START_TIME")
    private Date execStartTime;

    /**
     * 测点号
     */
    @TableField("SENID")
    private String senId;

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

}
