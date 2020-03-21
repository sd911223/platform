package com.sysm.back.exception;

import com.sysm.back.bean.result.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 异常处理器
 */
@Slf4j
@RestControllerAdvice
public class BusinessExceptionHandler {

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(BusinessException.class)
    public RestResponse handleBusinessException(BusinessException e) {
        RestResponse restResponse = new RestResponse();
        restResponse.put("code", e.getCode());
        restResponse.put("msg", e.getMessage());

        return restResponse;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public RestResponse handlerNoFoundException(Exception e) {
        log.error(e.getMessage(), e);
        return RestResponse.error(HttpStatus.NOT_FOUND.value(), "路径不存在，请检查路径是否正确");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public RestResponse handleDuplicateKeyException(DuplicateKeyException e) {
        log.error(e.getMessage(), e);
        return RestResponse.error("数据库中已存在该记录");
    }

    @ExceptionHandler(Exception.class)
    public RestResponse handleException(Exception e) {
        log.error(e.getMessage(), e);
        return RestResponse.error();
    }
}
