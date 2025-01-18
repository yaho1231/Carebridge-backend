package com.example.carebridge.controller;

import com.example.carebridge.dto.ExaminationScheduleDto;
import com.example.carebridge.service.ExaminationScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
public class ExaminationScheduleController {

    private final ExaminationScheduleService scheduleService;

    public ExaminationScheduleController(ExaminationScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    /**
     * 환자별 스케줄 조회 API
     *
     * @param patientId 환자 ID
     * @return 환자의 검사 일정 목록
     */
    @GetMapping("/patient/{patient_id}")
    @ResponseBody
    public ResponseEntity<List<ExaminationScheduleDto>> getSchedules(@PathVariable("patient_id") Integer patientId) {
        try {
            List<ExaminationScheduleDto> schedules = scheduleService.getSchedules(patientId);
            return ResponseEntity.ok(schedules);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}