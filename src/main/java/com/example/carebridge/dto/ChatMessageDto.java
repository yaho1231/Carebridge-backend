package com.example.carebridge.dto;

import lombok.*;

/**
 * ChatMessageDto 클래스는 채팅 메시지의 데이터 전송 객체입니다.
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {

    /**
     * MessageType 열거형은 메시지의 타입을 나타냅니다.
     * ENTER - 사용자가 채팅방에 입장할 때
     * TALK - 사용자가 채팅방에서 대화할 때
     */
    public enum MessageType {
        ENTER, TALK
    }

    /**
     * 메시지의 타입입니다.
     */
    private MessageType messageType;

    /**
     * 메시지가 속한 채팅방의 ID입니다.
     */
    private Integer chatRoomId;

    /**
     * 메시지를 보낸 사용자의 ID입니다.
     */
    private Long senderID;

    /**
     * 메시지의 내용입니다.
     */
    private String message;
}