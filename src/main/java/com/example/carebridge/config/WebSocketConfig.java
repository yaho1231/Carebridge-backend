package com.example.carebridge.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocketConfig 는 WebSocket 핸들러를 등록하고 설정하는 클래스입니다.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 메시지 브로커를 구성합니다.
     *
     * @param config 메시지 브로커 레지스트리
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 메모리 내 간단한 메시지 브로커를 활성화하고 "/sub" 경로를 설정합니다.
        config.enableSimpleBroker("/sub");
        // @MessageMapping 이 붙은 메서드로 전달되는 메시지의 경로 접두사를 "/pub"으로 설정합니다.
        config.setApplicationDestinationPrefixes("/pub");
    }

    /**
     * STOMP 엔드포인트를 등록합니다.
     *
     * @param registry STOMP 엔드포인트 레지스트리
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // "/ws-stomp" 엔드포인트를 등록하고 SockJS 폴백 옵션을 활성화합니다.
        registry.addEndpoint("/ws-stomp")
                // 모든 출처를 허용합니다.
                .setAllowedOriginPatterns("*");
                // SockJS 폴백 옵션을 활성화합니다.
//                .withSockJS();
    }
}