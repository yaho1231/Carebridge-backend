package com.example.carebridge.service;

import com.example.carebridge.dto.ExaminationScheduleDto;
import com.example.carebridge.entity.ExaminationSchedule;
import com.example.carebridge.repository.ExaminationScheduleRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExaminationScheduleService {

    private final ExaminationScheduleRepository scheduleRepository;

    // ExaminationScheduleRepository 를 주입받는 생성자
    public ExaminationScheduleService(ExaminationScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    /**
     * 환자 ID로 스케줄을 조회합니다.
     *
     * @param patientId 환자 ID
     * @return 환자의 검사 일정 목록
     */
    public List<ExaminationScheduleDto> getSchedules(Integer patientId) {
        List<ExaminationSchedule> schedules = scheduleRepository.findByPatientIdOrderByScheduleDateDesc(patientId);
        List<ExaminationScheduleDto> examinationScheduleDtoList = new ArrayList<>();
        for (ExaminationSchedule schedule : schedules) {
            ExaminationScheduleDto examinationScheduleDto = getExaminationScheduleDto(schedule);
            examinationScheduleDtoList.add(examinationScheduleDto);
        }
        return examinationScheduleDtoList;
    }

    /**
     * ExaminationSchedule 엔티티를 ExaminationScheduleDto 로 변환합니다.
     *
     * @param schedule 검사 일정 엔티티
     * @return 검사 일정 DTO
     */
    @NotNull
    private static ExaminationScheduleDto getExaminationScheduleDto(ExaminationSchedule schedule) {
        ExaminationScheduleDto examinationScheduleDto = new ExaminationScheduleDto();
        examinationScheduleDto.setId(schedule.getId());
        examinationScheduleDto.setPatientId(schedule.getPatientId());
        examinationScheduleDto.setScheduleDate(schedule.getScheduleDate());
        examinationScheduleDto.setCategory(schedule.getCategory());
        examinationScheduleDto.setMedicalStaffId(schedule.getMedicalStaffId());
        examinationScheduleDto.setDetails(schedule.getDetails());
        return examinationScheduleDto;
    }

}