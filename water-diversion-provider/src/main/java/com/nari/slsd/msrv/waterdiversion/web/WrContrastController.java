package com.nari.slsd.msrv.waterdiversion.web;


import com.alibaba.fastjson.JSONObject;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import com.nari.slsd.msrv.waterdiversion.feign.interfaces.ActivitiFeignClient;
import com.nari.slsd.msrv.waterdiversion.feign.interfaces.StudioFeignClient;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrContrastService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrPlanInterDayService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrPlanTaskService;
import com.nari.slsd.msrv.waterdiversion.model.vo.BuildingTask;
import com.nari.slsd.msrv.waterdiversion.model.vo.PlanContrast;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrPlanDataContrast;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseUnitNode;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  同期对比 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2021-08-05
 */
@RestController
@RequestMapping("/api/contrast")
public class WrContrastController {

    @Resource
    IWrContrastService wrContrastService;


    @GetMapping("/all")
    public ResultModel findtest() {
        try {
            List<WrUseUnitNode> wrUseUnitNodes = wrContrastService.getAllWaterUseUnitList();
            return ResultModelUtils.getSuccessInstanceExt(wrUseUnitNodes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     *
     * @param id 节点id
     * @param levels 树等级
     * @return
     */
    @GetMapping("/building-contrast")
    public ResultModel buildingContrast(@RequestParam(value = "id") String id,
                                        @RequestParam(value = "levels",required = false) String levels) {
        try {
            PlanContrast buildingContrast = wrContrastService.buildingContrast(id,levels);
            return ResultModelUtils.getSuccessInstanceExt(buildingContrast);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

//        try {
//            DataTableVO dataTableVO = wrPlanTaskService.findWrPlanTask(userId,unitLevel,buildType,startTime,endTime,year,pageIndex,pageSize);
//            return dataTableVO;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResultModelUtils.getFailInstanceExt();
//        }

}

