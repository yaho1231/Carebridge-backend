package com.example.carebridge.service;

import com.example.carebridge.dto.UserAccountDto;
import com.example.carebridge.dto.VerifyAccountDto;
import com.example.carebridge.entity.UserAccount;
import com.example.carebridge.repository.UserAccountRepository;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * UserAccountService 클래스에 대한 단위 테스트
 * 사용자 계정 관리의 주요 기능을 검증합니다.
 */
@Tag("service")
@DisplayName("사용자 계정 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private DefaultMessageService smsMessageService;

    @InjectMocks
    private UserAccountService userAccountService;

    // 공통으로 사용되는 테스트 데이터
    private String validPhoneNumber;
    private String validEmail;
    private String validName;
    private LocalDateTime validBirthDate;
    private UserAccount.Gender validGender;
    private UserAccount validUserAccount;
    private UserAccountDto validUserAccountDto;

    @BeforeEach
    void setUp() {
        // 공통 테스트 데이터 초기화
        validPhoneNumber = "01012345678";
        validEmail = "test@example.com";
        validName = "테스트 사용자";
        validBirthDate = LocalDateTime.of(1990, 1, 1, 0, 0);
        validGender = UserAccount.Gender.Male;

        // 유효한 UserAccount 생성
        validUserAccount = new UserAccount();
        validUserAccount.setPhoneNumber(validPhoneNumber);
        validUserAccount.setEmail(validEmail);
        validUserAccount.setName(validName);
        validUserAccount.setBirthDate(validBirthDate);
        validUserAccount.setGender(validGender);

        // 유효한 UserAccountDto 생성
        validUserAccountDto = new UserAccountDto();
        validUserAccountDto.setPhoneNumber(validPhoneNumber);
        validUserAccountDto.setEmail(validEmail);
        validUserAccountDto.setName(validName);
        validUserAccountDto.setBirthDate(validBirthDate);
        validUserAccountDto.setGender(validGender);
    }

    @Nested
    @DisplayName("getUserAccount 메소드 테스트")
    class GetUserAccountTest {

        @Test
        @DisplayName("전화번호로 사용자 조회 성공 테스트")
        void getUserAccount_Success() {
            // given
            lenient().when(userAccountRepository.findByPhoneNumber(validPhoneNumber)).thenReturn(Optional.of(validUserAccount));

            // when
            UserAccountDto result = userAccountService.getUserAccount(validPhoneNumber);

            // then
            assertNotNull(result);
            assertEquals(validPhoneNumber, result.getPhoneNumber());
            assertEquals(validEmail, result.getEmail());
            assertEquals(validName, result.getName());
            assertEquals(validBirthDate, result.getBirthDate());
            assertEquals(validGender, result.getGender());
            verify(userAccountRepository, times(1)).findByPhoneNumber(validPhoneNumber);
        }

        @Test
        @DisplayName("존재하지 않는 전화번호로 사용자 조회 시 예외 발생 테스트")
        void getUserAccount_NotFound() {
            // given
            lenient().when(userAccountRepository.findByPhoneNumber(validPhoneNumber)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> userAccountService.getUserAccount(validPhoneNumber));
            verify(userAccountRepository, times(1)).findByPhoneNumber(validPhoneNumber);
        }

        @Test
        @DisplayName("빈 전화번호로 사용자 조회 시 예외 발생 테스트")
        void getUserAccount_EmptyPhoneNumber() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> userAccountService.getUserAccount(""));
            assertThrows(IllegalArgumentException.class, () -> userAccountService.getUserAccount(null));
            verify(userAccountRepository, never()).findByPhoneNumber(anyString());
        }
    }

    @Nested
    @DisplayName("getUserAccountByEmail 메소드 테스트")
    class GetUserAccountByEmailTest {

        @Test
        @DisplayName("이메일로 사용자 조회 성공 테스트")
        void getUserAccountByEmail_Success() {
            // given
            lenient().when(userAccountRepository.findByEmail(validEmail)).thenReturn(Optional.of(validUserAccount));

            // when
            UserAccountDto result = userAccountService.getUserAccountByEmail(validEmail);

            // then
            assertNotNull(result);
            assertEquals(validPhoneNumber, result.getPhoneNumber());
            assertEquals(validEmail, result.getEmail());
            assertEquals(validName, result.getName());
            verify(userAccountRepository, times(1)).findByEmail(validEmail);
        }

        @Test
        @DisplayName("존재하지 않는 이메일로 사용자 조회 시 예외 발생 테스트")
        void getUserAccountByEmail_NotFound() {
            // given
            lenient().when(userAccountRepository.findByEmail(validEmail)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> userAccountService.getUserAccountByEmail(validEmail));
            verify(userAccountRepository, times(1)).findByEmail(validEmail);
        }

        @Test
        @DisplayName("빈 이메일로 사용자 조회 시 예외 발생 테스트")
        void getUserAccountByEmail_EmptyEmail() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> userAccountService.getUserAccountByEmail(""));
            assertThrows(IllegalArgumentException.class, () -> userAccountService.getUserAccountByEmail(null));
            verify(userAccountRepository, never()).findByEmail(anyString());
        }
    }

    @Nested
    @DisplayName("createUserAccount 메소드 테스트")
    class CreateUserAccountTest {

        @Test
        @DisplayName("사용자 계정 생성 성공 테스트")
        void createUserAccount_Success() {
            // given
            when(userAccountRepository.findByPhoneNumber(validPhoneNumber)).thenReturn(Optional.empty());

            // when
            userAccountService.createUserAccount(validUserAccountDto);

            // then
            verify(userAccountRepository, times(1)).findByPhoneNumber(validPhoneNumber);
            verify(userAccountRepository, times(1)).save(any(UserAccount.class));
        }

        @Test
        @DisplayName("이미 등록된 전화번호로 계정 생성 시 예외 발생 테스트")
        void createUserAccount_DuplicatePhoneNumber() {
            // given
            when(userAccountRepository.findByPhoneNumber(validPhoneNumber)).thenReturn(Optional.of(validUserAccount));

            // when & then
            assertThrows(DuplicateKeyException.class, () -> userAccountService.createUserAccount(validUserAccountDto));
            verify(userAccountRepository, times(1)).findByPhoneNumber(validPhoneNumber);
            verify(userAccountRepository, never()).save(any(UserAccount.class));
        }

        @Test
        @DisplayName("필수 값 누락 시 예외 발생 테스트")
        void createUserAccount_MissingRequiredFields() {
            // given
            UserAccountDto invalidDto = new UserAccountDto();
            invalidDto.setPhoneNumber(validPhoneNumber);
            // email과 name이 누락됨

            // when & then
            assertThrows(IllegalArgumentException.class, () -> userAccountService.createUserAccount(invalidDto));
            verify(userAccountRepository, never()).findByPhoneNumber(anyString());
            verify(userAccountRepository, never()).save(any(UserAccount.class));
        }
    }

    @Nested
    @DisplayName("updateUserAccount 메소드 테스트")
    class UpdateUserAccountTest {

        @Test
        @DisplayName("사용자 계정 업데이트 성공 테스트")
        void updateUserAccount_Success() {
            // given
            lenient().when(userAccountRepository.findByPhoneNumber(validPhoneNumber)).thenReturn(Optional.of(validUserAccount));
            
            UserAccountDto updateDto = new UserAccountDto();
            updateDto.setName("업데이트된 이름");
            updateDto.setEmail("updated@example.com");
            updateDto.setPhoneNumber(validPhoneNumber);
            updateDto.setBirthDate(validBirthDate);
            updateDto.setGender(validGender);

            // when
            UserAccountDto result = userAccountService.updateUserAccount(validPhoneNumber, updateDto);

            // then
            assertNotNull(result);
            assertEquals("업데이트된 이름", result.getName());
            assertEquals("updated@example.com", result.getEmail());
            verify(userAccountRepository, times(1)).findByPhoneNumber(validPhoneNumber);
            verify(userAccountRepository, times(1)).save(any(UserAccount.class));
        }

        @Test
        @DisplayName("존재하지 않는 전화번호로 업데이트 시 예외 발생 테스트")
        void updateUserAccount_NotFound() {
            // given
            when(userAccountRepository.findByPhoneNumber(validPhoneNumber)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, 
                () -> userAccountService.updateUserAccount(validPhoneNumber, validUserAccountDto));
            verify(userAccountRepository, times(1)).findByPhoneNumber(validPhoneNumber);
            verify(userAccountRepository, never()).save(any(UserAccount.class));
        }

        @Test
        @DisplayName("빈 전화번호로 업데이트 시 예외 발생 테스트")
        void updateUserAccount_EmptyPhoneNumber() {
            // when & then
            assertThrows(IllegalArgumentException.class, 
                () -> userAccountService.updateUserAccount("", validUserAccountDto));
            assertThrows(IllegalArgumentException.class, 
                () -> userAccountService.updateUserAccount(null, validUserAccountDto));
            verify(userAccountRepository, never()).findByPhoneNumber(anyString());
            verify(userAccountRepository, never()).save(any(UserAccount.class));
        }
    }

    @Nested
    @DisplayName("sendOtp 메소드 테스트")
    class SendOtpTest {

        @Test
        @DisplayName("회원가입용 OTP 전송 성공 테스트")
        void sendOtp_ForSignup_Success() {
            // given
            when(userAccountRepository.findByPhoneNumber(validPhoneNumber)).thenReturn(Optional.empty());
            ArgumentCaptor<UserAccount> userAccountCaptor = ArgumentCaptor.forClass(UserAccount.class);

            // when
            userAccountService.sendOtp(validPhoneNumber, true);

            // then
            verify(userAccountRepository, times(1)).findByPhoneNumber(validPhoneNumber);
            verify(userAccountRepository, times(1)).save(userAccountCaptor.capture());
            verify(smsMessageService, times(1)).sendOne(any(SingleMessageSendingRequest.class));

            UserAccount savedAccount = userAccountCaptor.getValue();
            assertNotNull(savedAccount.getOtp());
            assertNotNull(savedAccount.getOtpExpiry());
            assertEquals("UserName", savedAccount.getName());
            assertEquals(validPhoneNumber, savedAccount.getPhoneNumber());
        }

        @Test
        @DisplayName("로그인용 OTP 전송 성공 테스트")
        void sendOtp_ForLogin_Success() {
            // given
            when(userAccountRepository.findByPhoneNumber(validPhoneNumber)).thenReturn(Optional.of(validUserAccount));
            ArgumentCaptor<UserAccount> userAccountCaptor = ArgumentCaptor.forClass(UserAccount.class);

            // when
            userAccountService.sendOtp(validPhoneNumber, false);

            // then
            verify(userAccountRepository, times(1)).findByPhoneNumber(validPhoneNumber);
            verify(userAccountRepository, times(1)).save(userAccountCaptor.capture());
            verify(smsMessageService, times(1)).sendOne(any(SingleMessageSendingRequest.class));

            UserAccount savedAccount = userAccountCaptor.getValue();
            assertNotNull(savedAccount.getOtp());
            assertNotNull(savedAccount.getOtpExpiry());
        }

        @Test
        @DisplayName("등록되지 않은 전화번호로 로그인용 OTP 요청 시 예외 발생 테스트")
        void sendOtp_ForLogin_NotRegistered() {
            // given
            when(userAccountRepository.findByPhoneNumber(validPhoneNumber)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> userAccountService.sendOtp(validPhoneNumber, false));
            verify(userAccountRepository, times(1)).findByPhoneNumber(validPhoneNumber);
            verify(userAccountRepository, never()).save(any(UserAccount.class));
            verify(smsMessageService, never()).sendOne(any(SingleMessageSendingRequest.class));
        }

        @Test
        @DisplayName("이미 가입된 전화번호로 회원가입용 OTP 요청 시 예외 발생 테스트")
        void sendOtp_ForSignup_AlreadyRegistered() {
            // given
            when(userAccountRepository.findByPhoneNumber(validPhoneNumber)).thenReturn(Optional.of(validUserAccount));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> userAccountService.sendOtp(validPhoneNumber, true));
            verify(userAccountRepository, times(1)).findByPhoneNumber(validPhoneNumber);
            verify(userAccountRepository, never()).save(any(UserAccount.class));
            verify(smsMessageService, never()).sendOne(any(SingleMessageSendingRequest.class));
        }
    }

    @Nested
    @DisplayName("verifyOtp 메소드 테스트")
    class VerifyOtpTest {

        @Test
        @DisplayName("유효한 OTP 검증 성공 테스트")
        void verifyOtp_ValidOtp_Success() {
            // given
            String validOtp = "123456";
            LocalDateTime futureExpiry = LocalDateTime.now().plusMinutes(3);
            
            UserAccount accountWithOtp = new UserAccount();
            accountWithOtp.setPhoneNumber(validPhoneNumber);
            accountWithOtp.setOtp(validOtp);
            accountWithOtp.setOtpExpiry(futureExpiry);
            
            when(userAccountRepository.findByPhoneNumber(validPhoneNumber)).thenReturn(Optional.of(accountWithOtp));
            
            VerifyAccountDto verifyDto = new VerifyAccountDto(validOtp, validPhoneNumber);

            // when
            boolean result = userAccountService.verifyOtp(verifyDto);

            // then
            assertTrue(result);
            verify(userAccountRepository, times(1)).findByPhoneNumber(validPhoneNumber);
        }

        @Test
        @DisplayName("만료된 OTP 검증 실패 테스트")
        void verifyOtp_ExpiredOtp_Failure() {
            // given
            String validOtp = "123456";
            LocalDateTime pastExpiry = LocalDateTime.now().minusMinutes(1);
            
            UserAccount accountWithOtp = new UserAccount();
            accountWithOtp.setPhoneNumber(validPhoneNumber);
            accountWithOtp.setOtp(validOtp);
            accountWithOtp.setOtpExpiry(pastExpiry);
            
            when(userAccountRepository.findByPhoneNumber(validPhoneNumber)).thenReturn(Optional.of(accountWithOtp));
            
            VerifyAccountDto verifyDto = new VerifyAccountDto(validOtp, validPhoneNumber);

            // when
            boolean result = userAccountService.verifyOtp(verifyDto);

            // then
            assertFalse(result);
            verify(userAccountRepository, times(1)).findByPhoneNumber(validPhoneNumber);
        }

        @Test
        @DisplayName("잘못된 OTP 검증 실패 테스트")
        void verifyOtp_InvalidOtp_Failure() {
            // given
            String validOtp = "123456";
            String invalidOtp = "654321";
            LocalDateTime futureExpiry = LocalDateTime.now().plusMinutes(3);
            
            UserAccount accountWithOtp = new UserAccount();
            accountWithOtp.setPhoneNumber(validPhoneNumber);
            accountWithOtp.setOtp(validOtp);
            accountWithOtp.setOtpExpiry(futureExpiry);
            
            when(userAccountRepository.findByPhoneNumber(validPhoneNumber)).thenReturn(Optional.of(accountWithOtp));
            
            VerifyAccountDto verifyDto = new VerifyAccountDto(invalidOtp, validPhoneNumber);

            // when
            boolean result = userAccountService.verifyOtp(verifyDto);

            // then
            assertFalse(result);
            verify(userAccountRepository, times(1)).findByPhoneNumber(validPhoneNumber);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 OTP 검증 실패 테스트")
        void verifyOtp_UserNotFound_Failure() {
            // given
            lenient().when(userAccountRepository.findByPhoneNumber(validPhoneNumber)).thenReturn(Optional.empty());
            
            VerifyAccountDto verifyDto = new VerifyAccountDto("123456", validPhoneNumber);

            // when & then
            NoSuchElementException exception = assertThrows(NoSuchElementException.class, 
                () -> userAccountService.verifyOtp(verifyDto));
            
            // 메시지 검증 수정 - 서비스 코드의 메시지와 일치하도록
            assertTrue(exception.getMessage().contains("해당 전화번호의 사용자 계정을 찾을 수 없습니다"));
            verify(userAccountRepository, times(1)).findByPhoneNumber(validPhoneNumber);
        }
    }

    @Nested
    @DisplayName("기타 메소드 테스트")
    class OtherMethodsTest {

        @Test
        @DisplayName("RandomNumber 생성 테스트")
        void generateRandomNumber_Success() {
            // when
            String result1 = userAccountService.generateRandomNumber(4);
            String result2 = userAccountService.generateRandomNumber(6);

            // then
            assertEquals(4, result1.length());
            assertEquals(6, result2.length());
            assertTrue(result1.matches("\\d{4}"));
            assertTrue(result2.matches("\\d{6}"));
        }

        @Test
        @DisplayName("UserAccount 엔티티를 Dto로 변환 테스트")
        void convertUserAccountToUserAccountDto_Success() {
            // when
            UserAccountDto result = userAccountService.convertUserAccountToUserAccountDto(validUserAccount);

            // then
            assertNotNull(result);
            assertEquals(validPhoneNumber, result.getPhoneNumber());
            assertEquals(validEmail, result.getEmail());
            assertEquals(validName, result.getName());
            assertEquals(validBirthDate, result.getBirthDate());
            assertEquals(validGender, result.getGender());
        }
    }
} 