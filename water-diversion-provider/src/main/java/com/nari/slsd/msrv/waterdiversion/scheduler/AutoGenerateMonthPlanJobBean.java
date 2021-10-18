package com.nari.slsd.msrv.waterdiversion.scheduler;

import cn.hutool.core.date.SystemClock;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrPlanGenerateMonthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @title
 * @description 系统自动生成月计划
 * @author bigb
 * @updateTime 2021/9/14 13:45
 * @throws
 */
@Component
@Slf4j
public class AutoGenerateMonthPlanJobBean {
    @Autowired
    private IWrPlanGenerateMonthService wrPlanGenerateMonthService;

    @RefreshScope
    @Scheduled(cron = "${scheduler.autogenerate.month.plan.crons}")
    public void autoGenerateMonthPlan(){
        long startTime = SystemClock.now();
        log.info("<<<生成月计划开始执行>>>", startTime);
        wrPlanGenerateMonthService.autoGenerateMonthPlan();
        log.info("<<<生成月计划执行完成>>>，执行耗时： {}", (SystemClock.now() - startTime));
    }
}
