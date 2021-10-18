package com.nari.slsd.msrv.waterdiversion.web;


import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWmProjectLastRService;
import com.nari.slsd.msrv.waterdiversion.model.vo.BuildingExt;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * wm数据 前端控制器
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-30
 */
@RestController
@RequestMapping("/api/wm-data")
public class WmDataServiceController {

    @Resource
    IWmProjectLastRService wmProjectLastRService;


    /**
     * 查询 最新水情数据（水位、流量）
     *
     * @return 按管理单位-用水单位-引水口结构给出的水情数据
     */
    @GetMapping("/list")
    public ResultModel getNewestDataList(@RequestParam(value = "mngUnitIds") List<String> mngUnitIds,
                                         @RequestParam(value = "unitLevels", required = false) List<Integer> unitLevels,
                                         @RequestParam(value = "buildingTypes", required = false) List<String> buildingTypes,
                                         @RequestParam(value = "fillReport", required = false) Integer fillReport,
                                         @RequestParam(value = "buildingLevels", required = false) List<Integer> buildingLevels) {
        try {
            List<BuildingExt> result = wmProjectLastRService.getNewestDataWithBuildingExt(mngUnitIds, unitLevels, buildingTypes, fillReport, buildingLevels);
            return ResultModelUtils.getSuccessInstance(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

}

