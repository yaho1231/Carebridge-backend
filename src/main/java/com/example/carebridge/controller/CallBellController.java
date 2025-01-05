package com.example.carebridge.controller;

import com.example.carebridge.dto.ChatMessage;
import com.example.carebridge.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class CallBellController {
    private static final Logger logger = LoggerFactory.getLogger(CallBellController.class);
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatRoomRepository chatRoomRepository;

    @MessageMapping("chat/message")
    public void message(ChatMessage message){
        logger.info("Received message: {}", message);
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }
}