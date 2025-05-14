package com.example.carebridge.service;

import com.example.carebridge.entity.Hospital;
import com.example.carebridge.repository.HospitalRepository;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * HospitalService 클래스에 대한 단위 테스트
 * 병원 정보 관리의 주요 기능을 검증합니다.
 */
@Tag("service")
@DisplayName("병원 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class HospitalServiceTest {

    @Mock
    private HospitalRepository hospitalRepository;

    @InjectMocks
    private HospitalService hospitalService;

    // 공통으로 사용되는 테스트 데이터
    private Integer validHospitalId;
    private String validHospitalName;
    private String validLocation;
    private Hospital validHospital;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        validHospitalId = 1;
        validHospitalName = "테스트 병원";
        validLocation = "서울시 강남구 테스트로 123";

        // 유효한 병원 엔티티 생성
        validHospital = Hospital.builder()
                .hospitalId(validHospitalId)
                .name(validHospitalName)
                .location(validLocation)
                .build();
    }

    @Nested
    @DisplayName("getHospitalName 메소드 테스트")
    class GetHospitalNameTest {

        @Test
        @DisplayName("병원 ID로 병원 이름 조회 성공 테스트")
        void getHospitalName_Success() {
            // given
            when(hospitalRepository.findByHospitalId(validHospitalId)).thenReturn(Optional.of(validHospital));

            // when
            String result = hospitalService.getHospitalName(validHospitalId);

            // then
            assertEquals(validHospitalName, result);
            verify(hospitalRepository, times(1)).findByHospitalId(validHospitalId);
        }

        @Test
        @DisplayName("존재하지 않는 병원 ID로 조회 시 예외 발생 테스트")
        void getHospitalName_HospitalNotFound() {
            // given
            when(hospitalRepository.findByHospitalId(validHospitalId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> hospitalService.getHospitalName(validHospitalId));
            verify(hospitalRepository, times(1)).findByHospitalId(validHospitalId);
        }

        @Test
        @DisplayName("null 병원 ID로 조회 시 예외 발생 테스트")
        void getHospitalName_NullHospitalId() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> hospitalService.getHospitalName(null));
            verify(hospitalRepository, never()).findByHospitalId(anyInt());
        }
    }

    @Nested
    @DisplayName("isHospitalExist 메소드 테스트")
    class IsHospitalExistTest {

        @Test
        @DisplayName("존재하는 병원 확인 테스트")
        void isHospitalExist_Exists() {
            // given
            when(hospitalRepository.findByHospitalId(validHospitalId)).thenReturn(Optional.of(validHospital));

            // when
            boolean result = hospitalService.isHospitalExist(validHospitalId);

            // then
            assertTrue(result);
            verify(hospitalRepository, times(1)).findByHospitalId(validHospitalId);
        }

        @Test
        @DisplayName("존재하지 않는 병원 확인 테스트")
        void isHospitalExist_NotExists() {
            // given
            when(hospitalRepository.findByHospitalId(validHospitalId)).thenReturn(Optional.empty());

            // when
            boolean result = hospitalService.isHospitalExist(validHospitalId);

            // then
            assertFalse(result);
            verify(hospitalRepository, times(1)).findByHospitalId(validHospitalId);
        }

        @Test
        @DisplayName("null 병원 ID로 확인 시 예외 발생 테스트")
        void isHospitalExist_NullHospitalId() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> hospitalService.isHospitalExist(null));
            verify(hospitalRepository, never()).findByHospitalId(anyInt());
        }
    }

    @Nested
    @DisplayName("getHospitalSafely 메소드 테스트")
    class GetHospitalSafelyTest {

        @Test
        @DisplayName("병원 ID로 병원 조회 성공 테스트")
        void getHospitalSafely_Success() {
            // given
            when(hospitalRepository.findByHospitalId(validHospitalId)).thenReturn(Optional.of(validHospital));

            // when
            Hospital result = hospitalService.getHospitalSafely(validHospitalId);

            // then
            assertNotNull(result);
            assertEquals(validHospitalId, result.getHospitalId());
            assertEquals(validHospitalName, result.getName());
            assertEquals(validLocation, result.getLocation());
            verify(hospitalRepository, times(1)).findByHospitalId(validHospitalId);
        }

        @Test
        @DisplayName("존재하지 않는 병원 ID로 조회 시 예외 발생 테스트")
        void getHospitalSafely_HospitalNotFound() {
            // given
            when(hospitalRepository.findByHospitalId(validHospitalId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> hospitalService.getHospitalSafely(validHospitalId));
            verify(hospitalRepository, times(1)).findByHospitalId(validHospitalId);
        }

        @Test
        @DisplayName("null 병원 ID로 조회 시 예외 발생 테스트")
        void getHospitalSafely_NullHospitalId() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> hospitalService.getHospitalSafely(null));
            verify(hospitalRepository, never()).findByHospitalId(anyInt());
        }
    }
} 