package com.example.carebridge.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 채팅 메시지 정보를 전송하기 위한 DTO 클래스
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    
    /**
     * 메시지의 고유 식별자
     */
    @NotNull(message = "메시지 ID는 필수입니다")
    private Integer messageId;

    /**
     * 환자의 고유 식별자
     */
    private Integer patientId;

    /**
     * 의료진의 고유 식별자
     */
    private Integer medicalStaffId;

    /**
     * 메시지 내용
     * 최대 1000자까지 입력 가능합니다
     */
    @NotNull(message = "메시지 내용은 필수입니다")
    @Size(max = 1000, message = "메시지는 1000자를 초과할 수 없습니다")
    private String messageContent;

    /**
     * 메시지 발송 시간
     * ISO-8601 형식의 날짜/시간 문자열
     */
    @NotNull(message = "발송 시간은 필수입니다")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private String timestamp;

    /**
     * 메시지 읽음 여부
     */
    private Boolean readStatus;

    /**
     * 채팅방의 고유 식별자
     */
    @NotNull(message = "채팅방 ID는 필수입니다")
    private String chatRoomId;

    /**
     * 메시지 발신자의 고유 식별자
     */
    @NotNull(message = "발신자 ID는 필수입니다")
    private Integer senderId;

    /**
     * 발신자가 환자인지 여부
     * true: 환자가 보낸 메시지
     * false: 의료진이 보낸 메시지
     */
    @NotNull(message = "발신자 구분은 필수입니다")
    private Boolean isPatient;

    /**
     * 메시지 타입
     * 예: TEXT, IMAGE, FILE 등
     */
    @NotNull(message = "메시지 타입은 필수입니다")
    private String type;

    /**
     * 병원의 고유 식별자
     */
    @NotNull(message = "병원 ID는 필수입니다")
    private Integer hospitalId;

    /**
     * 메시지 카테고리
     * 예: 일반 문의, 응급 문의, 진료 관련 등
     */
    private String category;
}