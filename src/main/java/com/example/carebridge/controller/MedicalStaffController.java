package com.example.carebridge.controller;

import com.example.carebridge.entity.MedicalStaff;
import com.example.carebridge.service.MedicalStaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/medical-staff")
@Tag(name = "Medical Staff Controller", description = "의료진 분과 API")
public class MedicalStaffController {
    private final MedicalStaffService medicalStaffService;

    public MedicalStaffController(MedicalStaffService medicalStaffService) {
        this.medicalStaffService = medicalStaffService;
    }

    @Operation(
            summary = "병원 분과 조회",
            description = "병원 ID를 기반으로 해당 병원의 분과를 모두 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "분과 정보 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 병원 ID)"),
            @ApiResponse(responseCode = "404", description = "병원의 분과 정보를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류 발생")
    })
    @GetMapping("/{hospitalId}")
    @ResponseBody
    public ResponseEntity<List<MedicalStaff>> getMedicalStaffList(
            @Parameter(
                    description = "조회할 병원의 고유 식별자",
                    required = true,
                    example = "1"
            )
            @PathVariable Integer hospitalId
    ) {
        try {
            List<MedicalStaff> medicalStaffList = medicalStaffService.findAllByHospitalId(hospitalId);
            if (medicalStaffList.isEmpty())
                return ResponseEntity.noContent().build();
            return ResponseEntity.ok(medicalStaffList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
