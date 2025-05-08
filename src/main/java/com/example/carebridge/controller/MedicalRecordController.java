package com.example.carebridge.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.example.carebridge.service.MedicalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import java.util.NoSuchElementException;

/**
 * 의료 기록 관리 컨트롤러
 * 환자의 의료 기록을 조회하고 관리하는 REST API 컨트롤러입니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/medical-record")
@Tag(name = "Medical Record Controller", description = "의료 기록 관리 API")
public class MedicalRecordController {
    private final MedicalRecordService medicalRecordService;

    /**
     * MedicalRecordService를 주입받는 생성자
     *
     * @param medicalRecordService 의료 기록 서비스
     */
    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }
    
    /**
     * 환자의 질병 정보를 조회합니다.
     *
     * @param patientId 환자 ID (필수)
     * @return ResponseEntity<String> 질병 정보와 HTTP 상태 코드
     * @throws IllegalArgumentException 환자 ID가 유효하지 않은 경우
     * @throws IllegalStateException 환자의 질병 정보를 찾을 수 없는 경우
     */
    @Operation(
        summary = "환자 질병 정보 조회",
        description = "환자 ID를 기반으로 해당 환자의 상세 질병 정보와 병력을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "질병 정보 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 환자 ID)"),
        @ApiResponse(responseCode = "404", description = "환자의 질병 정보를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류 발생")
    })
    @GetMapping("/{patientId}")
    @ResponseBody
    public ResponseEntity<String> getDiseaseInfo(
        @Parameter(
            description = "조회할 환자의 고유 식별자",
            required = true,
            example = "20"
        )
        @PathVariable Integer patientId
    ) {
        try {
            log.debug("질병 정보 조회 요청 - 환자 ID: {}", patientId);
            String diseaseInfo = medicalRecordService.getDiseaseInfo(patientId);
            log.info("질병 정보 조회 성공 - 환자 ID: {}", patientId);
            return ResponseEntity.ok(diseaseInfo);
        } catch (NoSuchElementException e) {
            log.warn("해당 ID의 환자의 질병 정보를 찾을 수 없습니다 - 환자 ID: {}", patientId, e);
            return new ResponseEntity<>("해당 환자의 질병 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청 - 환자 ID: {}, 오류: {}", patientId, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("질병 정보 조회 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
