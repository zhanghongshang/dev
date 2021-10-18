package com.nari.slsd.msrv.waterdiversion.init.interfaces;

import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseUnitNode;

import java.util.List;

/**
 * @ClassName: IInitModelService
 * @Description: 模型初始化
 * @Author: sk
 * @Date: 2020/8/4 19:29
 * @Version: 1.0
 **/
public interface IInitModelService {

    List<WrUseUnitNode> createTree();

    /**
     * 构建父节点下的所有子树
     * @param nodeIds 父节点
     * @return
     */
    List<WrUseUnitNode> createTreeByIds(List<String> nodeIds);

    /**
     * 以当前节点为根节点构建树
     * @param nodeId 根节点
     * @return
     */
    WrUseUnitNode createTreeById(String nodeId);


}
