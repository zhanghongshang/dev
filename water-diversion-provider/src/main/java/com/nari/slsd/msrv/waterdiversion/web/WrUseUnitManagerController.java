package com.nari.slsd.msrv.waterdiversion.web;


import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.model.ResultCode;
import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrUseUnitManagerService;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrUseUnitManagerDTO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseUnitManagerVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseUnitNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用水单位管理 前端控制器
 * </p>
 *
 * @author reset kalar
 * @since 2021-07-29
 */
@RestController
@RequestMapping("/api/water-use-unit")
@Slf4j
public class WrUseUnitManagerController {

    @Resource
    IWrUseUnitManagerService waterUseUnitManagerService;

    /**
     * 新增用水单位
     *
     * @param dto
     * @return
     */
    @PostMapping
    public ResultModel addUnit(@RequestBody WrUseUnitManagerDTO dto) {
        try {
            waterUseUnitManagerService.addWaterUseUnit(dto);
            return ResultModelUtils.getAddSuccessInstance(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 修改用水单位
     *
     * @param dto
     * @return
     */

    @PutMapping
    public ResultModel updateUnit(@RequestBody WrUseUnitManagerDTO dto) {
        try {
            waterUseUnitManagerService.updateWaterUseUnit(dto);
            return ResultModelUtils.getEdiSuccessInstance(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 删除用水单位
     *
     * @param id
     * @return
     */
    @DeleteMapping
    public ResultModel deleteUnit(@RequestParam("id") String id) {
        try {
            waterUseUnitManagerService.deleteWaterUseUnitById(id);
            return ResultModelUtils.getDelSuccessInstance(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 查询所有用水单位
     *
     * @return
     */
    @GetMapping("/all")
    public ResultModel getAllUnit() {
        try {
            List<WrUseUnitManagerVO> voList = waterUseUnitManagerService.getAllWaterUseUnitList();
            return ResultModelUtils.getSuccessInstance(voList);
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
     * @param pid
     * @param state
     * @return
     */
    @GetMapping("/page")
    public ResultModel getUnit(@RequestParam(value = "start") Integer start,
                               @RequestParam(value = "length") Integer length,
                               @RequestParam(value = "pid", required = false) String pid,
                               @RequestParam(value = "state", required = false) Integer state) {
        try {
            DataTableVO tableVO = waterUseUnitManagerService.getWaterUseUnitPage(start, length, pid, state);
            return tableVO;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 获取用水单位模型树
     *
     * @param ids
     * @return
     */
    @GetMapping("/tree")
    public ResultModel getTree(@RequestParam(value = "ids") List<String> ids) {
        try {
            List<WrUseUnitNode> nodeList = waterUseUnitManagerService.getTreeFromCacheByIds(ids);
            return ResultModelUtils.getSuccessInstance(nodeList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 获取全部用水单位模型树
     *
     * @return
     */
    @GetMapping("/tree/all")
    public ResultModel getAllTrees() {
        try {
            List<WrUseUnitNode> nodeList = waterUseUnitManagerService.getAllTreeFromCacheByIds();
            return ResultModelUtils.getSuccessInstance(nodeList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }


    /**
     * 获取子节点ID
     *
     * @param pid
     * @return
     */
    @GetMapping("/id")
    public ResultModel getUnitIds(@RequestParam("pid") String pid) {
        try {
            List<String> ids = waterUseUnitManagerService.getUnitIdsByPid(pid);
            return ResultModelUtils.getSuccessInstance(ids);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModelUtils.getFailInstanceExt();
        }
    }


    @GetMapping("/code")
    public ResultModel getCode(@RequestParam("code") String code) {
        try {
            if (StringUtils.isEmpty(code)) {
                return ResultModelUtils.getFailInstanceExt("编码不能为空");
            }

            Boolean unique = waterUseUnitManagerService.checkUniqueCode(code);
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


//    @GetMapping("/tree/update")
//    public ResultModel updateTree(@RequestParam List<String> ids) {
//        try {
////            List<WaterUseUnitNode> nodeList = waterUseUnitManagerService.update();
//            return ResultModelUtils.getSuccessInstance(nodeList);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResultModelUtils.getFailInstanceExt();
//        }
//    }
}

