package com.example.carebridge.service;

import com.example.carebridge.dto.GuardianDto;
import com.example.carebridge.entity.Guardian;
import com.example.carebridge.mapper.GuardianMapper;
import com.example.carebridge.repository.GuardianRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * GuardianService 클래스에 대한 단위 테스트
 * 보호자 정보 관리의 주요 기능을 검증합니다.
 */
@Tag("service")
@DisplayName("보호자 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class GuardianServiceTest {

    @Mock
    private GuardianRepository guardianRepository;

    @Mock
    private GuardianMapper guardianMapper;

    @InjectMocks
    private GuardianService guardianService;

    // 공통으로 사용되는 테스트 데이터
    private String validGuardianId;
    private Integer validPatientId;
    private String validName;
    private String validPhoneNumber;
    private Guardian validGuardian;
    private GuardianDto validGuardianDto;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        validGuardianId = "guard-123";
        validPatientId = 1;
        validName = "보호자";
        validPhoneNumber = "01012345678";

        // 유효한 보호자 엔티티 생성
        validGuardian = new Guardian();
        validGuardian.setGuardianId(validGuardianId);
        validGuardian.setPatientId(validPatientId);
        validGuardian.setName(validName);
        validGuardian.setPhoneNumber(validPhoneNumber);

        // 유효한 보호자 DTO 생성
        validGuardianDto = new GuardianDto();
        validGuardianDto.setGuardianId(validGuardianId);
        validGuardianDto.setPatientId(validPatientId);
        validGuardianDto.setName(validName);
        validGuardianDto.setPhoneNumber(validPhoneNumber);
    }

    @Nested
    @DisplayName("getGuardianInfo 메소드 테스트")
    class GetGuardianInfoTest {

        @Test
        @DisplayName("전화번호로 보호자 정보 조회 성공 테스트")
        void getGuardianInfo_Success() {
            // given
            when(guardianRepository.findByPhoneNumber(validPhoneNumber)).thenReturn(Optional.of(validGuardian));
            when(guardianMapper.toDto(validGuardian)).thenReturn(validGuardianDto);

            // when
            GuardianDto result = guardianService.getGuardianInfo(validPhoneNumber);

            // then
            assertNotNull(result);
            assertEquals(validGuardianId, result.getGuardianId());
            assertEquals(validPatientId, result.getPatientId());
            assertEquals(validName, result.getName());
            assertEquals(validPhoneNumber, result.getPhoneNumber());
            verify(guardianRepository, times(1)).findByPhoneNumber(validPhoneNumber);
            verify(guardianMapper, times(1)).toDto(validGuardian);
        }

        @Test
        @DisplayName("존재하지 않는 전화번호로 조회 시 예외 발생 테스트")
        void getGuardianInfo_NotFound() {
            // given
            when(guardianRepository.findByPhoneNumber(validPhoneNumber)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> guardianService.getGuardianInfo(validPhoneNumber));
            verify(guardianRepository, times(1)).findByPhoneNumber(validPhoneNumber);
            verify(guardianMapper, never()).toDto(any(Guardian.class));
        }

        @Test
        @DisplayName("null 또는 빈 전화번호로 조회 시 예외 발생 테스트")
        void getGuardianInfo_EmptyPhoneNumber() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> guardianService.getGuardianInfo(null));
            assertThrows(IllegalArgumentException.class, () -> guardianService.getGuardianInfo(""));
            assertThrows(IllegalArgumentException.class, () -> guardianService.getGuardianInfo("  "));
            verify(guardianRepository, never()).findByPhoneNumber(anyString());
            verify(guardianMapper, never()).toDto(any(Guardian.class));
        }
    }

    @Nested
    @DisplayName("getGuardianList 메소드 테스트")
    class GetGuardianListTest {

        @Test
        @DisplayName("환자 ID로 보호자 목록 조회 성공 테스트")
        void getGuardianList_Success() {
            // given
            List<Guardian> guardianList = new ArrayList<>();
            guardianList.add(validGuardian);

            // 두 번째 보호자 추가
            Guardian secondGuardian = new Guardian();
            secondGuardian.setGuardianId("guard-456");
            secondGuardian.setPatientId(validPatientId);
            secondGuardian.setName("두번째보호자");
            secondGuardian.setPhoneNumber("01098765432");
            guardianList.add(secondGuardian);

            GuardianDto secondGuardianDto = new GuardianDto();
            secondGuardianDto.setGuardianId("guard-456");
            secondGuardianDto.setPatientId(validPatientId);
            secondGuardianDto.setName("두번째보호자");
            secondGuardianDto.setPhoneNumber("01098765432");

            when(guardianRepository.findAllByPatientId(validPatientId)).thenReturn(Optional.of(guardianList));
            when(guardianMapper.toDto(validGuardian)).thenReturn(validGuardianDto);
            when(guardianMapper.toDto(secondGuardian)).thenReturn(secondGuardianDto);

            // when
            List<GuardianDto> result = guardianService.getGuardianList(validPatientId);

            // then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(validGuardianId, result.get(0).getGuardianId());
            assertEquals("guard-456", result.get(1).getGuardianId());
            verify(guardianRepository, times(1)).findAllByPatientId(validPatientId);
            verify(guardianMapper, times(2)).toDto(any(Guardian.class));
        }

        @Test
        @DisplayName("보호자가 없는 환자 ID로 조회 시 예외 발생 테스트")
        void getGuardianList_NotFound() {
            // given
            when(guardianRepository.findAllByPatientId(validPatientId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> guardianService.getGuardianList(validPatientId));
            verify(guardianRepository, times(1)).findAllByPatientId(validPatientId);
            verify(guardianMapper, never()).toDto(any(Guardian.class));
        }

        @Test
        @DisplayName("null 환자 ID로 조회 시 예외 발생 테스트")
        void getGuardianList_NullPatientId() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> guardianService.getGuardianList(null));
            verify(guardianRepository, never()).findAllByPatientId(anyInt());
            verify(guardianMapper, never()).toDto(any(Guardian.class));
        }
    }

    @Nested
    @DisplayName("addGuardian 메소드 테스트")
    class AddGuardianTest {

        @Test
        @DisplayName("보호자 추가 성공 테스트")
        void addGuardian_Success() {
            // given
            ArgumentCaptor<Guardian> guardianCaptor = ArgumentCaptor.forClass(Guardian.class);

            // when
            guardianService.addGuardian(validPatientId, validName, validPhoneNumber);

            // then
            verify(guardianRepository, times(1)).save(guardianCaptor.capture());
            
            Guardian savedGuardian = guardianCaptor.getValue();
            assertNotNull(savedGuardian);
            assertNotNull(savedGuardian.getGuardianId());
            assertEquals(validPatientId, savedGuardian.getPatientId());
            assertEquals(validName, savedGuardian.getName());
            assertEquals(validPhoneNumber, savedGuardian.getPhoneNumber());
        }

        @Test
        @DisplayName("null 또는 빈 이름으로 보호자 추가 시 예외 발생 테스트")
        void addGuardian_EmptyName() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> guardianService.addGuardian(validPatientId, null, validPhoneNumber));
            assertThrows(IllegalArgumentException.class, () -> guardianService.addGuardian(validPatientId, "", validPhoneNumber));
            assertThrows(IllegalArgumentException.class, () -> guardianService.addGuardian(validPatientId, "  ", validPhoneNumber));
            verify(guardianRepository, never()).save(any(Guardian.class));
        }

        @Test
        @DisplayName("null 또는 빈 전화번호로 보호자 추가 시 예외 발생 테스트")
        void addGuardian_EmptyPhoneNumber() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> guardianService.addGuardian(validPatientId, validName, null));
            assertThrows(IllegalArgumentException.class, () -> guardianService.addGuardian(validPatientId, validName, ""));
            assertThrows(IllegalArgumentException.class, () -> guardianService.addGuardian(validPatientId, validName, "  "));
            verify(guardianRepository, never()).save(any(Guardian.class));
        }
    }

    @Nested
    @DisplayName("deleteGuardian 메소드 테스트")
    class DeleteGuardianTest {

        @Test
        @DisplayName("보호자 삭제 성공 테스트")
        void deleteGuardian_Success() {
            // given
            when(guardianRepository.findByPhoneNumber(validPhoneNumber)).thenReturn(Optional.of(validGuardian));

            // when
            guardianService.deleteGuardian(validPhoneNumber);

            // then
            verify(guardianRepository, times(1)).findByPhoneNumber(validPhoneNumber);
            verify(guardianRepository, times(1)).deleteByPhoneNumber(validPhoneNumber);
        }

        @Test
        @DisplayName("존재하지 않는 전화번호로 보호자 삭제 시 예외 발생 테스트")
        void deleteGuardian_NotFound() {
            // given
            when(guardianRepository.findByPhoneNumber(validPhoneNumber)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> guardianService.deleteGuardian(validPhoneNumber));
            verify(guardianRepository, times(1)).findByPhoneNumber(validPhoneNumber);
            verify(guardianRepository, never()).deleteByPhoneNumber(anyString());
        }

        @Test
        @DisplayName("null 또는 빈 전화번호로 보호자 삭제 시 예외 발생 테스트")
        void deleteGuardian_EmptyPhoneNumber() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> guardianService.deleteGuardian(null));
            assertThrows(IllegalArgumentException.class, () -> guardianService.deleteGuardian(""));
            assertThrows(IllegalArgumentException.class, () -> guardianService.deleteGuardian("  "));
            verify(guardianRepository, never()).findByPhoneNumber(anyString());
            verify(guardianRepository, never()).deleteByPhoneNumber(anyString());
        }
    }
} 