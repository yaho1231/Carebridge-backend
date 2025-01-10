package com.example.carebridge.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ChatMessageDto {
    /**
     * 메시지 타입을 정의하는 열거형입니다.
     * ENTER: 채팅방에 입장할 때
     * TALK: 채팅 중일 때
     * EXIT: 채팅방을 나갈 때
     * MATCH: 매칭이 성사되었을 때
     * MATCH_REQUEST: 매칭 요청을 보낼 때
     */
    public enum MessageType {
        ENTER, TALK, EXIT, MATCH, MATCH_REQUEST;
    }

    /**
     * 메시지 타입 (ENTER, TALK, EXIT, MATCH, MATCH_REQUEST 중 하나)
     */
    private MessageType type;

    /**
     * 메시지가 속한 채팅방의 ID
     */
    private String roomId;

    /**
     * 메시지를 보낸 사람의 ID;
     */
    private Integer senderId;

    private Boolean isPatient;

    /**
     * 실제 메시지 내용
     */
    private String message;
}