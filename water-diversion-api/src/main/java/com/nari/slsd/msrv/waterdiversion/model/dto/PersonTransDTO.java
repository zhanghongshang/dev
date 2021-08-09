package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Program: water-diversion
 * @Description:
 * @Author: reset kalar
 * @Date: 2021-08-03 12:01
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonTransDTO {

//    /**
//     * ID
//     */
//    private String Id;

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
//
//    /**
//     * 人员名称
//     */
//    private String userName;

}
