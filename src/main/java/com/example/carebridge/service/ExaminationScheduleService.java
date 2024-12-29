// ExaminationScheduleService.java
package com.example.carebridge.service;

import com.example.carebridge.dto.ExaminationScheduleDto;
import com.example.carebridge.entity.ExaminationSchedule;
import com.example.carebridge.repository.ExaminationScheduleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExaminationScheduleService {

    private final ExaminationScheduleRepository scheduleRepository;

    public ExaminationScheduleService(ExaminationScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    // 환자 전화번호로 스케줄 조회
    public List<ExaminationSchedule> getSchedulesByPatientPhone(String patientPhone) {
        return scheduleRepository.findByPatientPhone(patientPhone);
    }

    public void addSchedule(ExaminationSchedule schedule) {
        scheduleRepository.save(schedule);
    }

    public ExaminationSchedule updateSchedule(Integer id, ExaminationScheduleDto requestDto) {
        ExaminationSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with id: " + id));
        schedule.update(requestDto);
        scheduleRepository.save(schedule);
        return schedule;
    }
}
