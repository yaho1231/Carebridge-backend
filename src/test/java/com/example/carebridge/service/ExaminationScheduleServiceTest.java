package com.example.carebridge.service;

import com.example.carebridge.dto.ExaminationScheduleDto;
import com.example.carebridge.entity.ExaminationSchedule;
import com.example.carebridge.mapper.ExaminationScheduleMapper;
import com.example.carebridge.repository.ExaminationScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * ExaminationScheduleService 클래스에 대한 단위 테스트
 * 검사 일정 서비스의 주요 기능을 검증합니다.
 */
@Tag("service")
@DisplayName("검사 일정 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class ExaminationScheduleServiceTest {

    @Mock
    private ExaminationScheduleRepository scheduleRepository;

    @Mock
    private ExaminationScheduleMapper scheduleMapper;

    @InjectMocks
    private ExaminationScheduleService scheduleService;

    // 공통으로 사용되는 테스트 데이터
    private Integer validPatientId;
    private Integer validMedicalStaffId;
    private Integer validScheduleId;
    private LocalDateTime validScheduleDate;
    private String validDetails;
    private ExaminationSchedule validSchedule;
    private ExaminationScheduleDto validScheduleDto;
    private List<ExaminationSchedule> validScheduleList;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        validPatientId = 1;
        validMedicalStaffId = 2;
        validScheduleId = 1;
        validScheduleDate = LocalDateTime.now().plusDays(1);
        validDetails = "MRI 검사";

        // 유효한 ExaminationSchedule 생성
        validSchedule = new ExaminationSchedule();
        validSchedule.setId(validScheduleId);
        validSchedule.setPatientId(validPatientId);
        validSchedule.setMedicalStaffId(validMedicalStaffId);
        validSchedule.setScheduleDate(validScheduleDate);
        validSchedule.setDetails(validDetails);
        validSchedule.setCategory(ExaminationSchedule.Category.EXAMINATION);

        // 유효한 ExaminationScheduleDto 생성
        validScheduleDto = new ExaminationScheduleDto();
        validScheduleDto.setId(validScheduleId);
        validScheduleDto.setPatientId(validPatientId);
        validScheduleDto.setMedicalStaffId(validMedicalStaffId);
        validScheduleDto.setScheduleDate(validScheduleDate.toString());
        validScheduleDto.setDetails(validDetails);
        validScheduleDto.setCategory(ExaminationSchedule.Category.EXAMINATION);

        // 유효한 ExaminationSchedule 목록 생성
        validScheduleList = new ArrayList<>();
        validScheduleList.add(validSchedule);
    }

    @Nested
    @DisplayName("getSchedules 메소드 테스트")
    class GetSchedulesTest {

        @Test
        @DisplayName("환자 ID로 일정 조회 성공 테스트")
        void getSchedules_Success() {
            // given
            when(scheduleRepository.findByPatientId(validPatientId)).thenReturn(Optional.of(validScheduleList));
            when(scheduleMapper.toDto(validSchedule)).thenReturn(validScheduleDto);

            // when
            List<ExaminationScheduleDto> result = scheduleService.getSchedules(validPatientId);

            // then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(validScheduleId, result.get(0).getId());
            assertEquals(validPatientId, result.get(0).getPatientId());
            verify(scheduleRepository, times(1)).findByPatientId(validPatientId);
            verify(scheduleMapper, times(1)).toDto(validSchedule);
        }

        @Test
        @DisplayName("환자 ID가 null인 경우 예외 발생 테스트")
        void getSchedules_NullPatientId() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> scheduleService.getSchedules(null));
            verify(scheduleRepository, never()).findByPatientId(any());
            verify(scheduleMapper, never()).toDto(any());
        }

        @Test
        @DisplayName("일정이 없는 경우 예외 발생 테스트")
        void getSchedules_NoSchedules() {
            // given
            when(scheduleRepository.findByPatientId(validPatientId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> scheduleService.getSchedules(validPatientId));
            verify(scheduleRepository, times(1)).findByPatientId(validPatientId);
            verify(scheduleMapper, never()).toDto(any());
        }
    }

    @Nested
    @DisplayName("getSchedulesById 메소드 테스트")
    class GetSchedulesByIdTest {

        @Test
        @DisplayName("ID로 일정 조회 성공 테스트")
        void getSchedulesById_Success() {
            // given
            when(scheduleRepository.findById(validScheduleId)).thenReturn(Optional.of(validSchedule));

            // when
            ExaminationSchedule result = scheduleService.getSchedulesById(validScheduleId);

            // then
            assertNotNull(result);
            assertEquals(validScheduleId, result.getId());
            assertEquals(validPatientId, result.getPatientId());
            verify(scheduleRepository, times(1)).findById(validScheduleId);
        }

        @Test
        @DisplayName("ID가 null인 경우 예외 발생 테스트")
        void getSchedulesById_NullId() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> scheduleService.getSchedulesById(null));
            verify(scheduleRepository, never()).findById(anyInt());
        }

        @Test
        @DisplayName("일정이 없는 경우 예외 발생 테스트")
        void getSchedulesById_NoSchedule() {
            // given
            when(scheduleRepository.findById(validScheduleId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> scheduleService.getSchedulesById(validScheduleId));
            verify(scheduleRepository, times(1)).findById(validScheduleId);
        }
    }

    @Nested
    @DisplayName("getSchedulesByMedicalStaffId 메소드 테스트")
    class GetSchedulesByMedicalStaffIdTest {

        @Test
        @DisplayName("의료진 ID로 일정 조회 성공 테스트")
        void getSchedulesByMedicalStaffId_Success() {
            // given
            when(scheduleRepository.findByMedicalStaffId(validMedicalStaffId)).thenReturn(Optional.of(validScheduleList));
            when(scheduleMapper.toDto(validSchedule)).thenReturn(validScheduleDto);

            // when
            List<ExaminationScheduleDto> result = scheduleService.getSchedulesByMedicalStaffId(validMedicalStaffId);

            // then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(validScheduleId, result.get(0).getId());
            assertEquals(validMedicalStaffId, result.get(0).getMedicalStaffId());
            verify(scheduleRepository, times(1)).findByMedicalStaffId(validMedicalStaffId);
            verify(scheduleMapper, times(1)).toDto(validSchedule);
        }

        @Test
        @DisplayName("의료진 ID가 null인 경우 예외 발생 테스트")
        void getSchedulesByMedicalStaffId_NullMedicalStaffId() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> scheduleService.getSchedulesByMedicalStaffId(null));
            verify(scheduleRepository, never()).findByMedicalStaffId(any());
            verify(scheduleMapper, never()).toDto(any());
        }

        @Test
        @DisplayName("일정이 없는 경우 예외 발생 테스트")
        void getSchedulesByMedicalStaffId_NoSchedules() {
            // given
            when(scheduleRepository.findByMedicalStaffId(validMedicalStaffId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> scheduleService.getSchedulesByMedicalStaffId(validMedicalStaffId));
            verify(scheduleRepository, times(1)).findByMedicalStaffId(validMedicalStaffId);
            verify(scheduleMapper, never()).toDto(any());
        }
    }

    @Nested
    @DisplayName("getTodaySchedules 메소드 테스트")
    class GetTodaySchedulesTest {

        @Test
        @DisplayName("오늘 일정 조회 성공 테스트")
        void getTodaySchedules_Success() {
            // given
            when(scheduleRepository.findTodaySchedulesByPatientId(validPatientId)).thenReturn(Optional.of(validScheduleList));
            when(scheduleMapper.toDto(validSchedule)).thenReturn(validScheduleDto);

            // when
            List<ExaminationScheduleDto> result = scheduleService.getTodaySchedules(validPatientId);

            // then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(validScheduleId, result.get(0).getId());
            verify(scheduleRepository, times(1)).findTodaySchedulesByPatientId(validPatientId);
            verify(scheduleMapper, times(1)).toDto(validSchedule);
        }

        @Test
        @DisplayName("환자 ID가 null인 경우 예외 발생 테스트")
        void getTodaySchedules_NullPatientId() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> scheduleService.getTodaySchedules(null));
            verify(scheduleRepository, never()).findTodaySchedulesByPatientId(any());
            verify(scheduleMapper, never()).toDto(any());
        }

        @Test
        @DisplayName("오늘 일정이 없는 경우 예외 발생 테스트")
        void getTodaySchedules_NoSchedules() {
            // given
            when(scheduleRepository.findTodaySchedulesByPatientId(validPatientId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> scheduleService.getTodaySchedules(validPatientId));
            verify(scheduleRepository, times(1)).findTodaySchedulesByPatientId(validPatientId);
            verify(scheduleMapper, never()).toDto(any());
        }
    }

    @Nested
    @DisplayName("createSchedule 메소드 테스트")
    class CreateScheduleTest {

        @Test
        @DisplayName("일정 생성 성공 테스트")
        void createSchedule_Success() {
            // given
            when(scheduleMapper.toEntity(validScheduleDto)).thenReturn(validSchedule);
            when(scheduleRepository.save(any(ExaminationSchedule.class))).thenReturn(validSchedule);

            // when
            ExaminationSchedule result = scheduleService.createSchedule(validScheduleDto);

            // then
            assertNotNull(result);
            assertEquals(validScheduleId, result.getId());
            assertEquals(validPatientId, result.getPatientId());
            assertEquals(validMedicalStaffId, result.getMedicalStaffId());
            assertEquals(ExaminationSchedule.Category.EXAMINATION, result.getCategory());
            verify(scheduleMapper, times(1)).toEntity(validScheduleDto);
            verify(scheduleRepository, times(1)).save(any(ExaminationSchedule.class));
        }

        @Test
        @DisplayName("DTO가 null인 경우 예외 발생 테스트")
        void createSchedule_NullDto() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> scheduleService.createSchedule(null));
            verify(scheduleMapper, never()).toEntity(any());
            verify(scheduleRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateSchedule 메소드 테스트")
    class UpdateScheduleTest {

        @Test
        @DisplayName("일정 수정 성공 테스트")
        void updateSchedule_Success() {
            // given
            when(scheduleRepository.findById(validScheduleId)).thenReturn(Optional.of(validSchedule));
            when(scheduleRepository.save(any(ExaminationSchedule.class))).thenReturn(validSchedule);

            // when
            ExaminationSchedule result = scheduleService.updateSchedule(validScheduleDto);

            // then
            assertNotNull(result);
            assertEquals(validScheduleId, result.getId());
            assertEquals(validPatientId, result.getPatientId());
            assertEquals(validMedicalStaffId, result.getMedicalStaffId());
            verify(scheduleRepository, times(1)).findById(validScheduleId);
            verify(scheduleRepository, times(1)).save(validSchedule);
        }

        @Test
        @DisplayName("DTO가 null인 경우 예외 발생 테스트")
        void updateSchedule_NullDto() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> scheduleService.updateSchedule(null));
            verify(scheduleRepository, never()).findById(anyInt());
            verify(scheduleRepository, never()).save(any());
        }

        @Test
        @DisplayName("일정이 없는 경우 예외 발생 테스트")
        void updateSchedule_NoSchedule() {
            // given
            when(scheduleRepository.findById(validScheduleId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> scheduleService.updateSchedule(validScheduleDto));
            verify(scheduleRepository, times(1)).findById(validScheduleId);
            verify(scheduleRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteSchedule 메소드 테스트")
    class DeleteScheduleTest {

        @Test
        @DisplayName("일정 삭제 성공 테스트")
        void deleteSchedule_Success() {
            // given
            when(scheduleRepository.findById(validScheduleId)).thenReturn(Optional.of(validSchedule));
            doNothing().when(scheduleRepository).delete(validSchedule);

            // when
            ExaminationSchedule result = scheduleService.deleteSchedule(validScheduleId);

            // then
            assertNotNull(result);
            assertEquals(validScheduleId, result.getId());
            verify(scheduleRepository, times(1)).findById(validScheduleId);
            verify(scheduleRepository, times(1)).delete(validSchedule);
        }

        @Test
        @DisplayName("ID가 null인 경우 예외 발생 테스트")
        void deleteSchedule_NullId() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> scheduleService.deleteSchedule(null));
            verify(scheduleRepository, never()).findById(anyInt());
            verify(scheduleRepository, never()).delete(any());
        }

        @Test
        @DisplayName("일정이 없는 경우 예외 발생 테스트")
        void deleteSchedule_NoSchedule() {
            // given
            when(scheduleRepository.findById(validScheduleId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> scheduleService.deleteSchedule(validScheduleId));
            verify(scheduleRepository, times(1)).findById(validScheduleId);
            verify(scheduleRepository, never()).delete(any());
        }
    }
} 