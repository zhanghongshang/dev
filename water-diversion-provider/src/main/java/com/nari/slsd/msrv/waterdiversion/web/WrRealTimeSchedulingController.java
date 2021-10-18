package com.nari.slsd.msrv.waterdiversion.web;

import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import com.nari.slsd.msrv.waterdiversion.commons.InstructionEnum;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrRealTimeSchedulingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @title
 * @description gis调度管理
 * @author bigb
 * @updateTime 2021/9/2 14:13
 * @throws
 */
@RestController
@RequestMapping("/api/schedule")
@Slf4j
public class WrRealTimeSchedulingController {
    @Autowired
    private IWrRealTimeSchedulingService wrRealTimeSchedulingService;


    @GetMapping("/schedule_gis")
    public ResultModel schedulingByGis(@RequestParam(value = "scheduleMode") String scheduleMode,
                                       @RequestParam(value = "buildingTypeList") List<String> buildingTypeList,
                                       @RequestParam(value = "pointTypeList") List<String> pointTypeList,
                                       @RequestParam(value = "buildingIdList",required = false) List<String> buildingIdList,
                                       @RequestParam(value = "selectDate",required = false) Long selectDate) {
        try {
            Object data = null;
            if(InstructionEnum.SCHEDULE_MANUAL.equals(scheduleMode)){
                data = wrRealTimeSchedulingService.artificialScheduling(buildingIdList,selectDate,pointTypeList);
            }else if(InstructionEnum.SCHEDULE_PLAN.equals(scheduleMode)){
                data = wrRealTimeSchedulingService.planScheduling(buildingTypeList,pointTypeList);
            }
            return ResultModelUtils.getSuccessInstance(data);
        } catch (Exception e) {
            log.error("schedulingByGis fail , error is {}" , e);
            return ResultModelUtils.getFailInstanceExt();
        }
    }
}
