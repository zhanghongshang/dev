package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

/**
 * @Program: water-diversion
 * @Description:
 * @Author: reset kalar
 * @Date: 2021-08-03 12:01
 **/
@Data
public class PersonTransKey {
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


    public PersonTransKey() {
    }

    public PersonTransKey(String userId, Integer userType) {
        this.userId = userId;
        this.userType = userType;
    }
}
