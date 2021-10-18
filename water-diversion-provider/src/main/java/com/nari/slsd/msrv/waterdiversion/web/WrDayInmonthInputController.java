package com.nari.slsd.msrv.waterdiversion.web;

import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrDayInMonthInputService;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrDayInmonthInputTable;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrFlowDayInmonthRow;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 逐日水情 前端控制器
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-26
 */
@RestController
@RequestMapping("/api/wr-day-inmonth-input")
public class WrDayInmonthInputController {
    @Resource
    IWrDayInMonthInputService wrDayInmonthInputService;

    /**
     * 校核逐日水情数据
     *
     * @param dto
     * @return
     */
    @PutMapping("/batch/update")
    public ResultModel updateWrDayInmonthInputBatch(@RequestBody WrFlowDayInmonthRow dto) {
        try {
            wrDayInmonthInputService.updateBatch(dto);
            return ResultModelUtils.getEdiSuccessInstance(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 查询逐日水情数据
     *
     * @param mngUnitIds    管理单位
     * @param buildingTypes 引水口类型
     * @param fillReport    1
     * @param time          当月1号0时
     * @param status        2未校核 3已校核
     * @return
     */
    @GetMapping("/list")
    public ResultModel getWrDayInmonthInputList(@RequestParam(value = "mngUnitIds") List<String> mngUnitIds,
                                                @RequestParam(value = "buildingTypes", required = false) List<String> buildingTypes,
                                                @RequestParam(value = "fillReport", required = false) Integer fillReport,
                                                @RequestParam(value = "buildingLevels", required = false) List<Integer> buildingLevels,
                                                @RequestParam(value = "time") Long time,
                                                @RequestParam(value = "status", required = false) Integer status) {
        try {
            WrDayInmonthInputTable table = wrDayInmonthInputService.getDayInmonthInputTable(mngUnitIds, buildingTypes, fillReport, buildingLevels, time, status);
            return ResultModelUtils.getSuccessInstance(table);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }
}
