package com.nari.slsd.msrv.waterdiversion.services;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.utils.BeanUtils;
import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.msrv.waterdiversion.cache.interfaces.IModelCacheService;
import com.nari.slsd.msrv.waterdiversion.commons.TreeEnum;
import com.nari.slsd.msrv.waterdiversion.commons.WrBuildingEnum;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrDiversionPortService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrUseUnitManagerService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrUseUnitPersonService;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrBuildingAndDiversion;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WaterBuildingManager;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WaterBuildingManagerMapper;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWaterBuildingManagerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrDiversionPort;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrUseUnitManager;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrUseUnitPerson;
import com.nari.slsd.msrv.waterdiversion.model.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 水工建筑物管理 服务实现类
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-03
 */
@Service
@Slf4j
public class WaterBuildingManagerServiceImpl extends ServiceImpl<WaterBuildingManagerMapper, WaterBuildingManager> implements IWaterBuildingManagerService {

    @Resource
    TransactionTemplate transactionTemplate;

    @Resource
    IWrDiversionPortService wrDiversionPortService;

    @Resource
    IWrUseUnitPersonService wrUseUnitPersonService;

    @Resource
    IWrUseUnitManagerService wrUseUnitManagerService;

    @Resource
    IModelCacheService cacheService;

    @Override
    public List<WrUseUnitAndBuilding> getWrUseUnitAndBuildingsByMng(List<String> mngUnitIds, Boolean ifBuilding, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels) {
        List<WrUseUnitAndBuilding> resultList = new ArrayList<>();
        //根据管理单位查询测站引水口
        List<WrBuildingAndDiversion> buildingList = queryBuildingAndDiversionList(null, mngUnitIds, null, buildingTypes, fillReport, buildingLevels);
        //无测站直接返回
        if (buildingList != null && buildingList.size() != 0) {
            //根据测站引水口查询用水单位并过滤重复项
            //这里默认查到的就是叶结点 没有再去缓存取树重新查找叶结点
            List<String> unitIdList = buildingList.stream().map(WrBuildingAndDiversion::getWaterUnitId).filter(StringUtils::isNotEmpty).distinct().collect(Collectors.toList());
            List<WrUseUnitManager> unitList = wrUseUnitManagerService.listByIds(unitIdList);
            Map<String, WrUseUnitAndBuilding> unitAndBuildingMap = unitList.stream().map(this::convert2WrUseUnitAndBuilding).collect(Collectors.toMap(WrUseUnitAndBuilding::getWaterUnitId, unitAndBuilding -> unitAndBuilding));
            //需要测站引水口层级，存入
            if (ifBuilding) {
                fillUnitBuildings(unitAndBuildingMap, buildingList);
            }
            return new ArrayList<>(unitAndBuildingMap.values());
        }
        return resultList;
    }

    @Override
    public List<WrUseUnitAndBuilding> getWrUseUnitAndBuildingsByUser(String userId, Boolean ifBuilding, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels) {
        List<WrUseUnitAndBuilding> resultList = new ArrayList<>();
        //先通过用户ID 去查用户相关的用水单位ID 并过滤重复项
        List<String> unitIdList = wrUseUnitPersonService.lambdaQuery()
                .eq(WrUseUnitPerson::getUserId, userId)
                .list()
                .stream()
                .map(WrUseUnitPerson::getUnitId)
                .distinct()
                .collect(Collectors.toList());

        if (unitIdList.size() != 0) {
            //通过用水单位ID查用水单位
            //取出用水单位的路径集合
            List<WrUseUnitManager> wrUseUnitManagerList = wrUseUnitManagerService.listByIds(unitIdList);
            List<String> pathList = wrUseUnitManagerList.stream().map(WrUseUnitManager::getPath).collect(Collectors.toList());
            //获取用水单位后，根据path获取一级节点ID
            Set<String> rootIdSet = new HashSet<>();
            wrUseUnitManagerList.forEach(po -> {
                String rootId = wrUseUnitManagerService.getRootId(po);
                if (StringUtils.isNotEmpty(rootId)) {
                    rootIdSet.add(rootId);
                }
            });
            List<String> rootIdList = new ArrayList<>(rootIdSet);

            //根据一级节点从缓存获取树
            //遍历树 找叶结点并且在用户所属用水单位路径下
            List<WrUseUnitNode> nodeList = cacheService.getWaterUseUnitTreeList(rootIdList);
            List<WrUseUnitManager> leafList = new ArrayList<>();
            //所有用水单位节点的ID-NAME 键值对
            Map<String, String> idNameMap = new HashMap<>();
            //因为测站只挂在最后一层用水单位下,所以要先查最底层的用水单位
            //获取用水单位叶结点
            nodeList.forEach(wrUseUnitNode -> findLeaf(wrUseUnitNode, pathList, leafList, idNameMap));

            Map<String, WrUseUnitAndBuilding> leafMap = leafList.stream().map(this::convert2WrUseUnitAndBuilding).collect(Collectors.toMap(WrUseUnitAndBuilding::getWaterUnitId, leaf -> leaf));
            //需要测站层级
            if (ifBuilding) {
                //根据用水单位叶结点ID查询底下的测站
                List<String> leafIdList = leafList.stream().map(WrUseUnitManager::getId).collect(Collectors.toList());
                //根据测站类型筛选测站-引水口
                List<WrBuildingAndDiversion> buildingList = queryBuildingAndDiversionList(null, null, leafIdList, buildingTypes, fillReport, buildingLevels);
                fillUnitBuildings(leafMap, buildingList);
            }
            return new ArrayList<>(leafMap.values());
        }
        return resultList;
    }

    /**
     * 填充测站信息至最后一层用水单位-测站引水口结构
     *
     * @param unitAndBuildingMap 最后一层用水单位-测站引水口结构
     * @param buildingList       测站信息
     */
    private void fillUnitBuildings(Map<String, WrUseUnitAndBuilding> unitAndBuildingMap, List<WrBuildingAndDiversion> buildingList) {
        buildingList.forEach(building -> {
            if (unitAndBuildingMap.containsKey(building.getWaterUnitId())) {
                WrUseUnitAndBuilding unitAndBuilding = unitAndBuildingMap.get(building.getWaterUnitId());
                WrBuildingSimpleVO wrBuildingSimpleVO = new WrBuildingSimpleVO();
                wrBuildingSimpleVO.setBuildingId(building.getId());
                wrBuildingSimpleVO.setBuildingCode(building.getBuildingCode());
                wrBuildingSimpleVO.setBuildingName(building.getBuildingName());
                unitAndBuilding.getBuildings().add(wrBuildingSimpleVO);
            }
        });
    }

    @Override
    public List<WrBuildingAndDiversion> getWrBuildingAndDiversionList(List<String> stationIds) {
        List<WrBuildingAndDiversion> resultList = queryBuildingAndDiversionList(stationIds, null, null, null, null, null);
        //整合用水单位、管理单位名称
        List<String> unitIdList = resultList.stream()
                .filter(result -> StringUtils.isNotEmpty(result.getWaterUnitId()))
                .map(WrBuildingAndDiversion::getWaterUnitId)
                .distinct()
                .collect(Collectors.toList());
        if (unitIdList.size() != 0) {
            fillMngAndUnitName(resultList, unitIdList);
        }
        return resultList;
    }

    @Override
    public Map<String, WrBuildingAndDiversion> getBuildingMapByIds(List<String> stationIds) {
        QueryWrapper<WaterBuildingManager> wrapper = new QueryWrapper<>();
        wrapper.in("wb.ID", stationIds);
        List<WrBuildingAndDiversion> buildingList = baseMapper.getBuildingAndDiversionList(wrapper);
        return buildingList.stream().collect(Collectors.toMap(WrBuildingAndDiversion::getId, building -> building));
    }

    @Override
    public Map<String, WrBuildingAndDiversion> getBuildingMapByCodes(List<String> stationCodes) {
        QueryWrapper<WaterBuildingManager> wrapper = new QueryWrapper<>();
        wrapper.in("wd.BUILDING_CODE", stationCodes);
        List<WrBuildingAndDiversion> buildingList = baseMapper.getBuildingAndDiversionList(wrapper);
        return buildingList.stream().collect(Collectors.toMap(WrBuildingAndDiversion::getBuildingCode, building -> building));
    }

    @Override
    public List<WrBuildingAndDiversion> getWrBuildingAndDiversionListByUnit(List<String> waterUnitIds, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels) {
        List<WrBuildingAndDiversion> resultList = queryBuildingAndDiversionList(null, null, waterUnitIds, buildingTypes, fillReport, buildingLevels);
        //整合用水单位、管理单位名称
        List<String> unitIdList = resultList.stream()
                .filter(result -> StringUtils.isNotEmpty(result.getWaterUnitId()))
                .map(WrBuildingAndDiversion::getWaterUnitId)
                .distinct()
                .collect(Collectors.toList());
        if (unitIdList.size() != 0) {
            fillMngAndUnitName(resultList, unitIdList);
        }
        return resultList;
    }

    @Override
    public List<WrBuildingSimpleVO> getBuildingAndDiversionList(List<String> buildingTypes, List<Integer> buildingLevels, String search) {
        //中文编码转换 接参时已经自动转换
        List<WrBuildingSimpleVO> resultList = new ArrayList<>();
        QueryWrapper<WaterBuildingManager> wrapper = new QueryWrapper<>();
        wrapper.in(buildingTypes != null && buildingTypes.size() != 0, "wb.BUILDING_TYPE", buildingTypes);
        wrapper.in(buildingLevels != null && buildingLevels.size() != 0, "wd.BUILDING_LEVEL", buildingLevels);
        wrapper.and(StringUtils.isNotEmpty(search), wr -> wr.like("wb.BUILDING_NAME", search).or().like("wd.BUILDING_CODE", search));
        wrapper.orderByAsc("wb.ID");
        List<WrBuildingAndDiversion> buildingList = baseMapper.getBuildingAndDiversionList(wrapper);
        buildingList.forEach(building -> {
            WrBuildingSimpleVO vo = new WrBuildingSimpleVO();
            vo.setBuildingId(building.getId());
            vo.setBuildingCode(building.getBuildingCode());
            vo.setBuildingName(building.getBuildingName());
            resultList.add(vo);
        });
        return resultList;
    }

    @Override
    public DataTableVO getBuildingAndDiversionPage(Integer pageIndex, Integer pageSize, List<String> buildingTypes, List<Integer> buildingLevels, String waterUnitId, String mngUnitId) {
        //分页查询 按类型，上级用水单位，管理单位筛选
        QueryWrapper<WaterBuildingManager> pageWrapper = new QueryWrapper<>();
        pageWrapper.in(buildingTypes != null && buildingTypes.size() != 0, "wb.BUILDING_TYPE", buildingTypes)
                .eq(StringUtils.isNotEmpty(waterUnitId), "wd.WATER_UNIT_ID", waterUnitId)
                .eq(StringUtils.isNotEmpty(mngUnitId), "wd.MNG_UNIT_ID", mngUnitId)
                .in(buildingLevels != null && buildingLevels.size() != 0, "wd.BUILDING_LEVEL", buildingLevels)
                .orderByAsc("wb.ID");
        IPage<WrBuildingAndDiversion> page = new Page<>(pageIndex, pageSize);
        baseMapper.getBuildingAndDiversionPage(page, pageWrapper);
        List<WrBuildingAndDiversion> resultList = page.getRecords();
        //整合用水单位名称
        List<String> unitIdList = resultList.stream()
                .filter(buildingAndDiversion -> StringUtils.isNotEmpty(buildingAndDiversion.getWaterUnitId()))
                .map(WrBuildingAndDiversion::getWaterUnitId)
                .distinct()
                .collect(Collectors.toList());
        if (unitIdList.size() != 0) {
            fillMngAndUnitName(resultList, unitIdList);
        }
        DataTableVO dataTableVO = new DataTableVO();
        dataTableVO.setRecordsFiltered(page.getTotal());
        dataTableVO.setRecordsTotal(page.getTotal());
        dataTableVO.setData(resultList);
        return dataTableVO;
    }


    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void updateBuildingAndDiversion(WrBuildingAndDiversion dto) {
        //分割为两个表 修改左表水工建筑物表,右表测站-饮水口管理 记录不存在新增,存在则修改
//        WaterBuildingManager waterBuildingManager = convert2WaterBuildingManager(dto);
//        //不更新水工建筑物表的所属用水单位和管理单位字段
//        waterBuildingManager.setWaterUnitId(null);
//        waterBuildingManager.setMngUnitId(null);
        WrDiversionPort wrDiversionPort = convert2WrDiversionPort(dto);
        transactionTemplate.executeWithoutResult(transactionStatus -> {
//            updateById(waterBuildingManager);
            wrDiversionPortService.saveOrUpdate(wrDiversionPort);
        });
    }

    @Override
    public List<CommonNode> getMngAndBuildingsTree(List<String> mngUnitIds, List<String> buildingTypes, List<Integer> buildingLevels) {
        List<CommonNode> resultList = new ArrayList<>();
        List<WrBuildingAndDiversion> poList = queryBuildingAndDiversionList(null, mngUnitIds, null, buildingTypes, null, buildingLevels);
        //测站引水口转成树节点
        List<CommonNode> leafNodeList = new ArrayList<>();
        poList.forEach(po -> {
            CommonNode node = new CommonNode();
            node.setId(po.getId());
            node.setName(po.getBuildingName());
            node.setIsLeaf(true);
            node.setPid(po.getMngUnitId());
            node.setType(TreeEnum.NODE_TYPE_STATION);
            leafNodeList.add(node);
        });
        //按管理单位分组
        Map<String, List<CommonNode>> groupMap = leafNodeList.stream().collect(Collectors.groupingBy(CommonNode::getPid));

        groupMap.keySet().stream().distinct().forEach(id -> {
                    CommonNode pNode = new CommonNode();
                    pNode.setId(id);
                    //从缓存获取管理单位名称
                    pNode.setName(cacheService.getMngUnitName(id));
                    pNode.setType(TreeEnum.NODE_TYPE_DEPT);
                    pNode.setIsLeaf(false);
                    if (groupMap.containsKey(id)) {
                        pNode.setChildren(groupMap.get(id));
                    }
                    resultList.add(pNode);
                }
        );
        return resultList;
    }

    @Override
    public List<MngUnitAndBuilding> getMngAndBuildingsByMng(List<String> mngUnitIds, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels) {
        //根据条件查询测站引水口
        List<WrBuildingAndDiversion> poList = queryBuildingAndDiversionList(null, mngUnitIds, null, buildingTypes, fillReport, buildingLevels);
        //类型转换
        return convert2MngUnitAndBuildingList(poList);
    }

    @Override
    public List<MngUnitAndBuildings> getMngAndPBuildingsByMng(List<String> mngUnitIds, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels) {
        QueryWrapper<WaterBuildingManager> wrapper = new QueryWrapper<>();
        wrapper.in(mngUnitIds != null && mngUnitIds.size() != 0, "wd.MNG_UNIT_ID", mngUnitIds);
        wrapper.in(buildingTypes != null && buildingTypes.size() != 0, "wb.BUILDING_TYPE", buildingTypes);
        wrapper.eq(fillReport != null, "wb.FILL_REPORT", fillReport);
        wrapper.in(buildingLevels != null && buildingLevels.size() != 0, "wd.BUILDING_LEVEL", buildingLevels);
        wrapper.orderByAsc("wd.MNG_UNIT_ID", "wd.PID", "wb.ID");
        List<WrBuildingAndDiversion> poList = baseMapper.getPBuildingAndDiversionList(wrapper);

        return convert2MngUnitAndBuildingsList(poList);
    }

    @Override
    public List<MngUnitAndWrUseUnit> getMngAndWrUseUnitsByMng(List<String> mngUnitIds, List<Integer> unitLevels, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels) {
        List<MngUnitAndWrUseUnit> resultList = new ArrayList<>();
        List<BuildingExt> buildingExtList = getBuildingExtListByMng(mngUnitIds, unitLevels, buildingTypes, fillReport, buildingLevels);
        return distinctMngUnitAndWrUseUnit(resultList, buildingExtList);
    }

    @Override
    public List<MngUnitAndWrUseUnit> getMngAndWrUseUnitsByUser(String userId, List<Integer> unitLevels, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels) {
        List<MngUnitAndWrUseUnit> resultList = new ArrayList<>();
        List<BuildingExt> buildingExtList = getBuildingExtListByUser(userId, unitLevels, buildingTypes, fillReport, buildingLevels);
        return distinctMngUnitAndWrUseUnit(resultList, buildingExtList);
    }

    @Override
    public List<BuildingExt> getBuildingExtListByUser(String userId, List<Integer> unitLevels, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels) {
        List<BuildingExt> extList = new ArrayList<>();
        //先通过用户ID 去查用户相关的用水单位ID 并过滤重复项
        List<WrUseUnitPerson> wrUseUnitPersonList = wrUseUnitPersonService.lambdaQuery().eq(StringUtils.isNotEmpty(userId), WrUseUnitPerson::getUserId, userId).list();
        List<String> unitIdList = wrUseUnitPersonList.stream().map(WrUseUnitPerson::getUnitId).distinct().collect(Collectors.toList());
        if (unitIdList.size() == 0) {
            return extList;
        }
        //通过用水单位ID查用水单位
        //取出用水单位的路径集合
        List<WrUseUnitManager> wrUseUnitManagerList = wrUseUnitManagerService.listByIds(unitIdList);
        List<String> pathList = wrUseUnitManagerList.stream().map(WrUseUnitManager::getPath).collect(Collectors.toList());
        //获取用水单位后，根据path获取一级节点ID
        Set<String> rootIdSet = new HashSet<>();
        wrUseUnitManagerList.forEach(po -> {
            String rootId = wrUseUnitManagerService.getRootId(po);
            if (StringUtils.isNotEmpty(rootId)) {
                rootIdSet.add(rootId);
            }
        });
        List<String> rootIdList = new ArrayList<>(rootIdSet);
        //根据一级节点从缓存获取树
        //遍历树 找叶结点并且在用户所属用水单位路径下
        List<WrUseUnitNode> nodeList = cacheService.getWaterUseUnitTreeList(rootIdList);
        List<WrUseUnitManager> leafList = new ArrayList<>();
        //所有用水单位节点的ID-NAME 键值对
        Map<String, String> idNameMap = new HashMap<>();
        //因为测站只挂在最后一层用水单位下,所以要先查最底层的用水单位
        //获取用水单位叶结点
        nodeList.forEach(wrUseUnitNode -> findLeaf(wrUseUnitNode, pathList, leafList, idNameMap));

        List<String> leafIdList = leafList.stream().map(WrUseUnitManager::getId).collect(Collectors.toList());
        Map<String, WrUseUnitManager> leafMap = leafList.stream().collect(Collectors.toMap(WrUseUnitManager::getId, leaf -> leaf));
        //根据用水单位叶结点ID查询底下的测站
        //根据测站类型筛选测站-引水口
        List<WrBuildingAndDiversion> buildingList = queryBuildingAndDiversionList(null, null, leafIdList, buildingTypes, fillReport, buildingLevels);
        //测站转整合后的数据类型BuildingExt
        extList = convert2BuildingExtList(buildingList, leafMap, unitLevels, idNameMap);
        return extList;
    }

    @Override
    public List<BuildingExt> getBuildingExtListByUnit(List<String> waterUnitIds, List<Integer> unitLevels, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels) {
        List<BuildingExt> extList = new ArrayList<>();
        //通过用水单位ID查用水单位
        //取出用水单位的路径集合
        List<WrUseUnitManager> wrUseUnitManagerList = wrUseUnitManagerService.listByIds(waterUnitIds);
        List<String> pathList = wrUseUnitManagerList.stream().map(WrUseUnitManager::getPath).collect(Collectors.toList());
        //获取用水单位后，根据path获取一级节点ID
        Set<String> rootIdSet = new HashSet<>();
        wrUseUnitManagerList.forEach(po -> {
            String rootId = wrUseUnitManagerService.getRootId(po);
            if (StringUtils.isNotEmpty(rootId)) {
                rootIdSet.add(rootId);
            }
        });
        List<String> rootIdList = new ArrayList<>(rootIdSet);
        //根据一级节点从缓存获取树
        //遍历树 找叶结点并且在用户所属用水单位路径下
        List<WrUseUnitNode> nodeList = cacheService.getWaterUseUnitTreeList(rootIdList);
        List<WrUseUnitManager> leafList = new ArrayList<>();
        //所有用水单位节点的ID-NAME 键值对
        Map<String, String> idNameMap = new HashMap<>();
        //因为测站只挂在最后一层用水单位下,所以要先查最底层的用水单位
        //获取用水单位叶结点
        nodeList.forEach(wrUseUnitNode -> findLeaf(wrUseUnitNode, pathList, leafList, idNameMap));

        List<String> leafIdList = leafList.stream().map(WrUseUnitManager::getId).collect(Collectors.toList());
        Map<String, WrUseUnitManager> leafMap = leafList.stream().collect(Collectors.toMap(WrUseUnitManager::getId, leaf -> leaf));
        //根据用水单位叶结点ID查询底下的测站
        //根据测站类型筛选测站-引水口
        List<WrBuildingAndDiversion> buildingList = queryBuildingAndDiversionList(null, null, leafIdList, buildingTypes, fillReport, buildingLevels);
        //测站转整合后的数据类型BuildingExt
        extList = convert2BuildingExtList(buildingList, leafMap, unitLevels, idNameMap);
        return extList;
    }


    @Override
    public List<BuildingExt> getBuildingExtListByMng(List<String> mngUnitIds, List<Integer> unitLevels, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels) {
        List<BuildingExt> extList = new ArrayList<>();
        //根据管理单位查询测站引水口
        List<WrBuildingAndDiversion> buildingList = queryBuildingAndDiversionList(null, mngUnitIds, null, buildingTypes, fillReport, buildingLevels);
        //无测站直接返回
        if (buildingList == null || buildingList.size() == 0) {
            return extList;
        }
        //根据测站引水口查询用水单位（这里查到的就是叶结点）
        List<String> unitIdList = buildingList.stream().map(WrBuildingAndDiversion::getWaterUnitId).filter(StringUtils::isNotEmpty).distinct().collect(Collectors.toList());
        List<WrUseUnitManager> unitList = wrUseUnitManagerService.listByIds(unitIdList);
        List<String> pathList = unitList.stream().map(WrUseUnitManager::getPath).collect(Collectors.toList());
        //获取用水单位后，根据path获取一级节点ID
        Set<String> rootIdSet = new HashSet<>();
        unitList.forEach(po -> {
            String rootId = wrUseUnitManagerService.getRootId(po);
            if (StringUtils.isNotEmpty(rootId)) {
                rootIdSet.add(rootId);
            }
        });
        List<String> rootIdList = new ArrayList<>(rootIdSet);
        //根据一级节点从缓存获取树
        //遍历树 找叶结点并且在用水单位路径下
        List<WrUseUnitNode> nodeList = cacheService.getWaterUseUnitTreeList(rootIdList);

        List<WrUseUnitManager> leafList = new ArrayList<>();
        //所有用水单位节点的ID-NAME 键值对
        Map<String, String> idNameMap = new HashMap<>();
        //因为测站只挂在最后一层用水单位下,所以要先查最底层的用水单位
        //获取用水单位叶结点
        nodeList.forEach(wrUseUnitNode -> findLeaf(wrUseUnitNode, pathList, leafList, idNameMap));
        Map<String, WrUseUnitManager> leafMap = leafList.stream().collect(Collectors.toMap(WrUseUnitManager::getId, leaf -> leaf));
        //测站转整合后的数据类型BuildingExt
        extList = convert2BuildingExtList(buildingList, leafMap, unitLevels, idNameMap);
        return extList;
    }

    /**
     * 整合WrBuildingAndDiversion的管理单位、用水单位名称
     *
     * @param resultList 测站引水口联表信息
     * @param unitIdList 测站引水口左表ID
     */
    private void fillMngAndUnitName(List<WrBuildingAndDiversion> resultList, List<String> unitIdList) {
        List<WrUseUnitManager> unitList = wrUseUnitManagerService.listByIds(unitIdList);
        Map<String, WrUseUnitManager> unitMap = unitList.stream().collect(Collectors.toMap(WrUseUnitManager::getId, unit -> unit));
        resultList.forEach(result -> {
            String unitId = result.getWaterUnitId();
            String mngId = result.getMngUnitId();
            if (StringUtils.isNotEmpty(unitId) && unitMap.containsKey(unitId)) {
                result.setWaterUnitName(unitMap.get(unitId).getUnitName());
            }
            if (StringUtils.isNotEmpty(mngId)) {
                result.setMngUnitName(cacheService.getMngUnitName(mngId));
            }
        });
    }

    @Override
    public Boolean checkUniqueCode(String code) {
        Integer count = wrDiversionPortService.lambdaQuery().eq(StringUtils.isNotEmpty(code), WrDiversionPort::getBuildingCode, code).count();
        return count == 0;
    }


    /**
     * 根据条件查询测站信息
     *
     * @param mngUnitIds      所属管理单位
     * @param waterUseUnitIds 所属用水单位
     * @param buildingTypes   测站引水口类型
     * @param fillReport      是否填报
     * @return 测站信息list 按CODE排序
     */
    private List<WrBuildingAndDiversion> queryBuildingAndDiversionList(List<String> buildingIds, List<String> mngUnitIds, List<String> waterUseUnitIds, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels) {
        QueryWrapper<WaterBuildingManager> wrapper = new QueryWrapper<>();
        wrapper.in(buildingIds != null && buildingIds.size() != 0, "wb.ID", buildingIds);
        wrapper.in(mngUnitIds != null && mngUnitIds.size() != 0, "wd.MNG_UNIT_ID", mngUnitIds);
        wrapper.in(waterUseUnitIds != null && waterUseUnitIds.size() != 0, "wd.WATER_UNIT_ID", waterUseUnitIds);
        wrapper.in(buildingTypes != null && buildingTypes.size() != 0, "wb.BUILDING_TYPE", buildingTypes);
        wrapper.eq(fillReport != null, "wb.FILL_REPORT", fillReport);
        wrapper.in(buildingLevels != null && buildingLevels.size() != 0, "wd.BUILDING_LEVEL", buildingLevels);
        wrapper.orderByAsc("wb.ID");
        return baseMapper.getBuildingAndDiversionList(wrapper);
    }

    /**
     * 递归遍历树    找出子路径下的叶结点存入leafList  并保存所有节点的ID-NAME键值对
     *
     * @param node      节点
     * @param pathList  用水单位路径
     * @param leafList  用于存叶结点
     * @param idNameMap 用于存用水单位的ID-NAME键值对
     */
    private void findLeaf(WrUseUnitNode node, List<String> pathList, List<WrUseUnitManager> leafList, Map<String, String> idNameMap) {
        idNameMap.put(node.getId(), node.getName());
        if (node.getIsLeaf() && ifChildren(node.getPath(), pathList)) {
            WrUseUnitManager leaf = new WrUseUnitManager();
            leaf.setId(node.getId());
            leaf.setUnitName(node.getName());
            leaf.setUnitLevel(node.getLevel());
            leaf.setPath(node.getPath());
            leafList.add(leaf);
        }
        if (node.getChildren() != null) {
            node.getChildren().forEach(chNode -> findLeaf(chNode, pathList, leafList, idNameMap));
        }
    }

    /**
     * 判断是否为子节点路径
     *
     * @param path     路径
     * @param pathList 相关节点路径list
     * @return true是 false否
     */
    private boolean ifChildren(String path, List<String> pathList) {
        for (String str : pathList
        ) {
            if (path.contains(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 整合管理单位-用水单位-测站引水口
     *
     * @param po         测站
     * @param leafMap    叶结点map
     * @param unitLevels 所需层级
     * @param idNameMap  用水单位的id-name map
     * @return 管理单位-用水单位-测站引水口结构
     */
    private BuildingExt convert2BuildingExt(WrBuildingAndDiversion po, Map<String, WrUseUnitManager> leafMap, List<Integer> unitLevels, Map<String, String> idNameMap) {
        BuildingExt ext = new BuildingExt();
        ext.setBuildingId(po.getId());
        ext.setBuildingName(po.getBuildingName());
        ext.setMngUnitId(po.getMngUnitId());
        ext.setBuildingCode(po.getBuildingCode());
        //从缓存获取管理单位名称
        String mngUnitName = cacheService.getMngUnitName(po.getMngUnitId());
        ext.setMngUnitName(mngUnitName);
        //用水单位ID
        String unitId = po.getWaterUnitId();
        if (leafMap.containsKey(unitId)) {
            WrUseUnitManager leaf = leafMap.get(unitId);
            String path = leaf.getPath();
            String[] pathArr = path.split("/");
            //指定层级
            //增加逻辑 如果层级为-1 取最后一级
            if (unitLevels != null && unitLevels.size() != 0) {
                if (unitLevels.size() == 1 && unitLevels.get(0).equals(-1)) {
                    //取最后
                    WrUseUnitSimpleVO simpleVO = new WrUseUnitSimpleVO();
                    simpleVO.setWaterUnitId(leaf.getId());
                    //获取用水单位名称
                    if (idNameMap.containsKey(leaf.getId())) {
                        simpleVO.setWaterUnitName(leaf.getUnitName());
                    }
                    ext.getWaterUnits().add(simpleVO);
                } else {
                    for (Integer unitLevel : unitLevels) {
                        int index = unitLevel - 1;
                        if (index < pathArr.length) {
                            String currentUnitId = pathArr[index];
                            WrUseUnitSimpleVO simpleVO = new WrUseUnitSimpleVO();
                            simpleVO.setWaterUnitId(currentUnitId);
                            //获取用水单位名称
                            if (idNameMap.containsKey(currentUnitId)) {
                                simpleVO.setWaterUnitName(idNameMap.get(currentUnitId));
                            }
                            ext.getWaterUnits().add(simpleVO);
                        }
                    }
                }
            } else {
                //不指定层级查所有
                for (String currentUnitId : pathArr) {
                    WrUseUnitSimpleVO simpleVO = new WrUseUnitSimpleVO();
                    simpleVO.setWaterUnitId(currentUnitId);
                    //获取用水单位名称
                    if (idNameMap.containsKey(currentUnitId)) {
                        simpleVO.setWaterUnitName(idNameMap.get(currentUnitId));
                    }
                    ext.getWaterUnits().add(simpleVO);
                }
            }
        }
        return ext;
    }

    /**
     * 整合管理单位-用水单位-测站引水口 list
     *
     * @param poList     测站引水口信息
     * @param leafMap    用水单位叶节点MAP
     * @param unitLevels 所需用水单位层级
     * @param idNameMap  用水单位的id-name map
     * @return 管理单位-用水单位-测站引水口 list
     */
    private List<BuildingExt> convert2BuildingExtList(List<WrBuildingAndDiversion> poList, Map<String, WrUseUnitManager> leafMap, List<Integer> unitLevels, Map<String, String> idNameMap) {
        List<BuildingExt> extList = new ArrayList<>();
        poList.forEach(po -> extList.add(convert2BuildingExt(po, leafMap, unitLevels, idNameMap)));
        //按层级排序
        buildingExtListSort(extList);
        return extList;
    }

    /**
     * 转测站-引水口管理左表
     *
     * @param wrBuildingAndDiversion 测站引水口级联信息表
     * @return 测站引水口左表-水工建筑物表
     */
    private WaterBuildingManager convert2WaterBuildingManager(WrBuildingAndDiversion wrBuildingAndDiversion) {
        WaterBuildingManager waterBuildingManager = new WaterBuildingManager();
        BeanUtils.copyProperties(wrBuildingAndDiversion, waterBuildingManager);
        return waterBuildingManager;
    }

    /**
     * 转测站-引水口管理右表
     *
     * @param wrBuildingAndDiversion 测站引水口级联信息表
     * @return 测站引水口右表-扩展信息表
     */
    private WrDiversionPort convert2WrDiversionPort(WrBuildingAndDiversion wrBuildingAndDiversion) {
        WrDiversionPort wrDiversionPort = new WrDiversionPort();
        BeanUtils.copyProperties(wrBuildingAndDiversion, wrDiversionPort);
        return wrDiversionPort;
    }


    /**
     * 转型
     *
     * @param po 测站引水口
     * @return 管理单位-测站引水口结构
     */
    private MngUnitAndBuilding convert2MngUnitAndBuilding(WrBuildingAndDiversion po) {
        MngUnitAndBuilding vo = new MngUnitAndBuilding();
        BeanUtils.copyProperties(po, vo);
        vo.setBuildingId(po.getId());
        if (StringUtils.isNotEmpty(po.getMngUnitId())) {
            //缓存获取管理单位名称
            String mngUnitName = cacheService.getMngUnitName(po.getMngUnitId());
            vo.setMngUnitName(mngUnitName);
        }
        return vo;
    }

    /**
     * 转型
     *
     * @param po 测站引水口
     * @return 管理单位-上下级引水口结构
     */
    private MngUnitAndBuildings convert2MngUnitAndBuildings(WrBuildingAndDiversion po) {
        MngUnitAndBuildings vo = new MngUnitAndBuildings();
        BeanUtils.copyProperties(po, vo);
        vo.setBuildingId(po.getId());
        //设置上级引水口属性 如果是一二级共用 上级引水口为自身
        if (WrBuildingEnum.BUILDING_LEVEL_1_2.equals(po.getBuildingLevel())) {
            vo.setPBuildingId(po.getId());
            vo.setPBuildingName(po.getBuildingName());
            vo.setPBuildingCode(po.getBuildingCode());
        } else if (WrBuildingEnum.BUILDING_LEVEL_2.equals(po.getBuildingLevel())) {
            vo.setPBuildingId(po.getPid());
            vo.setPBuildingName(po.getPName());
            vo.setPBuildingCode(po.getPCode());
        }
        if (StringUtils.isNotEmpty(po.getMngUnitId())) {
            //缓存获取管理单位名称
            String mngUnitName = cacheService.getMngUnitName(po.getMngUnitId());
            vo.setMngUnitName(mngUnitName);
        }
        return vo;
    }

    /**
     * 转型
     *
     * @param poList 测站引水口list
     * @return 管理单位-测站引水口结构list
     */
    private List<MngUnitAndBuilding> convert2MngUnitAndBuildingList(List<WrBuildingAndDiversion> poList) {
        List<MngUnitAndBuilding> voList = new ArrayList<>();
        poList.forEach(po -> voList.add(convert2MngUnitAndBuilding(po)));
        voList.sort(((o1, o2) -> {
            if (StringUtils.isEmpty(o1.getMngUnitId()) && StringUtils.isNotEmpty(o2.getMngUnitId())) {
                return 1;
            } else if (StringUtils.isNotEmpty(o1.getMngUnitId()) && StringUtils.isEmpty(o2.getMngUnitId())) {
                return -1;
            } else if (StringUtils.isEmpty(o1.getMngUnitId()) && StringUtils.isEmpty(o2.getMngUnitId())) {
                return 0;
            } else if (o1.getMngUnitId().equals(o2.getMngUnitId())) {
                return o1.getBuildingId().compareTo(o2.getBuildingId());
            }
            return o1.getMngUnitId().compareTo(o2.getMngUnitId());
        }));
        return voList;
    }

    /**
     * 转型
     *
     * @param poList 测站引水口list
     * @return 管理单位-测站引水口结构list
     */
    private List<MngUnitAndBuildings> convert2MngUnitAndBuildingsList(List<WrBuildingAndDiversion> poList) {
        List<MngUnitAndBuildings> voList = new ArrayList<>();
        poList.forEach(po -> voList.add(convert2MngUnitAndBuildings(po)));
        voList.sort(((o1, o2) -> {
            if (StringUtils.isEmpty(o1.getMngUnitId()) && StringUtils.isNotEmpty(o2.getMngUnitId())) {
                return 1;
            } else if (StringUtils.isNotEmpty(o1.getMngUnitId()) && StringUtils.isEmpty(o2.getMngUnitId())) {
                return -1;
            } else if (StringUtils.isEmpty(o1.getMngUnitId()) && StringUtils.isEmpty(o2.getMngUnitId())) {
                return 0;
            } else if (o1.getMngUnitId().equals(o2.getMngUnitId())) {
                if (StringUtils.isEmpty(o1.getPBuildingId()) && StringUtils.isNotEmpty(o2.getPBuildingId())) {
                    return 1;
                } else if (StringUtils.isNotEmpty(o1.getPBuildingId()) && StringUtils.isEmpty(o2.getPBuildingId())) {
                    return -1;
                } else if (StringUtils.isEmpty(o1.getPBuildingId()) && StringUtils.isEmpty(o2.getPBuildingId())) {
                    return 0;
                } else if (o1.getPBuildingId().equals(o2.getPBuildingId())) {
                    return o1.getBuildingId().compareTo(o2.getBuildingId());
                }
                return o1.getPBuildingId().compareTo(o2.getPBuildingId());
            }
            return o1.getMngUnitId().compareTo(o2.getMngUnitId());
        }));
        return voList;
    }

    /**
     * 转型
     *
     * @param ext 管理单位-用水单位-测站引水口结构
     * @return 管理单位-用水单位结构
     */
    private MngUnitAndWrUseUnit convert2MngUnitAndWrUseUnit(BuildingExt ext) {
        MngUnitAndWrUseUnit mngUnitAndWrUseUnit = new MngUnitAndWrUseUnit();
        mngUnitAndWrUseUnit.setMngUnitId(ext.getMngUnitId());
        mngUnitAndWrUseUnit.setMngUnitName(ext.getMngUnitName());
        mngUnitAndWrUseUnit.setWaterUnits(ext.getWaterUnits());
        return mngUnitAndWrUseUnit;
    }

    /**
     * 转型
     *
     * @param extList 管理单位-用水单位-测站引水口结构list
     * @return 管理单位-用水单位结构list
     */
    private List<MngUnitAndWrUseUnit> convert2MngUnitAndWrUseUnitList(List<BuildingExt> extList) {
        List<MngUnitAndWrUseUnit> mngUnitAndWrUseUnitList = new ArrayList<>();
        extList.forEach(ext -> mngUnitAndWrUseUnitList.add(convert2MngUnitAndWrUseUnit(ext)));
        return mngUnitAndWrUseUnitList;
    }

    /**
     * 转型
     *
     * @param unit 最后一级用水单位
     * @return 最后一级用水单位-测站引水口结构
     */
    private WrUseUnitAndBuilding convert2WrUseUnitAndBuilding(WrUseUnitManager unit) {
        WrUseUnitAndBuilding unitAndBuilding = new WrUseUnitAndBuilding();
        unitAndBuilding.setWaterUnitId(unit.getId());
        unitAndBuilding.setWaterUnitName(unit.getUnitName());
        return unitAndBuilding;
    }

    /**
     * 按管理单位+用水单位 过滤管理单位-用水单位结构重复项
     *
     * @param resultList      管理单位-用水单位结构
     * @param buildingExtList 管理单位-用水单位-测站引水口结构
     * @return 管理单位-用水单位结构
     */
    private List<MngUnitAndWrUseUnit> distinctMngUnitAndWrUseUnit(List<MngUnitAndWrUseUnit> resultList, List<BuildingExt> buildingExtList) {
        if (buildingExtList != null && buildingExtList.size() != 0) {
            //按管理单位+用水单位 过滤重复项
            List<BuildingExt> filterList = buildingExtList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getMngUnitId() + ";" + JSON.toJSONString(o.getWaterUnits())))), ArrayList::new));
            resultList = convert2MngUnitAndWrUseUnitList(filterList);
        }
        return resultList;
    }

    /**
     * 按层级排序
     * 先按管理单位排，为空放最后
     * 再按用水单位所选层级遍历排，为空放最后
     * 最后按测站排
     *
     * @param extList 管理单位-用水单位-测站引水口结构
     */
    private void buildingExtListSort(List<BuildingExt> extList) {
        extList.sort((o1, o2) -> {
            if (StringUtils.isEmpty(o1.getMngUnitId()) && StringUtils.isNotEmpty(o2.getMngUnitId())) {
                return 1;
            } else if (StringUtils.isNotEmpty(o1.getMngUnitId()) && StringUtils.isEmpty(o2.getMngUnitId())) {
                return -1;
            } else if (StringUtils.isEmpty(o1.getMngUnitId()) && StringUtils.isEmpty(o2.getMngUnitId())) {
                return 0;
            } else if (o1.getMngUnitId().equals(o2.getMngUnitId())) {
                //用水单位为空置于最后
                if (o1.getWaterUnits().size() == 0 && o2.getWaterUnits().size() != 0) {
                    return 1;
                } else if (o1.getWaterUnits().size() != 0 && o2.getWaterUnits().size() == 0) {
                    return -1;
                } else if (o1.getWaterUnits().size() == 0 && o2.getWaterUnits().size() == 0) {
                    return 0;
                }
                //如果长度不同 排最小即可
                int count = Math.min(o1.getWaterUnits().size(), o2.getWaterUnits().size());
                for (int i = 0; i < count; i++) {
                    String wrUnitId1 = o1.getWaterUnits().get(i).getWaterUnitId();
                    String wrUnitId2 = o2.getWaterUnits().get(i).getWaterUnitId();
                    //相等直接比下一层 不等进行排序
                    if (!wrUnitId1.equals(wrUnitId2)) {
                        return wrUnitId1.compareTo(wrUnitId2);
                    }
                }
                return o1.getBuildingId().compareTo(o2.getBuildingId());
            }
            return o1.getMngUnitId().compareTo(o2.getMngUnitId());
        });
    }
}
