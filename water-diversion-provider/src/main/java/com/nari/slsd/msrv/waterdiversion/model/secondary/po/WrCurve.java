package com.nari.slsd.msrv.waterdiversion.model.secondary.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 曲线维护
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-08
 */
@Data
//@EqualsAndHashCode(callSuper = false)
@TableName("SL_WATER_CURVE")
public class WrCurve implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 曲线编号
     */
    @TableField("CURVE_CODE")
    private String curveCode;

    /**
     * 曲线名
     */
    @TableField("CURVE_NAME")
    private String curveName;

//    /**
//     * 61850名称
//     */
//    @TableField("NAME_61850")
//    private String name61850;

    /**
     * 测点ID
     */
    @TableField("SENID")
    private String senId;

    /**
     * 测站ID
     */
    @TableField("STATIONID")
    private String stationId;

    /**
     * 曲线类型
     */
    @TableField("CURVE_TYPE")
    private String curveType;

    /**
     * 操作时间
     */
    @TableField("UPDATE_TIME")
    private Date updateTime;

    /**
     * 启用时间
     */
    @TableField("START_TIME")
    private Date startTime;

    /**
     * 结束时间
     */
    @TableField("END_TIME")
    private Date endTime;

    /**
     * 状态 0未审核 1启用 2停用
     */
    @TableField("STATE")
    private Integer state;

    /**
     * 操作人员id
     */
    @TableField("PERSON_ID")
    private String personId;

    /**
     * 审批人员id
     */
    @TableField("APPROVER_ID")
    private String approverId;

    /**
     * 备注
     */
    @TableField("REMARK")
    private String remark;

    /**
     * 维数 2:2维，3：3维
     */
    @TableField("DIMENSIONALITY")
    private String dimensionality;

    /**
     * 维数说明
     */
    @TableField("DIM_EXPLAIN")
    private String dimExplain;

    /**
     * EXPL
     */
    @TableField("EXPL")
    private String expl;

    /**
     * 率定方式
     * 0  多项式
     * 1  对数函数
     * 2  指数函数
     * 3  幂函数
     */
    @TableField("CALIBRATION_MODE")
    private Integer calibrationMode;

    /**
     * 公式
     */
    @TableField("FOMULAR")
    private String fomular;

    /**
     * 确认系数
     */
    @TableField("COEFFICIENT")
    private Double coefficient;

}
