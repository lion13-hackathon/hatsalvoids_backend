package com.example.hatsalvoids.shade;

import com.example.hatsalvoids.global.success.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum ShadeSuccessCode implements SuccessCode {

    SHADE_FETCHED(HttpStatus.OK, "현재 위치 반경 %sM 내의 건물 및 그늘 목록 조회 성공"),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return "[SHADE] " + message;
    }
}
