package com.nari.slsd.msrv.waterdiversion.config.db;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.nari.slsd.msrv.waterdiversion.config.handler.MyMetaObjectHandler;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * dm-promng数据源配置
 * @author resetkalar
 */
@EnableTransactionManagement
@Configuration
@MapperScan(value = "com.nari.slsd.msrv.waterdiversion.mapper.primary", sqlSessionFactoryRef = "promngSqlSessionFactory")
public class PromngDataSourceConfig {

    @Primary
    @Bean(name = "dm-promng")
    @ConfigurationProperties(prefix = "spring.datasource.druid.dm-promng")
    public DataSource db() {
        return DruidDataSourceBuilder.create().build();
    }

    @Primary
    @Bean("promngSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dm-promng") DataSource dataSource,MybatisPlusProperties mybatisPlusProperties) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/primary/*.xml"));
        /**
         * 设置日志打印
         */
        sqlSessionFactory.setConfiguration(mybatisPlusProperties.getConfiguration());
        /**
         * 分页插件
         */
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.DM));
        sqlSessionFactory.setPlugins(interceptor);
        /**
         * 自动填充处理
         */
        GlobalConfig globalConfig=new GlobalConfig();
        globalConfig.setMetaObjectHandler(new MyMetaObjectHandler());
        sqlSessionFactory.setGlobalConfig(globalConfig);

        return sqlSessionFactory.getObject();
    }

    @Primary
    @Bean("promngSqlSessionTemplate")
    public SqlSessionTemplate dmSqlSessionTemplate(@Qualifier("promngSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
