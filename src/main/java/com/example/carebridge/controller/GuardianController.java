package com.example.carebridge.controller;

import com.example.carebridge.dto.GuardianDto;
import com.example.carebridge.service.GuardianService;
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
 * 보호자 정보 관리 컨트롤러
 * 보호자의 등록, 조회, 삭제 등 보호자 관련 기능을 제공하는 REST API 컨트롤러입니다.
 */
@Slf4j
@RestController
@RequestMapping("api/guardian")
@Tag(name = "Guardian Controller", description = "보호자 정보 관리 API")
public class GuardianController {
    private final GuardianService guardianService;
    private static final String PHONE_NUMBER_REGEX = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$";

    /**
     * GuardianService 를 주입받는 생성자
     *
     * @param guardianService 보호자 서비스
     */
    public GuardianController(GuardianService guardianService) {
        this.guardianService = guardianService;
    }

    /**
     * 전화번호 형식이 유효하지 않은지 검증합니다.
     *
     * @param phoneNumber 검증할 전화번호
     * @return 유효하지 않은 경우 true, 유효한 경우 false
     */
    private boolean isInvalidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return true;
        }
        return !phoneNumber.matches(PHONE_NUMBER_REGEX);
    }

    /**
     * 보호자 정보를 전화번호로 조회합니다.
     *
     * @param phone_number 보호자 전화번호 (필수)
     * @return ResponseEntity<GuardianDto> 보호자 정보와 HTTP 상태
     * @throws IllegalArgumentException 전화번호가 유효하지 않은 경우
     */
    @Operation(summary = "보호자 정보 조회", description = "전화번호로 보호자의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "보호자 정보 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 전화번호 형식"),
        @ApiResponse(responseCode = "404", description = "보호자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/info/{phone_number}")
    @ResponseBody
    public ResponseEntity<GuardianDto> getGuardianInfo(
            @Parameter(description = "보호자 전화번호", required = true)
            @PathVariable String phone_number) {
        try {
            log.debug("보호자 정보 조회 요청 - 전화번호: {}", phone_number);
            
            if (isInvalidPhoneNumber(phone_number)) {
                log.error("보호자 정보 조회 실패 - 유효하지 않은 전화번호: {}", phone_number);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            GuardianDto guardianDto = guardianService.getGuardianInfo(phone_number);
            
            if (guardianDto == null) {
                log.info("보호자를 찾을 수 없음 - 전화번호: {}", phone_number);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            log.debug("보호자 정보 조회 성공 - 전화번호: {}", phone_number);
            return new ResponseEntity<>(guardianDto, HttpStatus.OK);
        } catch (Exception e) {
            log.error("보호자 정보 조회 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 환자의 모든 보호자 정보를 조회합니다.
     *
     * @param patient_id 환자 ID (필수)
     * @return ResponseEntity<List<GuardianDto>> 보호자 목록과 HTTP 상태
     */
    @Operation(summary = "환자별 보호자 목록 조회", description = "특정 환자의 모든 보호자 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "보호자 목록 조회 성공"),
        @ApiResponse(responseCode = "204", description = "등록된 보호자 없음"),
        @ApiResponse(responseCode = "404", description = "환자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/list/{patient_id}")
    @ResponseBody
    public ResponseEntity<List<GuardianDto>> getGuardianList(
            @Parameter(description = "환자 ID", required = true)
            @PathVariable Integer patient_id) {
        try {
            log.debug("환자 ID {}의 보호자 목록 조회 요청", patient_id);
            List<GuardianDto> guardianList = guardianService.getGuardianList(patient_id);
            
            if (guardianList.isEmpty()) {
                log.info("환자 ID {}의 등록된 보호자가 없습니다.", patient_id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            log.debug("환자 ID {}의 보호자 {}명 조회 성공", patient_id, guardianList.size());
            return new ResponseEntity<>(guardianList, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("환자를 찾을 수 없음 - ID: {}", patient_id, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("보호자 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 새로운 보호자를 등록합니다.
     *
     * @param patient_id 환자 ID (필수)
     * @param name 보호자 이름 (필수)
     * @param phoneNumber 보호자 전화번호 (필수)
     * @return ResponseEntity<String> 등록 결과 메시지와 HTTP 상태
     */
    @Operation(summary = "보호자 등록", description = "환자에 새로운 보호자를 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "보호자 등록 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "환자를 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "이미 등록된 보호자"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/{patient_id}")
    @ResponseBody
    public ResponseEntity<String> addGuardian(
            @Parameter(description = "환자 ID", required = true)
            @PathVariable Integer patient_id,
            @Parameter(description = "보호자 이름", required = true)
            @RequestParam String name,
            @Parameter(description = "보호자 전화번호", required = true)
            @RequestParam String phoneNumber) {
        try {
            log.debug("보호자 등록 요청 - 환자 ID: {}, 이름: {}, 전화번호: {}", patient_id, name, phoneNumber);
            
            if (isInvalidPhoneNumber(phoneNumber)) {
                log.error("보호자 등록 실패 - 전화번호 형식 오류: {}", phoneNumber);
                return new ResponseEntity<>("Invalid phone number format", HttpStatus.BAD_REQUEST);
            }
            
            guardianService.addGuardian(patient_id, name, phoneNumber);
            log.info("보호자 등록 성공 - 환자 ID: {}, 전화번호: {}", patient_id, phoneNumber);
            return new ResponseEntity<>("보호자가 성공적으로 등록되었습니다.", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.error("보호자 등록 실패 - 잘못된 요청: {}", e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("보호자 등록 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 보호자 정보를 삭제합니다.
     *
     * @param phone_number 보호자 전화번호 (필수)
     * @return ResponseEntity<String> 삭제 결과 메시지와 HTTP 상태
     */
    @Operation(summary = "보호자 삭제", description = "등록된 보호자를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "보호자 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 전화번호 형식"),
        @ApiResponse(responseCode = "404", description = "보호자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{phone_number}")
    @ResponseBody
    public ResponseEntity<String> deleteGuardian(
            @Parameter(description = "보호자 전화번호", required = true)
            @PathVariable String phone_number) {
        try {
            log.debug("보호자 삭제 요청 - 전화번호: {}", phone_number);
            
            if (isInvalidPhoneNumber(phone_number)) {
                log.error("보호자 삭제 실패 - 올바르지 않은 전화번호 형식: {}", phone_number);
                return new ResponseEntity<>("Invalid phone number format", HttpStatus.BAD_REQUEST);
            }
            
            guardianService.deleteGuardian(phone_number);
            log.info("보호자 삭제 성공 - 전화번호: {}", phone_number);
            return new ResponseEntity<>("보호자가 성공적으로 삭제되었습니다.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("보호자를 찾을 수 없음 - 전화번호: {}", phone_number, e);
            return new ResponseEntity<>("존재하지 않는 보호자입니다.", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("보호자 삭제 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}