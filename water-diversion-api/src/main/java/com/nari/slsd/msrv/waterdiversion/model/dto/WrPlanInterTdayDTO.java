package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 旬月迭代
 * </p>
 *
 * @author zhs
 * @since 2021-08-10
 */
@Data
public class WrPlanInterTdayDTO {

    private String id;

    /**
     * 引水口ID
     */
    private String buildingId;
    /**
     * 时间
     */
    private Date supplyTime;
    /**
     * 时间类别
     */
    private String timeType;
    /**
     * 水量
     */
    private BigDecimal waterQuantity;
    /**
     * 流量(m³/s)
     */
    private BigDecimal waterFlow;

}
