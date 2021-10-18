package com.nari.slsd.msrv.waterdiversion.services;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.utils.BeanUtils;
import com.nari.slsd.msrv.common.utils.CollectionsUtils;
import com.nari.slsd.msrv.common.utils.DateUtils;
import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.msrv.waterdiversion.cache.interfaces.IModelCacheService;
import com.nari.slsd.msrv.waterdiversion.commons.TaskStateEnum;
import com.nari.slsd.msrv.waterdiversion.config.RedisUtil;
import com.nari.slsd.msrv.waterdiversion.interfaces.*;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrPlanTaskMapper;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrPlanTaskSubMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrPlanTaskDTO;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.*;
import com.nari.slsd.msrv.waterdiversion.model.vo.*;
import com.nari.slsd.msrv.waterdiversion.utils.CommonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  用水计划任务 服务实现类
 * </p>
 *
 * @author zhs
 * @since 2021-08-13
 */
@Service
public class WrPlanTaskImpl extends ServiceImpl<WrPlanTaskMapper, WrPlanTask> implements IWrPlanTaskService {

    @Resource
    WrPlanTaskMapper wrPlanTaskMapper;
    @Resource
    WrPlanTaskSubMapper wrPlanTaskSubMapper;
    @Resource
    IModelCacheService modelCacheService;
    @Resource
    IWaterBuildingManagerService waterBuildingManagerService;

    /**
     * 条件查询用水计划任务表
     * @param statrTime 开始时间
     * @param endTime   结束时间
     * @param pageIndex
     * @param pageSize
     * @return
     */

    @Override
    public DataTableVO findWrPlanTask(String userId,List<String> mngUnitId,List<Integer> unitLevel,List<String> buildType ,Integer fillReport ,Long statrTime, Long endTime, String planType,String state,List<Integer> levels,Integer pageIndex, Integer pageSize) {
        //获取用户id对应的用水单位集合
        List<String> waterList = taskList(userId,mngUnitId,unitLevel,buildType,fillReport,levels);
        //获取计划任务id
        List<String> taskList =  waterUnitList(waterList);
        QueryWrapper<WrPlanTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id",myList(taskList));
        if (StringUtils.isNotEmpty(statrTime)||StringUtils.isNotEmpty(endTime)){
            Date startDate = DateUtils.convertTimeToDate(statrTime);
            Date endDate = DateUtils.convertTimeToDate(endTime);
            queryWrapper.between("CREATE_DATE",startDate,endDate);
        }
        if (StringUtils.isNotEmpty(planType)){
            queryWrapper.eq("PLAN_TYPE",planType);
        }
        if (StringUtils.isNotEmpty(state)){
            queryWrapper.eq("STATE",state);
        }
        IPage<WrPlanTask> page = new Page<>(pageIndex, pageSize);
        page(page, queryWrapper);
        List<WrPlanTask> wrPlanTasks = page.getRecords();

        List<WrPlanTaskVO> wrPlanTaskVOS = new ArrayList<>();
        wrPlanTasks.forEach(wrPlanTask->{
            WrPlanTaskVO wrPlanTaskvo = new WrPlanTaskVO();
            BeanUtils.copyProperties(wrPlanTask, wrPlanTaskvo);
            wrPlanTaskvo.setCreateDate(DateUtils.convertDateToLong(wrPlanTask.getCreateDate()));
            //从缓存中获取人员名称
            wrPlanTaskvo.setPersonName(modelCacheService.getRealName(wrPlanTask.getPersonId()));
            wrPlanTaskVOS.add(wrPlanTaskvo);
        });
        //result
        DataTableVO dataTableVO = new DataTableVO();
        dataTableVO.setRecordsTotal(page.getTotal());
        dataTableVO.setRecordsFiltered(page.getTotal());
        dataTableVO.setData(wrPlanTaskVOS);
        return dataTableVO;
    }

    /**
     *  用户填报计划总览
     *
     * @param planType
     * @param userId
     * @param mngUnitId
     * @param unitLevels
     * @return
     */
    @Override
    public List<BuildingTask> findYearTasks(String time, String planType, String userId, List<String> buildingTypes,List<String> mngUnitId,Integer fillReport, List<Integer> unitLevels,List<Integer> levels) {
        List<MngUnitAndWrUseUnit>  mngUnitAndWrUseUnitList = new ArrayList<>();
        if (StringUtils.isNotEmpty(userId)){
            //根据用水户获取管理单位用水单位数据
            mngUnitAndWrUseUnitList = waterBuildingManagerService.getMngAndWrUseUnitsByUser(userId,unitLevels,buildingTypes,fillReport,levels);
        }else {
            //根据管理单位获取管理单位用水单位数据
            mngUnitAndWrUseUnitList = waterBuildingManagerService.getMngAndWrUseUnitsByMng(mngUnitId,unitLevels,buildingTypes,fillReport,levels);
        }
        //获取用水单位
        List<String> waterUnitIds = new ArrayList<>();
        for (MngUnitAndWrUseUnit mngUnitAndWrUseUnit:mngUnitAndWrUseUnitList){
            List<WrUseUnitSimpleVO> wrUseUnitSimpleVOS = mngUnitAndWrUseUnit.getWaterUnits();
            if(wrUseUnitSimpleVOS.size()>0){
                waterUnitIds.add(wrUseUnitSimpleVOS.get(wrUseUnitSimpleVOS.size()-1).getWaterUnitId());
            }
        }
        List<String> timeList = Arrays.asList(time.split("-"));
        List<String> unitId = new ArrayList<>();
        //String waterStr = StringUtils.join(waterUnitIds.toArray(), ",");
        QueryWrapper<WrPlanTask> queryWrapper = new QueryWrapper<>();
        if (planType.equals("0")){
            //年填报相关信息
            queryWrapper.eq("fw.YEAR",timeList.get(0));
            queryWrapper.eq("fw.PLAN_TYPE ",planType);
        }else if (planType.equals("1")){
            //月年填报相关信息
            queryWrapper.eq("fw.YEAR",timeList.get(0));
            queryWrapper.eq("fw.MONTH",timeList.get(1));
            queryWrapper.eq("fw.PLAN_TYPE ",planType);
        }
        queryWrapper.in("wp.UNIT_ID ",waterUnitIds);
        List<WrPlanTaskDTO> wrPlanTaskList = wrPlanTaskMapper.orderPlanYearList(queryWrapper);
        //整合计划任务列表
        List<BuildingTask> buildingTaskList = new ArrayList<>();
        for (WrPlanTaskDTO wrPlanTaskDto:wrPlanTaskList){
            //获取用水单位底层id
            List<WrUseUnitSimpleVO> wrUseUnitSimpleVOs = new ArrayList<>();
            WrUseUnitSimpleVO wrUseUnitSimpleVO = new WrUseUnitSimpleVO();
            wrUseUnitSimpleVO.setWaterUnitId(wrPlanTaskDto.getUnitId());
            wrUseUnitSimpleVOs.add(wrUseUnitSimpleVO);

            BuildingTask buildingTask = new BuildingTask();
            BeanUtils.copyProperties(wrPlanTaskDto, buildingTask);
            buildingTask.setCreateTime(DateUtils.dateToLong(wrPlanTaskDto.getCreateDate()));
            //获取用户名
            buildingTask.setPersonName(modelCacheService.getRealName(wrPlanTaskDto.getPersonId()));
            buildingTask.setState(wrPlanTaskDto.getState());
            buildingTask.setWaterUnits(wrUseUnitSimpleVOs);
            buildingTaskList.add(buildingTask);
            unitId.add(wrPlanTaskDto.getUnitId());
        }
        waterUnitIds.removeAll(unitId);
        //添加未填报数据
        for (String waterUnitId:waterUnitIds){
            //获取用水单位底层id
            List<WrUseUnitSimpleVO> wrUseUnitSimpleVOs = new ArrayList<>();
            WrUseUnitSimpleVO wrUseUnitSimpleVO = new WrUseUnitSimpleVO();
            wrUseUnitSimpleVO.setWaterUnitId(waterUnitId);
            wrUseUnitSimpleVOs.add(wrUseUnitSimpleVO);

            BuildingTask buildingTask = new BuildingTask();
            buildingTask.setWaterUnits(wrUseUnitSimpleVOs);
            //未填报状态
            buildingTask.setState("0");
            buildingTaskList.add(buildingTask);
        }
        List<BuildingTask> buildingTaskLists = new ArrayList<>();
        for (MngUnitAndWrUseUnit mngUnitAndWrUseUnit:mngUnitAndWrUseUnitList){
            List<WrUseUnitSimpleVO> wrUseUnitSimpleVOs = mngUnitAndWrUseUnit.getWaterUnits();
            if (wrUseUnitSimpleVOs.size()==0){
                continue;
            }
            //用户填报计划数据整合
            String waterUnitId = mngUnitAndWrUseUnit.getWaterUnits().get(wrUseUnitSimpleVOs.size()-1).getWaterUnitId();
            for (BuildingTask buildingTask:buildingTaskList){
                List<WrUseUnitSimpleVO> lastWrUseUnitSimpleVOs = buildingTask.getWaterUnits();
                if(lastWrUseUnitSimpleVOs.size()==0){
                    continue;
                }
                //处理相同的用水单位
                if (lastWrUseUnitSimpleVOs.get(lastWrUseUnitSimpleVOs.size()-1).getWaterUnitId().equals(waterUnitId)){
                    BuildingTask buildingTasks = new BuildingTask();
                    BeanUtils.copyProperties(buildingTask, buildingTasks);
                    buildingTasks.setWaterUnits(mngUnitAndWrUseUnit.getWaterUnits());
                    buildingTasks.setMngUnitName(mngUnitAndWrUseUnit.getMngUnitName());
                    buildingTasks.setMngUnitId(mngUnitAndWrUseUnit.getMngUnitId());
                    buildingTaskLists.add(buildingTasks);
                }
            }
        }
        return buildingTaskLists;
    }

    //获取用水单位ID集合
    public List<String> taskList(String userId,List<String> mngUnitId,List<Integer> unitLevel,List<String> buildType,Integer fillReport,List<Integer> levels){
        List<String> waterList = new ArrayList<>();
        List<BuildingExt>  buildingExtlsit = new ArrayList<>();
        if(StringUtils.isNotEmpty(userId)){
            buildingExtlsit = waterBuildingManagerService.getBuildingExtListByUser(userId,unitLevel,buildType,fillReport,levels);
        }else{
            buildingExtlsit = waterBuildingManagerService.getBuildingExtListByMng(mngUnitId,unitLevel,buildType,fillReport,levels);
        }
        for (BuildingExt buildingExt:buildingExtlsit){
            List<WrUseUnitSimpleVO> wrUseUnitSimpleVOS = buildingExt.getWaterUnits();
            if (wrUseUnitSimpleVOS.size()>0){
                waterList.add(wrUseUnitSimpleVOS.get(wrUseUnitSimpleVOS.size()-1).getWaterUnitId());
            }
        }
        return waterList;
    }
    //获取计划id
    private List<String> waterUnitList(List<String> waterList){
        List<String> taskList = new ArrayList<>();
        QueryWrapper<WrPlanTaskSub> wrapper = new QueryWrapper<>();
        if (CollectionUtils.isNotEmpty(waterList)){
            wrapper.in("UNIT_ID",myList(waterList));
        }
        List<WrPlanTaskSub> wrPlanTaskSubs = wrPlanTaskSubMapper.selectList(wrapper);
        for (WrPlanTaskSub wrPlanTaskSub:wrPlanTaskSubs ){
            taskList.add(wrPlanTaskSub.getTaskId());
        }
        return taskList;
    }
    //去重
    private static List<String> myList(List<String> list){
        List<String> myList = list.stream().distinct().collect(Collectors.toList());
        return myList ;
    }
    //计划任务表与关联表数据存储
    @Override
    public boolean insert(WrPlanTask wrPlanTask){
        try{
            wrPlanTaskMapper.insert(wrPlanTask);
            List<WrPlanTaskSub> wrPlanTaskSubList = wrPlanTask.getWrPlanTaskSubList();
            for (WrPlanTaskSub wrPlanTaskSub:wrPlanTaskSubList){
                wrPlanTaskSubMapper.insert(wrPlanTaskSub);
            }
        }catch(Exception e){
            return false;
        }
        return true;
    }

}
