package com.nari.slsd.msrv.waterdiversion.model.vo;


import com.nari.slsd.msrv.waterdiversion.model.dto.PersonTransDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
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
@AllArgsConstructor
public class WrUseUnitManagerVO {

    private static final long serialVersionUID = 1L;

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
     * 父级用水单位名称
     */
    private String punitName;

    /**
     * 编码
     */
    private String code;

    /**
     * 用水单位人员
     */
    private List<PersonTransDTO> personList;


    public WrUseUnitManagerVO() {
        this.personList = new ArrayList<>();
    }


}
