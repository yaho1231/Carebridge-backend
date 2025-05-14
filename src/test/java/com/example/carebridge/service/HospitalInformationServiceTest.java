package com.example.carebridge.service;

import com.example.carebridge.dto.HospitalInformationDto;
import com.example.carebridge.entity.Hospital;
import com.example.carebridge.entity.HospitalInformation;
import com.example.carebridge.repository.HospitalInformationRepository;
import com.example.carebridge.repository.HospitalRepository;
import com.example.carebridge.mapper.HospitalInformationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * HospitalInformationService 클래스에 대한 단위 테스트
 * 병원 정보 관리 서비스의 주요 기능을 검증합니다.
 */
@Tag("service")
@DisplayName("병원 정보 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class HospitalInformationServiceTest {

    @Mock
    private HospitalInformationRepository hospitalInformationRepository;

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private HospitalInformationMapper hospitalInformationMapper;

    @InjectMocks
    private HospitalInformationService hospitalInformationService;

    // 테스트 데이터
    private Hospital testHospital;
    private HospitalInformation testHospitalInfo1;
    private HospitalInformation testHospitalInfo2;
    private HospitalInformationDto testHospitalInfoDto;
    private final int testHospitalId = 1;
    private final String testTitle = "테스트 제목";
    private final String testInformation = "테스트 정보 내용입니다.";

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        testHospital = Hospital.builder()
                .hospitalId(testHospitalId)
                .name("테스트 병원")
                .location("테스트 위치")
                .build();

        testHospitalInfo1 = new HospitalInformation();
        testHospitalInfo1.setId(1);
        testHospitalInfo1.setHospital(testHospital);
        testHospitalInfo1.setTitle(testTitle);
        testHospitalInfo1.setInformation(testInformation);

        testHospitalInfo2 = new HospitalInformation();
        testHospitalInfo2.setId(2);
        testHospitalInfo2.setHospital(testHospital);
        testHospitalInfo2.setTitle("다른 제목");
        testHospitalInfo2.setInformation("다른 정보 내용입니다.");

        testHospitalInfoDto = new HospitalInformationDto();
        testHospitalInfoDto.setId(1);
        testHospitalInfoDto.setHospitalId(testHospitalId);
        testHospitalInfoDto.setTitle(testTitle);
        testHospitalInfoDto.setInformation(testInformation);
    }

    @Nested
    @DisplayName("getHospitalInformationList 메소드 테스트")
    class GetHospitalInformationListTest {

        @Test
        @DisplayName("병원 정보 목록 조회 성공 테스트")
        void getHospitalInformationList_Success() {
            // given
            List<HospitalInformation> mockInfoList = Arrays.asList(testHospitalInfo1, testHospitalInfo2);
            when(hospitalInformationRepository.findAllByHospitalId(testHospitalId))
                    .thenReturn(Optional.of(mockInfoList));
            when(hospitalInformationMapper.toDto(testHospitalInfo1)).thenReturn(testHospitalInfoDto);
            
            HospitalInformationDto dto2 = new HospitalInformationDto();
            dto2.setId(2);
            dto2.setHospitalId(testHospitalId);
            dto2.setTitle("다른 제목");
            dto2.setInformation("다른 정보 내용입니다.");
            when(hospitalInformationMapper.toDto(testHospitalInfo2)).thenReturn(dto2);

            // when
            List<HospitalInformationDto> result = hospitalInformationService.getHospitalInformationList(testHospitalId);

            // then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(testTitle, result.get(0).getTitle());
            assertEquals("다른 제목", result.get(1).getTitle());
            
            verify(hospitalInformationRepository, times(1)).findAllByHospitalId(testHospitalId);
            verify(hospitalInformationMapper, times(2)).toDto(any(HospitalInformation.class));
        }

        @Test
        @DisplayName("병원 정보가 없을 때 예외 발생 테스트")
        void getHospitalInformationList_NoInformation_ThrowsException() {
            // given
            when(hospitalInformationRepository.findAllByHospitalId(testHospitalId))
                    .thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> 
                hospitalInformationService.getHospitalInformationList(testHospitalId));
            
            verify(hospitalInformationRepository, times(1)).findAllByHospitalId(testHospitalId);
            verify(hospitalInformationMapper, never()).toDto(any(HospitalInformation.class));
        }
    }

    @Nested
    @DisplayName("getHospitalInformation 메소드 테스트")
    class GetHospitalInformationTest {

        @Test
        @DisplayName("특정 제목의 병원 정보 조회 성공 테스트")
        void getHospitalInformation_Success() {
            // given
            when(hospitalInformationRepository.findByHospitalIdAndTitle(testHospitalId, testTitle))
                    .thenReturn(Optional.of(testHospitalInfo1));

            // when
            String result = hospitalInformationService.getHospitalInformation(testHospitalId, testTitle);

            // then
            assertNotNull(result);
            assertEquals(testInformation, result);
            
            verify(hospitalInformationRepository, times(1)).findByHospitalIdAndTitle(testHospitalId, testTitle);
        }

        @Test
        @DisplayName("제목이 null인 경우 예외 발생 테스트")
        void getHospitalInformation_NullTitle_ThrowsException() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> 
                hospitalInformationService.getHospitalInformation(testHospitalId, null));
            
            verify(hospitalInformationRepository, never()).findByHospitalIdAndTitle(anyInt(), anyString());
        }

        @Test
        @DisplayName("정보를 찾을 수 없는 경우 예외 발생 테스트")
        void getHospitalInformation_NotFound_ThrowsException() {
            // given
            when(hospitalInformationRepository.findByHospitalIdAndTitle(testHospitalId, testTitle))
                    .thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> 
                hospitalInformationService.getHospitalInformation(testHospitalId, testTitle));
            
            verify(hospitalInformationRepository, times(1)).findByHospitalIdAndTitle(testHospitalId, testTitle);
        }
    }

    @Nested
    @DisplayName("addHospitalInformation 메소드 테스트")
    class AddHospitalInformationTest {

        @Test
        @DisplayName("병원 정보 추가 성공 테스트")
        void addHospitalInformation_Success() {
            // given
            when(hospitalRepository.findByHospitalId(testHospitalId)).thenReturn(Optional.of(testHospital));
            when(hospitalInformationMapper.toEntity(testHospitalInfoDto)).thenReturn(testHospitalInfo1);

            // when
            assertDoesNotThrow(() -> hospitalInformationService.addHospitalInformation(testHospitalInfoDto));

            // then
            verify(hospitalRepository, times(1)).findByHospitalId(testHospitalId);
            verify(hospitalInformationMapper, times(1)).toEntity(testHospitalInfoDto);
            verify(hospitalInformationRepository, times(1)).save(testHospitalInfo1);
        }

        @Test
        @DisplayName("병원 정보 DTO가 null인 경우 예외 발생 테스트")
        void addHospitalInformation_NullDto_ThrowsException() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> 
                hospitalInformationService.addHospitalInformation(null));
            
            verify(hospitalRepository, never()).findByHospitalId(anyInt());
            verify(hospitalInformationMapper, never()).toEntity(any());
            verify(hospitalInformationRepository, never()).save(any());
        }

        @Test
        @DisplayName("병원을 찾을 수 없는 경우 예외 발생 테스트")
        void addHospitalInformation_HospitalNotFound_ThrowsException() {
            // given
            when(hospitalRepository.findByHospitalId(testHospitalId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> 
                hospitalInformationService.addHospitalInformation(testHospitalInfoDto));
            
            verify(hospitalRepository, times(1)).findByHospitalId(testHospitalId);
            verify(hospitalInformationMapper, never()).toEntity(any());
            verify(hospitalInformationRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateHospitalInformation 메소드 테스트")
    class UpdateHospitalInformationTest {

        @Test
        @DisplayName("병원 정보 업데이트 성공 테스트")
        void updateHospitalInformation_Success() {
            // given
            when(hospitalInformationRepository.findByHospitalIdAndId(testHospitalId, 1))
                    .thenReturn(Optional.of(testHospitalInfo1));

            // when
            assertDoesNotThrow(() -> 
                hospitalInformationService.updateHospitalInformation(testHospitalId, 1, "새 제목", "새 정보"));

            // then
            assertEquals("새 제목", testHospitalInfo1.getTitle());
            assertEquals("새 정보", testHospitalInfo1.getInformation());
            
            verify(hospitalInformationRepository, times(1)).findByHospitalIdAndId(testHospitalId, 1);
            verify(hospitalInformationRepository, times(1)).save(testHospitalInfo1);
        }

        @Test
        @DisplayName("제목이 null인 경우 예외 발생 테스트")
        void updateHospitalInformation_NullTitle_ThrowsException() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> 
                hospitalInformationService.updateHospitalInformation(testHospitalId, 1, null, "새 정보"));
            
            verify(hospitalInformationRepository, never()).findByHospitalIdAndId(anyInt(), anyInt());
            verify(hospitalInformationRepository, never()).save(any());
        }

        @Test
        @DisplayName("정보 내용이 null인 경우 예외 발생 테스트")
        void updateHospitalInformation_NullInformation_ThrowsException() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> 
                hospitalInformationService.updateHospitalInformation(testHospitalId, 1, "새 제목", null));
            
            verify(hospitalInformationRepository, never()).findByHospitalIdAndId(anyInt(), anyInt());
            verify(hospitalInformationRepository, never()).save(any());
        }

        @Test
        @DisplayName("병원 정보를 찾을 수 없는 경우 예외 발생 테스트")
        void updateHospitalInformation_NotFound_ThrowsException() {
            // given
            when(hospitalInformationRepository.findByHospitalIdAndId(testHospitalId, 1))
                    .thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> 
                hospitalInformationService.updateHospitalInformation(testHospitalId, 1, "새 제목", "새 정보"));
            
            verify(hospitalInformationRepository, times(1)).findByHospitalIdAndId(testHospitalId, 1);
            verify(hospitalInformationRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteHospitalInformation 메소드 테스트")
    class DeleteHospitalInformationTest {

        @Test
        @DisplayName("병원 정보 삭제 성공 테스트")
        void deleteHospitalInformation_Success() {
            // given
            when(hospitalInformationRepository.findByHospitalIdAndTitle(testHospitalId, testTitle))
                    .thenReturn(Optional.of(testHospitalInfo1));

            // when
            assertDoesNotThrow(() -> 
                hospitalInformationService.deleteHospitalInformation(testHospitalId, testTitle));

            // then
            verify(hospitalInformationRepository, times(1)).findByHospitalIdAndTitle(testHospitalId, testTitle);
            verify(hospitalInformationRepository, times(1)).delete(testHospitalInfo1);
        }

        @Test
        @DisplayName("제목이 null인 경우 예외 발생 테스트")
        void deleteHospitalInformation_NullTitle_ThrowsException() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> 
                hospitalInformationService.deleteHospitalInformation(testHospitalId, null));
            
            verify(hospitalInformationRepository, never()).findByHospitalIdAndTitle(anyInt(), anyString());
            verify(hospitalInformationRepository, never()).delete(any());
        }

        @Test
        @DisplayName("병원 정보를 찾을 수 없는 경우 예외 발생 테스트")
        void deleteHospitalInformation_NotFound_ThrowsException() {
            // given
            when(hospitalInformationRepository.findByHospitalIdAndTitle(testHospitalId, testTitle))
                    .thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> 
                hospitalInformationService.deleteHospitalInformation(testHospitalId, testTitle));
            
            verify(hospitalInformationRepository, times(1)).findByHospitalIdAndTitle(testHospitalId, testTitle);
            verify(hospitalInformationRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("findMostSimilarHospitalInformation 메소드 테스트")
    class FindMostSimilarHospitalInformationTest {

        @Test
        @DisplayName("유사 정보 검색 성공 테스트")
        void findMostSimilarHospitalInformation_Success() {
            // given
            List<HospitalInformation> mockInfoList = Arrays.asList(testHospitalInfo1, testHospitalInfo2);
            when(hospitalInformationRepository.findAllByHospitalId(testHospitalId))
                    .thenReturn(Optional.of(mockInfoList));

            // when
            HospitalInformation result = hospitalInformationService
                    .findMostSimilarHospitalInformation("정보 내용", testHospitalId);

            // then
            assertNotNull(result);
            assertEquals(testHospitalInfo1.getId(), result.getId());
            
            verify(hospitalInformationRepository, times(1)).findAllByHospitalId(testHospitalId);
        }

        @Test
        @DisplayName("프롬프트가 null인 경우 예외 발생 테스트")
        void findMostSimilarHospitalInformation_NullPrompt_ThrowsException() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> 
                hospitalInformationService.findMostSimilarHospitalInformation(null, testHospitalId));
            
            verify(hospitalInformationRepository, never()).findAllByHospitalId(anyInt());
        }

        @Test
        @DisplayName("병원 정보가 없는 경우 예외 발생 테스트")
        void findMostSimilarHospitalInformation_NoInformation_ThrowsException() {
            // given
            when(hospitalInformationRepository.findAllByHospitalId(testHospitalId))
                    .thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> 
                hospitalInformationService.findMostSimilarHospitalInformation("정보 내용", testHospitalId));
            
            verify(hospitalInformationRepository, times(1)).findAllByHospitalId(testHospitalId);
        }
    }
} 