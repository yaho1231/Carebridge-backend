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
import java.util.NoSuchElementException;

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
        log.debug("검사 일정 조회 시도 - 환자 ID: {}", patientId);
        try {
            if (patientId == null) {
                log.error("환자 ID가 null입니다.");
                throw new IllegalArgumentException("환자 ID는 필수입니다.");
            }
            List<ExaminationSchedule> schedules = scheduleRepository.findByPatientId(patientId)
                    .orElseThrow(() -> {
                        log.error("환자 ID {}에 해당하는 스케줄을 찾을 수 없습니다.", patientId);
                        return new NoSuchElementException("해당 환자의 스케줄이 존재하지 않습니다.");
                    });
            List<ExaminationScheduleDto> scheduleDtoList = schedules.stream()
                    .map(scheduleMapper::toDto)
                    .collect(Collectors.toList());
            log.info("검사 일정 조회 완료 - 환자 ID: {}, 일정 수: {}", patientId, scheduleDtoList.size());
            return scheduleDtoList;
        } catch (IllegalArgumentException e) {
            log.error("검사 일정 조회 실패 - 환자 ID: {}, 오류: {}", patientId, e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("검사 일정 조회 실패 - 환자 ID: {}, 오류: {}", patientId, e.getMessage());
            throw new NoSuchElementException("해당 환자의 스케줄이 존재하지 않습니다.");
        }
    }

    @Transactional(readOnly = true)
    public ExaminationSchedule getSchedulesById(Integer id) {
        log.debug("검사 일정 단건 조회 시도 - ID: {}", id);
        try {
            if (id == null) {
                log.error("ID가 null입니다.");
                throw new IllegalArgumentException("ID는 필수입니다.");
            }
            ExaminationSchedule schedule = scheduleRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("ID {}에 해당하는 스케줄을 찾을 수 없습니다.", id);
                        return new NoSuchElementException("해당 ID의 스케줄이 존재하지 않습니다.");
                    });
            log.info("검사 일정 조회 완료 - ID: {}", id);
            return schedule;
        } catch (IllegalArgumentException e) {
            log.error("검사 일정 조회 실패 - ID: {}, 오류: {}", id, e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("검사 일정 조회 실패 - ID: {}, 오류: {}", id, e.getMessage());
            throw new NoSuchElementException("해당 ID의 스케줄이 존재하지 않습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<ExaminationScheduleDto> getSchedulesByMedicalStaffId(Integer medicalStaffId) {
        log.debug("의료진 ID {}의 검사 일정 조회 시도", medicalStaffId);
        try {
            if (medicalStaffId == null) {
                log.error("의료진 ID가 null입니다.");
                throw new IllegalArgumentException("의료진 ID는 필수입니다.");
            }
            List<ExaminationSchedule> schedules = scheduleRepository.findByMedicalStaffId(medicalStaffId)
                    .orElseThrow(() -> {
                        log.error("의료진 ID {}에 해당하는 스케줄을 찾을 수 없습니다.", medicalStaffId);
                        return new NoSuchElementException("해당 의료진의 스케줄이 존재하지 않습니다.");
                    });
            log.info("검사 일정 조회 완료 - 의료진 ID: {}, 일정 수: {}", medicalStaffId, schedules.size());
            return schedules.stream()
                    .map(scheduleMapper::toDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            log.error("검사 일정 조회 실패 - 의료진 ID: {}, 오류: {}", medicalStaffId, e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("검사 일정 조회 실패 - 의료진 ID: {}, 오류: {}", medicalStaffId, e.getMessage());
            throw new NoSuchElementException("해당 의료진의 스케줄이 존재하지 않습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<ExaminationScheduleDto> getTodaySchedules(Integer patientId) {
        log.debug("환자 ID {}의 오늘 검사 일정 조회 시도", patientId);
        try {
            if (patientId == null) {
                log.error("환자 ID가 null입니다.");
                throw new IllegalArgumentException("환자 ID는 필수입니다.");
            }
            List<ExaminationSchedule> schedules = scheduleRepository.findTodaySchedulesByPatientId(patientId)
                    .orElseThrow(() -> {
                        log.error("환자 ID {}에 해당하는 오늘의 스케줄을 찾을 수 없습니다.", patientId);
                        return new NoSuchElementException("해당 환자의 오늘 스케줄이 존재하지 않습니다.");
                    });
            log.info("오늘 검사 일정 조회 완료 - 환자 ID: {}, 일정 수: {}", patientId, schedules.size());
            return schedules.stream()
                    .map(scheduleMapper::toDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            log.error("오늘 검사 일정 조회 실패 - 환자 ID: {}, 오류: {}", patientId, e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("오늘 검사 일정 조회 실패 - 환자 ID: {}, 오류: {}", patientId, e.getMessage());
            throw new NoSuchElementException("해당 환자의 오늘 스케줄이 존재하지 않습니다.");
        }
    }

    @Transactional
    public ExaminationSchedule createSchedule(ExaminationScheduleDto examinationScheduleDto) {
        log.debug("검사 일정 생성 시도 - DTO: {}", examinationScheduleDto);
        try {
            if (examinationScheduleDto == null) {
                log.error("examinationScheduleDto가 null입니다.");
                throw new IllegalArgumentException("검사 일정 정보는 필수입니다.");
            }
            ExaminationSchedule schedule = scheduleMapper.toEntity(examinationScheduleDto);
            schedule.setCategory(examinationScheduleDto.getCategory());
            ExaminationSchedule savedSchedule = scheduleRepository.save(schedule);
            log.info("검사 일정 생성 완료 - 스케줄 ID: {}", savedSchedule.getId());
            return savedSchedule;
        } catch (IllegalArgumentException e) {
            log.error("검사 일정 생성 실패 - 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        }
    }

    @Transactional
    public ExaminationSchedule updateSchedule(ExaminationScheduleDto examinationScheduleDto) {
        log.debug("검사 일정 수정 시도 - DTO: {}", examinationScheduleDto);
        try {
            if (examinationScheduleDto == null) {
                log.error("examinationScheduleDto가 null입니다.");
                throw new IllegalArgumentException("검사 일정 정보는 필수입니다.");
            }
            ExaminationSchedule examinationSchedule = scheduleRepository.findById(examinationScheduleDto.getId())
                    .orElseThrow(() -> {
                        log.error("존재하지 않는 스케줄입니다. 스케줄 ID: {}", examinationScheduleDto.getId());
                        return new NoSuchElementException("존재하지 않는 스케줄입니다.");
                    });
            examinationSchedule.setPatientId(examinationScheduleDto.getPatientId());
            examinationSchedule.setMedicalStaffId(examinationScheduleDto.getMedicalStaffId());
            examinationSchedule.setScheduleDate(LocalDateTime.parse(examinationScheduleDto.getScheduleDate()));
            examinationSchedule.setDetails(examinationScheduleDto.getDetails());
            examinationSchedule.setCategory(examinationScheduleDto.getCategory());
            ExaminationSchedule updatedSchedule = scheduleRepository.save(examinationSchedule);
            log.info("검사 일정 수정 완료 - 스케줄 ID: {}", updatedSchedule.getId());
            return updatedSchedule;
        } catch (IllegalArgumentException e) {
            log.error("검사 일정 수정 실패 - 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("검사 일정 수정 실패 - 오류: {}", e.getMessage());
            throw new NoSuchElementException("해당 스케줄이 존재하지 않습니다.");
        }
    }

    @Transactional
    public ExaminationSchedule deleteSchedule(Integer scheduleId) {
        log.debug("검사 일정 삭제 시도 - 스케줄 ID: {}", scheduleId);
        try {
            if (scheduleId == null) {
                log.error("스케줄 ID가 null입니다.");
                throw new IllegalArgumentException("스케줄 ID는 필수입니다.");
            }
            ExaminationSchedule examinationSchedule = scheduleRepository.findById(scheduleId)
                    .orElseThrow(() -> {
                        log.error("존재하지 않는 스케줄입니다. 스케줄 ID: {}", scheduleId);
                        return new NoSuchElementException("존재하지 않는 스케줄입니다.");
                    });
            scheduleRepository.delete(examinationSchedule);
            log.info("검사 일정 삭제 완료 - 스케줄 ID: {}", scheduleId);
            return examinationSchedule;
        } catch (IllegalArgumentException e) {
            log.error("검사 일정 삭제 실패 - 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("검사 일정 삭제 실패 - 오류: {}", e.getMessage());
            throw new NoSuchElementException("해당 스케줄이 존재하지 않습니다.");
        }
    }
}