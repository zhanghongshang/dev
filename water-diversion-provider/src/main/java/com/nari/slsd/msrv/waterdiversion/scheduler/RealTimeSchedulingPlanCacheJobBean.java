package com.nari.slsd.msrv.waterdiversion.scheduler;

import cn.hutool.core.date.SystemClock;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrRealTimeSchedulingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
* @title
* @description 实时调度计划缓存job
* @author bigb
* @updateTime 2021/8/20 11:50
* @throws
*/
@Component
@Slf4j
public class RealTimeSchedulingPlanCacheJobBean {
    @Autowired
    private IWrRealTimeSchedulingService wrRealTimeSchedulingService;

    /**
     *
     */
    @RefreshScope
    @Scheduled(cron = "${scheduler.realtime.plan.crons}")
    public void realTimeSchedulingAndCaching(){
        long startTime = SystemClock.now();
        log.info("<<<实时调度计划缓存开始执行>>>", startTime);
        wrRealTimeSchedulingService.realTimeSchedulingAndCaching();
        log.info("<<<实时调度计划缓存执行完成>>>，执行耗时： {}", (SystemClock.now() - startTime));
    }
}
