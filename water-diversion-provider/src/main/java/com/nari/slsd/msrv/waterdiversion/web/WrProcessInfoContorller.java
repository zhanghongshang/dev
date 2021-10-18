package com.nari.slsd.msrv.waterdiversion.web;

import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrProcessInfoService;
import com.nari.slsd.msrv.waterdiversion.model.dto.ActivitiApproval;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrProcessInfo;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrPlanTaskVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrProcessInfoVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description 流程信息保存
 * @Author ZHS
 * @Date 2021/8/30 18:01
 */
@RestController
@RequestMapping("/api/process-info")
public class WrProcessInfoContorller {

    @Resource
    IWrProcessInfoService wrProcessInfoService;
    /**
     * 人员id流程信息查询接口（代办，已办）
     *
     * @param
     * @return
     */
    @GetMapping("/user-id-list")
    public ResultModel findWrPressInfoByUserId(@RequestParam("userId")String userId,
                                               @RequestParam(value = "startTime",required = false)Long startTime,
                                               @RequestParam(value = "endTime",required = false)Long endTime,
                                               @RequestParam(value = "type",required = false)String type) {
        try {
            List<WrPlanTaskVO> wrPlanTaskVOList = wrProcessInfoService.planTaskByProcessId(userId,startTime,endTime,type);
            return ResultModelUtils.getSuccessInstance(wrPlanTaskVOList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }
    /**
     * 流程id流程信息查询接口
     *
     * @param
     * @return
     */
    @GetMapping("/process-id-list")
    public ResultModel findWrPressInfoByProcessId(@RequestParam("processId")String userId) {
        try {
            List<WrProcessInfoVO> wrProcessInfoVOList = wrProcessInfoService.findWrPressInfoByProcessId(userId);
            return ResultModelUtils.getSuccessInstance(wrProcessInfoVOList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }
    /**
     * 流程id流程信息查询接口
     *
     * @param
     * @return
     */
    @PostMapping("/handle-type")
    public ResultModel addWrPressInfo(@RequestBody ActivitiApproval activitiApproval) {
        try {
            wrProcessInfoService.addWrPressInfo(activitiApproval);
            return ResultModelUtils.getInstance(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

}
