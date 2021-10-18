package com.nari.slsd.msrv.waterdiversion.web;

import com.nari.slsd.msrv.common.model.PageModel;
import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import com.nari.slsd.msrv.waterdiversion.config.annotations.ResponseResult;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrCmdManagerService;
import com.nari.slsd.msrv.waterdiversion.model.dto.SimpleManagerDto;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrCmdManagerDTO;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrCmdManagerOperateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @title
 * @description 调度指令管理
 * @author bigb
 * @updateTime 2021/9/16 2:14
 * @throws
 */
@ResponseResult
@RestController
@RequestMapping("/api/wr-cmd-manager")
@Slf4j
public class WrCmdManagerController {

    @Autowired
    private IWrCmdManagerService wrCmdManagerService;

    /**
     * 实时调度指令保存
     */
    @PostMapping
    public ResultModel saveWrCmdManager(@RequestBody WrCmdManagerDTO dto) {
        try {
            wrCmdManagerService.saveWrCmdManager(dto);
            return ResultModelUtils.getAddSuccessInstance(true);
        } catch (Exception e) {
            log.error("实时调度指令保存失败,error is {}",e);
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    @GetMapping("/page")
    public ResultModel getCmdManagerPage(@RequestParam(value = "pageIndex") Integer pageIndex,
                                                   @RequestParam(value = "pageSize") Integer pageSize ,
                                                   @RequestParam(value = "orderCode",required = false) String orderCode ,
                                                   @RequestParam(value = "orderName",required = false) String orderName ,
                                                   @RequestParam(value = "startTime",required = false) Long startTime ,
                                                   @RequestParam(value = "endTime",required = false) Long endTime ,
                                                   @RequestParam(value = "orderStatus",required = false) String orderStatus,
                                                   @RequestParam(value = "manageUnitIdList",required = false) List<String> manageUnitIdList) {
        try {
            WrCmdManagerOperateDto wrCmdManagerOperateDto = new WrCmdManagerOperateDto();
            wrCmdManagerOperateDto.setOrderCode(orderCode);
            wrCmdManagerOperateDto.setOrderName(orderName);
            wrCmdManagerOperateDto.setOrderStartTime(startTime);
            wrCmdManagerOperateDto.setOrderEndTime(endTime);
            wrCmdManagerOperateDto.setOrderStatus(orderStatus);
            //管理人员根据管理站查看调度指令
            wrCmdManagerOperateDto.setManageUnitIdList(manageUnitIdList);
            wrCmdManagerOperateDto.setQueryMine(false);
            PageModel pageModel = PageModel.builder()
                    .start(pageIndex)
                    .pageSize(pageSize)
                    .searchData(wrCmdManagerOperateDto)
                    .build();
            return wrCmdManagerService.getWrCmdManagerPage(pageModel);
        } catch (Exception e) {
            log.error("获取调度指令管理表信息失败,error is {}",e);
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    @GetMapping("/page/person")
    public ResultModel getCmdManagerPageForPerson(@RequestParam(value = "pageIndex") Integer pageIndex,
                                                   @RequestParam(value = "pageSize") Integer pageSize ,
                                                   @RequestParam(value = "orderCode",required = false) String orderCode ,
                                                   @RequestParam(value = "orderName",required = false) String orderName ,
                                                   @RequestParam(value = "startTime",required = false) Long startTime ,
                                                   @RequestParam(value = "endTime",required = false) Long endTime ,
                                                   @RequestParam(value = "orderStatus",required = false) String orderStatus,
                                                   @RequestParam(value = "launchName",required = false) String launchName) {
        try {
            WrCmdManagerOperateDto wrCmdManagerOperateDto = new WrCmdManagerOperateDto();
            wrCmdManagerOperateDto.setOrderCode(orderCode);
            wrCmdManagerOperateDto.setOrderName(orderName);
            wrCmdManagerOperateDto.setOrderStartTime(startTime);
            wrCmdManagerOperateDto.setOrderEndTime(endTime);
            wrCmdManagerOperateDto.setOrderStatus(orderStatus);
            //当前登录人
            wrCmdManagerOperateDto.setLaunchName(launchName);
            wrCmdManagerOperateDto.setQueryMine(true);
            PageModel pageModel = PageModel.builder()
                    .start(pageIndex)
                    .pageSize(pageSize)
                    .searchData(wrCmdManagerOperateDto)
                    .build();
            return wrCmdManagerService.getWrCmdManagerPage(pageModel);
        } catch (Exception e) {
            log.error("获取调度指令管理表信息失败,error is {}",e);
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 指令审批
     * @return
     */
    @PutMapping("/approve")
    public ResultModel approve(@RequestBody SimpleManagerDto simpleManagerDto) {
        try {
            wrCmdManagerService.updateWrCmdManager(simpleManagerDto.getManagerId(),simpleManagerDto.getApproveStatus(),
                    simpleManagerDto.getApproveName(),simpleManagerDto.getApproveContent());
            return ResultModelUtils.getAddSuccessInstance(true);
        } catch (Exception e) {
            log.error("调度指令审批失败,error is {}",e);
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 指令审批
     * @return
     */
    @DeleteMapping("/delete")
    public ResultModel delete(@RequestParam(value = "managerId") String managerId) {
        try {
            wrCmdManagerService.deleteWrCmdManager(managerId);
            return ResultModelUtils.getAddSuccessInstance(true);
        } catch (Exception e) {
            log.error("删除调度指令失败,error is {}",e);
            return ResultModelUtils.getFailInstanceExt();
        }
    }
}
