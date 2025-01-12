// ExaminationScheduleController.java
package com.example.carebridge.controller;

import com.example.carebridge.dto.ExaminationScheduleDto;
import com.example.carebridge.entity.ExaminationSchedule;
import com.example.carebridge.service.ExaminationScheduleService;
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

    // 스케줄 변경
    @PutMapping("/{id}")
    public ResponseEntity<ExaminationSchedule> updateSchedule(@PathVariable Integer id, @RequestBody ExaminationScheduleDto requestDto) {
        ExaminationSchedule updated = scheduleService.updateSchedule(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    // 스케줄 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ExaminationSchedule> deleteSchedule(@PathVariable Integer id) {
        ExaminationSchedule deleted = scheduleService.deleteSchedule(id);
        return ResponseEntity.ok(deleted);
    }

    //더미데이터
    @PostMapping("/notification")
    public ResponseEntity<ExaminationSchedule> sendNotification(@RequestBody ExaminationScheduleDto requestDto) {

        return null;
    }
}
