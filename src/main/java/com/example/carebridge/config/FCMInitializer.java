package com.example.carebridge.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Firebase Cloud Messaging 초기화 설정 클래스
 * <p>
 * 애플리케이션 시작 시 Firebase 서비스를 초기화하고 설정합니다.
 * FCM 을 사용하여 푸시 알림을 전송하기 위해서는 Firebase 프로젝트의 인증 정보가 필요합니다.
 * 이 클래스는 서버 시작 시 자동으로 Firebase 서비스를 초기화하고 인증 정보를 설정합니다.
 * <p>
 * 환경 변수를 통해 Firebase 인증 정보를 안전하게 관리합니다.
 */
@Service
public class FCMInitializer {

    /**
     * FCMInitializer 클래스의 로깅을 위한 Logger 인스턴스
     * Firebase 초기화 과정에서 발생하는 로그를 기록합니다.
     */
    private static final Logger logger = LoggerFactory.getLogger(FCMInitializer.class);

    @Value("${firebase.credentials}")
    private String firebaseCredentials;

    /**
     * Firebase 서비스 초기화 메서드
     * <p>
     * 애플리케이션 시작 시 자동으로 실행되어 Firebase 서비스를 초기화합니다.
     * 환경 변수에서 Firebase 인증 정보를 읽어와 서비스를 초기화합니다.
     * <p>
     * 초기화 과정:
     * 1. 환경 변수에서 Firebase 인증 정보를 읽어옵니다.
     * 2. 인증 정보를 기반으로 Firebase 옵션을 설정합니다.
     * 3. Firebase 애플리케이션을 초기화합니다.
     *
     */
    @PostConstruct
    public void initialize() {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(
                            new ByteArrayInputStream(firebaseCredentials.getBytes(StandardCharsets.UTF_8))))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                logger.info("Firebase application has been initialized");
            }
        } catch (IOException e) {
            logger.error("Firebase 초기화 실패: {}", e.getMessage());
            throw new RuntimeException("Firebase 초기화 중 오류가 발생했습니다", e);
        }
    }
}