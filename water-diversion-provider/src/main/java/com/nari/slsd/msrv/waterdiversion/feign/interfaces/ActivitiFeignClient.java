package com.nari.slsd.msrv.waterdiversion.feign.interfaces;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "activiti", url = "${activiti.url}")
public interface ActivitiFeignClient {
    /**
     *
     * @Author: zhs
     * @Description: 人员id查询流程节点
     * @Date: 2021/8/25 9:45 上午
     * @Version:1.0
     */
    @GetMapping(path = "acti/actManager/showTaskList")
    JSONObject getShowTaskList(@RequestParam("userId") String userId, @RequestParam("sort")String sort);

    /**
     *
     * @Author: zhs
     * @Description: 流程id查询流程节点
     * @Date: 2021/8/25 9:45 上午
     * @Version:1.0
     */
    @GetMapping(path = "acti/actManager/getActiveTaskByProcessInstanceId")
    JSONObject getActiveTaskByProcessInstanceId(@RequestParam("processInstanceId") String processInstanceId);

    /**
     *
     * @Author: zhs
     * @Description: 人员id查询已处理流量
     * @Date: 2021/8/25 9:45 上午
     * @Version:1.0
     */
    @GetMapping(path = "acti/actManager/getJobHistory")
    JSONObject getJobHistory(@RequestParam("userId") String userId,@RequestParam("pageSize")Integer pageSize,
                             @RequestParam("pageNum") Integer pageNum);

    /**
     *  删除流程
     * @param processInstanceId
     * @return
     */
    @GetMapping(path = "acti/actManager/getVariableByProcessInstanceId")
    JSONObject getVariableByProcessInstanceId(@RequestParam("processInstanceId") String processInstanceId,
                                              @RequestParam("key") String key);

    /**
     *  获取流程变量
     * @param processInstanceId
     * @return
     */
    @GetMapping(path = "acti/actManager/delInstance")
    JSONObject getDelInstance(@RequestParam("processInstanceId") String processInstanceId);
}
