package com.company.encryptedtrafficclassifier.common.handlers;

import com.company.encryptedtrafficclassifier.entity.JsonResult;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SystemGlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public JsonResult handlerException(Exception e) {
        return JsonResult.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }

}