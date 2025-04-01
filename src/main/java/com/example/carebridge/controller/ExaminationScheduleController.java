package com.example.carebridge.controller;

import com.example.carebridge.dto.ExaminationScheduleDto;
import com.example.carebridge.entity.ExaminationSchedule;
import com.example.carebridge.service.ExaminationScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 검사 일정 관리 컨트롤러
 * 환자의 검사 일정을 조회하고 관리하는 REST API 컨트롤러입니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/schedule")
@Tag(name = "Examination Schedule Controller", description = "검사 일정 관리 API")
public class ExaminationScheduleController {

    private final ExaminationScheduleService scheduleService;

    /**
     * 서비스 계층 의존성 주입을 위한 생성자
     *
     * @param scheduleService 검사 일정 서비스
     */
    public ExaminationScheduleController(ExaminationScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    /**
     * 환자별 검사 일정을 조회합니다.
     * 특정 환자의 모든 검사 일정을 조회하여 반환합니다.
     *
     * @param patientId 환자의 ID (필수)
     * @return ResponseEntity<List<ExaminationScheduleDto>> 검사 일정 목록과 HTTP 상태
     * @throws IllegalArgumentException 환자 ID가 존재하지 않는 경우
     */
    @Operation(summary = "환자별 검사 일정 조회", description = "특정 환자의 모든 검사 일정을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "검사 일정 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (환자 ID 오류)"),
        @ApiResponse(responseCode = "404", description = "환자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/patient/{patient_id}")
    @ResponseBody
    public ResponseEntity<List<ExaminationScheduleDto>> getSchedules(
            @Parameter(description = "환자 ID", required = true)
            @PathVariable("patient_id") Integer patientId) {
        try {
            log.debug("환자 ID {}의 검사 일정 조회 요청", patientId);
            
            List<ExaminationScheduleDto> schedules = scheduleService.getSchedules(patientId);
            log.debug("환자 ID {}의 검사 일정 조회 성공 - {} 건", patientId, schedules.size());
            return new ResponseEntity<>(schedules, HttpStatus.OK);
            
        } catch (IllegalArgumentException e) {
            log.error("잘못된 환자 ID: {}", patientId, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("검사 일정 조회 중 오류 발생 - 환자 ID: {}", patientId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "의료진별 검사 일정 조회", description = "특정 의료진에게 해당된 모든 검사 일정을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검사 일정 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (의료진 ID 오류)"),
            @ApiResponse(responseCode = "404", description = "의료진을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/medical-staff/{medical_staff_id}")
    public ResponseEntity<List<ExaminationScheduleDto>> getSchedulesByMedicalId(
            @Parameter(description = "의료진 ID", required = true)
            @PathVariable("medical_staff_id") Integer medicalId) {
        try {
            log.debug("의료진 ID {}의 검사 일정 조회 요청", medicalId);
            List<ExaminationScheduleDto> schedules = scheduleService.getSchedulesByMedicalStaffId(medicalId);
            log.debug("의료진 ID {}의 검사 일정 조회 성공 - {} 건", medicalId, schedules.size());

            return new ResponseEntity<>(schedules, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 의료진 ID: {}", medicalId, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("검사 일정 조회 중 오류 발생 - 의료진 ID: {}", medicalId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "오늘의 일정 조회")
    @GetMapping("/today/{patient_id}")
    public ResponseEntity<List<ExaminationScheduleDto>> getTodaySchedules(
            @Parameter(description = "환자 ID", required = true)
            @PathVariable("patient_id") Integer patientId) {
        try {
            List<ExaminationScheduleDto> schedules = scheduleService.getTodaySchedules(patientId);
            log.debug("오늘의 검사 일정 조회 성공 - {} 건", schedules.size());
            return new ResponseEntity<>(schedules, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 환자 ID: {}", patientId, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("검사 일정 조회 중 오류 발생 - 환자 ID: {}", patientId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "스케줄 생성")
    @PostMapping()
    public ResponseEntity<ExaminationSchedule> createSchedule(
            @RequestBody ExaminationScheduleDto examinationScheduleDto) {
        try {
            ExaminationSchedule examinationSchedule = scheduleService.createSchedule(examinationScheduleDto);
            log.debug("환자 ID : {} 의 스케줄 생성 성공", examinationSchedule.getPatientId());
            return new ResponseEntity<>(examinationSchedule, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 Dto");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("스케줄 생성중 오류 발생");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "스케줄 수정", description = "ExaminationScheduleDto의 id를 기준으로 데이터를 찾아 수정함.")
    @PutMapping()
    public ResponseEntity<ExaminationSchedule> updateSchedule(
            @RequestBody ExaminationScheduleDto examinationScheduleDto) {
        try {
            ExaminationSchedule examinationSchedule = scheduleService.updateSchedule(examinationScheduleDto);
            return new ResponseEntity<>(examinationSchedule, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 Dto");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("스케줄 수정중 오류 발생");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "스케줄 삭제", description = "파라미터의 id를 기준으로 Examination Schedule DB에서 해당하는 데이터를 찾아 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<ExaminationSchedule> deleteSchedule(@PathVariable("id") Integer id) {
        try {
            ExaminationSchedule examinationSchedule = scheduleService.deleteSchedule(id);
            return new ResponseEntity<>(examinationSchedule, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 스케줄 ID: {}", id, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("검사 일정 삭제 중 오류 발생 - 스케줄 ID: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "스케줄 조회", description = "파라미터의 id를 기준으로 Examination Schedule DB에서 해당하는 데이터를 찾아 리턴")
    @GetMapping("/{id}")
    public ResponseEntity<ExaminationSchedule> getSchedule(@PathVariable("id") Integer id) {
        try {
            ExaminationSchedule examinationSchedule = scheduleService.getSchedulesById(id);
            return new ResponseEntity<>(examinationSchedule, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 스케줄 ID: {}", id, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("검사 일정 조회 중 오류 발생 - 스케줄 ID: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}