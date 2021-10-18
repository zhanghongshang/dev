package com.nari.slsd.msrv.waterdiversion.web;

import com.nari.slsd.msrv.common.exception.TransactionException;
import com.nari.slsd.msrv.common.ext.enums.CodeEnum;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import com.nari.slsd.msrv.waterdiversion.config.annotations.ResponseResult;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrRightTradeService;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrRightTradeDTO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrRightTradeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author Created by ZHD
 * @program: WrRightTradeController
 * @description:
 * @date: 2021/8/17 11:06
 */
@ResponseResult
@RestController
@RequestMapping("/api/wr-right-trade")
@Slf4j
public class WrRightTradeController {

    @Autowired
    IWrRightTradeService iWrRightTradeService;

    @PostMapping(produces = "application/json;charset=utf-8")
    public ResultModel save(@RequestBody WrRightTradeDTO dto){
        try{
            if (dto == null) {
                throw new TransactionException(CodeEnum.NO_PARAM,"参数为null");
            }
            iWrRightTradeService.save (dto);
            return ResultModelUtils.getAddSuccessInstance(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }
    @PutMapping(produces = "application/json;charset=utf-8")
    public ResultModel update(@RequestBody WrRightTradeDTO dto){
        try{
            if (dto == null) {
                throw new TransactionException(CodeEnum.NO_PARAM,"参数为null");
            }
            iWrRightTradeService.update (dto);
            return ResultModelUtils.getEdiSuccessInstance(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }
    @DeleteMapping
    public ResultModel delete(@RequestParam("id") String id){
        try{
            if (id == null) {
                throw new TransactionException(CodeEnum.NO_PARAM,"参数为null");
            }
            iWrRightTradeService.delete (id);
            return ResultModelUtils.getDelSuccessInstance(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }
    @GetMapping("/page")
    public ResultModel getBuildingAndDiversionPage(@RequestParam(value = "pageIndex") Integer pageIndex,
                                                   @RequestParam(value = "pageSize") Integer pageSize,
                                                   @RequestParam(value = "year", required = false) String year) {
        try {

            DataTableVO tableVO = iWrRightTradeService.getWrRightTrade(pageIndex, pageSize, year);
            return tableVO;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }
    @GetMapping("/all-trade-in-year")
    public ResultModel getAllRightTradeInYear() {
        try {
            List<WrRightTradeVO> allRightTradeInYear = iWrRightTradeService.getAllRightTradeInYear();
            return ResultModelUtils.getSuccessInstanceExt(allRightTradeInYear);
        } catch (Exception e) {
            log.error("获取水权交易信息失败,error is {}",e);
            return ResultModelUtils.getFailInstanceExt();
        }
    }
}
