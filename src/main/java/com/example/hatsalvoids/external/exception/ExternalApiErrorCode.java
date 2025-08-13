package com.example.hatsalvoids.external.exception;

import com.example.hatsalvoids.global.error.core.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExternalApiErrorCode implements ErrorCode {
    REQUEST_FAIL(HttpStatus.BAD_GATEWAY, "API 요청에 실패했습니다.");

    private final HttpStatus status;
    private final String message;

    ExternalApiErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
} 