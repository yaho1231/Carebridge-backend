package com.example.carebridge.controller;

import com.example.carebridge.dto.UserAccountDto;
import com.example.carebridge.dto.VerifyAccountDto;
import com.example.carebridge.service.UserAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserAccountController {

    private final UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping("/profile/{phone}")
    public UserAccountDto openProfile(@PathVariable("phone") String phone) {
        return userAccountService.getUserAccount(phone);
    }

    @PutMapping("/profile/{phone}")
    public UserAccountDto modifyProfile(@PathVariable("phone") String phone, @RequestBody UserAccountDto userAccountDto) {
        return userAccountService.updateUserAccount(phone, userAccountDto);
    }

    @PostMapping("/send-otp/{phone}")
    public ResponseEntity<String> sendOtp(@PathVariable String phone) {
        userAccountService.sendOtp(phone);
        return null;
    }

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
    public ResponseEntity<UserAccountDto> signIn(@RequestBody UserAccountDto userAccountDto) {
        UserAccountDto createdUser = userAccountService.createUserAccount(userAccountDto);
        return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(VerifyAccountDto verifyAccountDto) {
        boolean isVerified = userAccountService.verifyOtp(verifyAccountDto);
        if (isVerified)
            return ResponseEntity.ok("Login successful!");
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid phone number or otp.");
    }
}
