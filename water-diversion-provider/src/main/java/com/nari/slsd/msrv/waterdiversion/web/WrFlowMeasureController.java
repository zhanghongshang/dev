package com.nari.slsd.msrv.waterdiversion.web;

import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrFlowMeasureService;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrFlowMeasureDTO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrFlowMeasureVO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 断面实测数据表 前端控制器
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-05
 */
@RestController
@RequestMapping("/api/wr-flow-measure")
public class WrFlowMeasureController {

    @Resource
    IWrFlowMeasureService wrFlowMeasureService;

    /**
     * 人工率定录入-新增
     *
     * @param dtoList
     * @return
     */
    @PostMapping("/batch")
    public ResultModel batchAdd(@RequestBody List<WrFlowMeasureDTO> dtoList) {
        try {
            wrFlowMeasureService.saveBatch(dtoList);
            return ResultModelUtils.getAddSuccessInstance(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 人工率定录入-修改
     *
     * @param dto
     * @return
     */
    @PutMapping
    public ResultModel update(@RequestBody WrFlowMeasureDTO dto) {
        try {
            wrFlowMeasureService.update(dto);
            return ResultModelUtils.getEdiSuccessInstance(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 人工率定录入-删除
     *
     * @param id
     * @return
     */
    @DeleteMapping
    public ResultModel delete(@RequestParam("id") String id) {
        try {
            wrFlowMeasureService.delete(id);
            return ResultModelUtils.getDelSuccessInstance(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }


    /**
     * 分页查询
     *
     * @param pageIndex
     * @param pageSize
     * @param stationId
     * @return
     */
    @GetMapping("/page")
    public ResultModel getDataPage(@RequestParam(value = "pageIndex") Integer pageIndex,
                                   @RequestParam(value = "pageSize") Integer pageSize,
                                   @RequestParam(value = "stationId", required = false) String stationId) {
        try {
            DataTableVO tableVO = wrFlowMeasureService.getDataPage(pageIndex, pageSize, stationId);
            return tableVO;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }


    /**
     * 查询指定时间内的数据
     *
     * @param sdt
     * @param edt
     * @param stationId
     * @return
     */
    @GetMapping("/list")
    public ResultModel getDataList(@RequestParam(value = "sdt",required = false) Long sdt,
                                   @RequestParam(value = "edt",required = false) Long edt,
                                   @RequestParam(value = "stationId") String stationId) {
        try {
            List<WrFlowMeasureVO> voList = wrFlowMeasureService.getDataList(sdt, edt, stationId);
            return ResultModelUtils.getSuccessInstance(voList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }


    /**
     * excel导入数据
     * TODO 无模板 预留
     *
     * @return
     */
    @PostMapping("/excel/import")
    public ResultModel excelImport(@RequestPart MultipartFile file) {
        try {
//            wrFlowMeasureService.excelImport(file);
            return ResultModelUtils.getAddSuccessInstance(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

}

