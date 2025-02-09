package com.example.carebridge.service;

import com.example.carebridge.dto.ExaminationScheduleDto;
import com.example.carebridge.entity.ExaminationSchedule;
import com.example.carebridge.mapper.ExaminationScheduleMapper;
import com.example.carebridge.repository.ExaminationScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 검사 일정 관리 서비스
 * 환자의 검사 일정을 조회, 등록, 수정, 삭제하는 기능을 제공하는 서비스 클래스입니다.
 */
@Slf4j
@Service
public class ExaminationScheduleService {

    private final ExaminationScheduleRepository scheduleRepository;
    private final ExaminationScheduleMapper scheduleMapper;

    /**
     * ExaminationScheduleRepository와 ExaminationScheduleMapper를 주입받는 생성자
     */
    public ExaminationScheduleService(ExaminationScheduleRepository scheduleRepository,
                                       ExaminationScheduleMapper scheduleMapper) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleMapper = scheduleMapper;
    }

    /**
     * 환자 ID로 스케줄을 조회합니다.
     *
     * @param patientId 환자 ID
     * @return 환자의 검사 일정 목록
     * @throws IllegalArgumentException 환자 ID가 유효하지 않은 경우
     */
    @Transactional(readOnly = true)
    public List<ExaminationScheduleDto> getSchedules(Integer patientId) {
        if (patientId == null) {
            log.error("환자 ID가 null입니다.");
            throw new IllegalArgumentException("환자 ID는 필수입니다.");
        }

        try {
            List<ExaminationSchedule> schedules = scheduleRepository.findByPatientId(patientId);
            log.debug("검사 일정 조회 성공 - 환자 ID: {}, 일정 수: {}", 
                    patientId, schedules.size());
            
            return schedules.stream()
                    .map(scheduleMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("검사 일정 조회 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("검사 일정 조회에 실패했습니다.", e);
        }
    }
}