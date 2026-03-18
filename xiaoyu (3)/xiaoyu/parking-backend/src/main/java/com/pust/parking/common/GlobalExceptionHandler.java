package com.pust.parking.common;

import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<Void> handleIllegalArgument(IllegalArgumentException ex) {
        return ApiResponse.error(ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ApiResponse<Void> handleValidation(Exception ex) {
        String msg = "参数错误";
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException e = (MethodArgumentNotValidException) ex;
            if (e.getBindingResult().getFieldError() != null) {
                msg = e.getBindingResult().getFieldError().getDefaultMessage();
            }
        } else if (ex instanceof BindException) {
            BindException e = (BindException) ex;
            if (e.getBindingResult().getFieldError() != null) {
                msg = e.getBindingResult().getFieldError().getDefaultMessage();
            }
        }
        return ApiResponse.error(msg);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleOther(Exception ex) {
        // 可以在这里记录日志
        return ApiResponse.error("服务器内部错误");
    }
}
