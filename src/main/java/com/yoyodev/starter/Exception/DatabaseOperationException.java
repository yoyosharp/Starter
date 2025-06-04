package com.yoyodev.starter.Exception;

import com.yoyodev.starter.Common.Enumerate.ErrorCode;

public class DatabaseOperationException extends BaseException {
    public DatabaseOperationException() {
        super(ErrorCode.PERSISTENCE_ERROR);
    }
}
