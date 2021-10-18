package com.nari.slsd.msrv.waterdiversion.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 曲线维护
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrCurveDTO {


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

//    /**
//     * 61850名称
//     */
//    private String name61850;
//
//    /**
//     * 测点ID
//     */
//    private String senId;

    /**
     * 测站ID
     */
    private String stationId;

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
     * 状态
     */
    private Integer state;

    /**
     * 操作人员id
     */
    private String personId;

    /**
     * 审批人员id
     */
    private String approverId;

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


}
