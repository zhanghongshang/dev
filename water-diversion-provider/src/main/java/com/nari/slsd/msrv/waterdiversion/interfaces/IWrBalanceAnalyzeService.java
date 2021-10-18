package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.nari.slsd.msrv.waterdiversion.model.dto.WrPlanFillInDayAllDTO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WaterBalanceAnalyzeVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseDetailListVO;

import java.util.Date;
import java.util.List;

/**
 * @title
 * @description 近期计划调整
 * @author bigb
 * @updateTime 2021/9/13 23:24
 * @throws
 */
public interface IWrBalanceAnalyzeService {

    WaterBalanceAnalyzeVO waterBalanceAnalyze(WrPlanFillInDayAllDTO allDTO);

    List<WrUseDetailListVO> getWaterUseDetail(List<String> buildingIdList, Date startDate, Date endDate);
}
