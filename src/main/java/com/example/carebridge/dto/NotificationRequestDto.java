package com.example.carebridge.dto;

import lombok.*;

/**
 * FCM 웹 푸시 알림 요청을 위한 DTO 클래스
 * Firebase Cloud Messaging 을 통해 웹 브라우저로 푸시 알림을 전송할 때 사용되는 데이터 구조
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDto {
    /**
     * FCM 클라이언트 토큰
     * 웹 브라우저에서 발급받은 고유한 FCM 등록 토큰으로, 특정 클라이언트를 식별하는데 사용됨
     */
    private String token;

    /**
     * 알림의 제목
     * 푸시 알림에 표시될 주요 제목 텍스트
     */
    private String title;

    /**
     * 알림의 본문
     * 푸시 알림에 표시될 상세 내용 텍스트
     */
    private String body;

    /**
     * 웹 푸시 관련 설정 정보
     * 웹 브라우저 특화된 푸시 알림 설정을 포함하는 객체
     */
    private WebPushConfigDto webPushConfig;

    /**
     * 웹 푸시 설정 정보를 담는 내부 클래스
     * TTL(Time To Live)과 알림 세부 정보를 포함
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WebPushConfigDto {
        /**
         * Time To Live (초 단위)
         * 푸시 메시지가 FCM 서버에서 유지되는 최대 시간
         * 클라이언트가 오프라인 상태일 때 메시지를 보관하는 기간을 지정
         */
        private String ttl;

        /**
         * 웹 푸시 알림 상세 정보
         * 브라우저에 표시될 알림의 구체적인 내용을 포함
         */
        private WebPushNotificationDto notification;
    }

    /**
     * 웹 푸시 알림의 실제 표시 내용을 담는 내부 클래스
     * 브라우저 알림창에 보여질 구체적인 정보 포함
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WebPushNotificationDto {
        /**
         * 브라우저 알림창에 표시될 제목
         */
        private String title;

        /**
         * 브라우저 알림창에 표시될 본문 내용
         */
        private String body;
    }
}
