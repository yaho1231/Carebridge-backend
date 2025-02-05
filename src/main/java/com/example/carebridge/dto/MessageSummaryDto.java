package com.example.carebridge.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * MessageSummaryDto 는 메시지 요약 정보를 담고 있는 데이터 전송 객체입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageSummaryDto {
    /**
     * 발신자 이름
     */
    @NotNull
    private String userName;

    /**
     * 대화 ID
     */
    @NotNull
    private String conversationId;

    /**
     * 미리보기 메시지
     */
    private String previewMessage;

    /**
     * 마지막 메시지 시간
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private String lastMessageTime;

    /**
     * 읽음 상태
     */
    @NotNull
    private Boolean isRead;

    public MessageSummaryDto(String patientName, String chatRoomId, String messageContent, LocalDateTime timestamp, Boolean readStatus) {
        this.userName = patientName;
        this.conversationId = chatRoomId;
        this.previewMessage = messageContent;
        this.lastMessageTime = timestamp.toString();
        this.isRead = readStatus;
    }
}