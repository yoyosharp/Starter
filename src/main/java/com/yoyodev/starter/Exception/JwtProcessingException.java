package com.yoyodev.starter.Exception;

import com.yoyodev.starter.Common.Enumerate.ErrorCode;

public class JwtProcessingException extends BaseException {
    public JwtProcessingException(String message) {
        super(ErrorCode.AUTH_JWT_PROCESSING_ERROR, message);
    }
}
