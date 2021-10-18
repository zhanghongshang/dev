package com.nari.slsd.msrv.waterdiversion;

import com.github.jeffreyning.mybatisplus.conf.EnableKeyGen;
import com.github.jeffreyning.mybatisplus.conf.EnableMPP;
import com.nari.slsd.msrv.waterdiversion.init.RedisListenerInit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;


/**
 * @ClassName: ProjectApplication
 * @Description:  springboot核心类
 * @Author: sk
 * @Date: 2020/4/13 15:44
 * @Version: 1.0
 * @Remark:
 **/
//@EnableSwagger2
//@EnableTransactionManagement(proxyTargetClass = true)
//@EnableConfigurationProperties
@EnableFeignClients(basePackages = {"com.nari.slsd.permission.**","com.nari"})//启动服务发现
//@EnableDiscoveryClient//服务注册
@SpringBootApplication//核心注解
@ComponentScan(basePackages = {"com.nari.slsd.permission.**","com.nari"})
@EnableMPP//启用mpp
@EnableKeyGen//启用主键自定义主键填充功能
public class WaterDiversionApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(WaterDiversionApplication.class);
        springApplication.addListeners(new RedisListenerInit());
        springApplication.run(args);
    }
}
