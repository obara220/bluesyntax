package com.panda.aoodds.esport.common.exception;

import com.panda.aoodds.esport.api.entity.Response;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolationException;

/**
 * 全局异常处理
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = ApiException.class)
    public Response apiException(ApiException e) {
        if (e.getErrorCode() != null) {
            return Response.failed(e.getErrorCode());
        }
        return Response.failed(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = ConstraintViolationException.class)
    public Response constraintViolationException(ConstraintViolationException e) {
        return Response.failed(e.getMessage());
    }


}
