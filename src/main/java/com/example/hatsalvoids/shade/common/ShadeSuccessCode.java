package com.example.hatsalvoids.shade.common;

import com.example.hatsalvoids.global.success.SuccessCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ShadeSuccessCode implements SuccessCode {
    SHADE_FETCHED(HttpStatus.OK, "그늘 정보 불러오기에 성공하였습니다."),

    ;

    private final HttpStatus status;
    private final String message;

    @Override
    public HttpStatus getStatus() { return status; }
    @Override
    public String getMessage() { return message; }
} 