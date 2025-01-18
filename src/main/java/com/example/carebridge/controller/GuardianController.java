package com.example.carebridge.controller;

import com.example.carebridge.dto.GuardianDto;
import com.example.carebridge.service.GuardianService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 보호자 관련 API 컨트롤러
 * 보호자 정보와 관련된 다양한 API 엔드포인트를 제공합니다.
 */
@RestController
@RequestMapping("api/guardian")
public class GuardianController {
    private final GuardianService guardianService;

    /**
     * GuardianService 를 주입받는 생성자
     *
     * @param guardianService 보호자 서비스
     */
    public GuardianController(GuardianService guardianService) {
        this.guardianService = guardianService;
    }

    /**
     * 보호자 정보를 조회합니다.
     *
     * @param phone_number 보호자 전화번호
     * @return 보호자 정보 DTO
     */
    @GetMapping("/info/{phone_number}")
    @ResponseBody
    public ResponseEntity<GuardianDto> getGuardianInfo(@PathVariable String phone_number) {
        GuardianDto guardianDto = guardianService.getGuardianInfo(phone_number);
        return ResponseEntity.ok(guardianDto);
    }

    /**
     * 특정 환자의 모든 보호자 정보를 조회합니다.
     *
     * @param patient_id 환자 ID
     * @return 보호자 정보 리스트
     */
    @GetMapping("/list/{patient_id}")
    @ResponseBody
    public ResponseEntity<List<GuardianDto>> getGuardianList(@PathVariable Integer patient_id) {
        List<GuardianDto> guardianList = guardianService.getGuardianList(patient_id);
        return ResponseEntity.ok(guardianList);
    }

    /**
     * 새로운 보호자를 추가합니다.
     *
     * @param patient_id 환자 ID
     * @param name 보호자 이름
     * @param phoneNumber 보호자 전화번호
     * @return 성공 메시지
     */
    @PostMapping("/{patient_id}")
    @ResponseBody
    public ResponseEntity<String> addGuardian(@PathVariable Integer patient_id, @RequestParam String name, @RequestParam String phoneNumber) {
        guardianService.addGuardian(patient_id, name, phoneNumber);
        return ResponseEntity.ok("Guardian added successfully.");
    }

    /**
     * 보호자를 삭제합니다.
     *
     * @param phone_number 보호자 전화번호
     * @return 성공 메시지
     */
    @DeleteMapping("/{phone_number}")
    @ResponseBody
    public ResponseEntity<String> deleteGuardian(@PathVariable String phone_number) {
        guardianService.deleteGuardian(phone_number);
        return ResponseEntity.ok("Guardian deleted successfully.");
    }
}