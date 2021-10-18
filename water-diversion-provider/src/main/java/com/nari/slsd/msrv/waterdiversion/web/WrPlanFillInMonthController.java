package com.nari.slsd.msrv.waterdiversion.web;

import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrPlanFillInMonthService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrPlanGenerateMonthService;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrPlanFillInValue;
import com.nari.slsd.msrv.waterdiversion.model.vo.BuildingExt;
import com.nari.slsd.msrv.waterdiversion.model.vo.PlanFillinStateExt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 月度用水计划填报 前端控制器前端控制器
 * </p>
 *
 * @author zhs
 * @since 2021-08-04
 */
@RestController
@RequestMapping("/api/water-plan-month")
@Slf4j
public class WrPlanFillInMonthController {
    @Resource
    IWrPlanFillInMonthService waterPlanFillinMonthService;

    @Autowired
    private IWrPlanGenerateMonthService wrPlanGenerateMonthService;

    /**
     * 新增月计划填报
     *
     * @param wrPlanFillInValue
     * @return
     */
    @PostMapping
    public ResultModel addPlanFillinMonth(@RequestBody WrPlanFillInValue wrPlanFillInValue) {
        try {
            waterPlanFillinMonthService.addWaterPlanValue(wrPlanFillInValue);
            return ResultModelUtils.getAddSuccessInstance(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 月计划生成
     * @return
     */
    @PostMapping("auto-generate-month-plan")
    public ResultModel autoGenerateMonthPlan() {
        try {
            wrPlanGenerateMonthService.autoGenerateMonthPlan();
            return ResultModelUtils.getAddSuccessInstance(true);
        } catch (Exception e) {
            log.error("月计划自动生成失败，error is {}",e);
            return ResultModelUtils.getFailInstanceExt();
        }
    }
    /**
     * 修改月计划填报
     *
     * @param wrPlanFillInValue
     * @return
     */
    @PutMapping
    public ResultModel updatePlanFillinMonth(@RequestBody WrPlanFillInValue wrPlanFillInValue) {
        try {
            waterPlanFillinMonthService.updateWaterPlanValue(wrPlanFillInValue);
            return ResultModelUtils.getEdiSuccessInstance(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

//    /**
//     * 查询月计划审批
//     *
//     * @param year
//     * @param month
//     * @param userId
//     * @param unitLevels
//     * @param buildingType
//     * @param fillReport
//     * @return
//     */
//    @GetMapping("/water-quantity")
//    public ResultModel findPlanFillinMonth(@RequestParam(value = "year") String year,
//                                           @RequestParam(value = "month") String month,
//                                           @RequestParam(value = "userId", required = false) String userId,
//                                           @RequestParam(value = "unitLevels", required = false) List<Integer> unitLevels,
//                                           @RequestParam(value = "buildingType", required = false) List<String> buildingType,
//                                           @RequestParam(value = "fillReport", required = false) Integer fillReport,
//                                           @RequestParam(value = "buildingLevels", required = false) List<Integer> levels) {
//        try {
//            List<BuildingExt> waterPlanFillinYearVOList = waterPlanFillinMonthService.findPlanValueByCodAndTime(year, month, userId, unitLevels, buildingType, fillReport,levels);
//            return ResultModelUtils.getSuccessInstance(waterPlanFillinYearVOList);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResultModelUtils.getFailInstanceExt();
//        }
//    }

    /**
     * 查询用户下的填报单位
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
                                          @RequestParam(value = "userId", required = false) String userId,
                                          @RequestParam(value = "mngUnitId", required = false) List<String> mngUnitId,
                                          @RequestParam(value = "unitLevels", required = false) List<Integer> unitLevels,
                                          @RequestParam(value = "buildingType", required = false) List<String> buildingType,
                                          @RequestParam(value = "fillReport", required = false) Integer fillReport,
                                          @RequestParam(value = "buildingLevels", required = false) List<Integer> buildingLevels) {
        try {
            PlanFillinStateExt planFillinStateExt = waterPlanFillinMonthService.findUnitByCodAndTime(time, mngUnitId, unitLevels, buildingType, fillReport,buildingLevels);
            return ResultModelUtils.getSuccessInstance(planFillinStateExt);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 查询月计划汇总（管理站）
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
                                           @RequestParam(value = "userId", required = false) String userId,
                                           @RequestParam(value = "mngUnitId", required = false) List<String> mngUnitId,
                                           @RequestParam(value = "unitLevels", required = false) List<Integer> unitLevels,
                                           @RequestParam(value = "buildingType", required = false) List<String> buildingType,
                                           @RequestParam(value = "fillReport", required = false) Integer fillReport,
                                           @RequestParam(value = "buildingLevels", required = false) List<Integer> levels) {
        try {
            List<BuildingExt> waterPlanFillinYearVOList = waterPlanFillinMonthService.findPlanAllValueByCodAndTime(time, userId, mngUnitId, unitLevels, buildingType, fillReport,levels);
            return ResultModelUtils.getSuccessInstance(waterPlanFillinYearVOList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

}
