// ExaminationScheduleController.java
package com.example.carebridge.controller;

import com.example.carebridge.dto.ExaminationScheduleDto;
import com.example.carebridge.entity.ExaminationSchedule;
import com.example.carebridge.service.ExaminationScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
public class ExaminationScheduleController {

    private final ExaminationScheduleService scheduleService;

    public ExaminationScheduleController(ExaminationScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    // 환자별 스케줄 조회 API
    @GetMapping("/patient/{phone}")
    public ResponseEntity<List<ExaminationSchedule>> getSchedulesByPatientPhone(@PathVariable("phone") String patientPhone) {
        List<ExaminationSchedule> schedules = scheduleService.getSchedulesByPatientPhone(patientPhone);
        return ResponseEntity.ok(schedules);
    }

    // 스케줄 추가 API
    @PostMapping
    public ResponseEntity<String> addSchedule(@RequestBody ExaminationSchedule schedule) {
        scheduleService.addSchedule(schedule);
        return ResponseEntity.ok("Schedule added successfully.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExaminationSchedule> updateSchedule(@PathVariable Integer id, @RequestBody ExaminationScheduleDto requestDto) {
        ExaminationSchedule updated = scheduleService.updateSchedule(id, requestDto);
        return ResponseEntity.ok(updated);
    }
}
