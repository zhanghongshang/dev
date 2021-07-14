package com.nari.slsd.msrv.waterdiversion.config.advices;

import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import com.nari.slsd.msrv.waterdiversion.config.interceptor.AddResponseResultAttrInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @description:controller返回的实体包装增强
 * @Author xzk
 * @Date 2020/4/23 23:38
 * @Version 1.0
 **/
@RestControllerAdvice
@Slf4j
public class ResponseResultAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest httpServletRequest = requestAttributes.getRequest();
        Object attr = httpServletRequest.getAttribute(AddResponseResultAttrInterceptor.RESPONSE_RESULT_KEY);
        return attr==null?false:true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if(o instanceof ResultModel){
            //已经打包成ResultModel则不需要再重新打包
            return o;
        }
        return ResultModelUtils.getSuccessInstanceExt ("操作成功",o);

    }
}
