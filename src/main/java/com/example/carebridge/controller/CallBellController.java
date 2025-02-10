package com.example.carebridge.controller;

import com.example.carebridge.dto.RequestDto;
import com.example.carebridge.entity.Request;
import com.example.carebridge.service.CallBellService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 콜벨(호출) 관리 컨트롤러
 * 환자의 호출 요청을 관리하는 REST API 컨트롤러입니다.
 */
@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("api/call-bell")
@Tag(name = "Call Bell Controller", description = "콜벨(호출) 관리 API")
public class CallBellController {

    private final CallBellService callBellService;

    /**
     * 요청 상태를 업데이트합니다.
     *
     * @param requestId 요청 ID
     * @param status   새로운 상태 값
     * @return 처리 결과와 HTTP 상태 코드
     */
    @Operation(summary = "요청 상태 업데이트", description = "환자의 호출 요청 상태를 업데이트합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "상태 업데이트 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "요청을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/request/status/{request_id}")
    @ResponseBody
    public ResponseEntity<String> acceptRequest(
            @Parameter(description = "요청 ID", required = true)
            @PathVariable("request_id") int requestId,
            @Parameter(description = "새로운 상태", required = true, example = "ACCEPTED/COMPLETED/REJECTED")
            @RequestParam("status") String status) {
        try {
            log.debug("요청 상태 업데이트 시도 - 요청 ID: {}, 새로운 상태: {}", requestId, status);
            
            // 상태값 유효성 검사
            if (!isValidStatus(status)) {
                log.error("잘못된 상태값: {}", status);
                return new ResponseEntity<>("유효하지 않은 상태값입니다.", HttpStatus.BAD_REQUEST);
            }
            
            callBellService.updateRequestStatus(requestId, status);
            log.info("요청 상태 업데이트 성공 - 요청 ID: {}, 상태: {}", requestId, status);
            return new ResponseEntity<>("요청 상태가 성공적으로 업데이트되었습니다.", HttpStatus.OK);
            
        } catch (IllegalArgumentException e) {
            log.error("요청을 찾을 수 없음 - 요청 ID: {}", requestId, e);
            return new ResponseEntity<>("해당 요청을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("요청 상태 업데이트 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 특정 의료진의 모든 요청을 조회합니다.
     *
     * @param medicalStaffId 의료진 ID
     * @return 요청 목록과 HTTP 상태 코드
     */
    @Operation(summary = "의료진별 요청 목록 조회", description = "특정 의료진에게 할당된 모든 요청을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청 목록 조회 성공"),
        @ApiResponse(responseCode = "204", description = "요청이 없음"),
        @ApiResponse(responseCode = "404", description = "의료진을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/request/staff/{staff_id}")
    @ResponseBody
    public ResponseEntity<List<RequestDto>> getAllRequest(
            @Parameter(description = "의료진 ID", required = true)
            @PathVariable("staff_id") int medicalStaffId) {
        try {
            log.debug("의료진 요청 목록 조회 시도 - 의료진 ID: {}", medicalStaffId);
            List<RequestDto> requests = callBellService.getAllRequests(medicalStaffId);
            
            if (requests.isEmpty()) {
                log.info("의료진의 요청이 없음 - 의료진 ID: {}", medicalStaffId);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            log.debug("의료진 요청 목록 조회 성공 - 의료진 ID: {}, 요청 수: {}", medicalStaffId, requests.size());
            return new ResponseEntity<>(requests, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("의료진을 찾을 수 없음 - ID: {}", medicalStaffId, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("요청 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 특정 환자의 모든 요청을 조회합니다.
     *
     * @param patientId 환자 ID
     * @return 요청 목록과 HTTP 상태 코드
     */
    @Operation(summary = "환자별 요청 목록 조회", description = "특정 환자의 모든 요청을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청 목록 조회 성공"),
        @ApiResponse(responseCode = "204", description = "요청이 없음"),
        @ApiResponse(responseCode = "404", description = "환자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/request/patient/{patient_id}")
    @ResponseBody
    public ResponseEntity<List<RequestDto>> getPatientRequestList(
            @Parameter(description = "환자 ID", required = true)
            @PathVariable("patient_id") int patientId) {
        try {
            log.debug("환자 요청 목록 조회 시도 - 환자 ID: {}", patientId);
            List<RequestDto> requests = callBellService.getPatientRequests(patientId);
            
            if (requests.isEmpty()) {
                log.info("환자의 요청이 없음 - 환자 ID: {}", patientId);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            log.debug("환자 요청 목록 조회 성공 - 환자 ID: {}, 요청 수: {}", patientId, requests.size());
            return new ResponseEntity<>(requests, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("환자를 찾을 수 없음 - ID: {}", patientId, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("요청 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 특정 요청을 삭제합니다.
     *
     * @param requestId 요청 ID
     * @return 처리 결과와 HTTP 상태 코드
     */
    @Operation(summary = "요청 삭제", description = "특정 요청을 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "요청을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/request/{request_id}")
    @ResponseBody
    public ResponseEntity<String> deleteRequest(
            @Parameter(description = "요청 ID", required = true)
            @PathVariable("request_id") int requestId) {
        try {
            log.debug("요청 삭제 시도 - 요청 ID: {}", requestId);
            callBellService.deleteRequest(requestId);
            log.info("요청 삭제 성공 - 요청 ID: {}", requestId);
            return new ResponseEntity<>("요청이 성공적으로 삭제되었습니다.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("요청을 찾을 수 없음 - 요청 ID: {}", requestId, e);
            return new ResponseEntity<>("해당 요청을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("요청 삭제 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 상태값의 유효성을 검사합니다.
     *
     * @param status 검사할 상태값
     * @return 유효성 여부
     */
    private boolean isValidStatus(String status) {
        return status != null && (
            status.equals("ACCEPTED") ||
            status.equals("COMPLETED") ||
            status.equals("REJECTED")
        );
    }
}