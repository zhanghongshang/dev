package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.nari.slsd.msrv.waterdiversion.model.vo.WrRecentPlanDetailVO;

/**
 * @title
 * @description 申请确认服务类
 * @author bigb
 * @updateTime 2021/9/14 22:34
 * @throws
 */
public interface IWrProposerConfirmService {

    WrRecentPlanDetailVO showBackLog(String taskId, String flag,String batchState);
}
