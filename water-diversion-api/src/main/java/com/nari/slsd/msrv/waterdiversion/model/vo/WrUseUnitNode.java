package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @ClassName: WaterUseUnitNode
 * @Description: 用水单位树节点
 * @Author: reset kalar
 * @Date: 2020/8/4 19:55
 * @Version: 1.0
 * @Remark:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrUseUnitNode {
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
     * 全路径
     */
    private String path;


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
