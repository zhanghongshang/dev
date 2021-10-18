package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Created by ZHD
 * @program: WaterPoint
 * @description:模型-测点管理
 * @date: 2021/8/16 10:47
 */
@Data
@TableName("WATER_POINT")
public class WaterPoint {

    @TableId(value = "ID")
    private String id;//测点编号

    @TableField(value = "PID")
    private String pid;//pid（水工建筑物ID或站内设备ID）

    @TableField(value = "POINT_NAME")
    private String pointName;//测点名称

    @TableField(value = "CORRELATION_CODE")
    private String correlationCode;//关联测点号

    @TableField(value = "CORRELATION_SOURCE")
    private String correlationSource;//关联应用来源

    @TableField(value = "POINT_TYPE")
    private String pointType;//测点类型

    @TableField(value = "IS_CALC")
    private String isCalc;//直接/计算

    @TableField(value = "CALC_MODE")
    private String calcMode;//计算方法

    @TableField(value = "INSERT_TYPE")
    private String insertType;//插值方式

    @TableField(value = "CALC_ID")
    private String calcId;//曲线/公式号

    @TableField(value = "DEFAULT_VAL")
    private Integer defaultVal;//默认值

    @TableField(value = "DATA_UNIT")
    private String dataUint;//数据单位
}
