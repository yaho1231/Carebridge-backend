package com.example.carebridge.config;

import com.example.carebridge.controller.WebsocketController;

import org.springframework.lang.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocketConfig는 WebSocket 핸들러를 등록하고 설정하는 클래스입니다.
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    // WebSocketController 인스턴스를 주입받습니다.
    private final WebsocketController websocketController;

    /**
     * WebSocket 핸들러를 등록합니다.
     *
     * @param registry WebSocket 핸들러를 등록할 레지스트리
     */
    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        // WebSocket 핸들러를 "/ws" 경로에 등록하고 모든 출처를 허용합니다.
        registry.addHandler(websocketController, "/ws").setAllowedOrigins("*");
    }
}