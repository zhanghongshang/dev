package com.nari.slsd.msrv.waterdiversion.init.service;

import com.nari.slsd.msrv.waterdiversion.cache.interfaces.IModelCacheService;
import com.nari.slsd.msrv.waterdiversion.init.interfaces.IInitModelCache;
import com.nari.slsd.msrv.waterdiversion.init.interfaces.IInitModelService;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseUnitNode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Objects;

/**
 * @ClassName: InitModelCacheImpl
 * @Description: 模型缓存初始化实现
 * @Author: sk
 * @Date: 2020/8/4 20:36
 * @Version: 1.0
 * @Remark:
 **/
@Service
public class InitModelCacheImpl implements IInitModelCache {

    @Autowired
    IInitModelService modelService;

    @Autowired
    IModelCacheService cacheService;


    @Override
    public void initWaterUseUnitTree() {

        List<WrUseUnitNode> nodeList = modelService.createTree();

        if (CollectionUtils.isEmpty(nodeList)) {
            return;
        }
        nodeList.stream().filter(Objects::nonNull)
                .forEach(node -> {
                    cacheService.setWaterUseUnitTree(node.getId(), node);
                });
    }

    @Override
    public void updateWaterUseUnitTree(List<String> nodeIds) {

        List<WrUseUnitNode> nodeList = modelService.createTreeByIds(nodeIds);

        if (CollectionUtils.isEmpty(nodeList)) {
            return;
        }
        nodeList.stream().filter(Objects::nonNull)
                .forEach(node -> {
                    cacheService.setWaterUseUnitTree(node.getId(), node);
                });
    }

    @Override
    public void updateWaterUseUnitTree(String nodeId) {

        WrUseUnitNode node = modelService.createTreeById(nodeId);

        if (node == null) {
            return;
        }
        cacheService.setWaterUseUnitTree(node.getId(), node);
    }


}
