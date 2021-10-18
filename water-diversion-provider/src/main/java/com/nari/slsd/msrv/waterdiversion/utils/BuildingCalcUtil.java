package com.nari.slsd.msrv.waterdiversion.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.LineReadWatcher;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.watchers.DelayWatcher;
import cn.hutool.core.util.ArrayUtil;
import com.nari.slsd.msrv.waterdiversion.model.dto.BuildingTree;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bigb
 * @version 1.0.0
 * @ClassName BuildingCalcUtil
 * @Description 引水口目标流量/水量计算
 * @createTime 2021年08月22日
 * @TODO 后续将递归改为尾递归，防止栈内存溢出
 */
@Slf4j
@Component
public class BuildingCalcUtil {
    /**
     * 上下游关系缓存
     */
    private static final Map<String, BuildingTree> BUILDING_TREE_MAP = new ConcurrentHashMap<>();
    /**
     * 下游水口-所有上游水口关联关系
     */
    private static final Map<String, Set<String>> CHILD_ALL_PARENT_MAP = new ConcurrentHashMap<>();
    /**
     * 直接上游水口（1个下游水口有多个上游水口）
     */
    private static final Map<String, List<String>> MULTI_DIRECT_PARENT_MAP = new ConcurrentHashMap<>();

    private static final String SPLIT_PLUS = "+";

    private static final String CONFIG_FILE_NAME = "monitor/building-tree.properties";

    static {
        loadConfigProperties(CONFIG_FILE_NAME);
        cacheAllParentForEveryChildBuilding();
        cacheMultiParentBuilding();
        //buildingConfigFileWatchMonitor(CONFIG_FILE_NAME);
    }

    public static void main(String[] args) {
        Set<String> set = new HashSet<>();
        getAllChildrenLeafNodes("A", set);
        System.out.println(set);
    }

    /**
     * 读取引水口上下游层级关系
     * TODO:嵌套太多,后期进行方法拆分
     */
    private static void loadConfigProperties(String fileName) {
        try {
            // 通过Spring中的PropertiesLoaderUtils工具类进行获取
            Properties prop = PropertiesLoaderUtils.loadAllProperties(fileName);
            prop.keySet().stream()
                    .forEach(e -> {
                        String key = (String) e;
                        BuildingTree buildingTree = new BuildingTree();
                        buildingTree.setId(key);
                        String[] childArr = StringUtils.split(prop.getProperty(key), SPLIT_PLUS);
                        if (ArrayUtil.isNotEmpty(childArr)) {
                            buildingTree.setLeaf(false);
                            buildingTree.setSubIdList(Arrays.asList(childArr));
                        }
                        BUILDING_TREE_MAP.put(buildingTree.getId(), buildingTree);
                    });
            // 根据关键字查询相应的值
        } catch (IOException e) {
            log.error("BuildingCalcUtil#readFile fail , error is {}", e);
        }
    }

    /**
     * 缓存所有节点的上游水口
     */
    private static void cacheAllParentForEveryChildBuilding() {
        //过滤掉叶子节点
        BUILDING_TREE_MAP.values().stream().forEach(e -> {
            Set<String> set = new HashSet<>();
            getAllParentNodes(e.getId(), set);
            CHILD_ALL_PARENT_MAP.put(e.getId(), set);
        });
    }

    /**
     * 获取所有叶子节点
     *
     * @param parentNode
     * @param subSet
     */
    private static void getAllChildrenLeafNodes(String parentNode, Set<String> subSet) {
        BuildingTree buildingTree = BUILDING_TREE_MAP.get(parentNode);
        if (null == buildingTree) {
            return;
        }
        //叶子节点
        if (buildingTree.isLeaf()) {
            subSet.add(buildingTree.getId());
            return;
        }
        List<String> subIdList = buildingTree.getSubIdList();
        if (CollectionUtils.isNotEmpty(subIdList)) {
            subIdList.stream().filter(id -> StringUtils.isNotEmpty(id)).forEach(id -> {
                //子节点
                getAllChildrenLeafNodes(id, subSet);
            });
        }
    }

    /**
     * 反向获取其所有父节点(一直到根节点)
     *
     * @param currentNode
     * @param parentSet
     */
    private static void getAllParentNodes(String currentNode, Set<String> parentSet) {
        if (StringUtils.isEmpty(currentNode)) {
            return;
        }
        Set<String> parentIdSet = new HashSet<>();
        BUILDING_TREE_MAP.values().stream().filter(building -> CollectionUtils.isNotEmpty(building.getSubIdList())).forEach(building -> {
            List<String> subIdList = building.getSubIdList();
            if (subIdList.contains(currentNode)) {
                parentIdSet.add(building.getId());
            }
        });
        if (CollectionUtils.isNotEmpty(parentIdSet)) {
            parentSet.addAll(parentIdSet);
            parentIdSet.stream().forEach(parent -> getAllParentNodes(parent, parentSet));
        }
    }

    /**
     * 缓存所有直接上游水口包含多个的引水口
     */
    private static void cacheMultiParentBuilding() {
        BUILDING_TREE_MAP.keySet().stream().forEach(buildingCode -> {
            List<String> parentList = new ArrayList<>();
            BUILDING_TREE_MAP.entrySet().stream().forEach(en -> {
                BuildingTree tree = en.getValue();
                List<String> subIdList = tree.getSubIdList();
                if(CollectionUtils.isNotEmpty(subIdList) && subIdList.contains(buildingCode)){
                    parentList.add(en.getKey());
                }
            });
            if(parentList.size() > 1){
                MULTI_DIRECT_PARENT_MAP.put(buildingCode,parentList);
            }
        });
    }

    /**
     * 获取当前水口所上游水口
     * @param buildingCode
     * @return
     */
    public static Set<String> getAllParentsUntilRoot(String buildingCode){
        return CHILD_ALL_PARENT_MAP.get(buildingCode);
    }

    /**
     * 配置文件监听
     */
    private static void buildingConfigFileWatchMonitor(String fileName) {
        try {
            File file = FileUtil.file(fileName);
            WatchMonitor watchMonitor = WatchMonitor.create(file, WatchMonitor.ENTRY_MODIFY);
            /**watchMonitor.setDaemon(true);**/
            watchMonitor.setName("thread-damon-monitor-file-update");
            RandomAccessFile accessFile = new RandomAccessFile(file, "r");
            LineReadWatcher lineReadWatcher = new LineReadWatcher(accessFile,
                    Charset.defaultCharset(), s -> log.info("content is {}", s)) {
                @Override
                public void onModify(WatchEvent<?> event, Path currentPath) {
                    super.onModify(event, currentPath);
                    loadConfigProperties(CONFIG_FILE_NAME);
                    cacheAllParentForEveryChildBuilding();
                    cacheMultiParentBuilding();
                }
            };
            //在监听目录或文件时，如果这个文件有修改操作，JDK会多次触发modify方法，为了解决这个问题，使用DelayWatcher
            DelayWatcher delayWatcher = new DelayWatcher(lineReadWatcher, 1000);
            watchMonitor.setWatcher(delayWatcher);

            //设置监听目录的最大深入，目录层级大于制定层级的变更将不被监听，默认只监听当前层级目录
            /**watchMonitor.setMaxDepth(1);**/
            //启动监听
            watchMonitor.start();
        } catch (Exception e) {
            log.error("配置文件监听失败，error is {}", e);
        }
    }
}
