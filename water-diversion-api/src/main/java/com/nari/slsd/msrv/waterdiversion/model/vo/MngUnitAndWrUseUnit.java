package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Program: water-diversion
 * @Description: 管理单位-用水单位
 * @Author: reset kalar
 * @Date: 2021-08-13 10:12
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MngUnitAndWrUseUnit {
    /**
     * 管理单位ID
     */
    private String mngUnitId;
    /**
     * 管理单位名称
     */
    private String mngUnitName;

    /**
     * 用水单位
     */
    private List<WrUseUnitSimpleVO> waterUnits;
    /**
     * 预留数据
     */
    private Object data;


}
