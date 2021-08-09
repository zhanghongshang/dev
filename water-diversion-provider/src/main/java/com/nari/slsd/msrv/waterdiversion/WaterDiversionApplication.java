package com.nari.slsd.msrv.waterdiversion;

import com.nari.slsd.msrv.waterdiversion.init.RedisListenerInit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


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
//@EnableFeignClients(basePackages = {"hd.msrv"})//启动服务发现
//@EnableDiscoveryClient//服务注册
@SpringBootApplication//核心注解
//@ComponentScan(basePackages = {"com.nari"})
public class WaterDiversionApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(WaterDiversionApplication.class);
        springApplication.addListeners(new RedisListenerInit());
        springApplication.run(args);
    }

}
