package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: UserRequest
 * @Description: XXX 数据接收实体
 * @Author: sk
 * @Date: 2020/4/13 15:44
 * @Version: 1.0
 * @Remark:
 **/
@Getter
@Setter
public class DemoDTO {

    private String id; // XX id
    private String name; //用户名称
    private Integer age;//用户年龄
    private Long time;//创建时间
}
