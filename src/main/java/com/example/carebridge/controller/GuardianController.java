package com.example.carebridge.controller;

import com.example.carebridge.dto.GuardianDto;
import com.example.carebridge.service.GuardianService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/guardian")
public class GuardianController {
    private final GuardianService guardianService;

    public GuardianController(GuardianService guardianService) {
        this.guardianService = guardianService;
    }

    /**
     * 보호자 정보를 조회합니다.
     *
     * @param guardian_id 보호자 ID
     * @return 보호자 정보 DTO
     */
    @GetMapping("/info/{guardian_id}")
    @ResponseBody
    public ResponseEntity<GuardianDto> getGuardianInfo(@PathVariable String guardian_id) {
        GuardianDto guardianDto = guardianService.getGuardianInfo(guardian_id);
        return ResponseEntity.ok(guardianDto);
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
     * @param guardian_id 보호자 ID
     * @return 성공 메시지
     */
    @DeleteMapping("/{guardian_id}")
    @ResponseBody
    public ResponseEntity<String> deleteGuardian(@PathVariable String guardian_id) {
        guardianService.deleteGuardian(guardian_id);
        return ResponseEntity.ok("Guardian deleted successfully.");
    }
}