package com.example.carebridge.controller;


import com.example.carebridge.dto.StaffAccountDto;
import com.example.carebridge.entity.MedicalStaff;
import com.example.carebridge.entity.StaffAccount;
import com.example.carebridge.service.StaffAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공 (병원 ID 반환)"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (잘못된 ID 또는 비밀번호)"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/login")
    public ResponseEntity<Integer> login(@RequestBody StaffAccountDto staffAccountDto, HttpSession session) {
        try {
            Boolean verify = staffAccountService.verifyStaffAccount(staffAccountDto);
            if (verify) {
                session.setAttribute("userId", staffAccountDto.getUserId());
                StaffAccount staffAccount = staffAccountService.findStaffAccountByUserId(staffAccountDto.getUserId());
                Integer hospitalId = staffAccount.getHospitalId();
                return ResponseEntity.ok(hospitalId);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } catch (NoSuchElementException e) {
            log.warn("해당 ID의 계정을 찾을 수 없습니다 - 사용자 ID: {}", staffAccountDto.getUserId(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 로그인 요청 - 사용자 ID: {}, 오류: {}", staffAccountDto.getUserId(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            log.error("로그인 처리 중 서버 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
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
            log.debug("비밀번호 찾기 요청 - 사용자 ID: {}", Id);
            String password = staffAccountService.findPassword(Id);
            log.info("비밀번호 찾기 성공 - 사용자 ID: {}", Id);
            return ResponseEntity.ok(password);
        } catch (NoSuchElementException e) {
            log.warn("해당 ID의 계정을 찾을 수 없습니다 - 사용자 ID: {}", Id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("계정을 찾을 수 없습니다.");
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청 - 사용자 ID: {}, 오류: {}", Id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("비밀번호 찾기 중 서버 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("비밀번호 찾기 중 오류가 발생했습니다.");
        }
    }

    @Operation(summary = "비밀번호 재설정", description = "의료진 ID와 기존 비밀번호를 비교하여 새로운 비밀번호로 재설정합니다.")
    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody StaffAccountDto staffAccountDto,
            @RequestParam String newPassword){
        try {
            log.debug("비밀번호 초기화 요청 - 사용자 ID: {}", staffAccountDto.getUserId());
            staffAccountService.resetPassword(staffAccountDto, newPassword);
            log.info("비밀번호 초기화 성공 - 사용자 ID: {}", staffAccountDto.getUserId());
            return ResponseEntity.ok("비밀번호 초기화 성공! 새로운 비밀번호: " + newPassword);
        } catch (NoSuchElementException e) {
            log.warn("해당 ID의 계정을 찾을 수 없습니다 - 사용자 ID: {}", staffAccountDto.getUserId(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("계정을 찾을 수 없습니다.");
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청 - 사용자 ID: {}, 오류: {}", staffAccountDto.getUserId(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("비밀번호 초기화 중 서버 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("비밀번호 초기화 중 오류가 발생했습니다.");
        }
    }
}
