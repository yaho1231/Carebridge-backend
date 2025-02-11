package com.example.carebridge.config;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.InputStream;
import org.springframework.core.io.ClassPathResource;

/**
 * Firebase Cloud Messaging 서비스 설정을 위한 Configuration 클래스
 * FCM을 통해 모바일 디바이스로 푸시 알림을 전송할 수 있도록 합니다.
 */
@Configuration
public class FirebaseConfig {
	/**
	 * 애플리케이션 시작 시 Firebase 초기화를 수행합니다.
	 * fcmSecretKey.json 파일에서 인증 정보를 읽어와 Firebase 앱을 구성합니다.
	 */
	@PostConstruct
	public void init() {
		try {
			// Firebase 서비스 계정 키 파일을 리소스에서 읽어옵니다.
			InputStream serviceAccount = new ClassPathResource("fcmSecretKey.json").getInputStream();
			// Firebase 옵션을 설정합니다.
			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount))
					.build();
			
			// Firebase 앱이 초기화되지 않은 경우에만 초기화를 수행합니다.
			if (FirebaseApp.getApps().isEmpty()) {
				FirebaseApp.initializeApp(options);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}