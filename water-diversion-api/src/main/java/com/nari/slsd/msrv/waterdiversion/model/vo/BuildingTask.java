package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Program: water-diversion
 * @Description:
 * @Author: zhs
 * @Date: 2021-08-18
 **/
@Data
public class BuildingTask {

    /**
     * 管理单位ID
     */
    private String mngUnitId;
    /**
     * 管理单位
     */
    private String mngUnitName;
    /**
     * 用水单位
     */
    private List<WrUseUnitSimpleVO> waterUnits;
    /**
     * 填报人
     */
    private String personName;
    /**
     * 填报时间
     */
    private Long createTime;
    /**
     * 预留存数据
     */
    private Object data;

    /**
     * 填报状态
     */
    private String state;

}
