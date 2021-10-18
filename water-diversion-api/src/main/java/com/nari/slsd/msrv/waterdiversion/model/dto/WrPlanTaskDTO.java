package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 用水计划任务
 * </p>
 *
 * @author zhs
 * @since 2021-08-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrPlanTaskDTO {

    /**
     * 单位id
     */
    private String unitId;
    /**
     * 操作人
     */
    private String personId;
    /**
     * 发起时间
     */
    private Date createDate;
    /**
     * 状态
     */
    private String state;
    /**
     * 流程实例ID
     */
    private String waterPlanFillIn;


}
