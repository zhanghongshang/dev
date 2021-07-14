package com.nari.slsd.msrv.waterdiversion.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @description:
 * @Author xzk
 * @Date 2020/4/23 23:07
 * @Version 1.0
 **/
@Configuration
public class FilterWebMvcConfigurer implements WebMvcConfigurer {
    @Autowired
    private HandlerInterceptor addResponseResultAttrInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(addResponseResultAttrInterceptor);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //调整HttpMessageConverter中Converter顺序
        converters.add(1,new MappingJackson2HttpMessageConverter ());
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        //默认格式为json
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }
}
