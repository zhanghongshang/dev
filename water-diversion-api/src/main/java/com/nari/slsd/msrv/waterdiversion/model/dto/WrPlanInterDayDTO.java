package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

import java.io.Serializable;
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
public class WrPlanInterDayDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * ID
     */
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
     * 水量
     */
    private BigDecimal waterQuantity;
    /**
     * 流量(m³/s)
     */
    private BigDecimal waterFlow;

}
