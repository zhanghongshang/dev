package com.nari.slsd.msrv.waterdiversion.web;

import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrDayInputService;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrDayInputDTO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrDayInputTable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 日水情输入 前端控制器
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-19
 */
@RestController
@RequestMapping("/api/wr-day-input")
public class WrDayInputController {
    @Resource
    IWrDayInputService wrDayInputService;

    /**
     * 保存/更新日水情数据
     *
     * @param dtoList
     * @return
     */
    @PostMapping("/batch/save-or-update")
    public ResultModel saveOrUpdateWrDayInputBatch(@RequestBody List<WrDayInputDTO> dtoList) {
        try {
            wrDayInputService.saveOrUpdateBatch(dtoList);
            return ResultModelUtils.getAddSuccessInstance(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 审核日水情数据
     *
     * @param dtoList
     * @return
     */
    @PutMapping("/batch/verify")
    public ResultModel verifyWrDayInputBatch(@RequestBody List<WrDayInputDTO> dtoList) {
        try {
            wrDayInputService.verifyBatch(dtoList);
            return ResultModelUtils.getEdiSuccessInstance(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 查询日水情数据
     *
     * @param mngUnitIds     管理单位
     * @param buildingTypes  测站类型 引水口
     * @param fillReport     需要填报，传1
     * @param buildingLevels 引水口类型 小引水口 传2,3
     * @param time           时间 当日0时
     * @param status         审核状态
     * @return
     */
    @GetMapping("/list")
    public ResultModel getWrDayInputList(@RequestParam(value = "mngUnitIds") List<String> mngUnitIds,
                                         @RequestParam(value = "buildingTypes") List<String> buildingTypes,
                                         @RequestParam(value = "fillReport") Integer fillReport,
                                         @RequestParam(value = "buildingLevels") List<Integer> buildingLevels,
                                         @RequestParam(value = "time") Long time,
                                         @RequestParam(value = "status", required = false) Integer status) {
        try {
            WrDayInputTable table = wrDayInputService.getDayInputDataTable(mngUnitIds, buildingTypes, fillReport, buildingLevels, time, status);
            return ResultModelUtils.getSuccessInstance(table);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }


}
