package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @Program: water-diversion
 * @Description: 数据VO
 * @Author: reset kalar
 * @Date: 2021-09-01 11:00
 **/
@Data
public class WrDataVO {
    /**
     * 数据时间
     */
    private Long time;

    private Map<String, Double> data;

    public WrDataVO() {
        data = new HashMap<>();
    }
}
