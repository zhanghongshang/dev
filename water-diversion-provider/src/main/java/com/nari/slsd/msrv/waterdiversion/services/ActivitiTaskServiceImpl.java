package com.nari.slsd.msrv.waterdiversion.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.filter.IFilterConfig;
import com.nari.slsd.msrv.waterdiversion.feign.interfaces.ActivitiFeignClient;
import com.nari.slsd.msrv.waterdiversion.feign.interfaces.StudioFeignClient;
import com.nari.slsd.msrv.waterdiversion.interfaces.IActiviciTaskService;
import com.nari.slsd.msrv.waterdiversion.model.dto.ActivitiApproval;
import com.nari.slsd.msrv.waterdiversion.model.dto.ActivitiHandle;
import com.nari.slsd.msrv.waterdiversion.model.vo.ShowTaskVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @Description 工作流公共方法 实现类
 * @Author ZHS
 * @Date 2021/10/8 16:24
 */
@Slf4j
@Service
public class ActivitiTaskServiceImpl implements IActiviciTaskService {
    @Resource
    ActivitiFeignClient activitiFeignClient;
    @Resource
    StudioFeignClient studioFeignClient;

    /**
     *  流程提交
     * @param activitiHandle
     * @return
     */
    @Override
    public Map<String,Object> getProcessInstanceList(ActivitiHandle activitiHandle) {
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(activitiHandle));
        JSONObject json = studioFeignClient.getProcessInstanceList(jsonObject);
        Map<String,Object> objectMap = (Map<String, Object>) json.get("processInfo");
        return objectMap;
    }
    /**
     *  流程审批
     * @param activitiApproval
     * @return
     */
    @Override
    public Map<String, Object> getProcessInstanceListByApproval(ActivitiApproval activitiApproval) {
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(activitiApproval));
        JSONObject json = studioFeignClient.getProcessInstanceList(jsonObject);
        Map<String,Object> objectMap = (Map<String, Object>) json.get("processInfo");
        return objectMap;
    }

    /**
     *  流程id查询流程节点
     * @param processInstanceId
     * @return
     */
    @Override
    public List<ShowTaskVO> findShowTaskByProcessId(String processInstanceId) {
        JSONObject jsonObject =  activitiFeignClient.getActiveTaskByProcessInstanceId(processInstanceId);
        List<ShowTaskVO> showTaskList =JSON.parseArray(JSON.toJSONString(jsonObject.get("data")),ShowTaskVO.class);
        return showTaskList;
    }
    /**
     *  人员id查询流程节点
     * @param userId
     * @return
     */
    @Override
    public List<ShowTaskVO> findShowTaskByUserId(String userId) {
        JSONObject jsonObject = activitiFeignClient.getShowTaskList(userId,"1");
        List<ShowTaskVO> showTaskList = JSON.parseArray(JSON.toJSONString(jsonObject.get("data")),ShowTaskVO.class);
        return showTaskList;
    }

    /**
     *  人员id查询已处理的任务节点
     * @param userId
     * @return
     */
    @Override
    public List<String> getJobHistory(String userId) {
        JSONObject jsonObject = activitiFeignClient.getJobHistory(userId,10000,1);
        List<Map<String,Object>> showTaskList = (List<Map<String, Object>>) jsonObject.get("data");
        List<String> processIds = new ArrayList<>();
        showTaskList.forEach(map -> {
            processIds.add(String.valueOf(map.get("processInstanceId")));
        });
        return processIds;
    }
    /**
     *  根据流程id与key获取变量(审批状态)
     * @param processInstanceId
     * @return
     */
    @Override
    public String getBatchState(String processInstanceId, String key) {
        JSONObject jsonObject = activitiFeignClient.getVariableByProcessInstanceId(processInstanceId,key);
        Object object = jsonObject.get("info");
        Map<String,Object> info = JSONObject.parseObject(object.toString());
        //Map<String,Object> info = (Map<String, Object>) ;
        String batchState = "3";
        if (info!=null&&info.get("batchState")!=null){
            batchState = String.valueOf(info.get(key));
        }
        return batchState;
    }
}
