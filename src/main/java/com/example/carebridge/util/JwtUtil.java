package com.example.carebridge.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtUtil {
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long ACCESS_EXPIRATION_TIME = 1000 * 60 * 30; // 30분
    private final long REFRESH_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // 7일
    private final ConcurrentHashMap<String, String> refreshTokenStore = new ConcurrentHashMap<>(); // Refresh Token 저장

    // Access & Refresh Token 생성
    public TokenPair generateTokens(String phoneNumber) {
        String accessToken = Jwts.builder()
                .setSubject(phoneNumber)
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION_TIME))
                .signWith(key)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(phoneNumber)
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
                .signWith(key)
                .compact();

        // Refresh Token 저장 (DB 또는 Redis 사용 가능)
        refreshTokenStore.put(phoneNumber, refreshToken);

        return new TokenPair(accessToken, refreshToken);
    }

    // Access Token 검증
    public boolean isTokenValid(String accessToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Access Token에서 전화번호 가져오기
    public String getPhoneNumber(String accessToken) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(accessToken).getBody().getSubject();
    }

    // Refresh Token을 사용하여 Access Token 재발급
    public TokenPair refreshAccessToken(String refreshToken) {
        try {
            String phoneNumber = getPhoneNumber(refreshToken);

            // 저장된 Refresh Token과 비교
            if (!refreshToken.equals(refreshTokenStore.get(phoneNumber))) {
                throw new RuntimeException("Invalid Refresh Token");
            }

            // 새로운 Access Token과 Refresh Token 발급
            return generateTokens(phoneNumber);
        } catch (Exception e) {
            throw new RuntimeException("Invalid Refresh Token");
        }
    }

    // Refresh Token 무효화 (로그아웃, 토큰 탈취 시 사용)
    public void invalidateRefreshToken(String phoneNumber) {
        refreshTokenStore.remove(phoneNumber);
    }

    // 토큰을 저장하는 데이터 클래스
    public static class TokenPair {
        public String accessToken;
        public String refreshToken;

        public TokenPair(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }
}