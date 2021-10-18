package com.nari.slsd.msrv.waterdiversion.web;

import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrPlanFillInYearService;
import com.nari.slsd.msrv.waterdiversion.model.dto.PlanFillinExamineDTO;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrPlanFillInValue;
import com.nari.slsd.msrv.waterdiversion.model.vo.BuildingExt;
import com.nari.slsd.msrv.waterdiversion.model.vo.PlanFillinStateExt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 年度用水计划填报 前端控制器前端控制器
 * </p>
 *
 * @author zhs
 * @since 2021-08-04
 */
@RestController
@RequestMapping("/api/water-plan-year")
@Slf4j
public class WrPlanFillInYearController {
    @Resource
    IWrPlanFillInYearService waterPlanFillinYearService;

    /**
     * 新增用水单位
     *
     * @param wrPlanFillInValue
     * @return
     */
    @PostMapping
    public ResultModel addPlanFillinYear(@RequestBody WrPlanFillInValue wrPlanFillInValue) {
        try {
            waterPlanFillinYearService.addWaterPlanValue(wrPlanFillInValue);
            return ResultModelUtils.getAddSuccessInstance(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

//    /**
//     *  查询年计划审批
//     * @return
//     */
//    @GetMapping("/water-quantity")
//    public ResultModel findPlanFillinYear(@RequestBody PlanFillinExamineDTO planFillinExamineDTO) {
//        try {
//            List<BuildingExt>  waterPlanFillinYearVOList= waterPlanFillinYearService.findPlanValueByCodAndTime(planFillinExamineDTO);
//            return ResultModelUtils.getSuccessInstance(waterPlanFillinYearVOList);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResultModelUtils.getFailInstanceExt();
//        }
//    }

    /**
     *   查询用户（管理站）权限下的填报单位
     *
     * @param time
     * @param userId
     * @param unitLevels
     * @param buildingType
     * @param fillReport
     * @return
     */
    @GetMapping("/unit")
    public ResultModel findUnitFillinYear(@RequestParam(value = "time") String time,
                                          @RequestParam(value = "userId",required = false) String userId,
                                          @RequestParam(value = "mngUnitId",required = false) List<String> mngUnitId,
                                          @RequestParam(value = "unitLevels", required = false) List<Integer> unitLevels,
                                          @RequestParam(value = "buildingType", required = false) List<String> buildingType,
                                          @RequestParam(value = "fillReport", required = false) Integer fillReport,
                                          @RequestParam(value = "buildingLevels", required = false) List<Integer> levels) {
        try {
            PlanFillinStateExt waterPlanFillinYearVOList= waterPlanFillinYearService.findUnitByCodAndTime(time,mngUnitId,unitLevels,buildingType,fillReport,levels);
            return ResultModelUtils.getSuccessInstance(waterPlanFillinYearVOList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }
    /**
     *   查询年计划汇总（管理站）
     *
     * @param time
     * @param userId
     * @param unitLevels
     * @param buildingType
     * @param fillReport
     * @return
     */
    @GetMapping("/summary")
    public ResultModel findPlanFillinMonth(@RequestParam(value = "time") String time,
                                           @RequestParam(value = "userId",required = false) String userId,
                                           @RequestParam(value = "mngUnitId",required = false) List<String> mngUnitId,
                                           @RequestParam(value = "unitLevels", required = false) List<Integer> unitLevels,
                                           @RequestParam(value = "buildingType", required = false) List<String> buildingType,
                                           @RequestParam(value = "fillReport", required = false) Integer fillReport,
                                           @RequestParam(value = "buildingLevels", required = false) List<Integer> levels,
                                           @RequestParam(value = "type", required = false) String type) {//0、一级 1、二级
        try {
            List<BuildingExt>  waterPlanFillinYearVOList= waterPlanFillinYearService.findPlanAllValueByCodAndTime(time,userId,mngUnitId,unitLevels,buildingType,fillReport,levels,type);
            return ResultModelUtils.getSuccessInstance(waterPlanFillinYearVOList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }
    @PutMapping
    public ResultModel updateWaterValue(@RequestBody WrPlanFillInValue wrPlanFillInValue) {
        try {
            waterPlanFillinYearService.updateWaterPlanValue(wrPlanFillInValue);
            return ResultModelUtils.getEdiSuccessInstance(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }
}
