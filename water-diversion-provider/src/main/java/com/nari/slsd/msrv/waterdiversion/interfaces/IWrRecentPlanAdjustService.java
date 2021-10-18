package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.nari.slsd.msrv.waterdiversion.model.dto.WrPlanFillInDayAllDTO;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrPlanInterDay;
import com.nari.slsd.msrv.waterdiversion.model.vo.LendOutDetailVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.SimpleWrBuildingVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrRecentPlanForMultiUseUnitVO;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @title
 * @description 近期计划调整
 * @author bigb
 * @updateTime 2021/9/13 23:24
 * @throws
 */
public interface IWrRecentPlanAdjustService {

    @Transactional
    void proposerConfirm(String operator, String taskId);

    List<WrPlanInterDay> getInterDayList(String buildingId, List<Date> dateList);

    WrRecentPlanForMultiUseUnitVO getAllBuildingsForUseUnit(String buyerUnitId, String saleUnitId);

    BigDecimal getRemainWaterOfUseUnit(String useUnitIt);

    Map<String, BigDecimal> getRemainOfMonth(List<String> buildingIdList);

    Map<String,BigDecimal> getRemainOfYear(List<String> buildingIdList);

    List<SimpleWrBuildingVO> getAllAdaptiveBorrowBuildings(String buildingId, String waterUseId);

    List<LendOutDetailVO> adjustDetail(String buildingId, BigDecimal lendWater, String adjustType, Long startTime, Long endTime);

    List<WrPlanInterDay> getWrPlanInterDays(List<String> buildingIdList, Date startTime, Date endTime);
}
