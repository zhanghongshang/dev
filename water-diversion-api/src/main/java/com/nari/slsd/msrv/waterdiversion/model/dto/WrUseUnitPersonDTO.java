package com.nari.slsd.msrv.waterdiversion.model.dto;

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
public class WrUseUnitPersonDTO {

    /**
     * ID
     */
    private String id;

    /**
     * 用水单位编号
     */
    private String unitId;

    /**
     * 人员ID
     */
    private String userId;

    /**
     * 人员类型
     * 1.创建人
     * 2.负责人
     * 3.用水单位人员
     */
    private Integer userType;


}
