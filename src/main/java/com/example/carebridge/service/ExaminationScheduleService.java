package com.example.carebridge.service;

import com.example.carebridge.dto.ExaminationScheduleDto;
import com.example.carebridge.entity.ExaminationSchedule;
import com.example.carebridge.mapper.ExaminationScheduleMapper;
import com.example.carebridge.repository.ExaminationScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Transactional(readOnly = true)
    public ExaminationSchedule getSchedulesById(Integer id) {
        if (id == null) {
            log.error("ID가 null입니다.");
            throw new IllegalArgumentException("ID는 필수입니다.");
        }
        try {
            return scheduleRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("스케줄을 찾을 수 없습니다. ID: {}", id);
                        return new IllegalArgumentException("해당 id의 스케줄을 찾을 수 없습니다.");
                    });
        } catch (Exception e) {
            log.error("검사 일정 조회 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("검사 일정 조회에 실패했습니다.", e);
        }
    }

    @Transactional(readOnly = true)
    public List<ExaminationScheduleDto> getSchedulesByMedicalStaffId(Integer medicalStaffId) {
        if (medicalStaffId == null) {
            log.error("의료진 ID가 null입니다.");
            throw new IllegalArgumentException("의료진 ID는 필수입니다.");
        }
        try {
            List<ExaminationSchedule> schedules = scheduleRepository.findByMedicalStaffId(medicalStaffId);
            log.debug("검사 일정 조회 성공 - 환자 ID: {}, 일정 수: {}",
                    medicalStaffId, schedules.size());

            return schedules.stream()
                    .map(scheduleMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("검사 일정 조회 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("검사 일정 조회에 실패했습니다.", e);
        }
    }

    @Transactional(readOnly = true)
    public List<ExaminationScheduleDto> getTodaySchedules(Integer patientId) {
        if (patientId == null) {
            log.error("환자 ID가 null입니다.");
            throw new IllegalArgumentException("환자 ID는 필수입니다.");
        }
        try {
            List<ExaminationSchedule> schedules = scheduleRepository.findTodaySchedulesByPatientId(patientId);
            log.debug("검사 일정 조회 성공 - 환자 ID: {}, 일정 수: {}", patientId, schedules.size());

            return schedules.stream()
                    .map(scheduleMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("검사 일정 조회 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("검사 일정 조회에 실패했습니다.", e);
        }
    }

    @Transactional
    public ExaminationSchedule createSchedule(ExaminationScheduleDto examinationScheduleDto) {
        if (examinationScheduleDto == null) {
            log.error("examinationScheduleDto가 null입니다.");
            throw new IllegalArgumentException("examinationScheduleDto는 필수입니다.");
        }
        try {
            ExaminationSchedule schedule = scheduleMapper.toEntity(examinationScheduleDto);
            schedule.setCategory(examinationScheduleDto.getCategory());
            return scheduleRepository.save(schedule);
        } catch (Exception e) {
            log.error("스케줄 등록중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("스케줄 등록에 실패했습니다.");
        }
    }

    @Transactional
    public ExaminationSchedule updateSchedule(ExaminationScheduleDto examinationScheduleDto) {
        if (examinationScheduleDto == null) {
            log.error("examinationScheduleDto가 null입니다.");
            throw new IllegalArgumentException("examinationScheduleDto는 필수입니다.");
        }
        ExaminationSchedule examinationSchedule = scheduleRepository.findById(examinationScheduleDto.getId())
                .orElseThrow(() -> {
                    log.error("존재하지 않는 스케줄입니다.");
                    return new IllegalArgumentException("존재하지 않는 스케줄입니다.");
                });
        try {
            examinationSchedule.setPatientId(examinationScheduleDto.getPatientId());
            examinationSchedule.setMedicalStaffId(examinationScheduleDto.getMedicalStaffId());
            examinationSchedule.setScheduleDate(LocalDateTime.parse(examinationScheduleDto.getScheduleDate()));
            examinationSchedule.setDetails(examinationScheduleDto.getDetails());
            examinationSchedule.setCategory(examinationScheduleDto.getCategory());

            return scheduleRepository.save(examinationSchedule);
        } catch (Exception e) {
            log.error("스케줄 수정 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("스케줄 수정에 실패했습니다.");
        }
    }

    @Transactional
    public ExaminationSchedule deleteSchedule(Integer scheduleId) {
        if (scheduleId == null) {
            log.error("스케줄 ID가 null입니다.");
            throw new IllegalArgumentException("스케줄 ID는 필수입니다.");
        }
        ExaminationSchedule examinationSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 스케줄입니다.");
                    return new IllegalArgumentException("존재하지 않는 스케줄입니다.");
                });
        try {
            scheduleRepository.delete(examinationSchedule);
            return examinationSchedule;
        } catch (Exception e) {
            log.error("스케줄 삭제 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("스케줄 삭제에 실패했습니다.");
        }
    }
}