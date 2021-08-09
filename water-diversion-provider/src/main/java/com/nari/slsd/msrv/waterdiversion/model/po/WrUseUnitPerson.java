package com.nari.slsd.msrv.waterdiversion.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用水单位人员表
 * </p>
 *
 * @author reset kalar
 * @since 2021-07-30
 */
@Data
//@EqualsAndHashCode(callSuper = false)
@TableName("WR_USE_UNIT_PERSON")
public class WrUseUnitPerson implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用水单位人员主键
     */
    @TableId(value = "ID", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 用水单位编号
     */
    @TableField("UNIT_ID")
    private String unitId;

    /**
     * 人员ID
     */
    @TableField("USER_ID")
    private String userId;

    /**
     * 人员类型
     * 1.创建人
     * 2.负责人
     * 3.用水单位人员
     */
    @TableField("USER_TYPE")
    private Integer userType;



}
