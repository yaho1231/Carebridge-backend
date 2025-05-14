package com.example.carebridge.service;

import com.example.carebridge.entity.MedicalStaff;
import com.example.carebridge.repository.MedicalStaffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * MedicalStaffService 클래스에 대한 단위 테스트
 * 의료진 정보 관리의 주요 기능을 검증합니다.
 */
@Tag("service")
@DisplayName("의료진 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class MedicalStaffServiceTest {

    @Mock
    private MedicalStaffRepository medicalStaffRepository;

    @InjectMocks
    private MedicalStaffService medicalStaffService;

    // 공통으로 사용되는 테스트 데이터
    private String validDepartment;
    private Integer validHospitalId;
    private String validPhraseHead;
    private String validPhraseTail;
    private MedicalStaff validMedicalStaff;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        validDepartment = "내과";
        validHospitalId = 1;
        validPhraseHead = "안녕하세요";
        validPhraseTail = "감사합니다";

        // 유효한 의료진 엔티티 생성
        validMedicalStaff = MedicalStaff.builder()
                .department(validDepartment)
                .hospitalId(validHospitalId)
                .phraseHead(validPhraseHead)
                .phraseTail(validPhraseTail)
                .build();
        
        // ID 설정을 위한 리플렉션이 필요하지만 테스트에서는 모킹된 값을 사용하므로 생략
    }

    @Nested
    @DisplayName("findAllByDepartment 메소드 테스트")
    class FindAllByDepartmentTest {

        @Test
        @DisplayName("부서명으로 의료진 조회 성공 테스트")
        void findAllByDepartment_Success() {
            // given
            when(medicalStaffRepository.findByDepartment(validDepartment)).thenReturn(Optional.of(validMedicalStaff));

            // when
            MedicalStaff result = medicalStaffService.findAllByDepartment(validDepartment);

            // then
            assertNotNull(result);
            assertEquals(validDepartment, result.getDepartment());
            assertEquals(validHospitalId, result.getHospitalId());
            verify(medicalStaffRepository, times(1)).findByDepartment(validDepartment);
        }

        @Test
        @DisplayName("존재하지 않는 부서명으로 조회 시 예외 발생 테스트")
        void findAllByDepartment_NotFound() {
            // given
            when(medicalStaffRepository.findByDepartment(validDepartment)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> medicalStaffService.findAllByDepartment(validDepartment));
            verify(medicalStaffRepository, times(1)).findByDepartment(validDepartment);
        }

        @Test
        @DisplayName("null 또는 빈 부서명으로 조회 시 예외 발생 테스트")
        void findAllByDepartment_EmptyDepartment() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> medicalStaffService.findAllByDepartment(null));
            assertThrows(IllegalArgumentException.class, () -> medicalStaffService.findAllByDepartment(""));
            assertThrows(IllegalArgumentException.class, () -> medicalStaffService.findAllByDepartment("  "));
            verify(medicalStaffRepository, never()).findByDepartment(anyString());
        }
    }

    @Nested
    @DisplayName("findAllByHospitalId 메소드 테스트")
    class FindAllByHospitalIdTest {

        @Test
        @DisplayName("병원 ID로 의료진 조회 성공 테스트")
        void findAllByHospitalId_Success() {
            // given
            List<MedicalStaff> medicalStaffList = new ArrayList<>();
            medicalStaffList.add(validMedicalStaff);

            // 다른 의료진 추가
            MedicalStaff anotherStaff = MedicalStaff.builder()
                    .department("외과")
                    .hospitalId(validHospitalId)
                    .phraseHead("안녕하세요")
                    .phraseTail("외과입니다")
                    .build();
            medicalStaffList.add(anotherStaff);

            when(medicalStaffRepository.findByHospitalId(validHospitalId)).thenReturn(Optional.of(medicalStaffList));

            // when
            List<MedicalStaff> result = medicalStaffService.findAllByHospitalId(validHospitalId);

            // then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(validDepartment, result.get(0).getDepartment());
            assertEquals(validHospitalId, result.get(0).getHospitalId());
            assertEquals("외과", result.get(1).getDepartment());
            verify(medicalStaffRepository, times(1)).findByHospitalId(validHospitalId);
        }

        @Test
        @DisplayName("존재하지 않는 병원 ID로 조회 시 예외 발생 테스트")
        void findAllByHospitalId_NotFound() {
            // given
            when(medicalStaffRepository.findByHospitalId(validHospitalId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> medicalStaffService.findAllByHospitalId(validHospitalId));
            verify(medicalStaffRepository, times(1)).findByHospitalId(validHospitalId);
        }

        @Test
        @DisplayName("null 병원 ID로 조회 시 예외 발생 테스트")
        void findAllByHospitalId_NullHospitalId() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> medicalStaffService.findAllByHospitalId(null));
            verify(medicalStaffRepository, never()).findByHospitalId(anyInt());
        }
    }
} 