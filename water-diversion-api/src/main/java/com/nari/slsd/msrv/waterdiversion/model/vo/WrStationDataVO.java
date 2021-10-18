package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @Program: water-diversion
 * @Description:
 * @Author: reset kalar
 * @Date: 2021-09-01 10:52
 **/
@Data
public class WrStationDataVO {
    /**
     * 测站编码
     */
    private String stationCode;

    /**
     * 测站名称
     */
    private String stationName;

    /**
     * 测站数据
     */
    private List<WrDataVO> data;

    public WrStationDataVO() {
        data = new ArrayList<>();
    }
}
