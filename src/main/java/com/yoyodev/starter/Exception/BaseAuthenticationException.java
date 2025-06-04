package com.yoyodev.starter.Exception;


import com.yoyodev.starter.Common.Enumerate.ErrorCode;

public class BaseAuthenticationException extends BaseException {
    public BaseAuthenticationException(String message) {
        super(message);
    }

    public BaseAuthenticationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
