package com.example.hatsalvoids.global.websocket;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebSocketErrorMessage {
    private String code;      // ex) BAD_REQUEST, VALIDATION_ERROR, INTERNAL_ERROR
    private String message;   // 사람이 읽을 수 있는 에러 설명
    private Object details;   // 필드 에러 등 세부 정보(선택)

    private WebSocketErrorMessage(String code, String message, Object details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }

    public static WebSocketErrorMessage of(String code, String message, Object details) {
        return new WebSocketErrorMessage(code, message, details);
    }
}