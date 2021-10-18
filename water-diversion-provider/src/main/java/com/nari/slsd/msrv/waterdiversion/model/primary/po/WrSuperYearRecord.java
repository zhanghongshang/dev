package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.math.BigDecimal;

/**
 * @Description 超年填报记录
 * @Author ZHS
 * @Date 2021/9/27 1:24
 */
@Data
@TableName("WR_SUPER_YEAR_RECORD")
public class WrSuperYearRecord {

    @TableId(value = "ID", type = IdType.INPUT)
    private String id;

    @TableField("TASK_ID")
    private String taskId; //任务id

    @TableField("TOTAL_WATER")
    private BigDecimal totalWater; //本次交易分配水量

    @TableField("WATER_REGIME_CODE")
    private String waterRegimeCode; //水权交易编码

}
