package com.example.carebridge.controller;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 연결을 관리하고 메시지를 처리하는 클래스입니다.
 */
@Component
public class WebSocketController extends TextWebSocketHandler {

    // 클라이언트 세션을 저장하는 ConcurrentHashMap
    private static final ConcurrentHashMap<String, WebSocketSession> CLIENTS = new ConcurrentHashMap<>();

    /**
     * 새로운 WebSocket 연결이 설정되었을 때 호출됩니다.
     *
     * @param session 새로운 WebSocket 세션
     * @throws Exception 예외가 발생할 경우
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        CLIENTS.put(session.getId(), session);
    }

    /**
     * WebSocket 연결이 닫혔을 때 호출됩니다.
     *
     * @param session 닫힌 WebSocket 세션
     * @param status  연결이 닫힌 상태
     * @throws Exception 예외가 발생할 경우
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        CLIENTS.remove(session.getId());
    }

    /**
     * 텍스트 메시지를 수신했을 때 호출됩니다.
     *
     * @param session 메시지를 보낸 WebSocket 세션
     * @param message 수신한 텍스트 메시지
     * @throws Exception 예외가 발생할 경우
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String id = session.getId();  // 메시지를 보낸 세션의 ID
        CLIENTS.entrySet().forEach(entry -> {
            if (!entry.getKey().equals(id)) {  // 같은 ID가 아니면 메시지를 전달합니다.
                try {
                    entry.getValue().sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}