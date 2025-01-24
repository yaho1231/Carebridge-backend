package com.example.carebridge.controller;

import com.example.carebridge.dto.KakaoDto;
import com.example.carebridge.dto.UserAccountDto;
import com.example.carebridge.dto.VerifyAccountDto;
import com.example.carebridge.entity.UserAccount;
import com.example.carebridge.service.OAuthService;
import com.example.carebridge.service.UserAccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserAccountController {

    private final UserAccountService userAccountService;
    private final OAuthService oAuthService;

    public UserAccountController(UserAccountService userAccountService, OAuthService oAuthService) {
        this.userAccountService = userAccountService;
        this.oAuthService = oAuthService;
    }

    /**
     * User profile 검색
     * @param phone
     * @return
     */
    @GetMapping("/profile/{phone}")
    public UserAccountDto openProfile(@PathVariable("phone") String phone) {
        return userAccountService.getUserAccount(phone);
    }

    /**
     * User profile 수정
     * @param phone
     * @param userAccountDto
     * @return
     */
    @PutMapping("/profile/{phone}")
    public UserAccountDto modifyProfile(@PathVariable("phone") String phone, @RequestBody UserAccountDto userAccountDto) {
        return userAccountService.updateUserAccount(phone, userAccountDto);
    }

    /**
     * phone 으로 인증문자(otp 포함) 전송
     * @param phone
     * @return
     */
    @PostMapping("/send-otp/{phone}")
    public ResponseEntity<String> sendOtp(@PathVariable String phone) {
        try {
            userAccountService.sendOtp(phone); // OTP 전송 로직 호출
            return ResponseEntity.ok("OTP sent successfully to " + phone);
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
    public ResponseEntity<String> verify(VerifyAccountDto verifyAccountDto) {
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

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody UserAccountDto userAccountDto) {
        try {
            userAccountService.createUserAccount(userAccountDto);
            return ResponseEntity.ok("Sign Up successful!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create user account: " + e.getMessage());
        }

    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody VerifyAccountDto verifyAccountDto) {
        boolean isVerified = userAccountService.verifyOtp(verifyAccountDto);
        if (isVerified)
            return ResponseEntity.ok("Login successful!");
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid phone number or otp.");
    }

    /**
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
     * 카카오 로그인 매핑 (링크로 사용 X)
     * 인가 code가 있어야 사용 가능 함.
     * 기본은 /social-login/kakao 를 통해 받는 login 페이지를 통해 접속
     * @return
     */
    @GetMapping("/social-login/kakao/token")
    public String kakaoLogin(HttpServletRequest request, HttpSession session) throws Exception {
        String code = request.getParameter("code");
        if (code == null)
            throw new IllegalArgumentException("Authorization code is missing");
        // 1. Access Token 발급
        String accessToken = oAuthService.getKakaoToken(code);
        // 2. 사용자 정보 조회
        KakaoDto kakaoDto = oAuthService.getUserInfoWithToken(accessToken);
        // 3. 서비스 사용자 정보 매핑 및 세션 저장
        UserAccountDto kakaoUserAccount = oAuthService.ifNeedKakaoInfo(kakaoDto);
        if (kakaoUserAccount != null) {
            session.setAttribute("loginUser", kakaoUserAccount);
            session.setMaxInactiveInterval(60 * 30); // 30분 유지
            session.setAttribute("kakaoToken", accessToken);
            return "redirect:/dashboard";
        } else {
            return "redirect:/login?error";
        }
    }

    /**
     * 카카오 로그아웃
     * @return
     */
    @GetMapping("/social-login/kakao/logout")
    public String kakaoLogout(HttpSession session) throws JsonProcessingException {
        String accessToken = (String) session.getAttribute("kakaoToken");
        if (accessToken != null) {
            oAuthService.kakaoDisconnect(accessToken);
        }
        session.invalidate(); // 세션 완전 종료
        return "redirect:/";
    }

    /**
     * 로그인시 생성한 session 확인
     * @param session
     * @return
     */
    @GetMapping("/check-session")
    public String checkSession(HttpSession session) {
        // 세션에서 특정 속성을 가져오기
        Object user = session.getAttribute("loginUser");

        if (user == null) {
            // 세션 속성이 없으면 세션이 삭제되었다고 간주
            return "세션이 삭제되었습니다.";
        } else {
            return "세션이 유지 중입니다. 사용자: " + user.toString();
        }
    }
}
