package com.example.carebridge.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * ChatMessageDto��� 채팅 메시지의 정보를 담고 있는 데이터 전송 객체입니다.
 */
@Data
@Getter
@Setter
public class ChatMessageDto {
    private Integer messageId; // 메시지 고유 ID

    private Integer patientId; // 환자 ID

    private Integer medicalStaffId; // 의료진 ID

    private String messageContent; // 메시지 내용

    private LocalDateTime timestamp; // 메시지 발송 시간

    private Boolean readStatus; // 메시지 읽음 여부

    private String chatRoomId; // 채팅방 ID

    private Integer sender_id; // 발신자 ID

    private Boolean isPatient; // 환자 여부
}