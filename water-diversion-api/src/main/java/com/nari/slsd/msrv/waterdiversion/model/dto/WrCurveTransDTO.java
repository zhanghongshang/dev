package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * @Program: water-diversion
 * @Description: 曲线率定DTO
 * @Author: reset kalar
 * @Date: 2021-08-09 09:45
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrCurveTransDTO {

    /**
     * ID
     */
    private String id;

    /**
     * 曲线编号
     */
    private String curveCode;

    /**
     * 曲线名
     */
    private String curveName;


    /**
     * 测站ID
     */
    private String stationId;

    /**
     * 测站Code
     */
    private String stationCode;

    /**
     * 测站name
     */
    private String stationName;


    /**
     * 曲线类型
     */
    private String curveType;

    /**
     * 操作时间
     */
    private Long updateTime;

    /**
     * 启用时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 状态 0未审核 1启用 2停用
     */
    private Integer state;

    /**
     * 操作人员id
     */
    private String personId;

    /**
     * 操作人员名称
     */
    private String personName;

    /**
     * 审批人员id
     */
    private String approverId;

    /**
     * 审批人员名称
     */
    private String approverName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 维数 2:2维，3：3维
     */
    private String dimensionality;

    /**
     * 维数说明
     */
    private String dimExplain;

    /**
     * EXPL
     */
    private String expl;

    /**
     * 率定方式
     */
    private Integer calibrationMode;

    /**
     * 公式
     */
    private String fomular;

    /**
     * 确认系数
     */
    private Double coefficient;

    /**
     * 率定后数据
     * {
     * {水位,流量},
     * {水位,流量},
     * {水位,流量},
     * {水位,流量}
     * }
     */
    private Double[][] data;


    /**
     * 原始数据
     */
    private List<WrCurveOriginalDTO> originalData;

}
