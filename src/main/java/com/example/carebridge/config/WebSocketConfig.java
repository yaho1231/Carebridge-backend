package com.example.carebridge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 설정 클래스
 * 실시간 양방향 통신을 위한 WebSocket 관련 설정을 정의합니다.
 * 
 * [현재 개발 환경 설정 개요]
 * - STOMP 프로토콜 사용
 * - 인메모리 메시지 브로커 사용
 * - 모든 도메인에서의 접근 허용
 * 
 * [배포 시 고려사항]
 * 1. 외부 메시지 브로커(RabbitMQ, ActiveMQ 등) 도입 검토
 * 2. 허용 도메인 제한
 * 3. SSL/TLS 보안 연결 적용
 * 4. 세션 관리 및 인증 강화
 */
@Configuration
@EnableWebSocketMessageBroker  // WebSocket 메시지 브로커 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Bean(name = "customTaskScheduler")
    public TaskScheduler customTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("wss-heartbeat-");
        scheduler.initialize();
        return scheduler;
    }
    /**
     * 메시지 브로커 설정 메서드
     * 메시지 라우팅과 브로커 동작 방식을 정의합니다.
     * 
     * [현재 개발 환경 설정]
     * - 심플 브로커: 인메모리 방식으로 가벼운 테스트에 적합
     * - 구독 prefix: /sub
     * - 발행 prefix: /pub
     * 
     * [배포 시 변경 필요 사항]
     * 1. enableSimpleBroker 대신 외부 메시지 브로커 사용 검토
     *    예: config.enableStompBrokerRelay()
     *       .setRelayHost("localhost")
     *       .setRelayPort(61613)
     *       .setClientLogin("guest")
     *       .setClientPasscode("guest");
     * 
     * 2. 메시지 크기 제한 설정
     * 3. 브로커 heartbeat 설정
     * 4. 메시지 전달 보증 수준 설정
     *
     * @param config 메시지 브로커 설정을 위한 레지스트리
     */
    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        // 구독 경로 설정 (/sub/chat/room/1 형태로 구독)
        config.enableSimpleBroker("/sub")
                .setTaskScheduler(customTaskScheduler())
                .setHeartbeatValue(new long[]{4000, 4000});
        
        // 메시지 발행 경로 설정 (/pub/chat/message 형태로 발행)
        config.setApplicationDestinationPrefixes("/pub");
    }

    /**
     * WebSocket 연결 엔드포인트 설정 메서드
     * 클라이언트가 WebSocket 연결을 맺기 위한 엔드포인트를 정의합니다.
     * 
     * [현재 개발 환경 설정]
     * - 엔드포인트: /ws-stomp
     * - 모든 도메인 허용 (*)
     * 
     * [배포 시 변경 필요 사항]
     * 1. setAllowedOriginPatterns 수정:
     *    .setAllowedOriginPatterns("https://*.carebridge.com")
     * 
     * 2. SockJS 활성화 (웹소켓을 지원하지 않는 환경 대응):
     *    .withSockJS()
     *    .setClientLibraryUrl("https://cdn.example.com/sockjs-client.js")
     *    .setStreamBytesLimit(512 * 1024)
     *    .setHttpMessageCacheSize(1000)
     *    .setDisconnectDelay(30 * 1000);
     * 
     * 3. 보안 설정 추가:
     *    - SSL/TLS 적용
     *    - CORS 정책 구체화
     *    - 세션 타임아웃 설정
     * 
     * 4. 핸드셰이크 인터셉터 추가:
     *    .addInterceptors(new HttpSessionHandshakeInterceptor())
     * 
     * @param registry STOMP 엔드포인트 설정을 위한 레지스트리
     */
    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp")        // WebSocket 엔드포인트 설정
                .setAllowedOriginPatterns("*");  // 개발환경용 모든 도메인 허용
    }
}