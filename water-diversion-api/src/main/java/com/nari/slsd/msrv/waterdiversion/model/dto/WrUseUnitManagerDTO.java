package com.nari.slsd.msrv.waterdiversion.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>
 * 用水单位管理
 * </p>
 *
 * @author reset kalar
 * @since 2021-07-29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrUseUnitManagerDTO {

    /**
     * 用水单位编号
     */
    private String id;

    /**
     * 用水单位名称
     */
    private String unitName;

    /**
     * 建户时间
     */
    private Long housesTime;

    /**
     * 状态 0：无效，1：有效
     */
    private Integer state;

    /**
     * 父级用水单位
     */
    private String pid;

    /**
     * 编码
     */
    private String code;

    /**
     * 用水单位人员
     * userId
     * userType
     *
     */
    private List<PersonTransDTO> personList;


}
