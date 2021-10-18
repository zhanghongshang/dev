package com.nari.slsd.msrv.waterdiversion.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.filter.IFilterConfig;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.*;
import com.nari.slsd.msrv.waterdiversion.cache.interfaces.IModelCacheService;
import com.nari.slsd.msrv.waterdiversion.commons.ActivitiEnum;
import com.nari.slsd.msrv.waterdiversion.commons.RecentPlanEnum;
import com.nari.slsd.msrv.waterdiversion.commons.TaskStateEnum;
import com.nari.slsd.msrv.waterdiversion.interfaces.IActiviciTaskService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrProcessInfoService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrRecentPlanAdjustService;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrPlanTaskMapper;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrProcessInfoMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.ActivitiApproval;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrPlanTaskPositive;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrPlanTask;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrProcessInfo;
import com.nari.slsd.msrv.waterdiversion.model.vo.ShowTaskVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.StudioProcLog;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrPlanTaskVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrProcessInfoVO;
import com.nari.slsd.msrv.waterdiversion.utils.CommonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.omg.CORBA.StringHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.reflect.generics.tree.VoidDescriptor;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Description 流程信息 实现类
 * @Author ZHS
 * @Date 2021/8/29 22:55
 */
@Service
public class WrProcessInfoServiceImpl extends ServiceImpl<WrProcessInfoMapper, WrProcessInfo> implements IWrProcessInfoService {
    @Resource
    IActiviciTaskService activiciTaskService;
    @Resource
    IModelCacheService modelCacheService;
    @Resource
    WrProcessInfoMapper wrProcessInfoMapper;
    @Resource
    WrPlanTaskMapper wrPlanTaskMapper;
    @Resource
    IWrRecentPlanAdjustService wrRecentPlanAdjustService;

    /**
     * 流程启动
     */
    /**
     * 根据流程id查询任务节点(用水户)
     * @param processId 流程id
     * @return
     */
    @Override
    public  List<WrProcessInfoVO>  findWrPressInfoByProcessId(String processId) {
        List<WrProcessInfo> wrProcessInfoList = wrProcessInfoListByProcessId(processId,null);

        List<WrProcessInfoVO> wrProcessInfoVOList = new ArrayList<>();
        wrProcessInfoList.forEach(wrProcessInfo->{
            WrProcessInfoVO wrProcessInfoVO = new WrProcessInfoVO();
            BeanUtils.copyProperties(wrProcessInfo,wrProcessInfoVO);
            //从缓存中获取人员名称
            wrProcessInfoVO.setPersonName(modelCacheService.getRealName(wrProcessInfo.getPersonId()));

            //接收时间
            Long peceiveTime =  DateUtils.convertDateToLong(wrProcessInfo.getPeceiveTime());
            //处理时间
            Long processTime =  DateUtils.convertDateToLong(wrProcessInfo.getProcessTime());
            wrProcessInfoVO.setPeceiveTime(DateUtils.convertDateToLong(wrProcessInfo.getPeceiveTime()));
            wrProcessInfoVO.setProcessTime(DateUtils.convertDateToLong(wrProcessInfo.getProcessTime()));
            double hourNum = CommonUtil.number((processTime-peceiveTime)/(1000*60*60.0)).doubleValue();//化为小时
            int hourNumInt = (int)hourNum;
            if (hourNumInt==hourNum){
                wrProcessInfoVO.setTakeUpTime(hourNumInt+"h");
            }else{
                wrProcessInfoVO.setTakeUpTime(hourNum+"h");
            }

            wrProcessInfoVOList.add(wrProcessInfoVO);
        });
        return wrProcessInfoVOList;
    }

    /**
     *  流程审批
     *  @param
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public ResultModel addWrPressInfo(ActivitiApproval activitiApproval) {
        try{
            WrProcessInfo wrProcessInfo = wrProcessInfo(activitiApproval);
            wrProcessInfoMapper.insert(wrProcessInfo);
            return ResultModelUtils.getInstance(true);
        }catch(Exception e){
            log.error("保存失败："+e.getMessage());
            return ResultModelUtils.getFailInstanceExt();
        }

}

    /*    *//**
     *  添加审批后的数据
     * @param userId
     *//*
    @Override
    public void addWrPressInfo() {
        //根据流程id查询

    }*/

    /**
     *  根据登录信息查询流程id关联的任务
     * @param userId
     * @return
     */
    @Override
    public List<WrPlanTaskVO> planTaskByProcessId(String userId,Long startTime,Long endTime,String type) {
        List<WrPlanTaskVO> wrPlanTaskVOList= new ArrayList<>();

        List<String> processIdList = new ArrayList<>();
        //工作流中获取节点信息
        List<ShowTaskVO> showTaskList = new ArrayList<>();
        showTaskList = activiciTaskService.findShowTaskByUserId(userId);
        Map<String,String> map  = new HashMap<>();
        if (type.equals("0")){//代办
            for (ShowTaskVO showTaskVO:showTaskList){
                //流程id
                String processId = showTaskVO.getProcessInstanceId();
                String flagName =  showTaskVO.getTaskDefinitionKey();
                map.put(showTaskVO.getProcessInstanceId(),flagName);
                processIdList.add(processId);
            }

        }else if(type.equals("1")){//已办
            //查询当前人员历史任务
            processIdList = activiciTaskService.getJobHistory(userId);
        }
        List<WrPlanTask> wrPlanTaskList= wrPlanTaskList(processIdList ,startTime,endTime);
        wrPlanTaskList.forEach(wrPlanTask->{
            WrPlanTaskVO wrPlanTaskVO = new WrPlanTaskVO();
            BeanUtils.copyProperties(wrPlanTask, wrPlanTaskVO);
            wrPlanTaskVO.setCreateDate(wrPlanTask.getCreateDate().getTime());
            wrPlanTaskVO.setPersonName(modelCacheService.getRealName(wrPlanTask.getPersonId()));
            for(String string:map.keySet()){
                String  processId = string;
                if (wrPlanTask.getWaterPlanFillIn().equals(processId)){
                    wrPlanTaskVO.setTaskDefinitionKey(map.get(string));
                    if (type.equals("0")){
                        //获取流程变量
                        String batchState = activiciTaskService.getBatchState(processId,"batchState");
                        wrPlanTaskVO.setBatchState(batchState);
                    }
                }
            }
            wrPlanTaskVOList.add(wrPlanTaskVO);
        });
        return wrPlanTaskVOList;
    }

    /**
     *  流程审批
     * @return
     */
    @Transactional
    @Override
    public String findWrPressInfoByPositive(WrPlanTaskPositive wrPlanTaskPositive) {
        String state = "0";
        String processId = wrPlanTaskPositive.getProcessId();
        String userId = wrPlanTaskPositive.getUserId();
        String handleType = wrPlanTaskPositive.getHandleType();
        String userName = wrPlanTaskPositive.getUserName();
        String notFlagKey =  wrPlanTaskPositive.getNodeFlag();
        //流程节点变量
        String batchState = wrPlanTaskPositive.getBatchState();

        //流程表中查节点数据
        if (CollectionUtils.isNotEmpty(wrProcessInfoListByProcessId(processId,wrPlanTaskPositive.getUserId()))){
            state = "1";
            return state;
        }
        //获取当前节点接收时间
        Long  createTime = findCreateTimebyProcessIdandUserId(processId,userId);

        ActivitiApproval activitiApproval = new ActivitiApproval();
        activitiApproval.setProcessId(processId);
        activitiApproval.setHandleType(handleType);//提交

        if (wrPlanTaskPositive.getType().equals(RecentPlanEnum.BUILDING_IN_MONTH)||
                (StringUtils.isNotEmpty(notFlagKey) && notFlagKey.equals("mngement") && batchState.equals("0"))){//月内 单引审批
            Map<String,Object> map = new HashMap<>();
            map.put("flag", ActivitiEnum.FLAG_ADOPT_ONE.getId());
            activitiApproval.setExpression("flag=="+ ActivitiEnum.FLAG_ADOPT_ONE.getId());//判断网关分支flag == 1 // flag ==2
            activitiApproval.setVariables(map);
        }else {
            Map<String,Object> map = new HashMap<>();
            map.put("flag", ActivitiEnum.FLAG_ADOPT_TWO.getId());
            activitiApproval.setExpression("flag=="+ ActivitiEnum.FLAG_ADOPT_TWO.getId());
            activitiApproval.setVariables(map);
        }
        activitiApproval.setSrc("promng");
        Map<String,Object> UserInfoMap = new HashMap<>();
        UserInfoMap.put("id",userId);
        UserInfoMap.put("name",userName);
        activitiApproval.setUserInfo(UserInfoMap);
        //流程审批
        Map<String,Object> map = activiciTaskService.getProcessInstanceListByApproval(activitiApproval);
        Map<String,Object> nodeBeforeMap = (Map<String, Object>) map.get("nodeBeforeHandle");
        if (nodeBeforeMap!=null){
            String nodeName = String.valueOf(nodeBeforeMap.get("name"));
            //保存流程信息
            wrProcessInfo(processId,userId,userName,wrPlanTaskPositive.getContent(),wrPlanTaskPositive.getTaskId(),nodeName,
                    handleType,createTime);
        }
        //TODO 判断流程是否已审批 已审批下达调度指令
        //若为操作人员节点确认，则下发到底指令
        if (StringUtils.isNotEmpty(notFlagKey)){
            if (notFlagKey.equals("confirm-01")||notFlagKey.equals("confirm-02")){
                wrRecentPlanAdjustService.proposerConfirm(userId,wrPlanTaskPositive.getTaskId());//调度指令
            }
        }

        return state;
    }

    /**
     *  获取当前流程节点中接收时间
     */
    private Long findCreateTimebyProcessIdandUserId(String processId,String userId){
       Long createTime = null;
        List<ShowTaskVO> showTaskVOS =  activiciTaskService.findShowTaskByProcessId(processId);
        for (ShowTaskVO showTaskVO:showTaskVOS){
            if (showTaskVO.getAssignee().equals(userId)){
                createTime = showTaskVO.getCreateTime();
            }
        }
        return createTime;
    }

    /*
      * 保存流程信息
     */
    @Transactional
    public void wrProcessInfo(String processId, String userId, String userName, String count, String taskId, String nodeName,
                              String handleType,Long createTime){
        WrProcessInfo wrProcessInfo = new WrProcessInfo();
        wrProcessInfo.setId(IDGenerator.getId());
        wrProcessInfo.setProcessInstanceId(processId);
        wrProcessInfo.setPersonId(userId);
        wrProcessInfo.setPersonName(userName);
        wrProcessInfo.setProcessTime(new Date());
        wrProcessInfo.setProcessResult(count);//内容
        wrProcessInfo.setTaskId(taskId);
        wrProcessInfo.setNodeName(nodeName);
        wrProcessInfo.setPeceiveTime(DateUtils.convertTimeToDate(createTime));
        //agree
        if(handleType.equals("positive")){//同意
            wrProcessInfo.setAgree("1");
        }else if (handleType.equals("negative")){//拒绝
            wrProcessInfo.setAgree("0");
        }
        save(wrProcessInfo);
    }

    //根据登录信息查询该人员需处理的流程节点流程id
    private List<WrProcessInfo> wrProcessInfoList(String userId){
        if (StringUtils.isEmpty(userId)){
            return new ArrayList<WrProcessInfo>();
        }
        //流程表中查节点数据
        QueryWrapper<WrProcessInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("PERSON_ID",userId);
        List<WrProcessInfo> wrProcessInfoList = wrProcessInfoMapper.selectList(queryWrapper);
        return wrProcessInfoList;
    }
    //根据流程id查询任务信息列表
    private List<WrPlanTask> wrPlanTaskList(List<String> processIdList,Long startTime,Long endTime){
        if (processIdList.size()==0){
            return new ArrayList<WrPlanTask>();
        }
        //流程表中查节点数据
        QueryWrapper<WrPlanTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("WATER_PLAN_FILL_IN",processIdList);
        if (startTime!=null&&endTime!=null) {
            queryWrapper.between("CREATE_DATE", DateUtils.convertTimeToDate(startTime), DateUtils.convertTimeToDate(endTime));
        }
        queryWrapper.orderByDesc("CREATE_DATE");
        List<WrPlanTask> wrPlanTaskList = wrPlanTaskMapper.selectList(queryWrapper);
        return wrPlanTaskList;
    }
    //根据流程id查询流程表中节点数据
    private List<WrProcessInfo> wrProcessInfoListByProcessId(String processId,String userId){
        if (StringUtils.isEmpty(processId)){
            return new ArrayList<WrProcessInfo>();
        }
        //流程表中查节点数据
        QueryWrapper<WrProcessInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("PROCESS_INSTANCE_ID",processId);
        if (StringUtils.isNotEmpty(userId)){
            queryWrapper.eq("PERSON_ID",userId);
        }
        List<WrProcessInfo> wrProcessInfoList = wrProcessInfoMapper.selectList(queryWrapper);
        return wrProcessInfoList;
    }
    //流程信息存储数据整合
    private WrProcessInfo wrProcessInfo(ActivitiApproval activitiApproval){
        String userId = String.valueOf(activitiApproval.getParam().get("id"));
        String processId = activitiApproval.getProcessId();
        //工作流中获取节点信息
        List<ShowTaskVO> showTaskList = activiciTaskService.findShowTaskByUserId(userId);
        WrProcessInfo wrProcessInfo = new WrProcessInfo();
        showTaskList.forEach(showTaskVO->{
            if (userId.equals(showTaskVO.getAssignee())){
                //接受时间
                wrProcessInfo.setPeceiveTime(DateUtils.convertTimeToDate(showTaskVO.getCreateTime()));
            }
        });
        //从审批查询中获取管理站id
        Map<String,Object> processInfo =  activiciTaskService.getProcessInstanceListByApproval(activitiApproval);

        //根据人员id对比当前审批节点
        List<StudioProcLog> studioProcLogs = JSON.parseArray(JSON.toJSONString(processInfo.get("studioProcLogs")),StudioProcLog.class);
        List<StudioProcLog> studioProcLogsList = new ArrayList<>();
        studioProcLogs.forEach(studioProcLog->{
            if (userId.equals(studioProcLog.getCreatorId())){
                StudioProcLog studioProcLogvo = new StudioProcLog();
                BeanUtils.copyProperties(studioProcLog,studioProcLogvo);
                studioProcLogsList.add(studioProcLogvo);
            }
        });
        //取最新一条当前节点数据
        Optional<StudioProcLog> max = studioProcLogsList.stream().max(Comparator.comparing(StudioProcLog::getCreatedAt));
        StudioProcLog studioProcLog = max.get();

        String handleAtNodeName = studioProcLog.getHandleAtNodeName();

        wrProcessInfo.setId(IDGenerator.getId());
        wrProcessInfo.setPersonId(userId);
        wrProcessInfo.setPersonName(modelCacheService.getRealName(userId));
        wrProcessInfo.setTaskId(activitiApproval.getPlanId());

        wrProcessInfo.setProcessTime(studioProcLog.getCreatedAt());
        //是否通过
        wrProcessInfo.setAgree(activitiApproval.getHandleType());
        wrProcessInfo.setProcessResult(activitiApproval.getProcessResult());
        //流程id
        wrProcessInfo.setProcessInstanceId(processId);
        wrProcessInfo.setNodeName(handleAtNodeName);
        return  wrProcessInfo;
    }
}
