package com.yoyodev.starter.Exception;

import com.yoyodev.starter.Common.Enumerate.ErrorCode;

public class InvalidStoredEnumValueException extends BaseException {
    public InvalidStoredEnumValueException() {
        super(ErrorCode.INVALID_DATABASE_VALUE);
    }

    public InvalidStoredEnumValueException(String message) {
        super(ErrorCode.INVALID_DATABASE_VALUE, message);
    }
}
