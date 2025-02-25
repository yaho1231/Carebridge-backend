package com.example.carebridge.controller;

import com.example.carebridge.dto.KakaoDto;
import com.example.carebridge.dto.UserAccountDto;
import com.example.carebridge.dto.VerifyAccountDto;
import com.example.carebridge.entity.Patient;
import com.example.carebridge.entity.UserAccount;
import com.example.carebridge.service.OAuthService;
import com.example.carebridge.service.PatientService;
import com.example.carebridge.service.UserAccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserAccountController {

    private final UserAccountService userAccountService;
    private final OAuthService oAuthService;
    private final PatientService patientService;

    public UserAccountController(UserAccountService userAccountService, OAuthService oAuthService, PatientService patientService) {
        this.userAccountService = userAccountService;
        this.oAuthService = oAuthService;
        this.patientService = patientService;
    }

    /**
     * User profile 검색
     * @param phoneNumber
     * @return
     */
    @GetMapping("/profile/{phoneNumber}")
    public UserAccountDto openProfile(@PathVariable("phoneNumber") String phoneNumber) {
        return userAccountService.getUserAccount(phoneNumber);
    }

    /**
     * User profile 수정
     * @param phoneNumber
     * @param userAccountDto
     * @return
     */
    @PutMapping("/profile/{phoneNumber}")
    public UserAccountDto modifyProfile(@PathVariable("phoneNumber") String phoneNumber, @RequestBody UserAccountDto userAccountDto) {
        return userAccountService.updateUserAccount(phoneNumber, userAccountDto);
    }

    /**
     * phone 으로 인증문자(otp 포함) 전송
     * @param phoneNumber
     * @param isSignup
     * @return
     */
    @PostMapping("/send-otp/{phoneNumber}")
    public ResponseEntity<String> sendOtp(@PathVariable String phoneNumber, @RequestParam boolean isSignup) {
        try {
            userAccountService.sendOtp(phoneNumber, isSignup); // OTP 전송 로직 호출
            return ResponseEntity.ok("OTP sent successfully to " + phoneNumber);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send OTP: " + e.getMessage());
        }
    }

    /**
     * 인증번호(otp)를 데이터베이스와 비교하여 검증
     * @param verifyAccountDto
     * @return
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verify(@RequestBody VerifyAccountDto verifyAccountDto) {
//        HttpSession session = request.getSession();
        boolean isVerified = userAccountService.verifyOtp(verifyAccountDto);

        if (isVerified) {
//            session.setAttribute("loginUser", userAccountDto.getPhoneNumber());
            return ResponseEntity.ok("Login successful!");
        } else {
//            String failMessage = "인증번호 혹은 전화번호가 잘못 되었습니다.";
//            rttr.addFlashAttribute("loginFail", failMessage);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid phone number or otp.");
        }
    }

    /**
     * User SignUp
     * @param userAccountDto
     * @return
     */
    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody UserAccountDto userAccountDto) {
        try {
            userAccountService.createUserAccount(userAccountDto);
            return ResponseEntity.ok("Sign Up successful!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create user account: " + e.getMessage());
        }

    }

    /**
     * User Login
     * @param verifyAccountDto
     * @param session
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Integer>> login(@RequestBody VerifyAccountDto verifyAccountDto, HttpSession session) {
        boolean isVerified = userAccountService.verifyOtp(verifyAccountDto);
        boolean isValid = userAccountService.isValidUserAccount(verifyAccountDto.getPhone());
        Patient patient = patientService.getPatientByPhone(verifyAccountDto.getPhone());
        if (patient == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        Integer patientId = patient.getPatientId();
        Integer userId = patient.getUserId();

        if (isVerified && isValid) {
            // 세션에 사용자 전화번호 저장 (자동 로그인 기능을 위한 세션 활용)
            session.setAttribute("userPhone", verifyAccountDto.getPhone());
            log.info("세션 생성됨: " + session.getId());

            Map<String, Integer> response = new HashMap<>();
            response.put("userId", userId);
            response.put("patientId", patientId);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    /**
     * User logout
     * @param session
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        log.info("세션 삭제됨: " + session.getId());
        session.invalidate();
        return ResponseEntity.ok("Logout successful!");
    }

    /**
     * get kakao login link
     * 카카오 로그인 기능
     * 인가코드 받는 링크 (login 페이지 link 요청)
     * @return
     */
    @GetMapping("/social-login/kakao")
    public String kakaoConnect() {
        return oAuthService.getKakaoLogin();
    }

    /**
     * 사용자 동의항목 페이지 초기화 기능
     * @param token
     * @throws Exception
     */
    @GetMapping("/social-login/kakao/unLink/{token}")
    public void kakaoUnLink(@PathVariable String token) throws Exception {
        oAuthService.unlinkKakaoAccount(token);
    }

    /**
     * 카카오 로그인 매핑 (url로 사용 X)
     * 인가 code가 있어야 사용 가능 함.
     * 기본은 /social-login/kakao 를 통해 받는 login 페이지를 통해 접속
     * @return
     */
    @GetMapping("/social-login/kakao/token")
    public ResponseEntity<String> kakaoLogin(HttpServletRequest request, HttpSession session) throws Exception {
        String code = request.getParameter("code");
        if (code == null)
            return ResponseEntity.badRequest().body("Authorization code is missing");
        // 1. Access Token 발급
        String accessToken = oAuthService.getKakaoToken(code);
        // 2. 사용자 정보 조회
        KakaoDto kakaoDto = oAuthService.getUserInfoWithToken(accessToken);
        // 3. 서비스 사용자 정보 매핑 및 세션 저장
        UserAccountDto kakaoUserAccount = oAuthService.ifNeedKakaoInfo(kakaoDto);
        if (kakaoUserAccount != null) {
            session.setAttribute("loginUser", kakaoUserAccount);
            session.setMaxInactiveInterval(60 * 60); // 60분 유지
            session.setAttribute("kakaoToken", accessToken);
            return ResponseEntity.ok("Login successful. Redirect to /dashboard");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed. Redirect to /login?error");
        }
    }

    /**
     * 카카오 로그아웃
     * @return
     */
    @GetMapping("/social-login/kakao/logout")
    public ResponseEntity<String> kakaoLogout(HttpSession session) throws JsonProcessingException {
        String accessToken = (String) session.getAttribute("kakaoToken");
        if (accessToken != null) {
            oAuthService.kakaoDisconnect(accessToken);
        }
        session.invalidate(); // 세션 완전 종료
        return ResponseEntity.ok("Logout successful");
    }

    /**
     * 자동으로 로그인 상태를 유지하기 위해
     * 세션에 저장된 정보를 확인
     * @param session
     * @return
     */
    @GetMapping("/session-check")
    public ResponseEntity<String> checkSession(HttpSession session) {
        String userPhone = (String) session.getAttribute("userPhone");
        log.info("세션 확인: " + session.getId());
        log.info("세션 전화번호 확인: " + session.getAttribute(userPhone));

        if (userPhone != null) {
            return ResponseEntity.ok("User is logged in with phone: " + userPhone);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
    }
}
