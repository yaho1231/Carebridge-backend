package com.example.carebridge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * MessageSummaryDto 는 메시지 요약 정보를 담고 있는 데이터 전송 객체입니다.
 */
@Data
@Setter
@Getter
@AllArgsConstructor
public class MessageSummaryDto {
    /**
     * 발신자 이름
     */
    private String userName; // 발신자 이름

    /**
     * 대화 ID
     */
    private String conversationId; // 대화 ID

    /**
     * 미리보기 메시지
     */
    private String previewMessage; // 미리보기 메시지

    /**
     * 마지막 메시지 시간
     */
    private LocalDateTime lastMessageTime; // 마지막 메시지 시간

    /**
     * 읽음 상태
     */
    private Boolean isRead; // 읽음 상태
}