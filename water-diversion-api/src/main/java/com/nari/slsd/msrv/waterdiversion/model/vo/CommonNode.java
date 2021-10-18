package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @Program: water-diversion
 * @Description: 树节点
 * @Author: reset kalar
 * @Date: 2021-08-13 15:50
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonNode {
    /**
     * 节点ID
     */
    private String id;
    /**
     * 节点名称
     */
    private String name;
    /**
     * 节点类型
     */
    private Integer type;
    /**
     * 父节点ID
     */
    private String pid;
    /**
     * 是否为叶节点
     */
    private Boolean isLeaf = false;

    /**
     * GIS坐标 [经度,纬度]
     * TODO 树结构存经纬度
     */
    private String latlngF;

    /**
     * 子节点
     */
    private List<CommonNode> children;

    public void setChildren(List<CommonNode> children) {
        this.isLeaf = false;
        if (CollectionUtils.isEmpty(children)) {
            this.isLeaf = true;
        }
        this.children = children;
    }
}
