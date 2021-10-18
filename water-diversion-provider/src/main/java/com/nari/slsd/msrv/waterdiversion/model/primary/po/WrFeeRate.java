package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * <p>
 * 水费费率表
 * </p>
 *
 * @author bigb
 * @since 2021-08-10
 */
@Data
@TableName("WR_FEE_RATE")
public class WrFeeRate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private String id;

    /**
     * 用水性质ID
     */
    @TableField(value = "CATEGORY_ID")
    private String categoryId;

    /**
     * 超水比率
     */
    @TableField("SURPASS_RATE")
    private Double surpassRate;

    /**
     * 费率
     */
    @TableField("FEE_RATE")
    private Double feeRate;

    /**
     * 操作人id
     */
    @TableField("PERSON_ID")
    private String personId;

    /**
     * 操作人姓名
     */
    @TableField("PERSON_NAME")
    private String personName;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME",fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 有效标识
     */
    @TableLogic
    @TableField(value = "ACTIVE_FLAG",fill = FieldFill.INSERT)
    private Integer activeFlag;

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        WrFeeRate wrFeeRate = (WrFeeRate) o;
        return id.equals(wrFeeRate.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
