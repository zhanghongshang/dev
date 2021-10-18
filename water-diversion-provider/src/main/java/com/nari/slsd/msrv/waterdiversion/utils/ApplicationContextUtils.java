package com.nari.slsd.msrv.waterdiversion.utils;

import org.springframework.context.ApplicationContext;

/**
 * @title
 * @description 获取spring boot应用上下文
 * @author bigb
 * @updateTime 2021/8/31 22:57
 * @throws
 */
public class ApplicationContextUtils {

    private static ApplicationContext context;

    public static void setContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    public static <T> T getBean(Class<T> t) {
        return context.getBean(t);
    }
}