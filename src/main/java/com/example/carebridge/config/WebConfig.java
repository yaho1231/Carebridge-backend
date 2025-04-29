package com.example.carebridge.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS(Cross-Origin Resource Sharing) 설정 구성 클래스
 * 현재 설정은 개발 환경용이며, 운영 환경 배포 전 보안 강화가 필요합니다.
 *
 * [개발 환경과 운영 환경의 CORS 설정 차이점]
 * 1. 개발 환경: 모든 도메인 허용, 다양한 HTTP 메서드 허용
 * 2. 운영 환경: 특정 도메인만 허용, 필요한 HTTP 메서드만 제한적 허용
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * CORS 정책 설정 메서드
     * 현재는 개발 편의성을 위해 모든 교차 출처 요청을 허용하도록 설정되어 있습니다.
     *
     * [현재 개발 환경 설정]
     * - 모든 경로와 출처 허용 ("/**", "*")
     * - 모든 주요 HTTP 메서드 허용
     * - 인증 정보 포함 허용
     *
     * [배포 시 변경 필요 사항]
     * 1. allowedOriginPatterns:
     *    - 현재: "*" (모든 도메인 허용)
     *    - 변경: "https://carebridge.com", "https://*.carebridge.com"
     *           (실제 서비스 도메인만 허용)
     *
     * 2. allowedMethods:
     *    - 현재: GET, POST, PUT, DELETE (모든 주요 메서드 허용)
     *    - 변경: 실제 사용하는 메서드만 명시적으로 허용
     *
     * 3. allowedHeaders:
     *    - 현재: Authorization, Content-Type (기본 헤더만 허용)
     *    - 변경: 서비스에서 실제 사용하는 커스텀 헤더들만 명시적으로 허용
     *
     * 4. exposedHeaders:
     *    - 현재: Custom-Header (테스트용)
     *    - 변경: 실제 클라이언트에 노출이 필요한 헤더만 설정
     *
     * 5. allowCredentials:
     *    - 현재: true (모든 인증 정보 허용)
     *    - 변경: 필요한 경우에만 true로 설정, 불필요시 false
     *
     * 6. maxAge:
     *    - 현재: 3600초 (1시간)
     *    - 변경: 서비스 요구사항에 맞는 적절한 시간으로 조정
     *
     * [보안 고려사항]
     * 1. 프로덕션 환경에서는 반드시 허용할 도메인을 명시적으로 지정
     * 2. 와일드카드(*) 사용을 최소화하여 보안 위험 감소
     * 3. 필요한 HTTP 메서드만 최소한으로 허용
     * 4. JWT 등 인증 토큰 사용 시 관련 헤더를 명시적으로 허용
     *
     * @param registry CORS 설정을 위한 CorsRegistry 객체 (null이 될 수 없음)
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")          // 모든 경로에 대해 CORS 설정 적용
                .allowedOrigins(
                        "http://localhost:5173",
                        "https://carebridge-frontend.vercel.app",
                        "https://carebridge.kro.kr"
                )
                .allowedMethods(            // HTTP 메서드 허용 설정
                        "GET",              // 리소스 조회
                        "POST",             // 리소스 생성
                        "PUT",              // 리소스 수정
                        "PATCH",            // 리소스 일부 수정
                        "DELETE"            // 리소스 삭제
                )
                .allowedHeaders(            // 허용할 요청 헤더
                        "Authorization",     // 인증 토큰
                        "Content-Type"       // 요청 본문 타입
                )
                .exposedHeaders(            // 클라이언트에 노출할 응답 헤더
                        "Custom-Header"      // 테스트용 커스텀 헤더
                )
                .allowCredentials(true)     // 인증 정보 포함 허용
                .maxAge(3600);              // 프리플라이트 요청 캐시 시간 (초)
    }
}