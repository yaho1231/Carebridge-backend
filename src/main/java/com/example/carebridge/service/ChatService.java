package com.example.carebridge.service;

import com.example.carebridge.dto.ChatMessageDto;
import com.example.carebridge.entity.Conversation;
import com.example.carebridge.repository.ConversationRepository;
import com.example.carebridge.controller.WebsocketController;
import com.example.carebridge.controller.CallbellController;
import org.springframework.web.socket.WebSocketSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final WebsocketController websocketController;
    private final ConversationRepository conversationRepository;
    private final RestTemplate restTemplate;

    /**
     * 환자의 요청을 처리합니다.
     *
     * @param chatMessageDto 환자의 요청 메시지
     */
    public void processPatientRequest(ChatMessageDto chatMessageDto) {
        // 외부 API를 사용하여 요청 유형 확인
        String apiUrl = "http://external-api.com/checkRequestType";
        String requestType = restTemplate.postForObject(apiUrl, chatMessageDto, String.class);

        if ("INFO_REQUEST".equals(requestType)) {
            // 정보 요청일 경우, API가 반환한 메시지를 환자에게 반환
            String responseMessage = getInformationFromApi(chatMessageDto);
            chatMessageDto.setMessage(responseMessage);
    
            // 환자의 세션 ID를 사용하여 메시지 전송
            Conversation conversation = conversationRepository.findById(chatMessageDto.getChatRoomId()).orElseThrow();
            WebSocketSession webSocketSession = websocketController.chatRoomSessions.get(conversation.getPatient().getChatRoomId());
            websocketController.sendMessageToSession(webSocketSession,chatMessageDto);
        } else if ("CONVERSATION_REQUEST".equals(requestType)) {
            // 대화 요청일 경우, 의료진에게 메시지 전달
            Conversation conversation = conversationRepository.findById(chatMessageDto.getChatRoomId()).orElseThrow();
            websocketController.sendMessageToChatRoom(chatMessageDto, websocketController.getSessionsForMedicalStaff(conversation.getMedicalStaff().getId()));
        }
    }

    /**
     * 외부 API를 통해 정보를 가져옵니다.
     *
     * @param chatMessageDto 요청 메시지
     * @return API로부터 받은 정보 메시지
     */
    private String getInformationFromApi(ChatMessageDto chatMessageDto) {
        // 외부 API 호출 로직 구현
        String apiUrl = "http://external-api.com/getInformation";
        return restTemplate.postForObject(apiUrl, chatMessageDto, String.class);
    }
} 



/*
 * 1. 환자와 의료진 로그인 시 세션 생성 및 환자의 경우 채팅방 생성
 * 2. 환자가 요청을 함 -> 
 *   정보 요청 시 - API 호출 후 답변 반환
 *   대화 요청 시 - 환자의 소속 의료진 세션과 연결 환자의 메세지 의료진에게 전달
 * 3. 환자화면에서 볼 대화 목록 - messages 테이블에서 chatRoomId로 조회 후 List<Message> 반환
 *
 */