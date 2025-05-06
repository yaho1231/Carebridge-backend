package com.example.carebridge.controller;

import com.example.carebridge.service.HospitalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

/**
 * 병원 관련 API 컨트롤러
 * 병원 정보와 관련된 다양한 API 엔드포인트를 제공합니다.
 */
@RestController
@RequestMapping("/api/hospital")
public class HospitalController {

    private final HospitalService hospitalService;

    /**
     * 병원 서비스를 주입받는 생성자입니다.
     * 
     * @param hospitalService 병원 관련 비즈니스 로직을 처리하는 서비스
     */
    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    /**
     * 병원 이름을 조회합니다.
     *
     * @param hospital_id 병원 ID
     * @return 병원 이름과 HTTP 상태 코드
     */
    @Operation(summary = "병원 이름 조회", description = "병원 ID로 병원 이름을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "병원 이름 조회 성공"),
        @ApiResponse(responseCode = "404", description = "해당 ID의 병원을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/name/{hospital_id}")
    @ResponseBody
    public ResponseEntity<String> getHospitalName(@PathVariable Integer hospital_id) {
        try {
            String hospitalName = hospitalService.getHospitalName(hospital_id);
            return ResponseEntity.ok(hospitalName);
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("해당 ID의 병원을 찾을 수 없습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("잘못된 병원 ID 형식입니다: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
