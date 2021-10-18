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
 * 日迭代
 * </p>
 *
 * @author zhs
 * @since 2021-08-10
 */
@Data
@TableName("WR_PLAN_ITER_DAY")
public class WrPlanInterDay implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 引水口ID
     */
    @TableField("BUILDING_ID")
    private String buildingId;
    /**
     * 时间
     */
    @TableField("SUPPLY_TIME")
    private Date supplyTime;
    /**
     * 水量
     */
    @TableField("WATER_QUANTITY")
    private BigDecimal waterQuantity;
    /**
     * 流量(m³/s)
     */
    @TableField("WATER_FLOW")
    private BigDecimal waterFlow;

}
