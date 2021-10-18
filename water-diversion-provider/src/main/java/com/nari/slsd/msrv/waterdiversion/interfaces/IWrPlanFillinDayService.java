package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrBuildingAndDiversion;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrPlanFillInDayAllDTO;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrPlanFillinDay;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrPlanAdjustVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrPlanFillInDayAndBuildingIdTreeVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrPlanFillinDayAdjustVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrPlanFillinDayVO;

import java.text.ParseException;
import java.util.List;

/**
 * @description: 近期计划填报接口
 * @author: zhs
 * @date: 2021/8/2o
 * @return:
 */
public interface IWrPlanFillinDayService extends IService<WrPlanFillinDay> {

    /**
     *  查询近期调整计划数据（月内）
     */
    public WrPlanFillInDayAndBuildingIdTreeVO findWithinMonth(Long startTime, Long endTime, List<String> buildingId, List<String> buildingName, String waterUnitId);
    /**
     *查询近期调整原计划数据（跨月）
     */
   public WrPlanFillinDayAdjustVO findSpanMonth(Long startTime, Long endTime, List<String> buildingId, List<String> buildingName, List<String> months);
    /**
     *  查询近期调整计划数据（超年）
     */
    public List<WrPlanFillinDayVO> findSuperYear(Long startTime, Long endTime, List<String> buildingId, List<String> buildingName);
    /**
     * 保存近期计划数据（月内）
     */
    public void updatePlanFullinDay(WrPlanFillInDayAllDTO wrPlanFillInDayAllDTO);

    /**
     * 调整计划查询
     *
     */
    public DataTableVO findAdjustList(Long startTime, Long endTime, List<String> mngUnitId, Integer pageIndex, Integer pageSize);
    /**
     * 调整计划查询(曲线)
     *
     */
   public WrPlanFillinDayVO findAdjustCurve(Long startTime, Long endTime, List<String> buildingId, String buildingName);

    /**
     * 查询管理站下的引水口
     */
    ResultModel getBuildingIdByMngUnitId(List<String> mngUnitId, String mngUnitName, String waterUnitId, String waterUnitName, List<String> buildingLevels);

    /**
     * 剩余水量对比校验
     */
    ResultModel checkWaterValue(Long startTime, Long endTime,List<String> months,String buildingName,String waterUnitId,List<String> buildingIds,
                                Double flowValue, String type);
}
