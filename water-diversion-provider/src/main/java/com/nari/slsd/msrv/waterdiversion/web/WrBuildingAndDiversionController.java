package com.nari.slsd.msrv.waterdiversion.web;

import com.nari.slsd.msrv.common.model.ResultCode;
import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWaterBuildingManagerService;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrBuildingAndDiversion;
import com.nari.slsd.msrv.waterdiversion.model.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 测站-引水口管理 前端控制器
 * </p>
 *
 * @author reset kalar
 * @since 2021-07-29
 */
@RestController
@RequestMapping("/api/building-and-diversion")
@Slf4j
public class WrBuildingAndDiversionController {

    @Resource
    IWaterBuildingManagerService waterBuildingManagerService;

    /**
     * 修改用水单位
     *
     * @param dto 用水单位DTO
     * @return 是否编辑成功
     */
    @PutMapping
    public ResultModel updateBuildingAndDiversion(@RequestBody WrBuildingAndDiversion dto) {
        try {
            waterBuildingManagerService.updateBuildingAndDiversion(dto);
            return ResultModelUtils.getEdiSuccessInstance(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 分页查询
     *
     * @param pageIndex      起始页码
     * @param pageSize       页面大小
     * @param buildingTypes  类型 测站/引水口
     * @param waterUnitId    上级用水单位 编码
     * @param mngUnitId      上级管理单位ID
     * @param buildingLevels 引水口层级
     * @return 引水口信息分页
     */
    @GetMapping("/page")
    public ResultModel getBuildingAndDiversionPage(@RequestParam(value = "pageIndex") Integer pageIndex,
                                                   @RequestParam(value = "pageSize") Integer pageSize,
                                                   @RequestParam(value = "buildingTypes", required = false) List<String> buildingTypes,
                                                   @RequestParam(value = "buildingLevels", required = false) List<Integer> buildingLevels,
                                                   @RequestParam(value = "waterUnitId", required = false) String waterUnitId,
                                                   @RequestParam(value = "mngUnitId", required = false) String mngUnitId) {
        try {
            return waterBuildingManagerService.getBuildingAndDiversionPage(pageIndex, pageSize, buildingTypes, buildingLevels, waterUnitId, mngUnitId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 查询上级引水口基本信息
     *
     * @param buildingTypes  引水口类型
     * @param buildingLevels 引水口层级 传1 只查一级
     * @param search         按名称、编码模糊查询（未使用）
     * @return 引水口ID CODE NAME 简单结果集
     */
    @GetMapping("/list")
    public ResultModel getBuildingSimpleList(@RequestParam(value = "buildingTypes", required = false) List<String> buildingTypes,
                                             @RequestParam(value = "buildingLevels") List<Integer> buildingLevels,
                                             @RequestParam(value = "search", required = false) String search) {
        try {
            List<WrBuildingSimpleVO> result = waterBuildingManagerService.getBuildingAndDiversionList(buildingTypes, buildingLevels, search);
            return ResultModelUtils.getSuccessInstance(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }

    }


    /**
     * 查用水单位下的测站-引水口
     * 服务用
     *
     * @param waterUnitIds   用水单位ID
     * @param buildingTypes  引水口类型
     * @param fillReport     是否需要填报
     * @param buildingLevels 引水口层级
     * @return 引水口结果集
     */
    @GetMapping("/unit/list")
    public ResultModel getBuildingAndDiversionListByUnit(@RequestParam(value = "waterUnitIds") List<String> waterUnitIds,
                                                         @RequestParam(value = "buildingTypes", required = false) List<String> buildingTypes,
                                                         @RequestParam(value = "fillReport", required = false) Integer fillReport,
                                                         @RequestParam(value = "buildingLevels", required = false) List<Integer> buildingLevels) {
        try {
            List<WrBuildingAndDiversion> voList = waterBuildingManagerService.getWrBuildingAndDiversionListByUnit(waterUnitIds, buildingTypes, fillReport, buildingLevels);
            return ResultModelUtils.getSuccessInstance(voList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 查管理单位下的测站-引水口
     * 管理单位 查 管理单位下引水口（历史数据双下拉框有用到）
     *
     * @param mngUnitIds     管理单位ID
     * @param buildingTypes  引水口类型
     * @param fillReport     是否需要填报
     * @param buildingLevels 引水口层级
     * @return 管理单位-引水口结构
     */
    @GetMapping("/mng/mng-building")
    public ResultModel getMngAndBuildingsByMng(@RequestParam(value = "mngUnitIds") List<String> mngUnitIds,
                                               @RequestParam(value = "buildingTypes", required = false) List<String> buildingTypes,
                                               @RequestParam(value = "fillReport", required = false) Integer fillReport,
                                               @RequestParam(value = "buildingLevels", required = false) List<Integer> buildingLevels) {
        try {
            List<MngUnitAndBuilding> voList = waterBuildingManagerService.getMngAndBuildingsByMng(mngUnitIds, buildingTypes, fillReport, buildingLevels);
            return ResultModelUtils.getSuccessInstance(voList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }


    /**
     * 查管理单位下的用水单位
     * 服务用
     *
     * @param mngUnitIds     管理单位层级
     * @param unitLevels     用水单位层级
     * @param buildingTypes  引水口类型
     * @param fillReport     是否需要填报
     * @param buildingLevels 引水口层级
     * @return 管理单位-用水单位结构
     */
    @GetMapping("/mng/mng-unit")
    public ResultModel getMngAndWrUseUnitsByMng(@RequestParam(value = "mngUnitIds") List<String> mngUnitIds,
                                                @RequestParam(value = "unitLevels", required = false) List<Integer> unitLevels,
                                                @RequestParam(value = "buildingTypes", required = false) List<String> buildingTypes,
                                                @RequestParam(value = "fillReport", required = false) Integer fillReport,
                                                @RequestParam(value = "buildingLevels", required = false) List<Integer> buildingLevels) {
        try {
            List<MngUnitAndWrUseUnit> voList = waterBuildingManagerService.getMngAndWrUseUnitsByMng(mngUnitIds, unitLevels, buildingTypes, fillReport, buildingLevels);
            return ResultModelUtils.getSuccessInstance(voList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }


    /**
     * 根据用户ID 查相关用水单位的指定层级、测站-引水口、管理单位
     * 服务用
     *
     * @param userId         用户ID
     * @param unitLevels     用水单位层级
     * @param buildingTypes  引水口类型
     * @param fillReport     是否需要填报
     * @param buildingLevels 引水口层级
     * @return 管理单位-用水单位-引水口结构
     */
    @GetMapping("/user/mng-unit-building")
    public ResultModel getBuildingExtListByUser(@RequestParam(value = "userId") String userId,
                                                @RequestParam(value = "unitLevels", required = false) List<Integer> unitLevels,
                                                @RequestParam(value = "buildingTypes", required = false) List<String> buildingTypes,
                                                @RequestParam(value = "fillReport", required = false) Integer fillReport,
                                                @RequestParam(value = "buildingLevels", required = false) List<Integer> buildingLevels) {
        try {
            List<BuildingExt> voList = waterBuildingManagerService.getBuildingExtListByUser(userId, unitLevels, buildingTypes, fillReport, buildingLevels);
            return ResultModelUtils.getSuccessInstance(voList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 根据管理单位 查相关用水单位的指定层级、测站-引水口、管理单位
     * 服务用
     *
     * @param mngUnitIds     管理单位
     * @param unitLevels     用水单位层级
     * @param buildingTypes  引水口类型
     * @param fillReport     是否需要填报
     * @param buildingLevels 引水口层级
     * @return 管理单位-用水单位-引水口结构
     */
    @GetMapping("/mng/mng-unit-building")
    public ResultModel getBuildingExtListByMng(@RequestParam(value = "mngUnitIds") List<String> mngUnitIds,
                                               @RequestParam(value = "unitLevels", required = false) List<Integer> unitLevels,
                                               @RequestParam(value = "buildingTypes", required = false) List<String> buildingTypes,
                                               @RequestParam(value = "fillReport", required = false) Integer fillReport,
                                               @RequestParam(value = "buildingLevels", required = false) List<Integer> buildingLevels) {
        try {
            List<BuildingExt> voList = waterBuildingManagerService.getBuildingExtListByMng(mngUnitIds, unitLevels, buildingTypes, fillReport, buildingLevels);
            return ResultModelUtils.getSuccessInstance(voList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 获取用水单位相关的管理单位-用水单位-测站引水口
     * 服务用
     *
     * @param waterUnitIds   用水单位ID
     * @param unitLevels     传-1查最后一级
     * @param buildingTypes  引水口层级
     * @param fillReport     是否需要填报
     * @param buildingLevels 引水口层级
     * @return 管理单位-用水单位-引水口结构
     */
    @GetMapping("/unit/mng-unit-building")
    public ResultModel getBuildingExtListByUnit(@RequestParam(value = "waterUnitIds") List<String> waterUnitIds,
                                                @RequestParam(value = "unitLevels", required = false) List<Integer> unitLevels,
                                                @RequestParam(value = "buildingTypes", required = false) List<String> buildingTypes,
                                                @RequestParam(value = "fillReport", required = false) Integer fillReport,
                                                @RequestParam(value = "buildingLevels", required = false) List<Integer> buildingLevels) {
        try {
            List<BuildingExt> voList = waterBuildingManagerService.getBuildingExtListByUnit(waterUnitIds, unitLevels, buildingTypes, fillReport, buildingLevels);
            return ResultModelUtils.getSuccessInstance(voList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 根据用户ID 查相关用水单位的最后一层、测站-引水口
     *
     * @param userId         用户ID
     * @param ifBuilding     是否需要测站层级 false否 true是
     * @param buildingTypes  引水口类型
     * @param fillReport     是否需要填报
     * @param buildingLevels 引水口类型
     * @return 最后一层用水单位-引水口
     */
    @GetMapping("/user/unit-building")
    public ResultModel getUnitAndBuildingByUser(@RequestParam(value = "userId") String userId,
                                                @RequestParam(value = "ifBuilding") Boolean ifBuilding,
                                                @RequestParam(value = "buildingTypes", required = false) List<String> buildingTypes,
                                                @RequestParam(value = "fillReport", required = false) Integer fillReport,
                                                @RequestParam(value = "buildingLevels", required = false) List<Integer> buildingLevels) {
        try {
            List<WrUseUnitAndBuilding> voList = waterBuildingManagerService.getWrUseUnitAndBuildingsByUser(userId, ifBuilding, buildingTypes, fillReport, buildingLevels);
            return ResultModelUtils.getSuccessInstance(voList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 根据管理单位 查相关用水单位的最后一层、测站-引水口
     *
     * @param mngUnitIds     管理单位ID
     * @param ifBuilding     是否需要测站层级 false否 true是
     * @param buildingTypes  引水口类型
     * @param fillReport     是否需要填报
     * @param buildingLevels 引水口层级
     * @return 最后一层用水单位-引水口
     */
    @GetMapping("/mng/unit-building")
    public ResultModel getUnitAndBuildingByMng(@RequestParam(value = "mngUnitIds") List<String> mngUnitIds,
                                               @RequestParam(value = "ifBuilding") Boolean ifBuilding,
                                               @RequestParam(value = "buildingTypes", required = false) List<String> buildingTypes,
                                               @RequestParam(value = "fillReport", required = false) Integer fillReport,
                                               @RequestParam(value = "buildingLevels", required = false) List<Integer> buildingLevels) {
        try {
            List<WrUseUnitAndBuilding> voList = waterBuildingManagerService.getWrUseUnitAndBuildingsByMng(mngUnitIds, ifBuilding, buildingTypes, fillReport, buildingLevels);
            return ResultModelUtils.getSuccessInstance(voList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }


    /**
     * 查管理单位下的测站-引水口树模型
     *
     * @param mngUnitIds     管理单位ID
     * @param buildingTypes  引水口类型
     * @param buildingLevels 引水口层级
     * @return 森林模型
     */
    @GetMapping("/tree/mng-building")
    public ResultModel getMngAndWrUseUnitsTree(@RequestParam(value = "mngUnitIds") List<String> mngUnitIds,
                                               @RequestParam(value = "buildingTypes", required = false) List<String> buildingTypes,
                                               @RequestParam(value = "buildingLevels", required = false) List<Integer> buildingLevels) {
        try {
            List<CommonNode> voList = waterBuildingManagerService.getMngAndBuildingsTree(mngUnitIds, buildingTypes, buildingLevels);
            return ResultModelUtils.getSuccessInstance(voList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 引水口编码唯一性校验
     *
     * @param code
     * @return
     */
    @GetMapping("/code")
    public ResultModel codeCheck(@RequestParam("code") String code) {
        try {
            if (StringUtils.isEmpty(code)) {
                return ResultModelUtils.getFailInstanceExt("编码不能为空");
            }
            Boolean unique = waterBuildingManagerService.checkUniqueCode(code);
            if (unique) {
                return ResultModelUtils.getInstance(true, ResultCode.RESULT_SUCC.toString(), "编码唯一,校验通过");
            } else {
                return ResultModelUtils.getFailInstanceExt("编码不唯一,校验失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }
}
