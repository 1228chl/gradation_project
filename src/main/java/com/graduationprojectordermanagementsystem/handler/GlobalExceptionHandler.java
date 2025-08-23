package com.graduationprojectordermanagementsystem.handler;

import com.graduationprojectordermanagementsystem.exception.BaseException;
import com.graduationprojectordermanagementsystem.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;




/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 捕获业务异常
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException e) {
        log.error("异常信息:{}", e.getMessage());
        return Result.error(e.getMessage());
    }

    /**
     * 处理所有未捕获的异常（兜底）
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleUnexpectedException(Exception e) {
        log.error("系统异常: ", e);
        return Result.error("系统内部错误，请联系管理员");
    }

}
