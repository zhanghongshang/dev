package com.nari.slsd.msrv.waterdiversion.web;

import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrRecentPlanAdjustService;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrRecentPlanForMultiUseUnitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @title
 * @description 超年计划填报
 * @author bigb
 * @updateTime 2021/9/15 17:21
 * @throws
 */
@RestController
@RequestMapping("/api/water-plan-in-multi-unit")
@Slf4j
public class WrPlanFillInMultiUseUnitController {

    @Autowired
    private IWrRecentPlanAdjustService wrRecentPlanAdjustService;

    @GetMapping("/getall-buildings")
    public ResultModel findAllBuildings(@RequestParam(value = "buyerUnitId") String buyerUnitId,
                                        @RequestParam(value = "saleUnitId") String saleUnitId) {
        try {
            WrRecentPlanForMultiUseUnitVO vo = wrRecentPlanAdjustService.getAllBuildingsForUseUnit(buyerUnitId, saleUnitId);
            return ResultModelUtils.getSuccessInstanceExt(vo);
        } catch (Exception e) {
            log.error("获取引水口信息失败,用水单位为:{},error is {}",new Object[]{buyerUnitId,saleUnitId,e});
            return ResultModelUtils.getFailInstanceExt();
        }
    }

}
