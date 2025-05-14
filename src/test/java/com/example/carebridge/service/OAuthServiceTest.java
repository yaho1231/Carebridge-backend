package com.example.carebridge.service;

import com.example.carebridge.dto.KakaoDto;
import com.example.carebridge.dto.UserAccountDto;
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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * OAuthService 클래스에 대한 단위 테스트
 * OAuth 인증 서비스의 주요 기능을 검증합니다.
 */
@Tag("service")
@DisplayName("OAuth 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class OAuthServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private UserAccountService userAccountService;

    @Spy
    @InjectMocks
    private OAuthService oAuthService;

    // 공통으로 사용되는 테스트 데이터
    private String validCode;
    private String validAccessToken;
    private String validEmail;
    private String validNickname;
    private UserAccount validUserAccount;
    private UserAccountDto validUserAccountDto;
    private KakaoDto validKakaoDto;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        validCode = "valid_auth_code";
        validAccessToken = "valid_access_token";
        validEmail = "test@example.com";
        validNickname = "테스트사용자";

        // 설정 값 주입
        ReflectionTestUtils.setField(oAuthService, "KAKAO_CLIENT_ID", "test_client_id");
        ReflectionTestUtils.setField(oAuthService, "KAKAO_CLIENT_SECRET", "test_client_secret");
        ReflectionTestUtils.setField(oAuthService, "KAKAO_REDIRECT_URL", "http://localhost:8080/oauth/callback");

        // 유효한 UserAccount 생성
        validUserAccount = new UserAccount();
        validUserAccount.setEmail(validEmail);
        validUserAccount.setName(validNickname);
        validUserAccount.setPhoneNumber("01012345678");
        validUserAccount.setGender(UserAccount.Gender.Male);
        validUserAccount.setBirthDate(LocalDateTime.now().minusYears(20));

        // 유효한 UserAccountDto 생성
        validUserAccountDto = new UserAccountDto();
        validUserAccountDto.setEmail(validEmail);
        validUserAccountDto.setName(validNickname);
        validUserAccountDto.setPhoneNumber("01012345678");
        validUserAccountDto.setGender(UserAccount.Gender.Male);
        validUserAccountDto.setBirthDate(LocalDateTime.now().minusYears(20));

        // 유효한 KakaoDto 생성
        validKakaoDto = KakaoDto.builder()
                .id(12345L)
                .email(validEmail)
                .nickname(validNickname)
                .build();
    }

    @Nested
    @DisplayName("getKakaoLogin 메소드 테스트")
    class GetKakaoLoginTest {

        @Test
        @DisplayName("카카오 로그인 URL 생성 성공 테스트")
        void getKakaoLogin_Success() {
            // when
            String loginUrl = oAuthService.getKakaoLogin();

            // then
            assertNotNull(loginUrl);
            assertTrue(loginUrl.contains("kauth.kakao.com/oauth/authorize"));
            assertTrue(loginUrl.contains("client_id=test_client_id"));
            assertTrue(loginUrl.contains("redirect_uri=http://localhost:8080/oauth/callback"));
            assertTrue(loginUrl.contains("response_type=code"));
        }
    }

    @Nested
    @DisplayName("getKakaoToken 메소드 테스트")
    class GetKakaoTokenTest {

        @Test
        @DisplayName("카카오 토큰 얻기 성공 테스트")
        void getKakaoToken_Success() throws Exception {
            // given
            doReturn("valid_access_token").when(oAuthService).getKakaoToken(validCode);

            // when
            String accessToken = oAuthService.getKakaoToken(validCode);

            // then
            assertNotNull(accessToken);
            assertEquals("valid_access_token", accessToken);
        }

        @Test
        @DisplayName("인증 코드가 null인 경우 예외 발생 테스트")
        void getKakaoToken_NullCode_ThrowsException() {
            // when & then
            assertThrows(Exception.class, () -> oAuthService.getKakaoToken(null));
        }
    }

    @Nested
    @DisplayName("getUserInfoWithToken 메소드 테스트")
    class GetUserInfoWithTokenTest {

        @Test
        @DisplayName("액세스 토큰으로 사용자 정보 가져오기 성공 테스트")
        void getUserInfoWithToken_Success() throws Exception {
            // given
            doReturn(validKakaoDto).when(oAuthService).getUserInfoWithToken(validAccessToken);

            // when
            KakaoDto kakaoDto = oAuthService.getUserInfoWithToken(validAccessToken);

            // then
            assertNotNull(kakaoDto);
            assertEquals(12345L, kakaoDto.getId());
            assertEquals("test@example.com", kakaoDto.getEmail());
            assertEquals("테스트사용자", kakaoDto.getNickname());
        }
    }

    @Nested
    @DisplayName("ifNeedKakaoInfo 메소드 테스트")
    class IfNeedKakaoInfoTest {

        @Test
        @DisplayName("이미 등록된 이메일이 있는 경우 해당 계정 반환 테스트")
        void ifNeedKakaoInfo_ExistingUser_Success() {
            // given
            when(userAccountRepository.findByEmail(validEmail)).thenReturn(Optional.of(validUserAccount));
            when(userAccountService.convertUserAccountToUserAccountDto(validUserAccount)).thenReturn(validUserAccountDto);

            // when
            UserAccountDto result = oAuthService.ifNeedKakaoInfo(validKakaoDto);

            // then
            assertNotNull(result);
            assertEquals(validEmail, result.getEmail());
            assertEquals(validNickname, result.getName());
            
            verify(userAccountRepository, times(1)).findByEmail(validEmail);
            verify(userAccountRepository, never()).save(any(UserAccount.class));
            verify(userAccountService, times(1)).convertUserAccountToUserAccountDto(validUserAccount);
        }

        @Test
        @DisplayName("등록되지 않은 이메일인 경우 새로운 계정 생성 테스트")
        void ifNeedKakaoInfo_NewUser_Success() {
            // given
            when(userAccountRepository.findByEmail(validEmail)).thenReturn(Optional.empty()).thenReturn(Optional.of(validUserAccount));
            when(userAccountService.generateRandomNumber(8)).thenReturn("12345678");
            when(userAccountService.convertUserAccountToUserAccountDto(validUserAccount)).thenReturn(validUserAccountDto);

            // when
            UserAccountDto result = oAuthService.ifNeedKakaoInfo(validKakaoDto);

            // then
            assertNotNull(result);
            assertEquals(validEmail, result.getEmail());
            assertEquals(validNickname, result.getName());
            
            verify(userAccountRepository, times(2)).findByEmail(validEmail);
            verify(userAccountRepository, times(1)).save(any(UserAccount.class));
            verify(userAccountService, times(1)).generateRandomNumber(8);
            verify(userAccountService, times(1)).convertUserAccountToUserAccountDto(validUserAccount);
        }

        @Test
        @DisplayName("저장 후 사용자를 찾을 수 없는 경우 예외 발생 테스트")
        void ifNeedKakaoInfo_UserNotFoundAfterSave_ThrowsException() {
            // given
            when(userAccountRepository.findByEmail(validEmail)).thenReturn(Optional.empty()).thenReturn(Optional.empty());
            when(userAccountService.generateRandomNumber(8)).thenReturn("12345678");

            // when & then
            assertThrows(NoSuchElementException.class, () -> oAuthService.ifNeedKakaoInfo(validKakaoDto));
            
            verify(userAccountRepository, times(2)).findByEmail(validEmail);
            verify(userAccountRepository, times(1)).save(any(UserAccount.class));
            verify(userAccountService, times(1)).generateRandomNumber(8);
            verify(userAccountService, never()).convertUserAccountToUserAccountDto(any(UserAccount.class));
        }
    }

    @Nested
    @DisplayName("unlinkKakaoAccount 메소드 테스트")
    class UnlinkKakaoAccountTest {

        @Test
        @DisplayName("카카오 계정 연동 해제 성공 테스트")
        void unlinkKakaoAccount_Success() throws Exception {
            // given
            doNothing().when(oAuthService).unlinkKakaoAccount(validAccessToken);

            // when & then
            assertDoesNotThrow(() -> oAuthService.unlinkKakaoAccount(validAccessToken));
        }
    }

    @Nested
    @DisplayName("kakaoDisconnect 메소드 테스트")
    class KakaoDisconnectTest {

        @Test
        @DisplayName("카카오 로그아웃 성공 테스트")
        void kakaoDisconnect_Success() throws Exception {
            // given
            doNothing().when(oAuthService).kakaoDisconnect(validAccessToken);

            // when & then
            assertDoesNotThrow(() -> oAuthService.kakaoDisconnect(validAccessToken));
        }
    }
} 