package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrBuildingAndDiversion;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WaterBuildingManager;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nari.slsd.msrv.waterdiversion.model.vo.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 水工建筑物管理 服务类
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-03
 */
public interface IWaterBuildingManagerService extends IService<WaterBuildingManager> {
    /**
     * 联表分页查询测站-引水口
     *
     * @param pageIndex      页码
     * @param pageSize       页面大小
     * @param buildingTypes  测站引水口类型
     * @param waterUnitId    用水单位ID 过滤条件
     * @param mngUnitId      管理单位ID 过滤条件
     * @param buildingLevels 引水口层级
     * @return 测站引水口信息分页
     */
    DataTableVO getBuildingAndDiversionPage(Integer pageIndex, Integer pageSize, List<String> buildingTypes, List<Integer> buildingLevels, String waterUnitId, String mngUnitId);


    /**
     * 查询测站list
     *
     * @param buildingTypes  测站引水口类型
     * @param buildingLevels 引水口层级
     * @param search         模糊查询
     * @return 引水口基本信息
     */
    List<WrBuildingSimpleVO> getBuildingAndDiversionList(List<String> buildingTypes, List<Integer> buildingLevels, String search);


    /**
     * 编辑更新测站-引水口(双表更新)
     *
     * @param dto 测站引水口DTO
     */
    void updateBuildingAndDiversion(WrBuildingAndDiversion dto);

    /**
     * 获取管理单位下的管理单位-测站引水口树模型
     *
     * @param mngUnitIds     视图权限获取的管理单位ID
     * @param buildingTypes  测站引水口类型
     * @param buildingLevels 引水口层级
     * @return 管理单位-测站两层树模型
     */
    List<CommonNode> getMngAndBuildingsTree(List<String> mngUnitIds, List<String> buildingTypes, List<Integer> buildingLevels);

    /**
     * 获取管理单位下的管理单位-测站引水口
     *
     * @param mngUnitIds     视图权限获取的管理单位ID
     * @param buildingTypes  测站引水口类型
     * @param fillReport     是否填报 0否 1是 报表相关传1
     * @param buildingLevels 引水口层级
     * @return 管理单位-测站引水口list 按层级排序
     */
    List<MngUnitAndBuilding> getMngAndBuildingsByMng(List<String> mngUnitIds, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels);

    /**
     * 获取管理单位下的管理单位-上级引水口-引水口
     *
     * @param mngUnitIds     视图权限获取的管理单位ID
     * @param buildingTypes  测站引水口类型
     * @param fillReport     是否填报 0否 1是 报表相关传1
     * @param buildingLevels 引水口层级
     * @return 管理单位-测站引水口list 按层级排序
     */
    List<MngUnitAndBuildings> getMngAndPBuildingsByMng(List<String> mngUnitIds, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels);


    /**
     * 获取用户下指定层级的管理单位-用水单位-测站引水口
     *
     * @param userId         用户ID
     * @param unitLevels     所需用水单位层级
     * @param buildingTypes  测站引水口类型
     * @param fillReport     是否填报 0否 1是 报表相关传1
     * @param buildingLevels 引水口层级
     * @return 管理单位-用水单位-测站引水口list 按层级排序
     */
    List<BuildingExt> getBuildingExtListByUser(String userId, List<Integer> unitLevels, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels);


    /**
     * 获取管理单位下指定层级的管理单位-用水单位-测站引水口
     *
     * @param mngUnitIds     视图权限获取的管理单位ID
     * @param unitLevels     所需用水单位层级
     * @param buildingTypes  测站引水口类型
     * @param fillReport     是否填报 0否 1是 报表相关传1
     * @param buildingLevels 引水口层级
     * @return 管理单位-用水单位-测站引水口list 按层级排序
     */
    List<BuildingExt> getBuildingExtListByMng(List<String> mngUnitIds, List<Integer> unitLevels, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels);

    /**
     * 获取用水单位相关的管理单位-用水单位-测站引水口
     *
     * @param waterUnitIds   最后一层用水单位ID
     * @param unitLevels     所需用水单位层级 传-1查最后一级
     * @param buildingTypes  测站引水口类型
     * @param fillReport     是否填报 0否 1是 报表相关传1
     * @param buildingLevels 引水口层级
     * @return 管理单位-用水单位-测站引水口list 按层级排序
     */
    List<BuildingExt> getBuildingExtListByUnit(List<String> waterUnitIds, List<Integer> unitLevels, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels);


    /**
     * 获取管理单位下的管理单位-用水单位
     *
     * @param mngUnitIds     视图权限获取的管理单位ID
     * @param unitLevels     所需用水单位层级
     * @param buildingTypes  测站引水口类型
     * @param fillReport     是否填报 0否 1是 报表相关传1
     * @param buildingLevels 引水口层级
     * @return 管理单位-用水单位 按层级排序
     */
    List<MngUnitAndWrUseUnit> getMngAndWrUseUnitsByMng(List<String> mngUnitIds, List<Integer> unitLevels, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels);

    /**
     * 获取用户下的管理单位-用水单位
     *
     * @param userId         用户ID
     * @param unitLevels     所需用水单位层级
     * @param buildingTypes  测站引水口类型
     * @param fillReport     是否填报 0否 1是 报表相关传1
     * @param buildingLevels 引水口层级
     * @return 管理单位-用水单位 按层级排序
     */
    List<MngUnitAndWrUseUnit> getMngAndWrUseUnitsByUser(String userId, List<Integer> unitLevels, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels);

    /**
     * 根据测站引水口ID获取测站引水口信息
     *
     * @param stationIds 测站引水口ID
     * @return 测站引水口信息
     */
    List<WrBuildingAndDiversion> getWrBuildingAndDiversionList(List<String> stationIds);

    /**
     * 根据ID获取测站引水口联表信息MAP 用于整合测站引水口名称
     * <p>
     *
     * @param stationIds 测站引水口ID
     * @return 测站引水口基础信息MAP
     */
    Map<String, WrBuildingAndDiversion> getBuildingMapByIds(List<String> stationIds);

    /**
     * 根据code获取测站引水口联表信息MAP 用于整合测站引水口名称
     * <p>
     *
     * @param stationCodes 测站引水口CODE
     * @return 测站引水口基础信息MAP
     */
    Map<String, WrBuildingAndDiversion> getBuildingMapByCodes(List<String> stationCodes);


    /**
     * 根据用水单位ID获取测站引水口信息
     *
     * @param waterUnitIds   用水单位ID
     * @param buildingTypes  测站引水口类型
     * @param fillReport     是否填报 0否 1是 报表相关传1
     * @param buildingLevels 引水口层级
     * @return 测站引水口信息
     */
    List<WrBuildingAndDiversion> getWrBuildingAndDiversionListByUnit(List<String> waterUnitIds, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels);


    /**
     * 根据用户ID 查最后一层用水单位及其下属测站引水口
     *
     * @param userId         用户ID
     * @param ifBuilding     是否需要测站层级
     * @param buildingTypes  测站引水口类型
     * @param fillReport     是否填报 0否 1是 报表相关传1
     * @param buildingLevels 引水口层级
     * @return 最后一层用水单位-测站引水口
     */
    List<WrUseUnitAndBuilding> getWrUseUnitAndBuildingsByUser(String userId, Boolean ifBuilding, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels);

    /**
     * 根据管理单位 查最后一层用水单位及其下属测站引水口
     *
     * @param mngUnitIds     视图权限获取的管理单位ID
     * @param ifBuilding     是否需要测站层级
     * @param buildingTypes  测站引水口类型
     * @param fillReport     是否填报 0否 1是 报表相关传1
     * @param buildingLevels 测站层级
     * @return 最后一层用水单位-测站引水口
     */
    List<WrUseUnitAndBuilding> getWrUseUnitAndBuildingsByMng(List<String> mngUnitIds, Boolean ifBuilding, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels);


    /**
     * 校验code唯一性
     *
     * @param code 引水口编码
     * @return  是否唯一
     */
    Boolean checkUniqueCode(String code);
}
