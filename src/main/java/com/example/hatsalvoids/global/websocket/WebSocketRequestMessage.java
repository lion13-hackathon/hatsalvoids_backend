package com.example.hatsalvoids.global.websocket;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebSocketRequestMessage {
    private String type;        // ex) ack, message, error
    private String requestId;   // 요청과 매칭
    private Object payload;     // 응답 데이터

    public static WebSocketRequestMessage of(String type, String requestId, Object payload) {
        return new WebSocketRequestMessage(type, requestId, payload);
    }
}
