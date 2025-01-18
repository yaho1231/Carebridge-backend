package com.example.carebridge.controller;

import com.example.carebridge.dto.HospitalInformationDto;
import com.example.carebridge.entity.HospitalInformation;
import com.example.carebridge.service.HospitalInformationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 병원 정보 관련 API 컨트롤러
 * 병원 정보와 관련된 다양한 API 엔드포인트를 제공합니다.
 */
@RestController
@RequestMapping("/api/hospital-info")
public class HospitalInformationController {

    private final HospitalInformationService hospitalInformationService;

    /**
     * HospitalInformationService 를 주입받는 생성자
     *
     * @param hospitalInformationService 병원 정보 서비스
     */
    public HospitalInformationController(HospitalInformationService hospitalInformationService) {
        this.hospitalInformationService = hospitalInformationService;
    }

    /**
     * 주어진 프롬프트와 가장 유사한 병원 정보를 검색합니다.
     *
     * @param prompt 프롬프트 내용
     * @param hospital_id 병원 ID
     * @return 가장 유사한 병원 정보
     */
    @GetMapping("/search/{hospital_id}")
    @ResponseBody
    public HospitalInformation searchHospitalInformation(@RequestParam String prompt, @PathVariable int hospital_id) {
        return hospitalInformationService.findMostSimilarHospitalInformation(prompt, hospital_id);
    }

    /**
     * 특정 병원의 모든 병원 정보를 조회합니다.
     *
     * @param hospital_id 병원 ID
     * @return 병원 정보 리스트
     */
    @GetMapping("/list/{hospital_id}")
    @ResponseBody
    public ResponseEntity<List<HospitalInformationDto>> getHospitalInformationList(@PathVariable int hospital_id) {
        List<HospitalInformationDto> hospitalInformationList = hospitalInformationService.getHospitalInformationList(hospital_id);
        return ResponseEntity.ok(hospitalInformationList);
    }

    /**
     * 특정 병원의 특정 제목을 가진 병원 정보를 조회합니다.
     *
     * @param hospital_id 병원 ID
     * @param title 정보 제목
     * @return 병원 정보 내용
     */
    @GetMapping("/{hospital_id}/{title}")
    @ResponseBody
    public ResponseEntity<String> getHospitalInformation(@PathVariable int hospital_id, @PathVariable String title) {
        try {
            return ResponseEntity.ok(hospitalInformationService.getHospitalInformation(hospital_id, title));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 새로운 병원 정보를 추가합니다.
     *
     * @param hospital_id 병원 ID
     * @param hospitalInformationDto 병원 정보 DTO
     * @return 성공 메시지
     */
    @PostMapping("/{hospital_id}")
    @ResponseBody
    public ResponseEntity<String> addHospitalInformation(@PathVariable int hospital_id, @RequestBody HospitalInformationDto hospitalInformationDto) {
        try {
            hospitalInformationService.addHospitalInformation(hospital_id, hospitalInformationDto);
            return ResponseEntity.ok("Hospital information added successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error adding hospital information: " + e.getMessage());
        }
    }

    /**
     * 기존 병원 정보를 업데이트합니다.
     *
     * @param hospital_id 병원 ID
     * @param title 정보 제목
     * @param information 새로운 정보 내용
     * @return 성공 메시지
     */
    @PutMapping("/{hospital_id}/{title}")
    @ResponseBody
    public ResponseEntity<String> updateHospitalInformation(@PathVariable int hospital_id, @PathVariable String title, @RequestParam String information) {
        try {
            hospitalInformationService.updateHospitalInformation(hospital_id, title, information);
            return ResponseEntity.ok("Hospital information updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating hospital information: " + e.getMessage());
        }
    }

    /**
     * 기존 병원 정보를 삭제합니다.
     *
     * @param hospital_id 병원 ID
     * @param title 정보 제목
     * @return 성공 메시지
     */
    @DeleteMapping("/{hospital_id}/{title}")
    @ResponseBody
    public ResponseEntity<String> deleteHospitalInformation(@PathVariable int hospital_id, @PathVariable String title) {
        try {
            hospitalInformationService.deleteHospitalInformation(hospital_id, title);
            return ResponseEntity.ok("Hospital information deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting hospital information: " + e.getMessage());
        }
    }
}