package com.nari.slsd.msrv.waterdiversion.feign.interfaces;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "studio", url = "${studio-server.url}")
public interface StudioFeignClient {

    /**
     *
     * @Author: zmh
     * @Description: 流程处理接口
     * @Date: 2021/8/25 9:45 上午
     * @Version:1.0
     */
    @PostMapping(path = "api/flow/handle", consumes = MediaType.APPLICATION_JSON_VALUE)
    JSONObject getProcessInstanceList(@RequestBody JSONObject param);

}
