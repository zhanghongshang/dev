package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @title
 * @description 日水情累计流量
 * @author bigb
 * @updateTime 2021/9/25 1:00
 * @throws
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleWrDayInputVO {
    /**
     * 引水口编码
     */
    private String stationId;

    /**
     * 流量(m³/s)
     */
    private Double waterFlow;
}
