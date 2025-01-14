package com.example.carebridge.controller;

import com.example.carebridge.dto.PatientDto;
import com.example.carebridge.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * 모든 환자 정보를 조회합니다.
     *
     * @return 환자 리스트
     */
    @GetMapping("/users")
    public ResponseEntity<List<PatientDto>> getPatientList() {
        try {
            List<PatientDto> patientDtoList = patientService.getPatientList();
            if (patientDtoList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(patientDtoList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 환자의 ID로 환자 정보를 조회합니다.
     *
     * @param patientId 환자의 ID
     * @return 환자 객체
     */
    @GetMapping("/user/{patient_id}")
    public ResponseEntity<PatientDto> getPatientById(@PathVariable("patient_id") int patientId) {
        try {
            PatientDto patientDto = patientService.getPatientById(patientId);
            if (patientDto == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(patientDto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}