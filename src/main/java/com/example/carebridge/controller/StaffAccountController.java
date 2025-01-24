package com.example.carebridge.controller;


import com.example.carebridge.dto.StaffAccountDto;
import com.example.carebridge.entity.MedicalStaff;
import com.example.carebridge.entity.StaffAccount;
import com.example.carebridge.service.StaffAccountService;
import jakarta.servlet.http.HttpSession;
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
     * Medical Staff login
     * @param staffAccountDto
     * @return
     */
    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody StaffAccountDto staffAccountDto, HttpSession session) {
        Boolean verify = staffAccountService.veriftStaffAccount(staffAccountDto);
        if (verify) {
            session.setAttribute("userId", staffAccountDto.getUserId());
            return ResponseEntity.ok("Login successful!");
        }
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Id or password.");
    }

    /**
     * Staff logout
     * @param session
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout successful!");
    }
}
