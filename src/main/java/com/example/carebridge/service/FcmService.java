package com.example.carebridge.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;
import com.example.carebridge.dto.NotificationRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Firebase Cloud Messaging(FCM) 서비스
 * 웹 푸시 알림을 전송하기 위한 서비스 클래스입니다.
 * 
 * 주요 기능:
 * 1. 단일 사용자에게 알림 전송
 * 2. 다수의 사용자에게 동시에 알림 전송
 * 
 * 사용 예시:
 * List<NotificationRequestDto> notifications = new ArrayList<>();
 * notifications.add(new NotificationRequestDto("토큰", "제목", "내용"));
 * fcmService.sendBatchNotifications(notifications);
 */
@Service
public class FcmService {
    private static final Logger logger = LoggerFactory.getLogger(FcmService.class);

    /**
     * 다수의 사용자에게 FCM 푸시 알림을 전송하는 메소드
     * 
     * @param notificationRequests 알림 전송 요청 목록
     * @throws InterruptedException 알림 전송 중 인터럽트 발생 시
     * @throws ExecutionException 알림 전송 실행 중 오류 발생 시
     * 
     * 처리 과정:
     * 1. 각 알림 요청에 대해 메시지 객체 생성
     * 2. FCM 서버로 메시지 전송
     * 3. 전송 결과 로깅
     * 
     * 주의사항:
     * - 토큰이 유효하지 않은 경우 해당 메시지만 실패하고 나머지는 계속 진행됩니다.
     * - 대량 전송 시 FCM 서버의 처리 용량을 고려해야 합니다.
     */
    public void send(List<NotificationRequestDto> notificationRequests) throws InterruptedException, ExecutionException {
        for (NotificationRequestDto request : notificationRequests) {
            try {
                Message message = createMessage(request);
                String response = sendMessage(message);
                logResponse(response, request.getToken());
            } catch (Exception e) {
                logger.error("Failed to send notification to token: {}, error: {}", 
                    request.getToken(), e.getMessage());
            }
        }
    }

    /**
     * FCM 메시지 객체를 생성하는 private 메소드
     * 
     * @param request 알림 요청 정보
     * @return 생성된 FCM 메시지 객체
     */
    private Message createMessage(NotificationRequestDto request) {
        return Message.builder()
                .setToken(request.getToken())
                .setWebpushConfig(WebpushConfig.builder()
                        .setNotification(WebpushNotification.builder()
                                .setTitle(request.getTitle())
                                .setBody(request.getBody())
                                .build())
                        .build())
                .build();
    }

    /**
     * FCM 서버로 메시지를 전송하는 private 메소드
     * 
     * @param message 전송할 메시지
     * @return 전송 응답
     */
    private String sendMessage(Message message) throws InterruptedException, ExecutionException {
        return FirebaseMessaging.getInstance().sendAsync(message).get();
    }

    /**
     * 전송 결과를 로깅하는 private 메소드
     * 
     * @param response FCM 서버 응답
     * @param token 대상 토큰
     */
    private void logResponse(String response, String token) {
        logger.info("Notification sent successfully to token: {}, response: {}", token, response);
    }
}

