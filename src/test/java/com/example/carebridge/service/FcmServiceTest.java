package com.example.carebridge.service;

import com.example.carebridge.dto.NotificationRequestDto;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * FcmService 클래스에 대한 단위 테스트
 * Firebase Cloud Messaging 서비스의 주요 기능을 검증합니다.
 */
@Tag("service")
@DisplayName("FCM 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class FcmServiceTest {

    @InjectMocks
    private FcmService fcmService;

    // 공통으로 사용되는 테스트 데이터
    private String defaultToken;
    private String defaultTitle;
    private String defaultBody;
    private NotificationRequestDto defaultRequestDto;
    private List<NotificationRequestDto> defaultRequestList;

    /**
     * 각 테스트 실행 전에 공통으로 사용되는 테스트 데이터를 초기화합니다.
     */
    @BeforeEach
    void setUp() {
        // 공통 테스트 데이터 초기화
        defaultToken = "fcm-test-token-123";
        defaultTitle = "테스트 알림 제목";
        defaultBody = "테스트 알림 내용입니다.";

        // 기본 알림 요청 DTO 생성
        defaultRequestDto = NotificationRequestDto.builder()
                .token(defaultToken)
                .title(defaultTitle)
                .body(defaultBody)
                .build();

        // 알림 요청 목록 생성
        defaultRequestList = new ArrayList<>();
        defaultRequestList.add(defaultRequestDto);
    }

    @Nested
    @DisplayName("send 메소드 테스트")
    class SendTest {

        @Test
        @DisplayName("단일 알림 전송 성공 테스트")
        void send_SingleNotification_Success() throws ExecutionException, InterruptedException {
            // given
            String expectedResponse = "message-id-123";
            ApiFuture<String> future = ApiFutures.immediateFuture(expectedResponse);

            // FirebaseMessaging 정적 메소드를 모킹
            try (MockedStatic<FirebaseMessaging> mockedFirebaseMessaging = mockStatic(FirebaseMessaging.class)) {
                FirebaseMessaging firebaseMessaging = mock(FirebaseMessaging.class);
                
                mockedFirebaseMessaging.when(FirebaseMessaging::getInstance).thenReturn(firebaseMessaging);
                when(firebaseMessaging.sendAsync(any(Message.class))).thenReturn(future);

                // when
                fcmService.send(defaultRequestList);

                // then
                mockedFirebaseMessaging.verify(FirebaseMessaging::getInstance, times(1));
                verify(firebaseMessaging, times(1)).sendAsync(any(Message.class));
            }
        }

        @Test
        @DisplayName("다중 알림 전송 성공 테스트")
        void send_MultipleNotifications_Success() throws ExecutionException, InterruptedException {
            // given
            String expectedResponse = "message-id-123";
            ApiFuture<String> future = ApiFutures.immediateFuture(expectedResponse);
            
            // 두 번째 알림 요청 추가
            NotificationRequestDto secondRequest = NotificationRequestDto.builder()
                    .token("second-token")
                    .title("두 번째 알림")
                    .body("두 번째 알림 내용")
                    .build();
            defaultRequestList.add(secondRequest);

            // FirebaseMessaging 정적 메소드를 모킹
            try (MockedStatic<FirebaseMessaging> mockedFirebaseMessaging = mockStatic(FirebaseMessaging.class)) {
                FirebaseMessaging firebaseMessaging = mock(FirebaseMessaging.class);
                
                mockedFirebaseMessaging.when(FirebaseMessaging::getInstance).thenReturn(firebaseMessaging);
                when(firebaseMessaging.sendAsync(any(Message.class))).thenReturn(future);

                // when
                fcmService.send(defaultRequestList);

                // then
                mockedFirebaseMessaging.verify(FirebaseMessaging::getInstance, times(2));
                verify(firebaseMessaging, times(2)).sendAsync(any(Message.class));
            }
        }

        @Test
        @DisplayName("알림 전송 실패 처리 테스트")
        void send_HandlesException() throws ExecutionException, InterruptedException {
            // given
            @SuppressWarnings("unchecked")
            ApiFuture<String> future = mock(ApiFuture.class);
            when(future.get()).thenThrow(new ExecutionException("FCM 서버 오류", new RuntimeException()));

            // FirebaseMessaging 정적 메소드를 모킹
            try (MockedStatic<FirebaseMessaging> mockedFirebaseMessaging = mockStatic(FirebaseMessaging.class)) {
                FirebaseMessaging firebaseMessaging = mock(FirebaseMessaging.class);
                
                mockedFirebaseMessaging.when(FirebaseMessaging::getInstance).thenReturn(firebaseMessaging);
                when(firebaseMessaging.sendAsync(any(Message.class))).thenReturn(future);

                // when & then
                // 예외가 전파되지 않고 내부적으로 처리되는지 확인
                assertDoesNotThrow(() -> fcmService.send(defaultRequestList));
                
                mockedFirebaseMessaging.verify(FirebaseMessaging::getInstance, times(1));
                verify(firebaseMessaging, times(1)).sendAsync(any(Message.class));
            }
        }

        @Test
        @DisplayName("빈 알림 요청 목록 처리 테스트")
        void send_EmptyNotificationList() throws ExecutionException, InterruptedException {
            // given
            List<NotificationRequestDto> emptyList = new ArrayList<>();

            // FirebaseMessaging 정적 메소드를 모킹
            try (MockedStatic<FirebaseMessaging> mockedFirebaseMessaging = mockStatic(FirebaseMessaging.class)) {
                FirebaseMessaging firebaseMessaging = mock(FirebaseMessaging.class);
                
                mockedFirebaseMessaging.when(FirebaseMessaging::getInstance).thenReturn(firebaseMessaging);

                // when
                fcmService.send(emptyList);

                // then
                mockedFirebaseMessaging.verify(FirebaseMessaging::getInstance, never());
                verify(firebaseMessaging, never()).sendAsync(any(Message.class));
            }
        }

        @Test
        @DisplayName("null 토큰으로 알림 요청 처리 테스트")
        void send_WithNullToken() throws ExecutionException, InterruptedException {
            // given
            NotificationRequestDto nullTokenRequest = NotificationRequestDto.builder()
                    .token(null)
                    .title(defaultTitle)
                    .body(defaultBody)
                    .build();
            
            List<NotificationRequestDto> requestList = new ArrayList<>();
            requestList.add(nullTokenRequest);

            // FirebaseMessaging 정적 메소드를 모킹
            try (MockedStatic<FirebaseMessaging> mockedFirebaseMessaging = mockStatic(FirebaseMessaging.class)) {
                FirebaseMessaging firebaseMessaging = mock(FirebaseMessaging.class);
                
                mockedFirebaseMessaging.when(FirebaseMessaging::getInstance).thenReturn(firebaseMessaging);

                // when & then
                // null 토큰이 있어도 예외가 발생하지 않고 내부적으로 처리되어야 함
                assertDoesNotThrow(() -> fcmService.send(requestList));
                
                // null 토큰으로 인해 메시지가 생성되지 않으므로 호출되지 않아야 함
                mockedFirebaseMessaging.verify(FirebaseMessaging::getInstance, never());
                verify(firebaseMessaging, never()).sendAsync(any(Message.class));
            }
        }
    }
} 