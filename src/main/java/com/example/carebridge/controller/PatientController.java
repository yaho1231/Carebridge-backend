package com.example.carebridge.controller;

import com.example.carebridge.dto.PatientDto;
import com.example.carebridge.entity.Patient;
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
     * 모든 담당 환자 정보를 조회합니다.
     *
     * @return 환자 리스트
     */
    @GetMapping("/users/{staff_id}")
    @ResponseBody
    public ResponseEntity<List<PatientDto>> getPatientList(@PathVariable("staff_id") int staffId) {
        try {
            List<PatientDto> patientDtoList = patientService.getPatientList(staffId);
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
    @ResponseBody
    public ResponseEntity<Patient> getPatientById(@PathVariable("patient_id") int patientId) {
        try {
            Patient patient = patientService.getPatientById(patientId);
            if (patient == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(patient, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 환자의 채팅방 존재 여부를 확인합니다.
     *
     * @param patientId 환자의 ID
     * @return 채팅방 존재 여부
     */
    @GetMapping("/chatroom/{patient_id}")
    @ResponseBody
    public ResponseEntity<Boolean> isChatRoomExist(@PathVariable("patient_id") int patientId) {
        try {
            boolean isChatRoomExist = patientService.isChatRoomExist(patientId);
            return new ResponseEntity<>(isChatRoomExist, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 환자 정보를 생성합니다.
     * User\_Account 가 이미 생성되어 있어야 합니다.
     *
     * @param patientDto 환자 DTO
     * @return 생성된 환자 객체와 HTTP 상태 코드
     */
    @PostMapping("/user")
    @ResponseBody
    public ResponseEntity<Patient> createPatient(@RequestBody PatientDto patientDto) {
        try {
            Patient patient1 = patientService.createPatient(patientDto);
            return new ResponseEntity<>(patient1, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 환자의 전화번호를 업데이트합니다.
     *
     * @param patientId 환자의 ID
     * @param phoneNumber 새로운 전화번호
     * @return HTTP 상태 코드
     */
    @PutMapping("/phone/{patient_id}")
    @ResponseBody
    public ResponseEntity<Void> updatePhoneNumber(@PathVariable("patient_id") int patientId, @RequestBody String phoneNumber) {
        try {
            patientService.updatePhoneNumber(patientId, phoneNumber);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}