package com.example.hatsalvoids.global.websocket;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebSocketResponseMessage {
    private String type;        // ex) ack, message, error
    private String requestId;   // 요청과 매칭
    private Object payload;     // 응답 데이터

    private WebSocketResponseMessage(String type, String requestId, Object payload) {
        this.type = type;
        this.requestId = requestId;
        this.payload = payload;
    }

    public static WebSocketResponseMessage of(String type, String requestId, Object payload) {
        return new WebSocketResponseMessage(type, requestId, payload);
    }
}
