package com.example.carebridge.controller;


import com.example.carebridge.dto.StaffAccountDto;
import com.example.carebridge.entity.MedicalStaff;
import com.example.carebridge.entity.StaffAccount;
import com.example.carebridge.service.StaffAccountService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "의료진 로그인")
    @PostMapping("/login")
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
    @Operation(summary = "의료진 로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout successful!");
    }

    @Operation(summary = "비밀번호 찾기", description = "의료진 ID를 기준으로 비밀번호를 찾습니다.")
    @GetMapping("/find-password")
    public ResponseEntity<String> findPassword(@RequestParam String Id){
        try {
            return ResponseEntity.ok(staffAccountService.findPassword(Id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to find password: " + e.getMessage());
        }
    }

    @Operation(summary = "비밀번호 재설정", description = "의료진 ID와 기존 비밀번호를 비교하여 새로운 비밀번호로 재설정합니다.")
    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody StaffAccountDto staffAccountDto,
            @RequestParam String newPassword){
        try{
            staffAccountService.resetPassword(staffAccountDto, newPassword);
            return ResponseEntity.ok("password reset successful! new Password : " + newPassword);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reset password: " + e.getMessage());
        }
    }
}
