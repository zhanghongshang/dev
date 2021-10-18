package com.nari.slsd.msrv.waterdiversion.web;

import com.alibaba.fastjson.JSONArray;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrBalanceAnalyzeService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrPlanFillinDayService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrRecentPlanAdjustService;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrBuildingAndDiversion;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrPlanFillInDayAllDTO;
import com.nari.slsd.msrv.waterdiversion.model.vo.*;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 近期用水计划填报 前端控制器前端控制器
 * </p>
 *
 * @author zhs
 * @since 2021-08-20
 */
@RestController
@RequestMapping("/api/water-plan-day")
@Slf4j
public class WrPlanFillinDayContorller {
    @Resource
    IWrPlanFillinDayService wrPlanFillinDayService;
    @Resource
    IWrRecentPlanAdjustService wrRecentPlanAdjustService;
    @Resource
    IWrBalanceAnalyzeService wrBalanceAnalyzeService;
    /**
     *   查询用水户权近期用水计划（月内）
     *
     */
    @GetMapping("/within-month")
    public ResultModel findUnitFillinWithinMonth(@RequestParam(value = "startTime") Long startTime,
                                                 @RequestParam(value = "endTime") Long endTime,
                                                 @RequestParam(value = "buildingName",required = false)List<String> buildingName,
                                                 @RequestParam(value="buildingId",required = false)List<String> buildingId,
                                                 @RequestParam(value="waterUnitId",required = false)String waterUnitId) {
        try {
            WrPlanFillInDayAndBuildingIdTreeVO wrPlanFillinDayVOList = wrPlanFillinDayService.findWithinMonth(startTime,endTime,buildingId,buildingName,waterUnitId);
            return ResultModelUtils.getSuccessInstance(wrPlanFillinDayVOList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }
    /**
          *   查询用水户权近期用水计划（跨月）
          *
          */
    @GetMapping("/span-month")
    public ResultModel findUnitFillinSpanMonth(@RequestParam(value = "startTime") Long startTime,
                                               @RequestParam(value = "endTime") Long endTime,
                                               @RequestParam(value = "buildingId",required = false)List<String> buildingName,
                                               @RequestParam(value="buildingName",required = false)List<String> buildingId,
                                               @RequestParam(value="months",required = false)List<String> months) {
        try {
            WrPlanFillinDayAdjustVO wrPlanFillinDayAdjustVO = wrPlanFillinDayService.findSpanMonth(startTime,endTime,buildingName,buildingId,months);
            return ResultModelUtils.getSuccessInstance(wrPlanFillinDayAdjustVO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }
    @GetMapping("/super-year")
    public ResultModel findUnitFillinSuperYear(@RequestParam(value = "startTime") Long startTime,
                                                 @RequestParam(value = "endTime") Long endTime,
                                                 @RequestParam(value = "buildingName",required = false)List<String> buildingName,
                                                 @RequestParam(value="buildingId",required = false)List<String> buildingId) {
        try {
            List<WrPlanFillinDayVO> WrPlanFillinDayVOList = wrPlanFillinDayService.findSuperYear(startTime,endTime,buildingId,buildingName);
            return ResultModelUtils.getSuccessInstance(WrPlanFillinDayVOList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }
    /**
     *   查询调整计划列表
     *
     */
    @GetMapping("/adjust-list")
    public ResultModel findAdjust(@RequestParam(value = "startTime",required = false) Long startTime,
                                  @RequestParam(value = "endTime",required = false) Long endTime,
                                  @RequestParam(value="mngUnitId",required = false)List<String> mngUnitIds,
                                  @RequestParam(value="pageIndex",required = false)Integer pageIndex,
                                  @RequestParam(value="pageSize",required = false)Integer pageSize) {
        try {
            DataTableVO wrPlanAdjustVOs =  wrPlanFillinDayService.findAdjustList(startTime,endTime,mngUnitIds,pageIndex,pageSize);
            return wrPlanAdjustVOs;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }
    /**
     *   近期用水计划（曲线）
     *
     */
    @GetMapping("/curve")
    public ResultModel findAdjustCurve(@RequestParam(value = "startTime") Long startTime,
                                  @RequestParam(value = "endTime") Long endTime,
                                  @RequestParam(value = "buildingId",required = false)List<String> buildingId,
                                  @RequestParam(value="buildingName",required = false)String buildingName) {
        try {
            WrPlanFillinDayVO wrPlanFillinDayVO = wrPlanFillinDayService.findAdjustCurve(startTime,endTime,buildingId,buildingName);
            return ResultModelUtils.getSuccessInstance(wrPlanFillinDayVO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }
    /**
     *   近期用水计划填报
     *
     */
    @PostMapping
    public ResultModel updatePlanFullinDay(@RequestBody WrPlanFillInDayAllDTO wrPlanFillInDayAllDTO) {
        try {
            wrPlanFillinDayService.updatePlanFullinDay(wrPlanFillInDayAllDTO);
            return ResultModelUtils.getInstance(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
     }
    /**
     * 管理站查询引水口id
     *
     * @param mngUnitId
     * @return
     */
    @GetMapping("/building_id")
    public ResultModel findBuildingIdByMngUnitId(@RequestParam(value = "mngUnitId") List<String> mngUnitId,
                                                 @RequestParam(value = "mngUnitName",required = false) String mngUnitName,
                                                 @RequestParam(value = "waterUnitId",required = false) String waterUnitId,
                                                 @RequestParam(value = "waterUnitName",required = false) String waterUnitName,
                                                 @RequestParam(value = "buildingLevels") List<String> buildingLevels) {
        try {
            ResultModel resultModel = wrPlanFillinDayService.getBuildingIdByMngUnitId(mngUnitId,mngUnitName,waterUnitId,waterUnitName,buildingLevels);
            return resultModel;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }
    /**
     * 借调详情
     *
     * @param
     * @return
     */
    @GetMapping("/adjust_detail")
    public ResultModel findAdjustDetail(@RequestParam(value = "buildingId",required = false) String buildingId,
                                        @RequestParam(value = "lendWater",required = false) BigDecimal lendWater,
                                        @RequestParam(value = "adjustType",required = false) String adjustType,
                                        @RequestParam(value = "startTime",required = false) Long startTime,
                                        @RequestParam(value = "endTime",required = false) Long endTime) {
        try {
            List<LendOutDetailVO> lendOutDetailVOs = wrRecentPlanAdjustService.adjustDetail(buildingId,lendWater,adjustType,startTime,endTime);
            return ResultModelUtils.getSuccessInstance(lendOutDetailVOs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }
    /**
     * 剩余水量对比校验
     *
     * @param
     * @return
     */
    @GetMapping("/check_water")
    public ResultModel checkWater(@RequestParam(value="startTime",required = false) Long startTime,
                                  @RequestParam(value="endTime",required = false) Long endTime,
                                  @RequestParam(value="months",required = false) List<String> months,
                                  @RequestParam(value="buildingIds",required = false) List<String> buildingId,
                                  @RequestParam(value="flowValue",required = false) Double flowValue,
                                  @RequestParam(value="buildingName",required = false) String buildingName,
                                  @RequestParam(value="waterUnitId",required = false) String waterUnitId,
                                  @RequestParam(value="type",required = false) String type) {
        try {
            ResultModel resultModel = wrPlanFillinDayService.checkWaterValue(startTime,endTime,months,buildingName,waterUnitId,buildingId,flowValue,type);
            return resultModel;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }
    /**
     * 水量平衡
     *
     * @param
     * @return
     */
    @PostMapping("/analyze")
    public ResultModel findWaterBalanceAnalyze(@RequestBody WrPlanFillInDayAllDTO allDTO) {
        try {
            WaterBalanceAnalyzeVO waterBalanceAnalyze = wrBalanceAnalyzeService.waterBalanceAnalyze(allDTO);
            return ResultModelUtils.getSuccessInstance(waterBalanceAnalyze);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }
}
