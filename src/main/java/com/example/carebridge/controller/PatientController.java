package com.example.carebridge.controller;

import com.example.carebridge.dto.PatientDto;
import com.example.carebridge.entity.Patient;
import com.example.carebridge.service.PatientService;
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
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 환자 정보 관리 컨트롤러
 * 환자의 기본 정보 조회, 생성, 수정 등의 기능을 제공합니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/patient")
@Tag(name = "Patient Controller", description = "환자 정보 관리 API")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * 의료진이 담당하는 모든 환자 정보를 조회합니다.
     *
     * @param staffId 의료진 ID
     * @return 환자 목록과 HTTP 상태 코드
     */
    @Operation(summary = "담당 환자 목록 조회", description = "의료진 ID로 담당하는 모든 환자의 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "환자 목록 조회 성공"),
        @ApiResponse(responseCode = "204", description = "조회된 환자가 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/users/{staff_id}")
    @ResponseBody
    public ResponseEntity<List<PatientDto>> getPatientList(
            @Parameter(description = "의료진 ID", required = true)
            @PathVariable("staff_id") int staffId) {
        try {
            log.debug("의료진 ID {}의 담당 환자 목록 조회 요청", staffId);
            List<PatientDto> patientDtoList = patientService.getPatientList(staffId);

            log.debug("의료진 ID {}의 담당 환자 {}명 조회 성공", staffId, patientDtoList.size());
            return new ResponseEntity<>(patientDtoList, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            log.warn("해당 ID의 의료진의 환자 목록을 찾을 수 없습니다 - 의료진 ID: {}", staffId, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청 - 의료진 ID: {}, 오류: {}", staffId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("환자 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 특정 환자의 상세 정보를 조회합니다.
     *
     * @param patientId 환자 ID
     * @return 환자 정보와 HTTP 상태 코드
     */
    @Operation(summary = "환자 상세 정보 조회", description = "환자 ID로 특정 환자의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "환자 정보 조회 성공"),
        @ApiResponse(responseCode = "404", description = "환자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/user/{patient_id}")
    @ResponseBody
    public ResponseEntity<PatientDto> getPatientById(
            @Parameter(description = "환자 ID", required = true)
            @PathVariable("patient_id") int patientId) {
        try {
            log.debug("환자 ID {} 정보 조회 요청", patientId);
            Patient patient = patientService.getPatientById(patientId);
            PatientDto patientDto = patientService.convertToDto(patient);
            log.debug("환자 ID {} 정보 조회 성공", patientId);
            return new ResponseEntity<>(patientDto, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            log.warn("해당 ID의 환자를 찾을 수 없습니다 - 환자 ID: {}", patientId, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청 - 환자 ID: {}, 오류: {}", patientId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("환자 정보 조회 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 환자의 채팅방 존재 여부를 확인합니다.
     *
     * @param patientId 환자 ID
     * @return 채팅방 존재 여부와 HTTP 상태 코드
     */
    @Operation(summary = "채팅방 존재 여부 확인", description = "환자 ID로 해당 환자의 채팅방 존재 여부를 확인합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "채팅방 존재 여부 확인 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/chatroom/{patient_id}")
    @ResponseBody
    public ResponseEntity<Boolean> isChatRoomExist(
            @Parameter(description = "환자 ID", required = true)
            @PathVariable("patient_id") int patientId) {
        try {
            log.debug("환자 ID {}의 채팅방 존재 여부 확인 요청", patientId);
            boolean isChatRoomExist = patientService.isChatRoomExist(patientId);
            log.debug("환자 ID {}의 채팅방 존재 여부: {}", patientId, isChatRoomExist);
            return new ResponseEntity<>(isChatRoomExist, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            log.warn("해당 ID의 환자를 찾을 수 없습니다 - 환자 ID: {}", patientId, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청 - 환자 ID: {}, 오류: {}", patientId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("채팅방 존재 여부 확인 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 새로운 환자 정보를 생성합니다.
     * 사용자 계정이 이미 생성되어 있어야 합니다.
     *
     * @param patient 환자 정보 DTO
     * @return 생성된 환자 정보와 HTTP 상태 코드
     */
    @Operation(summary = "환자 정보 생성", description = "새로운 환자 정보를 생성합니다. 사용자 계정이 필요합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "환자 정보 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/user")
    @ResponseBody
    public ResponseEntity<Patient> createPatient(
            @Parameter(description = "환자 정보", required = true)
            @RequestBody Patient patient) {
        try {
            log.debug("새로운 환자 정보 생성 요청: {}", patient);
            Patient newPatient = patientService.createPatient(patient);
            log.info("새로운 환자 정보 생성 성공. ID: {}", newPatient.getPatientId());
            return new ResponseEntity<>(patient, HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            log.warn("관련 리소스를 찾을 수 없습니다 - 요청 정보: {}", patient, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청 - 요청 정보: {}, 오류: {}", patient, e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("환자 정보 생성 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 환자의 전화번호를 업데이트합니다.
     *
     * @param patientId 환자 ID
     * @param request 새로운 전화번호 정보 {"phoneNumber": "010xxxxxxxx"}
     * @return HTTP 상태 코드
     */
    @Operation(summary = "환자 전화번호 수정", description = "환자의 전화번호 정보를 업데이트합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "전화번호 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 전화번호 형식"),
        @ApiResponse(responseCode = "404", description = "환자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/phone/{patient_id}")
    @ResponseBody
    public ResponseEntity<Void> updatePhoneNumber(
            @Parameter(description = "환자 ID", required = true)
            @PathVariable("patient_id") int patientId,
            @Parameter(description = "새로운 전화번호", required = true)
            @RequestBody Map<String, String> request) {
        try {
            log.debug("환자 ID {}의 전화번호 수정 요청", patientId);
            String newPhoneNumber = request.get("phoneNumber");
            
            if (newPhoneNumber == null || !newPhoneNumber.matches("^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$")) {
                log.error("잘못된 전화번호 형식: {}", newPhoneNumber);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            patientService.updatePhoneNumber(patientId, newPhoneNumber);
            log.info("환자 ID {}의 전화번호 수정 성공", patientId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            log.warn("해당 ID의 환자를 찾을 수 없습니다 - 환자 ID: {}", patientId, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청 - 환자 ID: {}, 오류: {}", patientId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("전화번호 수정 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}