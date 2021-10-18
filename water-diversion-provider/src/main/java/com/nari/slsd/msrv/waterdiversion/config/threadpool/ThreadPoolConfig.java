package com.nari.slsd.msrv.waterdiversion.config.threadpool;

import cn.hutool.core.thread.ThreadUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @title 线程池配置
 * @description
 * @author bigb
 * @updateTime 2021/9/12 10:58
 * @throws
 */
@Configuration
public class ThreadPoolConfig {

    /**
     * 年月计划填报
     * @return
     */
    @Bean(value = "planFillThreadPool")
    public ThreadPoolExecutor planFillThreadPool(){
        //thread factory
        //服务器配置为8核
        ThreadFactory namedThreadFactory = ThreadUtil.newNamedThreadFactory("plan-fill-thread-",false);
        return new ThreadPoolExecutor(
                8,
                8,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(200),
                namedThreadFactory,
                //拒绝策略使用主线程去处理
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

}