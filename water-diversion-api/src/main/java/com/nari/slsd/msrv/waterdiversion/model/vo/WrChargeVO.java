package com.nari.slsd.msrv.waterdiversion.model.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * gis实时调度
 * </p>
 *
 * @author bigb
 * @since 2021-08-11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WrChargeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private String id;

    /**
     * 缴费记录编号
     * 2021082200000001
     */
    private String recordCode;

    /**
     *用水单位id
     */
    private String waterUnitId;

    /**
     * 年份
     */
    private String year;

    /**
     * 缴费类型
     */
    private String feeType;

    /**
     * 实收金额
     */
    private Double realAmount;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 收费人
     */
    private String personId;

    /**
     * 备注
     */
    private String remark;


}
