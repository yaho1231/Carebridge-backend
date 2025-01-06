package com.example.carebridge.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebConfig는 CORS(Cross-Origin Resource Sharing) 설정을 구성하는 클래스입니다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * CORS 매핑을 추가합니다.
     *
     * @param registry CORS 레지스트리 객체
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 모든 경로에 대해 CORS 설정을 적용합니다.
        registry.addMapping("/**")
                // 모든 출처를 허용합니다.
                .allowedOriginPatterns("*")
                // 허용할 HTTP 메서드를 지정합니다.
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                // 허용할 헤더를 지정합니다.
                .allowedHeaders("Authorization", "Content-Type")
                // 노출할 헤더를 지정합니다.
                .exposedHeaders("Custom-Header")
                // 자격 증명(쿠키, 인증 정보 등)을 허용합니다.
                .allowCredentials(true)
                // CORS 설정의 유효 시간을 초 단위로 지정합니다.
                .maxAge(3600);
    }
}