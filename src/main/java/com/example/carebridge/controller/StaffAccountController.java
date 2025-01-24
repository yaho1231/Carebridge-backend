package com.example.carebridge.controller;


import com.example.carebridge.dto.StaffAccountDto;
import com.example.carebridge.entity.MedicalStaff;
import com.example.carebridge.entity.StaffAccount;
import com.example.carebridge.service.StaffAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffAccountController {

    private final StaffAccountService staffAccountService;
    public StaffAccountController(StaffAccountService staffAccountService) {
        this.staffAccountService = staffAccountService;
    }

    /**
     * Medical Staff Login
     * @param staffAccountDto
     * @return
     */
    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody StaffAccountDto staffAccountDto) {
        try {
            staffAccountService.login(staffAccountDto);
            return ResponseEntity.ok("Login successful!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

}
