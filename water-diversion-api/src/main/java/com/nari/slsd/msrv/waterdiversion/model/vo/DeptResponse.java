package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2018/11/16.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeptResponse implements Serializable {
    /**
     * 部门ID  非空
     */
    private String deptId;

    /**
     * 部门编码 非空
     */
    private String code="";

    /**
     * 名称 varchar 20
     */
    private String name = "";

    /**
     * 层级 varchar 200
     */
    private String level="";

    /**
     * 当前层级中的排序
     */
    private Integer seq=0;

    /**
     * 备注 varchar 200
     */
    private String remark="";

    /**
     * 上级部门id
     */
    private String parentId="-1";

    /**
     * 0：不可用 1：可用 2：删除
     * @mbggenerated
     */
    private Integer status=1;

    /**
     * 1：根目录，2：集团，3：中心，4：厂站，5：部门 0：其他
     * @mbggenerated
     */
    private Integer type = 4;

    /**
     * 部门主管
     */
    private String director = "";

    /**
     * 部门主管Id
     */
    private String directorId ="";

    /**
     * 部门传真号
     */
    private String facsimile = "";

    /**
     * 部门电话
     */
    private String phone = "";

    /**
     * 部门电话
     */
    private String modelId = "";

    private List<DeptStationRequest> deptStationRequestList=new ArrayList<>();
}
