package com.nari.slsd.msrv.waterdiversion.init.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.msrv.waterdiversion.init.interfaces.IInitModelService;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrUseUnitManagerMapper;
import com.nari.slsd.msrv.waterdiversion.model.po.WrUseUnitManager;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseUnitNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @ClassName: InitModelServiceImpl
 * @Description: TODO
 * @Author: sk
 * @Date: 2020/8/4 20:14
 * @Version: 1.0
 * @Remark:
 **/
@Service
@Slf4j
public class InitModelServiceImpl implements IInitModelService {

    @Resource
    WrUseUnitManagerMapper managerMapper;

    @Override
    public List<WrUseUnitNode> createTree() {
        /**
         * 获取PID为-1的用水单位，是树模型的第一层
         */
        QueryWrapper<WrUseUnitManager> wrapper = new QueryWrapper<>();
        wrapper.eq("PID", "-1");
        List<WrUseUnitManager> managerList = managerMapper.selectList(wrapper);
        return createTree(managerList);
    }

    @Override
    public List<WrUseUnitNode> createTreeByIds(List<String> nodeIds) {
        if (CollectionUtils.isEmpty(nodeIds)) {
            return null;
        }
        QueryWrapper<WrUseUnitManager> wrapper = new QueryWrapper<>();
        wrapper.in("PID", nodeIds);
        List<WrUseUnitManager> managerList = managerMapper.selectList(wrapper);

        return createTree(managerList);
    }

    @Override
    public WrUseUnitNode createTreeById(String nodeId) {
        if (StringUtils.isEmpty(nodeId)) {
            return null;
        }
        WrUseUnitManager manager = managerMapper.selectById(nodeId);
        WrUseUnitNode node = convertNode(manager);
        node.setChildren(getChildren(manager.getId()));
        return node;
    }


    /**
     * 根据树模型第一层查找子节点，生成模型树
     *
     * @param managerList
     * @return
     */
    private List<WrUseUnitNode> createTree(List<WrUseUnitManager> managerList) {
        List<WrUseUnitNode> nodeList = new ArrayList<>();
        //如果为空直接返回
        if (CollectionUtils.isEmpty(managerList)) {
            return null;
        }
        managerList.stream().forEach(manager -> {
            /**
             * 用水单位转节点
             */
            WrUseUnitNode node = convertNode(manager);
            node.setChildren(getChildren(manager.getId()));
            nodeList.add(node);
        });
        return nodeList;
    }

    /**
     * 递归查询出当前id下的所有节点
     */
    public List<WrUseUnitNode> getChildren(String pid) {

        List<WrUseUnitNode> nodeList = new ArrayList<>();
        /**
         * 查找子节点
         */
        //如果为空直接返回
        QueryWrapper<WrUseUnitManager> wrapper = new QueryWrapper<>();
        wrapper.eq("PID", pid);
        List<WrUseUnitManager> managerList = managerMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(managerList)) {
            return null;
        }
        nodeList = convertNodeList(managerList);

        nodeList.stream().forEach(chNode -> {
            List<WrUseUnitNode> chNodeList = getChildren(chNode.getId());
            chNode.setChildren(chNodeList);
        });
        return nodeList;
    }

    public static WrUseUnitNode convertNode(WrUseUnitManager manager) {

        WrUseUnitNode node = new WrUseUnitNode();
        node.setId(manager.getId());
        node.setName(manager.getUnitName());
        node.setPid(manager.getPid());
        return node;
    }

    public static List<WrUseUnitNode> convertNodeList(Collection<WrUseUnitManager> managerList) {
        List<WrUseUnitNode> nodeList = new ArrayList<>();
        if (CollectionUtils.isEmpty(managerList)) {
            return null;
        }
        managerList.stream().forEach(manager -> {
            nodeList.add(convertNode(manager));
        });
        return nodeList;
    }
}
