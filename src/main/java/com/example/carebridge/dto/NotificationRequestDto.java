package com.example.carebridge.dto;

import lombok.*;

/**
 * 웹 전용 푸시 알림 요청을 위한 DTO 클래스
 * 웹 브라우저로의 푸시 알림 전송에 필요한 최소한의 정보만 포함
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDto {
    /**
     * FCM 클라이언트 토큰
     */
    private String token;

    /**
     * 알림의 제목
     */
    private String title;

    /**
     * 알림의 본문
     */
    private String body;

    /**
     * 알림 만료 시간 (초)
     */
    private String ttl;
}
