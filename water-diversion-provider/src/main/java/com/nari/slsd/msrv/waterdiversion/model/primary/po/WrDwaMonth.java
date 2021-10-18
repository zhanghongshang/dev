package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description 月度滚存指标
 * @Author ZHS
 * @Date 2021/9/20 17:10
 */
@Data
@TableName("WR_DWA_MONTH")
public class WrDwaMonth implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID",type = IdType.INPUT)
    private String Id;//ID

    @TableField(value = "BUILDING_ID")
    private String buildingId;//引水口id

    @TableField(value = "BUILDING_NAME")
    private String buildingName;//引水口名称

    @TableField(value = "YEAR")
    private String year;//年份

    @TableField(value = "MONTH")
    private String month;//月份

    @TableField(value = "PROPORTION")
    private String proportion;//占比

    @TableField(value = "TARGER")
    private BigDecimal targer;//指标（水量）

}
