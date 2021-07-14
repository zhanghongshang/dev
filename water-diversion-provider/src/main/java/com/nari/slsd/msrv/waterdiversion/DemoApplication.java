package com.nari.slsd.msrv.waterdiversion;

import com.nari.slsd.msrv.waterdiversion.commons.EnhanceJpaRepository;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * @ClassName: ProjectApplication
 * @Description:  springboot核心类
 * @Author: sk
 * @Date: 2020/4/13 15:44
 * @Version: 1.0
 * @Remark:
 **/
@EnableSwagger2
@EnableTransactionManagement(proxyTargetClass = true)
@EnableConfigurationProperties
@EnableFeignClients(basePackages = {"hd.msrv"})//启动服务发现
@EnableDiscoveryClient//服务注册
@SpringBootApplication//核心注解
@MapperScan("com.nari.slsd.msrv.waterdiversion.mapper")
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(DemoApplication.class);
        springApplication.run(args);
    }

}
