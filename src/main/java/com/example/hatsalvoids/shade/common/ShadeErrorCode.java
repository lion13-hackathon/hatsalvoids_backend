package com.example.hatsalvoids.shade.common;

import com.example.hatsalvoids.global.error.core.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ShadeErrorCode implements ErrorCode {
    SOLAR_IS_UNDER(HttpStatus.BAD_REQUEST, "지금 시간대에는 그늘이 없어요."),
    POINT_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "유효한 LinearRing 좌표를 찾지 못했습니다."),
    ;

    private final HttpStatus status;
    private final String message;

    @Override
    public HttpStatus getStatus() { return status; }
    @Override
    public String getMessage() { return message; }
} 