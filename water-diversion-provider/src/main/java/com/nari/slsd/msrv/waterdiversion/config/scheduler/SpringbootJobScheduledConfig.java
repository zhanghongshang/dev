package com.nari.slsd.msrv.waterdiversion.config.scheduler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @title
 * @description 定时任务配置
 * @author bigb
 * @updateTime 2021/8/21 9:46
 * @throws
 */
@Configuration
public class SpringbootJobScheduledConfig {
	@Bean
	public TaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler scheduling = new ThreadPoolTaskScheduler();
		scheduling.setPoolSize(8);
		scheduling.setThreadNamePrefix("spring-boot-schedule-job-");
		scheduling.initialize();
		return scheduling;
	}
}