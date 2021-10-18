package com.nari.slsd.msrv.waterdiversion.web;

import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.model.ResultCode;
import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrCurveService;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrCurveDTO;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrCurveTransDTO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 曲线维护 前端控制器
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-08
 */
@RestController
@RequestMapping("/api/wr-curve")
public class WrCurveController {
    @Resource
    IWrCurveService wrCurveService;

    /**
     * 保存曲线数据
     */
    @PostMapping
    public ResultModel saveCurve(@RequestBody WrCurveTransDTO dto) {
        try {
            wrCurveService.saveCurve(dto);
            return ResultModelUtils.getAddSuccessInstance(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 率定曲线分页预览
     */
    @GetMapping("/page")
    public ResultModel getCurvePage(@RequestParam("pageIndex") Integer pageIndex,
                                    @RequestParam("pageSize") Integer pageSize,
                                    @RequestParam(value = "stationIds", required = false) List<String> stationIds) {
        try {
            DataTableVO vo = wrCurveService.getCurvePage(pageIndex, pageSize, stationIds);
            return vo;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }


    /**
     * 率定曲线查看
     */
    @GetMapping
    public ResultModel getCurve(@RequestParam(value = "id") String id) {
        try {
            WrCurveTransDTO vo = wrCurveService.getCurve(id);
            return ResultModelUtils.getSuccessInstance(vo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 率定曲线审核
     */
    @PutMapping
    public ResultModel updateCurve(@RequestBody WrCurveDTO dto) {
        try {
            wrCurveService.updateCurve(dto);
            return ResultModelUtils.getEdiSuccessInstance(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 率定曲线删除
     */
    @DeleteMapping
    public ResultModel deleteCurve(@RequestParam(value = "id") String id) {
        try {
            wrCurveService.deleteCurve(id);
            return ResultModelUtils.getDelSuccessInstance(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }


    /**
     * 曲线编码唯一性校验
     *
     * @param code
     * @return
     */
    @GetMapping("/code")
    public ResultModel codeCheck(@RequestParam("code") String code) {
        try {
            if (StringUtils.isEmpty(code)) {
                return ResultModelUtils.getFailInstanceExt("编码不能为空");
            }

            Boolean unique = wrCurveService.checkUniqueCode(code);
            if (unique) {
                return ResultModelUtils.getInstance(true, ResultCode.RESULT_SUCC.toString(), "编码唯一,校验通过");
            } else {
                return ResultModelUtils.getFailInstanceExt("编码不唯一,校验失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }


    /**
     * 通过时间、测站找水位流量曲线
     */
    @GetMapping("/station-time")
    public ResultModel getCurveByStationAndTime(@RequestParam("stationId") String stationId,
                                                @RequestParam("time") Long time) {
        try {
            WrCurveTransDTO dto = wrCurveService.getCurve(stationId, time);
            return ResultModelUtils.getSuccessInstance(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }
}

