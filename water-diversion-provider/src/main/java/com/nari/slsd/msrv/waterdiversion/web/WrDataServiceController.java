package com.nari.slsd.msrv.waterdiversion.web;

import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrDataService;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrStationDataVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * wr数据 前端控制器
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-31
 */
@RestController
@RequestMapping("/api/wr-data")
public class WrDataServiceController {

    @Resource
    IWrDataService wrDataService;

    /**
     * 按条件查询 数据
     *
     * @param swfcds
     * @param sdt
     * @param edt
     * @param runDataType 所查表 month daychecked hour
     * @param valType     所查值类型 z q
     * @return
     */
    @GetMapping("/list/his")
    public ResultModel getNewestDataList(@RequestParam(value = "swfcds") List<String> swfcds,
                                         @RequestParam(value = "sdt") Long sdt,
                                         @RequestParam(value = "edt") Long edt,
                                         @RequestParam(value = "runDataType") String runDataType,
                                         @RequestParam(value = "valTypes", required = false) List<String> valType) {
        try {
            List<WrStationDataVO> result = wrDataService.getWrData(swfcds, sdt, edt, runDataType, valType);
            return ResultModelUtils.getSuccessInstance(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }
}
