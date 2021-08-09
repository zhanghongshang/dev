package com.nari.slsd.msrv.waterdiversion.config.db;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = -100)
@Slf4j
@Aspect
public class DataSourceAspect {
    @Pointcut("execution(* com.nari.slsd.msrv.waterdiversion.mapper.primary..*.*(..))")
    private void db1Aspect() {
    }

    @Pointcut("execution(* com.nari.slsd.msrv.waterdiversion.mapper.secondary..*.*(..))")
    private void db2Aspect() {
    }

    @Before("db1Aspect()")
    public void db1() {
        log.info("切换到dm-promng 数据源...");
        DataSourceContextHolder.setDbType(DBTypeEnum.db1);
    }

    @Before("db2Aspect()")
    public void db2() {
        log.info("切换到db2 数据源...");
        DataSourceContextHolder.setDbType(DBTypeEnum.db2);
    }
}
