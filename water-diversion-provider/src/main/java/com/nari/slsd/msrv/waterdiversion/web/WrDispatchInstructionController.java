package com.nari.slsd.msrv.waterdiversion.web;


import com.nari.slsd.msrv.common.model.PageModel;
import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrDispatchInstructionService;
import com.nari.slsd.msrv.waterdiversion.model.dto.SimpleInstructionExecuteDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @title
 * @description 调度指令
 * @author bigb
 * @updateTime 2021/9/17 20:45
 * @throws
 */
@RestController
@RequestMapping("/api/water-instruction")
@Slf4j
public class WrDispatchInstructionController {

    @Autowired
    private IWrDispatchInstructionService wrDispatchInstructionService;

    @GetMapping("/page")
    public ResultModel getInstructionPage(@RequestParam(value = "pageIndex") Integer pageIndex,
                                          @RequestParam(value = "pageSize") Integer pageSize ,
                                          @RequestParam(value = "mangerId") String mangerId) {
        try {
            PageModel pageModel = PageModel.builder()
                    .start(pageIndex)
                    .pageSize(pageSize)
                    .build();
            return wrDispatchInstructionService.getWrDispatchInstructionPage(pageModel,mangerId);
        } catch (Exception e) {
            log.error("获取调度指令信息失败,error is {}",e);
            return ResultModelUtils.getFailInstanceExt();
        }
    }

    /**
     * 指令执行
     * @return
     */
    @PutMapping("/execute")
    public ResultModel executeInstruction(@RequestBody SimpleInstructionExecuteDto simpleInstructionExecuteDto) {
        try {
            wrDispatchInstructionService.executeInstruction(simpleInstructionExecuteDto.getId(),simpleInstructionExecuteDto.getOperateType(),simpleInstructionExecuteDto.getPersonId());
            return ResultModelUtils.getAddSuccessInstance(true);
        } catch (Exception e) {
            log.error("调度指令执行失败,error is {}",e);
            return ResultModelUtils.getFailInstanceExt();
        }
    }
}

