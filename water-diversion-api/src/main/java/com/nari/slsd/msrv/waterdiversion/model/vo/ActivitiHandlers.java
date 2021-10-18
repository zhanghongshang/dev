package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

/**
 * @Description 工作流下级处理返回信息
 * @Author ZHS
 * @Date 2021/8/29 21:49
 */
@Data
public class ActivitiHandlers {
    //管理站人人员名称
    private String pralname;
    //处理人id
    private String userId;
    //登录账号
    private String username;
    //
    private String type;


}
