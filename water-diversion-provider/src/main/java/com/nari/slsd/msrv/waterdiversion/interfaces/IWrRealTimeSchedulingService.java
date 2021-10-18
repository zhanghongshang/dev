package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.nari.slsd.msrv.waterdiversion.model.dto.RecentPlanResizeDTO;
import com.nari.slsd.msrv.waterdiversion.model.vo.GisRealtimeDispatchListVO;

import java.sql.Timestamp;
import java.util.List;

/**
 * <p>
 * 实时调度服务类
 * </p>
 *
 * @author bigb
 * @since 2021-08-10
 */
public interface IWrRealTimeSchedulingService {

    /**
     * 获取未来n天各引水口流量调整情况
     */
    GisRealtimeDispatchListVO realTimeSchedulingAndCaching();

    /**
     * 近期用水计划填报，更新实时调度缓存
     * @param dtoList
     */
    void recentPlanResizeForRedis(List<RecentPlanResizeDTO> dtoList);

    /**
     * 人工选择调度
     * @param buildingIdList
     * @param selectDate
     * @param pointTypeList
     */
    List<GisRealtimeDispatchListVO> artificialScheduling(List<String> buildingIdList, Long selectDate, List<String> pointTypeList);

    /**
     * 计划调度
     * @param buildingTypeList
     * @param pointTypeList
     * @return
     */
    List<GisRealtimeDispatchListVO> planScheduling(List<String> buildingTypeList , List<String> pointTypeList);
}
