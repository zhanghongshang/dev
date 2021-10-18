package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.nari.slsd.msrv.waterdiversion.model.dto.ActivitiApproval;
import com.nari.slsd.msrv.waterdiversion.model.dto.ActivitiHandle;
import com.nari.slsd.msrv.waterdiversion.model.vo.ShowTaskVO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 工作流引用 服务类
 * </p>
 *
 * @author zhs
 * @since 2021-08-26
 */
public interface IActiviciTaskService {
    /**
     *  提交流程
     *
     * @param
     * @return
     */
    Map<String,Object> getProcessInstanceList(ActivitiHandle activitiHandle);

    /**
     *  审批流程
     *
     * @param
     * @return
     */
    Map<String,Object> getProcessInstanceListByApproval(ActivitiApproval activitiApproval);
    /**
     * 根据流程id查询流程节点
     *
     *  @param processInstanceId
     *  @return
     */
    List<ShowTaskVO> findShowTaskByProcessId(String processInstanceId);

    /**
     * 根据人员id查询任务节点
     * @param userId
     * @return
     */
    List<ShowTaskVO> findShowTaskByUserId(String userId);

    /**
     * 根据人员id查询该人员已处理的任务
     * @param
     * @return
     */
    List<String> getJobHistory(String userId);

    /**
     * 根据流程id与key获取变量(审批状态)
     */
    String getBatchState(String processInstanceId,String key);//key = batchState
}
