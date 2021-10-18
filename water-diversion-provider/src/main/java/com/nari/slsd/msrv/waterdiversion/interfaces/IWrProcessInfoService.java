package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.waterdiversion.model.dto.ActivitiApproval;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrPlanTaskPositive;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrProcessInfo;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrPlanTaskVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrProcessInfoVO;

import java.util.List;

/**
 * @Description 流程信息 服务类
 * @Author ZHS
 * @Date 2021/8/29 22:53
 */
public interface IWrProcessInfoService extends IService<WrProcessInfo> {

    /**
     *   根据流程id查询流程节点状态
     * @param processId 流程id
     * @return
     */
    List<WrProcessInfoVO>  findWrPressInfoByProcessId(String processId);

    /**
     *  添加流程节点信息
     * @param
     * @return
     */
    ResultModel addWrPressInfo(ActivitiApproval activitiApproval);

    /**
     *  根据流程id查询当前任务
     * @return
     */
    List<WrPlanTaskVO> planTaskByProcessId(String userId,Long startTime,Long endTime,String type);

    /**
     *流程审批
     */
    String findWrPressInfoByPositive(WrPlanTaskPositive wrPlanTaskPositive);

}
