package com.yoyodev.starter.Exception;

import com.yoyodev.starter.Common.Enumerate.ErrorCode;
import org.springframework.security.core.AuthenticationException;

public class JwtVerificationException extends AuthenticationException {

    public JwtVerificationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public JwtVerificationException(String msg) {
        super(msg);
    }
}
