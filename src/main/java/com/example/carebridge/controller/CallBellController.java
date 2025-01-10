package com.example.carebridge.controller;

import com.example.carebridge.dto.ChatMessageDto;
import com.example.carebridge.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

/**
 * CallBellController 는 클라이언트로부터 수신된 채팅 메시지를 처리하고
 * 해당 채팅방의 구독자들에게 메시지를 전송하는 역할을 합니다.
 */
@RequiredArgsConstructor
@Controller
public class CallBellController {
    private static final Logger logger = LoggerFactory.getLogger(CallBellController.class);
    private final SimpMessageSendingOperations messagingTemplate;
    private final MessageService messageService;

    /**
     * 클라이언트로부터 채팅 메시지를 수신하여 처리하는 메서드입니다.
     *
     * @param message 수신된 채팅 메시지 객체
     */
    @MessageMapping("api/chat/message")
    public void message(ChatMessageDto message) {
        // 수신된 메시지를 로그에 기록합니다.
        logger.info("Received message: {}", message);

        // 메시지를 데이터베이스에 저장합니다.
        messageService.saveMessage(message);

        // 수신된 메시지를 해당 채팅방의 구독자들에게 전송합니다.
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }
}