package com.nari.slsd.msrv.waterdiversion.web;


import com.alibaba.fastjson.JSONObject;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import com.nari.slsd.msrv.waterdiversion.feign.interfaces.ActivitiFeignClient;
import com.nari.slsd.msrv.waterdiversion.feign.interfaces.StudioFeignClient;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrPlanInterDayService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrPlanTaskService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrProcessInfoService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrProposerConfirmService;
import com.nari.slsd.msrv.waterdiversion.model.dto.ActivitiHandle;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrPlanTaskPositive;
import com.nari.slsd.msrv.waterdiversion.model.vo.BuildingTask;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrPlanDataContrast;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrRecentPlanDetailVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  计划任务审批 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2021-08-05
 */
@RestController
@RequestMapping("/api/water-plan-task")
public class WrPlanTaskController {

    @Resource
    IWrPlanTaskService wrPlanTaskService;

    @Resource
    StudioFeignClient studioFeignClient;
    @Resource
    ActivitiFeignClient activitiFeignClient;
    @Resource
    IWrPlanInterDayService wrPlanInterDayService;
    @Resource
    IWrProposerConfirmService wrProposerConfirmService;
    @Resource
    IWrProcessInfoService wrProcessInfoService;


    /**
     * 用水计划任务查询接口
     *
     * @param
     * @return
     */
    @GetMapping("/list")
    public ResultModel batchAdd(@RequestParam(value = "userId", required = false) String userId,
                                @RequestParam(value = "mngUnitId", required = false) List<String> mngUnitId,
                                @RequestParam(value = "unitLevel", required = false) List<Integer> unitLevel,
                                @RequestParam(value = "buildType", required = false) List<String> buildType,
                                @RequestParam(value = "fillReport", required = false) Integer fillReport,
                                @RequestParam(value = "startTime", required = false) Long startTime,
                                @RequestParam(value = "endTime", required = false) Long endTime,
                                @RequestParam(value = "planType", required = false) String planType,
                                @RequestParam(value = "state", required = false) String state,
                                @RequestParam(value = "buildingLevels", required = false) List<Integer> levels,
                                @RequestParam("pageIndex") Integer pageIndex,
                                @RequestParam("pageSize") Integer pageSize) {
        try {
            DataTableVO dataTableVO = wrPlanTaskService.findWrPlanTask(userId, mngUnitId, unitLevel, buildType, fillReport, startTime, endTime, planType, state, levels,pageIndex, pageSize);
            return dataTableVO;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 任务计划总览(管理站/用水户)
     *
     * @param
     * @param userId
     * @param mngUnitId
     * @param unitLevels
     * @return
     */
    @GetMapping("/all")
    public ResultModel findYearPlanTatsk(@RequestParam(value = "time") String time,
                                         @RequestParam(value = "planType") String planType,
                                         @RequestParam(value = "userId", required = false) String userId,
                                         @RequestParam(value = "buildingTypes", required = false) List<String> buildingTypes,
                                         @RequestParam(value = "mingUnitId", required = false) List<String> mngUnitId,
                                         @RequestParam(value = "fillReport", required = false) Integer fillReport,
                                         @RequestParam(value = "unitLevels", required = false) List<Integer> unitLevels,
                                         @RequestParam(value = "levels", required = false) List<Integer> levels) {
        try {
            List<BuildingTask> buildingTasks = wrPlanTaskService.findYearTasks(time, planType, userId, buildingTypes, mngUnitId, fillReport, unitLevels,levels);
            return ResultModelUtils.getSuccessInstance(buildingTasks);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    @PostMapping("task-act")
    public JSONObject batchAdd(@RequestBody JSONObject jsonObject) {
        JSONObject object = studioFeignClient.getProcessInstanceList(jsonObject);
        return object;
    }

    //    /**
//     *
//     *  用水计划审批查询(管理站)
//     * @param
//     * @param userId
//     * @param mngUnitId
//     * @param unitLevels
//     * @return
//     */
    @GetMapping("/examine")
    public ResultModel findPlanTatsk(@RequestParam(value = "planId") String planId,
                                     @RequestParam(value = "userId") String userId,
                                     @RequestParam(value = "planType") String planType,
                                     @RequestParam(value = "unitLevels", required = false) List<Integer> unitLevels,
                                     @RequestParam(value = "buildingType", required = false) List<String> buildingType,
                                     @RequestParam(value = "fillReport", required = false) Integer fillReport
    ) {
        try {

            return ResultModelUtils.getSuccessInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    @GetMapping("/delprocess")
    public JSONObject findPlanTatsk(@RequestParam(value = "processInstanceId") String processInstanceId) {
        JSONObject object = activitiFeignClient.getDelInstance(processInstanceId);
        return object;
    }
    @GetMapping("/test")
    public ResultModel  findtest() {
        try {
            List<WrPlanDataContrast> list = wrPlanInterDayService.planInterDayValue();
            return ResultModelUtils.getSuccessInstance(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }

    }

    /**
     * 待办任务跳转产线审批详情
     * @param taskId
     * @param taskDefinitionKey
     * @return
     */
    @GetMapping("/approve")
    public ResultModel findPlanTatskList(@RequestParam(value = "taskId") String taskId,
                                         @RequestParam(value = "taskDefinitionKey") String taskDefinitionKey,
                                         @RequestParam(value = "batchState") String batchState){
        try {
            WrRecentPlanDetailVO wrRecentPlanDetailVO = wrProposerConfirmService.showBackLog(taskId,taskDefinitionKey,batchState);
            return ResultModelUtils.getSuccessInstance(wrRecentPlanDetailVO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 流程审批
     * @param
     * @param
     * @return
     */
    @PostMapping("/adopt")
    public ResultModel findPlanTatskLists(@RequestBody WrPlanTaskPositive wrPlanTaskPositive){
        try {
            String  state = wrProcessInfoService.findWrPressInfoByPositive(wrPlanTaskPositive);

            String message = "该任务已审批";
            if (state.equals("0")){
                message = "审批通过";
            }
            return ResultModelUtils.getSuccessInstanceExt(message,state);
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

