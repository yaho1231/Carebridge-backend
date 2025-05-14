package com.example.carebridge.service;

import com.example.carebridge.entity.MedicalRecord;
import com.example.carebridge.repository.MedicalRecordRepository;
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
import static org.mockito.Mockito.*;

/**
 * MedicalRecordService 클래스에 대한 단위 테스트
 * 의료 기록 서비스의 주요 기능을 검증합니다.
 */
@Tag("service")
@DisplayName("의료 기록 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    // 공통으로 사용되는 테스트 데이터
    private Integer validPatientId;
    private Integer validHospitalId;
    private String validDiseaseInfo;
    private MedicalRecord validMedicalRecord;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        validPatientId = 1;
        validHospitalId = 1;
        validDiseaseInfo = "고혈압, 당뇨";

        // 유효한 의료 기록 생성
        validMedicalRecord = MedicalRecord.builder()
                .patientId(validPatientId)
                .hospitalId(validHospitalId)
                .diseaseInfo(validDiseaseInfo)
                .build();
    }

    @Nested
    @DisplayName("getDiseaseInfo 메소드 테스트")
    class GetDiseaseInfoTest {

        @Test
        @DisplayName("환자 질병 정보 조회 성공 테스트")
        void getDiseaseInfo_Success() {
            // given
            when(medicalRecordRepository.findByPatientId(validPatientId)).thenReturn(Optional.of(validMedicalRecord));

            // when
            String result = medicalRecordService.getDiseaseInfo(validPatientId);

            // then
            assertNotNull(result);
            assertEquals(validDiseaseInfo, result);
            verify(medicalRecordRepository, times(1)).findByPatientId(validPatientId);
        }

        @Test
        @DisplayName("존재하지 않는 환자 ID로 조회 시 예외 발생 테스트")
        void getDiseaseInfo_PatientNotFound() {
            // given
            when(medicalRecordRepository.findByPatientId(validPatientId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> medicalRecordService.getDiseaseInfo(validPatientId));
            verify(medicalRecordRepository, times(1)).findByPatientId(validPatientId);
        }

        @Test
        @DisplayName("환자 ID가 null인 경우 예외 발생 테스트")
        void getDiseaseInfo_NullPatientId() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> medicalRecordService.getDiseaseInfo(null));
            verify(medicalRecordRepository, never()).findByPatientId(any());
        }

        @Test
        @DisplayName("환자 ID가 0 이하인 경우 예외 발생 테스트")
        void getDiseaseInfo_InvalidPatientId() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> medicalRecordService.getDiseaseInfo(0));
            assertThrows(IllegalArgumentException.class, () -> medicalRecordService.getDiseaseInfo(-1));
            verify(medicalRecordRepository, never()).findByPatientId(any());
        }

        @Test
        @DisplayName("질병 정보가 빈 문자열인 경우 예외 발생 테스트")
        void getDiseaseInfo_EmptyDiseaseInfo() {
            // given
            MedicalRecord emptyRecord = MedicalRecord.builder()
                    .patientId(validPatientId)
                    .hospitalId(validHospitalId)
                    .diseaseInfo("")
                    .build();
            when(medicalRecordRepository.findByPatientId(validPatientId)).thenReturn(Optional.of(emptyRecord));

            // when & then
            assertThrows(NoSuchElementException.class, () -> medicalRecordService.getDiseaseInfo(validPatientId));
            verify(medicalRecordRepository, times(1)).findByPatientId(validPatientId);
        }

        @Test
        @DisplayName("질병 정보가 null인 경우 예외 발생 테스트")
        void getDiseaseInfo_NullDiseaseInfo() {
            // given
            MedicalRecord nullRecord = MedicalRecord.builder()
                    .patientId(validPatientId)
                    .hospitalId(validHospitalId)
                    .diseaseInfo(null)
                    .build();
            when(medicalRecordRepository.findByPatientId(validPatientId)).thenReturn(Optional.of(nullRecord));

            // when & then
            assertThrows(NoSuchElementException.class, () -> medicalRecordService.getDiseaseInfo(validPatientId));
            verify(medicalRecordRepository, times(1)).findByPatientId(validPatientId);
        }
    }
} 