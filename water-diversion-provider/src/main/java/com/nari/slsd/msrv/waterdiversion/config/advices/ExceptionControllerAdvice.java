package com.nari.slsd.msrv.waterdiversion.config.advices;

import com.nari.slsd.msrv.common.exception.TransactionException;
import com.nari.slsd.msrv.common.ext.enums.CodeEnum;
import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.Objects;

/**
 * Controller层异常捕获
 * @Author xzk
 * @Date 2020/3/28 14:21
 * @Version 1.0
 **/
@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice{

    /**
     * 全局异常捕捉处理
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public ResultModel errorHandler(Exception e) {
        log.error("处理异常：{}",e.getMessage());
        e.printStackTrace();
        return ResultModelUtils.getFailInstanceExt (CodeEnum.ERROR, e.getMessage());
    }

    /**
     * 拦截捕捉自定义异常 ControllerRequestException.class
     * @param e
     * @return
     */
    @ExceptionHandler(value = TransactionException.class)
    public ResultModel myErrorHandler(TransactionException e) {
        log.error("自定义异常：{}",e.getMessage());
        return ResultModelUtils.getFailInstanceExt(CodeEnum.ERROR, e.getMessage());
    }

    /**
     * 请求参数校验未通过异常
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultModel handleValidException(MethodArgumentNotValidException e){
        //日志记录错误信息
        log.error("参数校验未通过异常{}",Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
        return ResultModelUtils.getFailInstanceExt(CodeEnum.ERROR, Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
    }
    /**
     * 权限不合法异常
     * @param e
     * @return
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResultModel accessDeniedException(AccessDeniedException e){
        //日志记录错误信息
        log.error("权限不合法异常{}",e.getMessage());
        //将错误信息返回给前台
        return ResultModelUtils.getFailInstanceExt(CodeEnum.ERROR, "权限不合法异常");

    }
}
