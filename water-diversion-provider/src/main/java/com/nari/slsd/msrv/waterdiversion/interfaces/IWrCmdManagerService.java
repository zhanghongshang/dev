package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nari.slsd.msrv.common.exception.TransactionException;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.model.PageModel;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrCmdManagerDTO;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrCmdManagerOperateDto;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrCmdManager;
import org.springframework.transaction.annotation.Transactional;

/**
 * @title
 * @description 调度指令管理服务类
 * @author bigb
 * @updateTime 2021/8/27 11:12
 * @throws
 */
public interface IWrCmdManagerService extends IService<WrCmdManager> {

    DataTableVO getWrCmdManagerPage(PageModel pageModel);

    int insertWrCmdManager(WrCmdManagerOperateDto dto);

    int updateWrCmdManager(String managerId, String approveStatus, String approveName, String approveContent);

    @Transactional(rollbackFor = Exception.class)
    int deleteWrCmdManager(String cmdManagerId);

    @Transactional(rollbackFor = Exception.class)
    void saveWrCmdManager(WrCmdManagerDTO dto);
}
