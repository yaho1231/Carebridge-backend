package com.example.carebridge.service;

import com.example.carebridge.dto.StaffAccountDto;
import com.example.carebridge.entity.StaffAccount;
import com.example.carebridge.repository.StaffAccountRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * StaffAccountService 클래스에 대한 단위 테스트
 * 의료진 계정 관리 서비스의 주요 기능을 검증합니다.
 */
@Tag("service")
@DisplayName("의료진 계정 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class StaffAccountServiceTest {

    @Mock
    private StaffAccountRepository staffAccountRepository;

    @InjectMocks
    private StaffAccountService staffAccountService;

    // 테스트 데이터
    private StaffAccount validStaffAccount;
    private StaffAccountDto validStaffAccountDto;
    private final String validUserId = "testUser";
    private final String validPassword = "testPassword";
    private final int validHospitalId = 1;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        validStaffAccount = new StaffAccount();
        validStaffAccount.setId(1L);
        validStaffAccount.setUserId(validUserId);
        validStaffAccount.setPassword(validPassword);
        validStaffAccount.setHospitalId(validHospitalId);

        validStaffAccountDto = new StaffAccountDto();
        validStaffAccountDto.setUserId(validUserId);
        validStaffAccountDto.setPassword(validPassword);
    }

    @Nested
    @DisplayName("verifyStaffAccount 메소드 테스트")
    class VerifyStaffAccountTest {

        @Test
        @DisplayName("유효한 계정 검증 성공 테스트")
        void verifyStaffAccount_ValidAccount_ReturnsTrue() {
            // given
            when(staffAccountRepository.getStaffAccountByUserId(validUserId)).thenReturn(Optional.of(validStaffAccount));

            // when
            Boolean result = staffAccountService.verifyStaffAccount(validStaffAccountDto);

            // then
            assertTrue(result);
            verify(staffAccountRepository, times(1)).getStaffAccountByUserId(validUserId);
        }

        @Test
        @DisplayName("계정 정보가 없을 때 예외 발생 테스트")
        void verifyStaffAccount_AccountNotFound_ThrowsException() {
            // given
            when(staffAccountRepository.getStaffAccountByUserId(validUserId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> 
                staffAccountService.verifyStaffAccount(validStaffAccountDto));
            
            verify(staffAccountRepository, times(1)).getStaffAccountByUserId(validUserId);
        }

        @Test
        @DisplayName("비밀번호 불일치 시 false 반환 테스트")
        void verifyStaffAccount_InvalidPassword_ReturnsFalse() {
            // given
            StaffAccountDto invalidDto = new StaffAccountDto();
            invalidDto.setUserId(validUserId);
            invalidDto.setPassword("wrongPassword");
            
            when(staffAccountRepository.getStaffAccountByUserId(validUserId)).thenReturn(Optional.of(validStaffAccount));

            // when
            Boolean result = staffAccountService.verifyStaffAccount(invalidDto);

            // then
            assertFalse(result);
            verify(staffAccountRepository, times(1)).getStaffAccountByUserId(validUserId);
        }

        @Test
        @DisplayName("계정 정보가 null일 때 예외 발생 테스트")
        void verifyStaffAccount_NullAccount_ThrowsException() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> 
                staffAccountService.verifyStaffAccount(null));
            
            verify(staffAccountRepository, never()).getStaffAccountByUserId(anyString());
        }
    }

    @Nested
    @DisplayName("findPassword 메소드 테스트")
    class FindPasswordTest {

        @Test
        @DisplayName("비밀번호 조회 성공 테스트")
        void findPassword_Success() {
            // given
            when(staffAccountRepository.getStaffAccountByUserId(validUserId)).thenReturn(Optional.of(validStaffAccount));

            // when
            String result = staffAccountService.findPassword(validUserId);

            // then
            assertEquals(validPassword, result);
            verify(staffAccountRepository, times(1)).getStaffAccountByUserId(validUserId);
        }

        @Test
        @DisplayName("계정을 찾을 수 없을 때 예외 발생 테스트")
        void findPassword_AccountNotFound_ThrowsException() {
            // given
            when(staffAccountRepository.getStaffAccountByUserId(validUserId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> 
                staffAccountService.findPassword(validUserId));
            
            verify(staffAccountRepository, times(1)).getStaffAccountByUserId(validUserId);
        }

        @Test
        @DisplayName("유저 ID가 null일 때 예외 발생 테스트")
        void findPassword_NullUserId_ThrowsException() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> 
                staffAccountService.findPassword(null));
            
            verify(staffAccountRepository, never()).getStaffAccountByUserId(anyString());
        }
    }

    @Nested
    @DisplayName("resetPassword 메소드 테스트")
    class ResetPasswordTest {

        @Test
        @DisplayName("비밀번호 초기화 성공 테스트")
        void resetPassword_Success() {
            // given
            String newPassword = "newPassword";
            when(staffAccountRepository.getStaffAccountByUserId(validUserId)).thenReturn(Optional.of(validStaffAccount));

            // when
            assertDoesNotThrow(() -> 
                staffAccountService.resetPassword(validStaffAccountDto, newPassword));

            // then
            assertEquals(newPassword, validStaffAccount.getPassword());
            verify(staffAccountRepository, times(1)).getStaffAccountByUserId(validUserId);
            verify(staffAccountRepository, times(1)).save(validStaffAccount);
        }

        @Test
        @DisplayName("계정을 찾을 수 없을 때 예외 발생 테스트")
        void resetPassword_AccountNotFound_ThrowsException() {
            // given
            String newPassword = "newPassword";
            when(staffAccountRepository.getStaffAccountByUserId(validUserId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> 
                staffAccountService.resetPassword(validStaffAccountDto, newPassword));
            
            verify(staffAccountRepository, times(1)).getStaffAccountByUserId(validUserId);
            verify(staffAccountRepository, never()).save(any());
        }

        @Test
        @DisplayName("기존 비밀번호가 일치하지 않을 때 예외 발생 테스트")
        void resetPassword_WrongOldPassword_ThrowsException() {
            // given
            String newPassword = "newPassword";
            StaffAccountDto wrongDto = new StaffAccountDto();
            wrongDto.setUserId(validUserId);
            wrongDto.setPassword("wrongPassword");
            
            when(staffAccountRepository.getStaffAccountByUserId(validUserId)).thenReturn(Optional.of(validStaffAccount));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> 
                staffAccountService.resetPassword(wrongDto, newPassword));
            
            verify(staffAccountRepository, times(1)).getStaffAccountByUserId(validUserId);
            verify(staffAccountRepository, never()).save(any());
        }

        @Test
        @DisplayName("새 비밀번호가 현재 비밀번호와 같을 때 예외 발생 테스트")
        void resetPassword_SamePassword_ThrowsException() {
            // given
            when(staffAccountRepository.getStaffAccountByUserId(validUserId)).thenReturn(Optional.of(validStaffAccount));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> 
                staffAccountService.resetPassword(validStaffAccountDto, validPassword));
            
            verify(staffAccountRepository, times(1)).getStaffAccountByUserId(validUserId);
            verify(staffAccountRepository, never()).save(any());
        }

        @Test
        @DisplayName("계정 정보나 새 비밀번호가 null일 때 예외 발생 테스트")
        void resetPassword_NullInput_ThrowsException() {
            // 1. null 계정 정보
            assertThrows(IllegalArgumentException.class, () -> 
                staffAccountService.resetPassword(null, "newPassword"));
            
            // 2. null 비밀번호
            assertThrows(IllegalArgumentException.class, () -> 
                staffAccountService.resetPassword(validStaffAccountDto, null));
            
            verify(staffAccountRepository, never()).getStaffAccountByUserId(anyString());
            verify(staffAccountRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("findStaffAccountByUserId 메소드 테스트")
    class FindStaffAccountByUserIdTest {

        @Test
        @DisplayName("유저 ID로 계정 조회 성공 테스트")
        void findStaffAccountByUserId_Success() {
            // given
            when(staffAccountRepository.getStaffAccountByUserId(validUserId)).thenReturn(Optional.of(validStaffAccount));

            // when
            StaffAccount result = staffAccountService.findStaffAccountByUserId(validUserId);

            // then
            assertNotNull(result);
            assertEquals(validUserId, result.getUserId());
            assertEquals(validPassword, result.getPassword());
            assertEquals(validHospitalId, result.getHospitalId());
            
            verify(staffAccountRepository, times(1)).getStaffAccountByUserId(validUserId);
        }

        @Test
        @DisplayName("계정을 찾을 수 없을 때 예외 발생 테스트")
        void findStaffAccountByUserId_NotFound_ThrowsException() {
            // given
            when(staffAccountRepository.getStaffAccountByUserId(validUserId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> 
                staffAccountService.findStaffAccountByUserId(validUserId));
            
            verify(staffAccountRepository, times(1)).getStaffAccountByUserId(validUserId);
        }

        @Test
        @DisplayName("유저 ID가 null일 때 예외 발생 테스트")
        void findStaffAccountByUserId_NullId_ThrowsException() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> 
                staffAccountService.findStaffAccountByUserId(null));
            
            verify(staffAccountRepository, never()).getStaffAccountByUserId(anyString());
        }
    }

    @Nested
    @DisplayName("convertStaffAccountToStaffAccountDto 메소드 테스트")
    class ConvertStaffAccountToStaffAccountDtoTest {

        @Test
        @DisplayName("StaffAccount를 DTO로 변환 성공 테스트")
        void convertStaffAccountToStaffAccountDto_Success() {
            // when
            StaffAccountDto result = staffAccountService.convertStaffAccountToStaffAccountDto(validStaffAccount);

            // then
            assertNotNull(result);
            assertEquals(validUserId, result.getUserId());
            assertEquals(validPassword, result.getPassword());
        }
    }
} 