package com.nari.slsd.msrv.waterdiversion.web;

import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrBuildingAndDiversionService;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrBuildingAndDiversion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 测站-引水口管理 前端控制器
 * </p>
 *
 * @author reset kalar
 * @since 2021-07-29
 */
@RestController
@RequestMapping("/api/building-and-diversion")
@Slf4j
public class WrBuildingAndDiversionController {
    @Resource
    IWrBuildingAndDiversionService wrBuildingAndDiversionService;


    /**
     * 修改用水单位
     *
     * @param dto
     * @return
     */

    @PutMapping
    public ResultModel updateBuildingAndDiversion(@RequestBody WrBuildingAndDiversion dto) {
        try {
            wrBuildingAndDiversionService.updateBuildingAndDiversion(dto);
            return ResultModelUtils.getEdiSuccessInstance(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 分页查询
     *
     * @param start
     * @param length
     * @param buildingType  类型 测站/引水口
     * @param waterUnitId 上级用水单位 编码
     * @param mngUnitId     上级管理单位ID
     * @return
     */
    @GetMapping("/page")
    public ResultModel getBuildingAndDiversionPage(@RequestParam(value = "start") Integer start,
                                                   @RequestParam(value = "length") Integer length,
                                                   @RequestParam(value = "buildingType", required = false) Integer buildingType,
                                                   @RequestParam(value = "waterUnitId", required = false) String waterUnitId,
                                                   @RequestParam(value = "mngUnitId", required = false) String mngUnitId) {
        try {
            DataTableVO tableVO = wrBuildingAndDiversionService.getBuildingAndDiversionPage(start, length, buildingType, waterUnitId, mngUnitId);
            return tableVO;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }


}
