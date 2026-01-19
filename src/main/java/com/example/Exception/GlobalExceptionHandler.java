package com.example.Exception;

import com.example.Utils.Result;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        e.printStackTrace();
        //如果有异常就返回异常信息，反之就返回操作失败
        return Result.error(StringUtils.hasLength(e.getMessage())?e.getMessage():"操作失败");
    }
}
