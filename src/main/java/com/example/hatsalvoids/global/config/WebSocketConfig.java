package com.example.hatsalvoids.global.config;

import com.example.hatsalvoids.building.BuildingService;
import com.example.hatsalvoids.global.websocket.EchoWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    private final BuildingService buildingService;

    @Bean
    public EchoWebSocketHandler echoWebSocketHandler() {
        return new EchoWebSocketHandler(buildingService);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(echoWebSocketHandler(), "/ws/buildingSummary")
                .setAllowedOrigins(allowedOrigins) // CORS 허용
                .withSockJS(); // 필요 시 폴백
    }
}
