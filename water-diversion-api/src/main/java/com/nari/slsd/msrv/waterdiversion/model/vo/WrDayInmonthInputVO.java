package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 逐日水情输入表 VO
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrDayInmonthInputVO {

    /**
     * 主键ID
     */
    private String id;

//    /**
//     * 引水口ID
//     */
//    private String stationId;

    /**
     * 数据时间
     */
    private Long time;

    /**
     * 流量(m³/s)
     */
    private Double waterFlow;

}


