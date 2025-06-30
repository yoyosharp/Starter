package com.yoyodev.starter.Controllers;

import com.yoyodev.starter.Common.Enumeration.ErrorCode;
import com.yoyodev.starter.Exception.BaseAuthenticationException;
import com.yoyodev.starter.Exception.BaseException;
import com.yoyodev.starter.Model.DTO.UserPrincipal;
import com.yoyodev.starter.Model.Response.ResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.yoyodev.starter.Common.Constants.Constants.*;

public class BaseController {
    public <T> ResponseEntity<ResponseWrapper<T>> getSuccess(T obj) {
        ResponseWrapper<T> response = new ResponseWrapper<>(false, ErrorCode.SUCCESS, SUCCESS, obj);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ResponseWrapper<String>> getSuccess() {
        ResponseWrapper<String> response = new ResponseWrapper<>(false, ErrorCode.SUCCESS, SUCCESS, EMPTY_STRING);
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
    public ResponseEntity<ResponseWrapper<Object>> getFailed(ErrorCode errorCode, String... causes) {
        ResponseWrapper<Object> response = new ResponseWrapper<>(true, errorCode, FAILED, causes);
        return ResponseEntity.ok(response);
    }

    // failed from an exception
    public ResponseEntity<ResponseWrapper<String>> getFailed(BaseException e) {
        return getFailed(e.getErrorCode(), e.getMessage());
    }

    public UserPrincipal getAuthentication() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrincipal == null) {
            throw new BaseAuthenticationException("Cannot get current user principal");
        }
        return userPrincipal;
    }
}
