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

    @GetMapping("/request/{staff_id}")
    @ResponseBody
    public ResponseEntity<List<RequestDto>> getRequest(@PathVariable("staff_id") int medicalStaffId) {
        try {
            List<RequestDto> request = callBellService.getAllRequests(medicalStaffId);
            return new ResponseEntity<>(request, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}