package com.nari.slsd.msrv.waterdiversion.init.interfaces;


import java.util.List;

/**
 * @ClassName: IInitModelCache
 * @Description: 模型缓存初始化
 * @Author: sk
 * @Date: 2020/8/4 19:32
 * @Version: 1.0
 * @Remark:
 **/
public interface IInitModelCache {

    void initWaterUseUnitTree();

    /**
     * 以父节点更新所有子树
     *
     * @param nodeIds
     */
    void updateWaterUseUnitTree(List<String> nodeIds);

    /**
     * 以该节点为根节点更新树
     *
     * @param nodeId
     */
    void updateWaterUseUnitTree(String nodeId);

}
