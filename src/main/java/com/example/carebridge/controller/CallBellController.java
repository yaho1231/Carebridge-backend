package com.example.carebridge.controller;

import com.example.carebridge.dto.RequestDto;
import com.example.carebridge.service.CallBellService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("api/call-bell")
public class CallBellController {

    private final CallBellService callBellService;

    /**
     * 요청 상태를 업데이트합니다.
     *
     * @param requestId 요청 ID
     * @param status 새로운 상태
     * @return 성공 메시지
     */
    @PutMapping("/request/status/{request_id}")
    @ResponseBody
    public ResponseEntity<String> acceptRequest(@PathVariable("request_id") int requestId, @RequestParam("status") String status) {
        try {
            callBellService.updateRequestStatus(requestId, status);
            return new ResponseEntity<>("Request status updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update request status", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 특정 의료진의 모든 요청을 조회합니다.
     *
     * @param medicalStaffId 의료진 ID
     * @return 요청 목록
     */
    @GetMapping("/request/{staff_id}")
    @ResponseBody
    public ResponseEntity<List<RequestDto>> getAllRequest(@PathVariable("staff_id") int medicalStaffId) {
        try {
            List<RequestDto> request = callBellService.getAllRequests(medicalStaffId);
            return new ResponseEntity<>(request, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 특정 환자의 모든 요청을 조회합니다.
     *
     * @param patientId 환자 ID
     * @return 요청 목록
     */
    @GetMapping("/request/{patient_id}")
    @ResponseBody
    public ResponseEntity<List<RequestDto>> getPatientRequestList(@PathVariable("patient_id") int patientId) {
        try {
            List<RequestDto> request = callBellService.getPatientRequests(patientId);
            return new ResponseEntity<>(request, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 특정 요청을 삭제합니다.
     *
     * @param requestId 요청 ID
     * @return 성공 메시지
     */
    @DeleteMapping("/request/{request_id}")
    @ResponseBody
    public ResponseEntity<String> deleteRequest(@PathVariable("request_id") int requestId) {
        try {
            callBellService.deleteRequest(requestId);
            return new ResponseEntity<>("Request deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete request", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}