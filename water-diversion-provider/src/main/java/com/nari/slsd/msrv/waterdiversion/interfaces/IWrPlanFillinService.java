package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrPlanTask;

import java.util.List;
import java.util.Map;

/**
 * @description: 调度公共数据接口
 * @author: zhs
 * @date: 2021/8/18
 * @return:
 */
public interface IWrPlanFillinService {
    //获取用水单位填报状态与流程id
    Map<String,Object> state(String userId, List<Integer> unitLevel, List<String> buildType, String year, String month, Integer fillReport,List<Integer> levels);

    //获取用管理站填报状态
    String fillInState(List<String> mngUnitId, String year, String month);

    //任务数据整合(管理站)
    WrPlanTask wrPlanTask(String planId, String mngUnitId, String mngUnitName, String userId, String content, String month,
                                  String year, String planType, String taskName);
}
