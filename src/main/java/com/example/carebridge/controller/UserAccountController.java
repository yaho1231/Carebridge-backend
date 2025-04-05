package com.example.carebridge.controller;

import com.example.carebridge.dto.KakaoDto;
import com.example.carebridge.dto.UserAccountDto;
import com.example.carebridge.dto.UserLoginDto;
import com.example.carebridge.dto.VerifyAccountDto;
import com.example.carebridge.entity.Patient;
import com.example.carebridge.entity.UserAccount;
import com.example.carebridge.service.OAuthService;
import com.example.carebridge.service.PatientService;
import com.example.carebridge.service.UserAccountService;
import com.example.carebridge.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserAccountController {

    private final UserAccountService userAccountService;
    private final OAuthService oAuthService;
    private final PatientService patientService;
    private final JwtUtil jwtUtil;

    public UserAccountController(UserAccountService userAccountService, OAuthService oAuthService, PatientService patientService, JwtUtil jwtUtil) {
        this.userAccountService = userAccountService;
        this.oAuthService = oAuthService;
        this.patientService = patientService;
        this.jwtUtil = jwtUtil;
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
    @Operation(summary = "OTP 전송", description = "회원가입 또는 로그인 시 필요한 OTP를 지정된 전화번호로 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP 전송 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "해당 전화번호를 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "OTP 전송 불가 (유효하지 않은 상태)"),
            @ApiResponse(responseCode = "500", description = "서버 오류로 인해 OTP 전송 실패")
    })
    @PostMapping("/send-otp/{phoneNumber}")
    public ResponseEntity<String> sendOtp(@PathVariable String phoneNumber, @RequestParam boolean isSignup) {
        try {
            userAccountService.sendOtp(phoneNumber, isSignup); // OTP 전송 로직 호출
            return ResponseEntity.ok("OTP sent successfully to " + phoneNumber);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such phone number");
        } catch (IllegalStateException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send OTP: " + e.getMessage());
        }
    }

    /**
     * 인증번호(otp)를 데이터베이스와 비교하여 검증
     * @param verifyAccountDto
     * @return
     */
    @Operation(summary = "OTP 검증", description = "사용자가 입력한 OTP를 검증하여 인증을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP 인증 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "해당 전화번호를 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "OTP 인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류로 인해 OTP 인증 실패")
    })
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verify(@RequestBody VerifyAccountDto verifyAccountDto) {
        try {
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
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such phone number");
        }

    }

    /**
     * User SignUp
     * @param userAccountDto
     * @return
     */
    @Operation(summary = "회원가입", description = "새로운 사용자 계정을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 계정 (중복된 키)"),
            @ApiResponse(responseCode = "500", description = "서버 오류로 인해 회원가입 실패")
    })
    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody UserAccountDto userAccountDto) {
        try {
            userAccountService.createUserAccount(userAccountDto);
            return ResponseEntity.ok("Sign Up successful!");
        } catch (DuplicateKeyException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
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
    @Operation(summary = "계정 인증", description = "OTP 인증을 수행하고, 성공 시 JWT를 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 성공 및 JWT 발급"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "OTP 인증 실패 또는 유효하지 않은 계정 상태"),
            @ApiResponse(responseCode = "404", description = "등록된 환자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/login")
    public ResponseEntity<UserLoginDto> login(@RequestBody VerifyAccountDto verifyAccountDto, HttpSession session) {
        try {
            boolean isVerified = userAccountService.verifyOtp(verifyAccountDto);
            boolean isValid = userAccountService.isValidUserAccount(verifyAccountDto.getPhone());
            Patient patient = patientService.getPatientByPhone(verifyAccountDto.getPhone());
            if (patient == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            Integer patientId = patient.getPatientId();
            Integer userId = patient.getUserId();

            if (isVerified && isValid) {
                //jwt 발급
                JwtUtil.TokenPair token = jwtUtil.generateTokens(verifyAccountDto.getPhone());
                // 세션에 사용자 전화번호 저장 (자동 로그인 기능을 위한 세션 활용)
//            session.setAttribute("userPhone", verifyAccountDto.getPhone());
//            log.info("세션 생성됨: " + session.getId());
//            log.info("세션 확인용" + session.getAttribute("userPhone"));
                UserLoginDto userLoginDto = new UserLoginDto();
                userLoginDto.setUserId(userId);
                userLoginDto.setAccessToken(token.accessToken);
                userLoginDto.setRefreshToken(token.refreshToken);
                userLoginDto.setPatientId(patientId);
                userLoginDto.setPhoneNumber(verifyAccountDto.getPhone());

                return ResponseEntity.ok(userLoginDto);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * User logout
     * @param phoneNumber
     * @return
     */
    @Operation(summary = "사용자 logout", description = "사용자를 로그아웃하고 Refresh Token을 무효화합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공(Refresh Token 무효화)"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/logout")
//    public ResponseEntity<String> logout(HttpSession session) {
    public ResponseEntity<String> logout(String phoneNumber) {
//        log.info("세션 삭제됨: " + session.getId());
//        session.invalidate();
        jwtUtil.invalidateRefreshToken(phoneNumber);
        return ResponseEntity.ok("Logout successful!");
    }

    /**
     * Refresh 토큰을 사용해 Access 토큰 재발급
     * @param refreshToken
     * @return
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken) {
        try {
            JwtUtil.TokenPair tokens = jwtUtil.refreshAccessToken(refreshToken);
            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid Refresh Token");
        }
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
    @Operation(summary = "카카오 로그인", description = "카카오 OAuth를 통해 로그인하고 사용자 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공, 사용자 정보 반환"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (code 값 없음)"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류로 인해 로그인 실패")
    })
    @GetMapping("/social-login/kakao/token")
    public ResponseEntity<?> kakaoLogin(HttpServletRequest request, HttpSession session) throws Exception {
        try {
            String code = request.getParameter("code");
            if (code == null) {
//            return ResponseEntity.status(HttpStatus.FOUND)
//                    .location(URI.create("/login?error=missing_code"))
//                    .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
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

                String email = kakaoDto.getEmail();
                Patient patient = patientService.getPatientByEmail(email);
                Integer patientId = patient.getPatientId();

            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create("https://carebridge-frontend.vercel.app/redirection?patientId=" + patientId));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
//                return ResponseEntity.ok(patientId);
            } else {
//            return ResponseEntity.status(HttpStatus.FOUND)
//                    .location(URI.create("/login?error=unauthorized"))
//                    .build();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
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

//    /**
//     * 자동으로 로그인 상태를 유지하기 위해
//     * 세션에 저장된 정보를 확인
//     * @param session
//     * @return
//     */
//    @GetMapping("/session-check")
//    public ResponseEntity<String> checkSession(HttpSession session) {
//        String userPhone = (String) session.getAttribute("userPhone");
//        log.info("세션 확인: " + session.getId());
//        log.info("세션 전화번호 확인: " + session.getAttribute("userPhone"));
//
//        if (userPhone != null) {
//            return ResponseEntity.ok("User is logged in with phone: " + userPhone);
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
//        }
//    }

    //자동 로그인 (JWT 검증)
    @Operation(summary = "자동 로그인", description = "JWT를 검증하여 자동 로그인을 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "자동 로그인 성공"),
            @ApiResponse(responseCode = "400", description = "토큰이 제공되지 않음"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰"),
            @ApiResponse(responseCode = "500", description = "서버 오류로 인해 토큰 검증 실패")
    })
    @PostMapping("/auto-login")
    public ResponseEntity<Map<String, String>> autoLogin(@RequestHeader(value = "Authorization") String accessToken) {
        Map<String, String> response = new HashMap<>();

        if (accessToken == null || accessToken.isBlank()) {
            response.put("message", "토큰이 제공되지 않았습니다.");
            log.info("token : " + accessToken);
            return ResponseEntity.status(400).body(response);
        }

        try {
            // "Bearer "가 포함된 경우 제거, 그렇지 않으면 그대로 사용
            String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;

            if (jwtUtil.isTokenValid(jwtToken)) {
                response.put("message", "자동 로그인 성공");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "유효하지 않은 토큰입니다.");
                return ResponseEntity.status(401).body(response);
            }
        } catch (Exception e) {
            response.put("message", "토큰 검증 중 오류 발생");
            return ResponseEntity.status(500).body(response);
        }
    }

}
