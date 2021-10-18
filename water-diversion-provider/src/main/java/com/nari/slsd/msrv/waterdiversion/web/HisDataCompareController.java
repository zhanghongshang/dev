package com.nari.slsd.msrv.waterdiversion.web;

import com.nari.slsd.hu.mplat.imc.client.Param.Param;
import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import com.nari.slsd.msrv.waterdiversion.interfaces.IHisDataCompareService;
import com.nari.slsd.msrv.waterdiversion.model.dto.HisDataCompareMonthDto;
import com.nari.slsd.msrv.waterdiversion.model.dto.HisDataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Created by ZHD
 * @program: HisDataCompareController
 * @description:历史数据同期对比
 * @date: 2021/8/22 16:53
 */
@RestController
@RequestMapping("/api/hisdata/compare")
@Slf4j
public class HisDataCompareController {

    @Autowired
    IHisDataCompareService iHisDataCompareService;

    @PostMapping("/")
    public ResultModel getHisDataCompare(@RequestBody HisDataDto dataDto) {
        try {
            if (dataDto.getRunDataType().equals(Param.RunDataType.RUN_MONTH)){//月
                List<HisDataCompareMonthDto> result  = iHisDataCompareService.getHisDataCompareMonth(dataDto.getBuilding(),
                        dataDto.getPointType(),dataDto.getRunDataType(),dataDto.getValType(),dataDto.getYears());
                return ResultModelUtils.getSuccessInstance(result);
            }else if (dataDto.getRunDataType().equals(Param.RunDataType.RUN_DAY)){//日
                List<HisDataCompareMonthDto> result  = iHisDataCompareService.getHisDataCompareDay(dataDto.getBuilding(),dataDto.getPointType(),
                        dataDto.getRunDataType(),dataDto.getValType(),dataDto.getDate(),dataDto.getYears());
                return ResultModelUtils.getSuccessInstance(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
        return ResultModelUtils.getFailInstanceExt();
    }


}
