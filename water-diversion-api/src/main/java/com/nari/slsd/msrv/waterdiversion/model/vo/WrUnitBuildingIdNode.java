package com.nari.slsd.msrv.waterdiversion.model.vo;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @Description 用水单位下引水口树结构及对应的对比值
 * @Author ZHS
 * @Date 2021/9/12 11:49
 */
public class WrUnitBuildingIdNode {
    /**
     * 节点ID
     */
    private String id;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 父节点
     */
    private String pid;

    /**
     * 子节点列表
     */
    private List<WrUseUnitNode> children;

    /**
     * 是否为叶节点
     */
    private Boolean isLeaf = false;


    /**
     * 层级
     */
    private Integer level;

    /**
     * 同期比对值
     */
    private Object data;


    /**
     * 标识是否为叶子节点 false标识不是叶子节点（底下还有子节点） true标识是叶子节点（底下没有子节点）
     */

    public void setChildren(List<WrUseUnitNode> children) {
        this.isLeaf = false;
        if (CollectionUtils.isEmpty(children)) {
            this.isLeaf = true;
        }
        this.children = children;
    }

}
