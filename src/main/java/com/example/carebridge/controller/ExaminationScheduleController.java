package com.example.carebridge.controller;

import com.example.carebridge.dto.ExaminationScheduleDto;
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
     * @param patientService 환자 정보 서비스
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
}