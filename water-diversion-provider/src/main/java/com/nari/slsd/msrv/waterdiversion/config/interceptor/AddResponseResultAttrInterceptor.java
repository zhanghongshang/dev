package com.nari.slsd.msrv.waterdiversion.config.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import com.nari.slsd.msrv.waterdiversion.config.annotations.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @description: 在请求中添加ResponseResult属性的拦截器
 * @Author xzk
 * @Date 2020/4/23 23:09
 * @Version 1.0
 **/
@Component
public class AddResponseResultAttrInterceptor implements HandlerInterceptor {
    public final static String RESPONSE_RESULT_KEY = "RESPONSE_RESULT_KEY";



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            final HandlerMethod handlerMethod = (HandlerMethod) handler;
            Class<?> clazz = handlerMethod.getBeanType();
            Method method = handlerMethod.getMethod();
            if(clazz.isAnnotationPresent(ResponseResult.class)){
                request.setAttribute(RESPONSE_RESULT_KEY,clazz.getAnnotation(ResponseResult.class));
            }else if(method.isAnnotationPresent(ResponseResult.class)){
                request.setAttribute(RESPONSE_RESULT_KEY,method.getAnnotation(ResponseResult.class));
            }
        }
        return true;
    }
}
