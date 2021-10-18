//package com.nari.slsd.msrv.waterdiversion.web;
//
//import com.nari.slsd.msrv.common.model.DataTableVO;
//import com.nari.slsd.msrv.common.model.ResultModel;
//import com.nari.slsd.msrv.common.utils.ResultModelUtils;
//import com.nari.slsd.msrv.waterdiversion.interfaces.IWrPlanFillinDayService;
//import com.nari.slsd.msrv.waterdiversion.model.dto.WrPlanFillInDayAllDTO;
//import com.nari.slsd.msrv.waterdiversion.model.vo.WrPlanFillInDayAndBuildingIdTreeVO;
//import com.nari.slsd.msrv.waterdiversion.model.vo.WrPlanFillinDayAdjustVO;
//import com.nari.slsd.msrv.waterdiversion.model.vo.WrPlanFillinDayVO;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.Resource;
//import java.util.List;
//
///**
// * <p>
// * 近期用水计划填报 前端控制器前端控制器
// * </p>
// *
// * @author zhs
// * @since 2021-08-20
// */
//@RestController
//@RequestMapping("/api/water-plan-day")
//@Slf4j
//public class WrPlanFillinDayContorllers {
//    @Resource
//    IWrPlanFillinDayService wrPlanFillinDayService;
//
//    /**
//     *   查询用水户权近期用水计划（月内）
//     *
//     */
//    @GetMapping("/within-month")
//    public ResultModel findUnitFillinWithinMonth(@RequestParam(value = "startTime") Long startTime,
//                                                 @RequestParam(value = "endTime") Long endTime,
//                                                 @RequestParam(value = "buildingName",required = false)List<String> buildingName,
//                                                 @RequestParam(value="buildingId",required = false)List<String> buildingId,
//                                                 @RequestParam(value="waterUnitId",required = false)String waterUnitId,
//                                                 @RequestParam(value="type",required = false)String type) {
//        try {
//            WrPlanFillInDayAndBuildingIdTreeVO wrPlanFillinDayVOList = wrPlanFillinDayService.findDayPlanWaterValue(startTime,endTime,buildingId,buildingName,waterUnitId,type);
//            return ResultModelUtils.getSuccessInstance(wrPlanFillinDayVOList);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResultModelUtils.getFailInstanceExt();
//        }
//    }
//    /**
//     *   查询用水户权近期用水计划（跨月）
//     *
//     */
//    @GetMapping("/span-month")
//    public ResultModel findUnitFillinSpanMonth(@RequestParam(value = "startTime") Long startTime,
//                                               @RequestParam(value = "endTime") Long endTime,
//                                               @RequestParam(value = "buildingId",required = false)List<String> buildingName,
//                                               @RequestParam(value="buildingName",required = false)List<String> buildingId,
//                                               @RequestParam(value="months",required = false)List<String> months) {
//        try {
//            WrPlanFillinDayAdjustVO wrPlanFillinDayAdjustVO = wrPlanFillinDayService.findDayAndTdayPlanWaterValue(startTime,endTime,buildingName,buildingId,months);
//            return ResultModelUtils.getSuccessInstance(wrPlanFillinDayAdjustVO);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResultModelUtils.getFailInstanceExt();
//        }
//    }
//    @GetMapping("/super-year")
//    public ResultModel findUnitFillinSuperYear(@RequestParam(value = "startTime") Long startTime,
//                                                 @RequestParam(value = "endTime") Long endTime,
//                                                 @RequestParam(value = "buildingName",required = false)List<String> buildingName,
//                                                 @RequestParam(value="buildingId",required = false)List<String> buildingId) {
//        try {
//            List<WrPlanFillinDayVO> WrPlanFillinDayVOList = wrPlanFillinDayService.findDayPlanWaterValueByYear(startTime,endTime,buildingId,buildingName);
//            return ResultModelUtils.getSuccessInstance(WrPlanFillinDayVOList);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResultModelUtils.getFailInstanceExt();
//        }
//    }
//    /**
//     *   查询调整计划列表
//     *
//     */
//    @GetMapping("/adjust-list")
//    public ResultModel findAdjust(@RequestParam(value = "startTime",required = false) Long startTime,
//                                  @RequestParam(value = "endTime",required = false) Long endTime,
//                                  @RequestParam(value="mngUnitId",required = false)List<String> mngUnitIds,
//                                  @RequestParam(value="pageIndex",required = false)Integer pageIndex,
//                                  @RequestParam(value="pageSize",required = false)Integer pageSize) {
//        try {
//            DataTableVO wrPlanAdjustVOs =  wrPlanFillinDayService.findAdjustList(startTime,endTime,mngUnitIds,pageIndex,pageSize);
//            return wrPlanAdjustVOs;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResultModelUtils.getFailInstanceExt();
//        }
//    }
//    /**
//     *   近期用水计划（曲线）
//     *
//     */
//    @GetMapping("/curve")
//    public ResultModel findAdjustCurve(@RequestParam(value = "startTime") Long startTime,
//                                  @RequestParam(value = "endTime") Long endTime,
//                                  @RequestParam(value = "buildingId",required = false)String buildingId,
//                                  @RequestParam(value="buildingName",required = false)String buildingName) {
//        try {
//            WrPlanFillinDayVO wrPlanFillinDayVO = wrPlanFillinDayService.findAdjustCurve(startTime,endTime,buildingId,buildingName);
//            return ResultModelUtils.getSuccessInstance(wrPlanFillinDayVO);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResultModelUtils.getFailInstanceExt();
//        }
//    }
//    /**
//     *   近期用水计划填报
//     *
//     */
//    @PostMapping
//    public ResultModel updatePlanFullinDay(@RequestBody WrPlanFillInDayAllDTO wrPlanFillInDayAllDTO) {
//        try {
//            wrPlanFillinDayService.updatePlanFullinDay(wrPlanFillInDayAllDTO);
//            return ResultModelUtils.getInstance(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResultModelUtils.getFailInstanceExt();
//        }
//     }
//    /**
//     * 管理站查询引水口id
//     *
//     * @param mngUnitId
//     * @return
//     */
//    @GetMapping("/building_id")
//    public ResultModel findBuildingIdByMngUnitId(@RequestParam(value = "mngUnitId") String mngUnitId,
//                                                 @RequestParam(value = "mngUnitName") String mngUnitName,
//                                                 @RequestParam(value = "waterUnitId") String waterUnitId,
//                                                 @RequestParam(value = "waterUnitName") String waterUnitName,
//                                                 @RequestParam(value = "buildingLevels") List<String> buildingLevels) {
//        try {
//            ResultModel resultModel = wrPlanFillinDayService.getBuildingIdByMngUnitId(mngUnitId,mngUnitName,waterUnitId,waterUnitName,buildingLevels);
//            return resultModel;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResultModelUtils.getFailInstanceExt();
//        }
//    }
//}
