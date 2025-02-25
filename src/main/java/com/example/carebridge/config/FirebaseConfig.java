package com.example.carebridge.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Firebase Cloud Messaging(FCM) 서비스 설정을 위한 설정 클래스
 * 
 * 이 클래스는 다음과 같은 역할을 수행합니다:
 * 1. Firebase 서비스 계정 인증 정보 초기화
 * 2. FCM을 통한 푸시 알림 전송 기능 활성화
 * 3. 애플리케이션 시작 시 자동으로 Firebase 초기화
 */
@Configuration
public class FirebaseConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    /**
     * Firebase 초기화를 수행하는 메서드
     * 
     * 수행하는 작업:
     * 1. fcmSecretKey.json 파일에서 서비스 계정 인증 정보를 읽어옴
     * 2. Firebase 옵션을 설정하고 초기화
     * 3. 중복 초기화 방지를 위한 검사 수행
     * 
     * @throws IOException 파일 읽기 또는 Firebase 초기화 중 오류 발생 시
     */
    @PostConstruct
    public void init() {
        try {
            // Firebase 서비스 계정 키 파일을 리소스에서 로드
            InputStream serviceAccount = new ClassPathResource("fcmSecretKey.json").getInputStream();
            
            // Firebase 설정 옵션 구성
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            
            // Firebase가 아직 초기화되지 않은 경우에만 초기화 수행
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                logger.info("Firebase 애플리케이션이 성공적으로 초기화되었습니다.");
            }
        } catch (IOException e) {
            logger.error("Firebase 초기화 중 오류가 발생했습니다: {}", e.getMessage());
            logger.debug("상세 오류 정보:", e);
            throw new RuntimeException("Firebase 초기화 실패", e);
        }
    }
}