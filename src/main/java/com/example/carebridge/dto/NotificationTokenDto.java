package com.example.carebridge.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * FCM 알림 토큰 정보를 전달하기 위한 DTO 클래스
 * 사용자의 FCM 토큰 등록 및 관리에 사용됩니다.
 */
@Data
@Getter
@Setter
public class NotificationTokenDto {
    
    /**
     * FCM 토큰 값
     * Firebase Cloud Messaging에서 발급받은 디바이스 고유 토큰입니다.
     */
    private String token;

    /**
     * 사용자 ID
     * 토큰을 등록할 사용자의 고유 식별자입니다.
     */
    private Integer userId;
}