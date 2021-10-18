package com.nari.slsd.msrv.waterdiversion.model.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * <p>
 * 用水单位人员表
 * </p>
 *
 * @author reset kalar
 * @since 2021-07-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrUseUnitPersonVO {


    /**
     * ID
     */
    private String id;

    /**
     * 用水单位编号
     */
    private String unitId;

    /**
     * 用水单位名称
     */
    private String unitName;

    /**
     * 人员ID
     */
    private String userId;

    /**
     * 人员名称
     */
    private String userName;

    /**
     * 人员类型
     * 1.创建人
     * 2.负责人
     * 3.用水单位人员
     */
    private Integer userType;


}
