package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.model.PageModel;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrDispatchInstructionDto;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrDispatchInstruction;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrDispatchSchemeParam;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrDispatchInstructionVO;

import java.util.List;

/**
 * @title
 * @description 调度方案指令服务类
 * @author bigb
 * @updateTime 2021/8/21 11:12
 * @throws
 */
public interface IWrDispatchInstructionService extends IService<WrDispatchInstruction> {

    List<WrDispatchInstructionVO> getAllPendingIssueOrder();

    DataTableVO getWrDispatchInstructionPage(PageModel pageModel, String mangerId);

    void batchUpdateInstruction(List<WrDispatchInstructionDto> dtoList);

    void executeInstruction(String id, String operateType , String personId , String executeContent);
}
