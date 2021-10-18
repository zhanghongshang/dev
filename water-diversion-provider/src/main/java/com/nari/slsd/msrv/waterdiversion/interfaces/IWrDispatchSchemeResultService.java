package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.waterdiversion.model.dto.ModelRequestDto;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrUseUnitManagerDTO;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrDispatchSchemeResult;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrUseUnitManager;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrDispatchSchemeResultVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseUnitManagerVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseUnitNode;

import java.util.List;

/**
 * @title
 * @description 调度方案结果服务类
 * @author bigb
 * @updateTime 2021/8/21 11:12
 * @throws
 */
public interface IWrDispatchSchemeResultService extends IService<WrDispatchSchemeResult> {

    List<WrDispatchSchemeResultVO> getAllSchemeResult(String schemeId);

    void batchConfirm(List<String> idList);
}
