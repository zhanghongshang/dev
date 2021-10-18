package com.nari.slsd.msrv.waterdiversion.services;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nari.slsd.msrv.common.utils.IDGenerator;
import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.msrv.waterdiversion.commons.PlanFillInTypeEnum;
import com.nari.slsd.msrv.waterdiversion.commons.TaskStateEnum;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWaterBuildingManagerService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrPlanFillinService;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrPlanTaskMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrPlanTaskDTO;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WaterBuildingManager;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrPlanTask;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrPlanTaskSub;
import com.nari.slsd.msrv.waterdiversion.model.vo.BuildingExt;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseUnitSimpleVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * @Description 计划填报公共实现类
 * @Author ZHS
 * @Date 2021/10/8 16:24
 */
@Service
public class WaterPlanFillinServiceImp implements IWrPlanFillinService {
    @Resource
    WrPlanTaskMapper wrPlanTaskMapper;
    @Resource
    IWaterBuildingManagerService waterBuildingManagerService;
    //获取用户填报状态
    @Override
    public Map<String,Object> state(String userId,List<Integer> unitLevel,List<String> buildType,String year,String month,Integer fillReport,List<Integer> levels){
        Map<String,Object> map = new HashMap<>();
        String state = "0";
        String processId = "plan_year_id";
        //获取用户id对应的用水单位集合
        List<String> waterList = taskList(userId,unitLevel,buildType,fillReport,levels).stream().distinct().collect(Collectors.toList());
        //String waterStr = StringUtils.join(myList.toArray(), ",");
        QueryWrapper<WrPlanTask> queryWrapper = new QueryWrapper<>();

        if (StringUtils.isNotEmpty(month)){
            queryWrapper.eq("fw.YEAR",year);
            queryWrapper.eq("fw.MONTH",month);
            queryWrapper.eq("fw.PLAN_TYPE ","1");
            queryWrapper.in("wp.UNIT_ID ",waterList);
        }else{
            queryWrapper.eq("fw.YEAR",year);
            queryWrapper.eq("fw.PLAN_TYPE ","0");
            queryWrapper.in("wp.UNIT_ID ",waterList);
        }
        List<WrPlanTaskDTO> wrPlanTaskList =  wrPlanTaskMapper.orderPlanYearList(queryWrapper);
        if (wrPlanTaskList.size()>0){
            state = "1";
            processId = wrPlanTaskList.get(0).getWaterPlanFillIn();
        }
        map.put("state",state);
        map.put("processId",processId);
        return  map;
    }

    /**
     *  获取管理填报状态
     * @param mngUnitId
     * @param year
     * @param month
     * @return
     */
    @Override
    public String fillInState(List<String> mngUnitId, String year, String month) {
        String state = "0";
        //获取用户id对应的用水单位集合
        //List<String> waterList = taskListByMngUnitId(mngUnitId,unitLevel,buildType,fillReport,buildLevels).stream().distinct().collect(Collectors.toList());
        //String waterStr = StringUtils.join(myList.toArray(), ",");
        QueryWrapper<WrPlanTask> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(month)){
            queryWrapper.eq("YEAR",year);
            queryWrapper.eq("MONTH",month);
            queryWrapper.eq("PLAN_TYPE ","1");
        }else{
            queryWrapper.eq("YEAR",year);
            queryWrapper.eq("PLAN_TYPE ","0");
        }
        List<WrPlanTask> wrPlanTaskLists =  wrPlanTaskMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(wrPlanTaskLists)){
            if (wrPlanTaskLists.get(0).getFillType().equals("0")){
                state ="1";
            }
        }else{
            if (StringUtils.isNotEmpty(month)){
                queryWrapper.eq("fw.YEAR",year);
                queryWrapper.eq("fw.MONTH",month);
                queryWrapper.eq("fw.PLAN_TYPE ","1");
                queryWrapper.in("wp.UNIT_ID ",mngUnitId);
            }else{
                queryWrapper.eq("fw.YEAR",year);
                queryWrapper.eq("fw.PLAN_TYPE ","0");
                queryWrapper.in("wp.UNIT_ID ",mngUnitId);
            }
            List<WrPlanTaskDTO> wrPlanTaskList =  wrPlanTaskMapper.orderPlanYearList(queryWrapper);
            if (wrPlanTaskList.size()>0){
                state = "1";
            }
        }
        return  state;
    }

    @Override
    public WrPlanTask wrPlanTask(String planId, String mngUnitId, String mngUnitName, String userId,
                                 String content, String month, String year,String planType,String taskName) {
        //任务关联表数据
        List<WrPlanTaskSub> wrPlanTaskSubList = new ArrayList<>();
        WrPlanTaskSub wrPlanTaskSub = new WrPlanTaskSub();
        wrPlanTaskSub.setId(IDGenerator.getId());
        wrPlanTaskSub.setTaskId(planId);
        wrPlanTaskSub.setUnitType(PlanFillInTypeEnum.MNG_UNIT.getId());//管理站单位
        wrPlanTaskSub.setUnitId(mngUnitId);
        wrPlanTaskSub.setUnitName(mngUnitName);
        wrPlanTaskSubList.add(wrPlanTaskSub);

        //填报人id
        WrPlanTask wrPlanTask = new WrPlanTask();
        wrPlanTask.setId(planId);
        wrPlanTask.setPersonId(userId);
        wrPlanTask.setContent(content);
        wrPlanTask.setPlanType(planType);//年计划填报
        wrPlanTask.setTaskName(taskName);
        wrPlanTask.setMonth(month);
        wrPlanTask.setYear(year);
        wrPlanTask.setState(TaskStateEnum.UNDER_APPROVAL.getId());//待审核
        wrPlanTask.setWrPlanTaskSubList(wrPlanTaskSubList);
        return wrPlanTask;
    }

    //获取用水单位ID集合
    List<String> taskList(String userId,List<Integer> unitLevel,List<String> buildType,Integer fillReport,List<Integer> levels){
        List<String> waterList = new ArrayList<>();
        List<BuildingExt>  buildingExtlsit = waterBuildingManagerService.getBuildingExtListByUser(userId,unitLevel,buildType,fillReport,levels);
        for (BuildingExt buildingExt:buildingExtlsit){
            List<WrUseUnitSimpleVO> wrUseUnitSimpleVOS = buildingExt.getWaterUnits();
            if (wrUseUnitSimpleVOS.size()>0){
                waterList.add(wrUseUnitSimpleVOS.get(wrUseUnitSimpleVOS.size()-1).getWaterUnitId());
            }
        }
        return waterList;
    }

    //获取用水单位ID集合
    List<String> taskListByMngUnitId(List<String> mngUnitId,List<Integer> unitLevel,List<String> buildType,Integer fillReport,List<Integer> levels){
        List<String> waterList = new ArrayList<>();
        List<BuildingExt>  buildingExtlsit = waterBuildingManagerService.getBuildingExtListByMng(mngUnitId,unitLevel,buildType,fillReport,levels);
        for (BuildingExt buildingExt:buildingExtlsit){
            List<WrUseUnitSimpleVO> wrUseUnitSimpleVOS = buildingExt.getWaterUnits();
            if (wrUseUnitSimpleVOS.size()>0){
                waterList.add(wrUseUnitSimpleVOS.get(wrUseUnitSimpleVOS.size()-1).getWaterUnitId());
            }
        }
        return waterList;
    }

}
