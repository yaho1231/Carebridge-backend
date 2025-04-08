package com.example.carebridge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults()) // ✅ CORS 설정 사용하도록 명시
                .csrf(csrf -> csrf.disable()) // CSRF는 API 서버라면 보통 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll() // 모든 경로 허용 (필요시 조정)
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}