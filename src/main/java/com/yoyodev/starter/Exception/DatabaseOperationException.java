package com.yoyodev.starter.Exception;

import com.yoyodev.starter.Common.Enumeration.ErrorCode;

public class DatabaseOperationException extends BaseException {
    public DatabaseOperationException() {
        super(ErrorCode.PERSISTENCE_ERROR);
    }
}
