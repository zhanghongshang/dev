package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by asus on 2018/11/16.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse implements Serializable {
    /**
     * 用户ID
     *
     * @mbggenerated
     */
    private String userId;
    /**
     * 性别
     *
     * @mbggenerated
     */
    private int sex;

    /**
     * 用户名称 varchar(20)
     *
     * @mbggenerated
     */
    private String username = "";

    /**
     * 真实姓名 varchar(20)
     *
     * @mbggenerated
     */
    private String realname = "";

    /**
     * 联系电话 varchar(13)
     *
     * @mbggenerated
     */
    private String telephone = "";

    /**
     * 邮箱 varchar(30)
     *
     * @mbggenerated
     */
    private String mail = "";

    /**
     * 用户头像 varchar(32)
     *
     * @mbggenerated
     */
    private String icon;

    /**
     * 用户码 varchar(32)
     *
     * @mbggenerated
     */
    private String userNum;

    /**
     * 移动电话 varchar(13)
     *
     * @mbggenerated
     */
    private String mobile;

    /**
     * 密码（加密） varchar(32)
     *
     * @mbggenerated
     */
    private String password = "";

    /**
     * 备注 varchar(200)
     *
     * @mbggenerated
     */
    private String remark = "";

    /**
     * 状态 0:冻结 1：正常  2：删除
     *
     * @mbggenerated
     */
    private Integer status = 1;

    /**
     * 随机加密盐 varchar(20)
     *
     * @mbggenerated
     */
    private String credentialsSalt = "";

    /**
     * 部门  多个用户属于一个部门
     */
    private List<DeptResponse> sysDepts;
    /**
     * 权限扩展配置
     */
    private String individuation;
    /**
     * 用户签名
     */
    private String signatrue;

    /**
     * 第三方角色 1 管理员 2普通用户
     */
    private String applicationRole;

    /**
     * 是否在线
     */
    private boolean isOnline;

    /**
     * 最后一次登录时间
     */
    private Long lastTimeLogin;

    /**
     * 最后一次登录时间
     */
    private Date loginTime;
}
