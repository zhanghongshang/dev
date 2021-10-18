package com.nari.slsd.msrv.waterdiversion.config.db;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.github.jeffreyning.mybatisplus.base.MppSqlInjector;
import com.github.jeffreyning.mybatisplus.handler.DataAutoFill;
import com.github.jeffreyning.mybatisplus.handler.MppKeyGenerator;
import com.nari.slsd.msrv.waterdiversion.config.handler.MyMetaObjectHandler;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * @Program: water-diversion
 * @Description: oracle数据源测试
 * @Author: reset kalar
 * @Date: 2021-08-27 14:30
 **/
@EnableTransactionManagement
@Configuration
@MapperScan(value = "com.nari.slsd.msrv.waterdiversion.mapper.fourth", sqlSessionFactoryRef = "wmSqlSessionFactory")
public class OracleWMDataSourceConfig {

    @Bean(name = "oracle-wm")
    @ConfigurationProperties(prefix = "spring.datasource.druid.oracle-wm")
    public DataSource db() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean("wmSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("oracle-wm") DataSource dataSource, MybatisPlusProperties mybatisPlusProperties, MppSqlInjector mppSqlInjector, MppKeyGenerator mppKeyGenerator, DataAutoFill dataAutoFill) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/fourth/*.xml"));
        /**
         * 分页插件
         */
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.ORACLE));
        sqlSessionFactory.setPlugins(interceptor);
        /**
         * MPP配置注入
         */
        GlobalConfig globalConfig=mybatisPlusProperties.getGlobalConfig();
        globalConfig.setSqlInjector(mppSqlInjector);
        globalConfig.setMetaObjectHandler(dataAutoFill);
        globalConfig.getDbConfig().setKeyGenerator(mppKeyGenerator);
//        globalConfig.setMetaObjectHandler(new MyMetaObjectHandler());
        sqlSessionFactory.setGlobalConfig(globalConfig);
        return sqlSessionFactory.getObject();
    }

    @Bean("wmSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("wmSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}