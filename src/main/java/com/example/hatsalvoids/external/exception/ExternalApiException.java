package com.example.hatsalvoids.external.exception;

import com.example.hatsalvoids.global.error.core.BaseException;

public class ExternalApiException extends BaseException {
    public ExternalApiException(ExternalApiErrorCode errorCode) {
        super(errorCode);
    }
    public ExternalApiException(ExternalApiErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
} 