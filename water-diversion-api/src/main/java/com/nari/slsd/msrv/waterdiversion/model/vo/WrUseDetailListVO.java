package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @title
 * @description 水量使用情况
 * @author bigb
 * @updateTime 2021/9/26 17:46
 * @throws
 */
@Data
public class WrUseDetailListVO {
    /**
     * 引水口id
     */
    private String buildingId;

    /**
     * 开始时间
     */
    private Date startDate;

    /**
     * 结束时间
     */
    private Date endDate;

    /**
     * 总结余
     */
    private BigDecimal totalDifference;

    private List<WrUseDetailVO> voList;
}
