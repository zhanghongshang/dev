package com.nari.slsd.msrv.waterdiversion.web;

import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrDwaMonthService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrProcessInfoService;
import com.nari.slsd.msrv.waterdiversion.model.dto.ActivitiApproval;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrPlanTaskVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrProcessInfoVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description 月滚存指标查询
 * @Author ZHS
 * @Date 2021/9/22 10:21
 */
@RestController
@RequestMapping("/api/dwa-month")
public class WrDwaMonthContorller {

    @Resource
    IWrDwaMonthService wrDwaMonthService;
    /**
     * 人员id流程信息查询接口
     *
     * @param
     * @return
     */
    @GetMapping("/list")
    public ResultModel findWrPressInfoByUserId(@RequestParam(value = "mngUnitId",required = false)List<String> mngUnitId,
                                               @RequestParam(value = "buildingName",required = false)String buildName,
                                               @RequestParam(value = "time",required = false)String time,
                                               @RequestParam(value = "buildingLevels",required = false)List<Integer> buildingLevels,
                                               @RequestParam(value = "pageIndex")Integer pageIndex,
                                               @RequestParam(value = "pageSize")Integer pageSize) {
        try {
            DataTableVO dataTableVO = wrDwaMonthService.findWdaValue(mngUnitId,buildName,time,buildingLevels,pageIndex,pageSize);
            return dataTableVO;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

}
