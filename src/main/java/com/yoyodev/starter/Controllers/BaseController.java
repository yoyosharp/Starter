package com.yoyodev.starter.Controllers;

import com.yoyodev.starter.Common.Enumeration.ErrorCode;
import com.yoyodev.starter.Exception.BaseAuthenticationException;
import com.yoyodev.starter.Exception.BaseException;
import com.yoyodev.starter.Model.DTO.UserPrincipal;
import com.yoyodev.starter.Model.Response.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Locale;

import static com.yoyodev.starter.Common.Constants.Constants.EMPTY_STRING;

public class BaseController {

    @Autowired
    protected MessageSource messageSource;

    protected String getMessage(String code) {
        return messageSource.getMessage(code, null, getCurrentLocale());
    }

    protected String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, getCurrentLocale());
    }

    protected Locale getCurrentLocale() {
        return LocaleContextHolder.getLocale();
    }

    public <T> ResponseEntity<ResponseWrapper<T>> getSuccess(T obj) {
        ResponseWrapper<T> response = new ResponseWrapper<>(false, ErrorCode.SUCCESS, getMessage("common.success"), obj);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ResponseWrapper<String>> getSuccess() {
        ResponseWrapper<String> response = new ResponseWrapper<>(false, ErrorCode.SUCCESS, getMessage("common.success"), EMPTY_STRING);
        return ResponseEntity.ok(response);
    }

    // failed with message
    public ResponseEntity<ResponseWrapper<String>> getFailed(String message) {
        ResponseWrapper<String> response = new ResponseWrapper<>(true, ErrorCode.DEFAULT, message, EMPTY_STRING);
        return ResponseEntity.ok(response);
    }

    // failed with known error and message
    public ResponseEntity<ResponseWrapper<String>> getFailed(ErrorCode errorCode, String message) {
        ResponseWrapper<String> response = new ResponseWrapper<>(true, errorCode, message, EMPTY_STRING);
        return ResponseEntity.ok(response);
    }

    // failed with a multiple causes
    public ResponseEntity<ResponseWrapper<?>> getFailed(ErrorCode errorCode, String... causes) {
        ResponseWrapper<?> response = new ResponseWrapper<>(true, errorCode, getMessage("common.failed"), causes);
        return ResponseEntity.ok(response);
    }

    // failed from an exception
    public ResponseEntity<ResponseWrapper<String>> getFailed(BaseException e) {
        return getFailed(e.getErrorCode(), e.getMessage());
    }

    public UserPrincipal getAuthentication() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrincipal == null) {
            throw new BaseAuthenticationException(getMessage("auth.user.not.found"));
        }
        return userPrincipal;
    }
}
