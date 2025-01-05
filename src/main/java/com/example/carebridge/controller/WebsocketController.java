package com.example.carebridge.controller;

import com.example.carebridge.dto.ChatMessageDto;
import com.example.carebridge.entity.ChatRoom;
import com.example.carebridge.entity.Patient;
import com.example.carebridge.repository.ChatRoomRepository;
import com.example.carebridge.repository.PatientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
/**
 * WebsocketController 클래스는 WebSocket을 통해 채팅 메시지를 처리하는 핸들러입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebsocketController extends TextWebSocketHandler {
    private final ObjectMapper mapper;
    private final ChatRoomRepository chatRoomRepository;
    private final PatientRepository patientRepository;
    /**
     * 채팅방 ID를 키로 하고, 환자와 의료진의 WebSocketSession 배열을 값으로 하는 Map
     */
    public final Map<Integer, WebSocketSession[]> chatRoomSessions = new HashMap<>();

    /**
     * 새로운 WebSocket 연결이 설정되었을 때 호출됩니다.
     *
     * @param session 새로 연결된 WebSocket 세션
     */
    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        log.info("{} 연결됨", session.getId());
        // 세션이 연결될 때 채팅방 ID를 가져와서 Map에 저장
        Integer chatRoomId = getChatRoomIdFromSession(session);
        WebSocketSession[] sessions = chatRoomSessions.computeIfAbsent(chatRoomId, k -> new WebSocketSession[2]);
        if (isPatientSession(session)) {
            sessions[0] = session; // 환자 세션
        } else {
            sessions[1] = session; // 의료진 세션
        }
    }

    /**
     * 텍스트 메시지를 수신했을 때 호출됩니다.
     *
     * @param session 메시지를 보낸 WebSocket 세션
     * @param message 수신한 텍스트 메시지
     * @throws Exception 예외가 발생할 경우
     */
    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload {}", payload);

        // 페이로드를 ChatMessageDto로 변환
        ChatMessageDto chatMessageDto = mapper.readValue(payload, ChatMessageDto.class);
        log.info("session {}", chatMessageDto.toString());

        // 채팅방 ID를 사용하여 해당 채팅방의 모든 세션에 메시지를 전송
        Integer chatRoomId = getChatRoomIdFromSession(session);
        WebSocketSession[] sessions = chatRoomSessions.get(chatRoomId);
        if (sessions != null) {
            for (WebSocketSession sess : sessions) {
                if (sess != null && sess.isOpen()) {
                    sendMessage(sess, chatMessageDto);
                }
            }
        }
    }

    /**
     * WebSocket 연결이 종료되었을 때 호출됩니다.
     *
     * @param session 종료된 WebSocket 세션
     * @param status  연결 종료 상태
     * @throws Exception 예외가 발생할 경우
     */
    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        log.info("{} 연결 끊김", session.getId());
        // 세션이 닫힐 때 채팅방 ID를 가져와서 Map에서 제거
        Integer chatRoomId = getChatRoomIdFromSession(session);
        WebSocketSession[] sessions = chatRoomSessions.get(chatRoomId);
        if (sessions != null) {
            if (isPatientSession(session)) {
                sessions[0] = null; // 환자 세션 제거
            } else {
                sessions[1] = null; // 의료진 세션 제거
            }
            if (sessions[0] == null && sessions[1] == null) {
                chatRoomSessions.remove(chatRoomId);
            }
        }
    }

    /**
     * WebSocket 세션에 메시지를 전송합니다.
     *
     * @param session 메시지를 보낼 WebSocket 세션
     * @param message 전송할 메시지
     * @param <T> 메시지의 타입
     */
    public <T> void sendMessage(WebSocketSession session, T message) {
        try {
            if (session != null && session.isOpen()) {
                session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Integer getChatRoomIdFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery();
        Integer patientId = Integer.parseInt(query.split("=")[1]); // 간단한 예시

        Optional<Patient> optionalPatient = patientRepository.findByPatientId(patientId);
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            // 환자 정보를 사용하여 Conversation을 찾습니다.
            Conversation conversation = conversationRepository.findByPatient(patientId);
            if (conversation == null) {
                // 환자 ID에 대한 레코드가 없으면 새로 추가
                conversation = new Conversation();
                conversation.setPatient(patient);
                conversationRepository.save(conversation);
            }
            // 세션 ID 반환
            return conversation.getConversation_id();
        } else {
            // 환자가 존재하지 않을 경우의 처리
            log.warn("환자 ID {}에 대한 레코드가 없습니다.", patientId);
            return null; // 또는 예외를 던지거나 다른 적절한 처리를 수행
        }
    }

    private boolean isPatientSession(WebSocketSession session) {
        // 세션이 환자 세션인지 여부를 판단하는 로직을 구현
        // 예: 세션의 URI 또는 쿼리 파라미터를 검사하여 환자 세션인지 확인
        return session.getUri().getPath().contains("patient");
    }
}