package com.example.carebridge.service;

import com.example.carebridge.entity.UserAccount;
import com.example.carebridge.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * NotificationService 클래스에 대한 단위 테스트
 * FCM 토큰 등록 및 삭제 기능을 검증합니다.
 */
@Tag("service")
@DisplayName("알림 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private NotificationService notificationService;

    // 공통으로 사용되는 테스트 데이터
    private Integer defaultUserId;
    private String defaultFcmToken;
    private UserAccount defaultUserAccount;

    /**
     * 각 테스트 실행 전에 공통으로 사용되는 테스트 데이터를 초기화합니다.
     */
    @BeforeEach
    void setUp() {
        // 공통 테스트 데이터 초기화
        defaultUserId = 1;
        defaultFcmToken = "fcm-test-token-123";

        // 기본 사용자 계정 정보 생성
        defaultUserAccount = new UserAccount();
        defaultUserAccount.setId(defaultUserId);
    }

    @Nested
    @DisplayName("register 메소드 테스트")
    class RegisterTest {

        @Test
        @DisplayName("FCM 토큰 등록 성공 테스트")
        void register_Success() {
            // given
            when(userAccountRepository.findById(defaultUserId)).thenReturn(Optional.of(defaultUserAccount));
            when(userAccountRepository.save(any(UserAccount.class))).thenReturn(defaultUserAccount);

            // when
            notificationService.register(defaultUserId, defaultFcmToken);

            // then
            assertEquals(defaultFcmToken, defaultUserAccount.getFcmToken());
            verify(userAccountRepository, times(1)).findById(defaultUserId);
            verify(userAccountRepository, times(1)).save(defaultUserAccount);
        }

        @Test
        @DisplayName("null 사용자 ID로 예외 발생 테스트")
        void register_NullUserId() {
            // when & then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> notificationService.register(null, defaultFcmToken));
            assertTrue(exception.getMessage().contains("사용자 ID는 필수 입력값입니다"));
            
            verify(userAccountRepository, never()).findById(any());
            verify(userAccountRepository, never()).save(any());
        }

        @Test
        @DisplayName("null 토큰으로 예외 발생 테스트")
        void register_NullToken() {
            // when & then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> notificationService.register(defaultUserId, null));
            assertTrue(exception.getMessage().contains("FCM 토큰은 필수 입력값입니다"));
            
            verify(userAccountRepository, never()).findById(any());
            verify(userAccountRepository, never()).save(any());
        }

        @Test
        @DisplayName("빈 문자열 토큰으로 예외 발생 테스트")
        void register_EmptyToken() {
            // when & then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> notificationService.register(defaultUserId, ""));
            assertTrue(exception.getMessage().contains("FCM 토큰은 필수 입력값입니다"));
            
            verify(userAccountRepository, never()).findById(any());
            verify(userAccountRepository, never()).save(any());
        }

        @Test
        @DisplayName("사용자를 찾을 수 없는 경우 예외 발생 테스트")
        void register_UserNotFound() {
            // given
            when(userAccountRepository.findById(defaultUserId)).thenReturn(Optional.empty());

            // when & then
            NoSuchElementException exception = assertThrows(NoSuchElementException.class, 
                () -> notificationService.register(defaultUserId, defaultFcmToken));
            assertTrue(exception.getMessage().contains("해당 ID의 사용자 계정을 찾을 수 없습니다"));
            
            verify(userAccountRepository, times(1)).findById(defaultUserId);
            verify(userAccountRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteToken 메소드 테스트")
    class DeleteTokenTest {

        @Test
        @DisplayName("FCM 토큰 삭제 성공 테스트")
        void deleteToken_Success() {
            // given
            defaultUserAccount.setFcmToken(defaultFcmToken); // 기존 토큰 설정
            when(userAccountRepository.findById(defaultUserId)).thenReturn(Optional.of(defaultUserAccount));
            when(userAccountRepository.save(any(UserAccount.class))).thenReturn(defaultUserAccount);

            // when
            notificationService.deleteToken(defaultUserId);

            // then
            assertNull(defaultUserAccount.getFcmToken());
            verify(userAccountRepository, times(1)).findById(defaultUserId);
            verify(userAccountRepository, times(1)).save(defaultUserAccount);
        }

        @Test
        @DisplayName("null 사용자 ID로 예외 발생 테스트")
        void deleteToken_NullUserId() {
            // when & then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> notificationService.deleteToken(null));
            assertTrue(exception.getMessage().contains("사용자 ID는 필수 입력값입니다"));
            
            verify(userAccountRepository, never()).findById(any());
            verify(userAccountRepository, never()).save(any());
        }

        @Test
        @DisplayName("사용자를 찾을 수 없는 경우 예외 발생 테스트")
        void deleteToken_UserNotFound() {
            // given
            when(userAccountRepository.findById(defaultUserId)).thenReturn(Optional.empty());

            // when & then
            NoSuchElementException exception = assertThrows(NoSuchElementException.class, 
                () -> notificationService.deleteToken(defaultUserId));
            assertTrue(exception.getMessage().contains("해당 ID의 사용자를 찾을 수 없습니다"));
            
            verify(userAccountRepository, times(1)).findById(defaultUserId);
            verify(userAccountRepository, never()).save(any());
        }

        @Test
        @DisplayName("이미 토큰이 없는 사용자 처리 테스트")
        void deleteToken_AlreadyNull() {
            // given
            defaultUserAccount.setFcmToken(null); // 기존 토큰이 이미 null
            when(userAccountRepository.findById(defaultUserId)).thenReturn(Optional.of(defaultUserAccount));
            when(userAccountRepository.save(any(UserAccount.class))).thenReturn(defaultUserAccount);

            // when
            notificationService.deleteToken(defaultUserId);

            // then
            assertNull(defaultUserAccount.getFcmToken());
            verify(userAccountRepository, times(1)).findById(defaultUserId);
            verify(userAccountRepository, times(1)).save(defaultUserAccount);
        }
    }
} 