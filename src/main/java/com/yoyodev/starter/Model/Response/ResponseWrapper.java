package com.yoyodev.starter.Model.Response;

import com.yoyodev.starter.Common.Enumerate.ErrorCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class ResponseWrapper<T> {
    private boolean failed;
    private String resultCode;
    private String timestamp;
    private Result results;

    public ResponseWrapper(boolean failed, ErrorCode resultCode, String message, T data) {
        this.timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS").format(LocalDateTime.now());
        this.failed = failed;
        this.resultCode = resultCode.getValue();
        this.results = new Result(message, data);
    }

    @Getter
    @Setter
    private class Result {
        private String message;
        private T data;

        public Result(String message, T data) {
            this.message = message;
            this.data = data;
        }
    }
}
