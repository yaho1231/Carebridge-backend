package com.example.carebridge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * MessageSummaryDto 는 메시지 요약 정보를 담고 있는 데이터 전송 객체입니다.
 */
@Data
@AllArgsConstructor
public class MessageSummaryDto {
    /**
     * 발신자 이름
     */
    private String name;

    /**
     * 마지막 메시지 내용
     */
    private String lastMessage;

    /**
     * 메시지 타임스탬프
     */
    private String timestamp;
}