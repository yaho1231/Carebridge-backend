package com.example.carebridge.service;

import com.example.carebridge.dto.UserAccountDto;
import com.example.carebridge.dto.VerifyAccountDto;
import com.example.carebridge.entity.UserAccount;
import com.example.carebridge.repository.UserAccountRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@Getter
@Setter
public class UserAccountService {
    private final UserAccountRepository userAccountRepository;
    private final DefaultMessageService smsMessageService;

    public UserAccountService(UserAccountRepository userAccountRepository, DefaultMessageService smsMessageService) {
        this.userAccountRepository = userAccountRepository;
        this.smsMessageService = smsMessageService;
    }

    @Transactional
    public UserAccountDto getUserAccount(String phoneNumber) {
        log.debug("전화번호로 사용자 계정 조회 시도 - 전화번호: {}", phoneNumber);
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            log.error("전화번호가 누락되었습니다 - 입력값: {}", phoneNumber);
            throw new IllegalArgumentException("전화번호는 필수 입력값입니다.");
        }
        try {
            UserAccount userAccount = userAccountRepository.findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> {
                        log.error("사용자 계정을 찾을 수 없습니다 - 전화번호: {}", phoneNumber);
                        return new NoSuchElementException("해당 전화번호의 사용자 계정을 찾을 수 없습니다: " + phoneNumber);
                    });
            return convertUserAccountToUserAccountDto(userAccount);
        } catch (IllegalArgumentException e) {
            log.error("사용자 계정 조회 실패 - 잘못된 입력값: {}", e.getMessage());
            throw e;
        } catch (NoSuchElementException e) {
            log.error("사용자 계정 조회 실패 - 데이터 없음: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("사용자 계정 조회 중 예외 발생: {}", e.getMessage(), e);
            throw new RuntimeException("사용자 계정 조회에 실패했습니다.", e);
        }
    }

    @Transactional
    public UserAccountDto getUserAccountByEmail(String email) {
        log.debug("이메일로 사용자 계정 조회 시도 - 이메일: {}", email);
        if (email == null || email.trim().isEmpty()) {
            log.error("이메일이 누락되었습니다 - 입력값: {}", email);
            throw new IllegalArgumentException("이메일은 필수 입력값입니다.");
        }
        try {
            UserAccount userAccount = userAccountRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.error("사용자 계정을 찾을 수 없습니다 - 이메일: {}", email);
                        return new NoSuchElementException("해당 이메일의 사용자 계정을 찾을 수 없습니다: " + email);
                    });
            return convertUserAccountToUserAccountDto(userAccount);
        } catch (IllegalArgumentException e) {
            log.error("사용자 계정 조회 실패 - 잘못된 입력값: {}", e.getMessage());
            throw e;
        } catch (NoSuchElementException e) {
            log.error("사용자 계정 조회 실패 - 데이터 없음: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("사용자 계정 조회 중 예외 발생: {}", e.getMessage(), e);
            throw new RuntimeException("사용자 계정 조회에 실패했습니다.", e);
        }
    }

    @Transactional
    public void createUserAccount(UserAccountDto userAccountDto) {
        log.debug("사용자 계정 생성 시도 - UserAccountDto: {}", userAccountDto);
        if (userAccountDto == null || userAccountDto.getPhoneNumber() == null || userAccountDto.getPhoneNumber().trim().isEmpty()) {
            log.error("사용자 계정 생성 입력값이 누락되었습니다 - UserAccountDto: {}", userAccountDto);
            throw new IllegalArgumentException("사용자 계정 생성에 필요한 전화번호가 누락되었습니다.");
        }
        try {
            userAccountRepository.findByPhoneNumber(userAccountDto.getPhoneNumber())
                    .ifPresent(u -> {
                        log.error("이미 등록된 전화번호입니다 - 전화번호: {}", userAccountDto.getPhoneNumber());
                        throw new DuplicateKeyException("이미 등록된 전화번호입니다: " + userAccountDto.getPhoneNumber());
                    });
            UserAccount newUserAccount = new UserAccount();
            newUserAccount.update(userAccountDto);
            userAccountRepository.save(newUserAccount);
            log.info("사용자 계정 생성 성공 - 전화번호: {}", userAccountDto.getPhoneNumber());
        } catch (IllegalArgumentException e) {
            log.error("사용자 계정 생성 실패 - 잘못된 입력값: {}", e.getMessage());
            throw e;
        } catch (DuplicateKeyException e) {
            log.error("사용자 계정 생성 실패 - 중복 데이터: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("사용자 계정 생성 중 예외 발생: {}", e.getMessage(), e);
            throw new RuntimeException("사용자 계정 생성에 실패했습니다.", e);
        }
    }

    @Transactional
    public UserAccountDto updateUserAccount(String phoneNumber, UserAccountDto userAccountDto) {
        log.debug("사용자 계정 업데이트 시도 - 전화번호: {}, UserAccountDto: {}", phoneNumber, userAccountDto);
        if (phoneNumber == null || phoneNumber.trim().isEmpty() || userAccountDto == null) {
            log.error("사용자 계정 업데이트 입력값이 누락되었습니다 - 전화번호: {}, UserAccountDto: {}", phoneNumber, userAccountDto);
            throw new IllegalArgumentException("사용자 계정 업데이트에 필요한 입력값이 부족합니다.");
        }
        try {
            UserAccount userAccount = userAccountRepository.findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> {
                        log.error("사용자 계정을 찾을 수 없습니다 - 전화번호: {}", phoneNumber);
                        return new NoSuchElementException("해당 전화번호의 사용자 계정을 찾을 수 없습니다: " + phoneNumber);
                    });
            userAccount.update(userAccountDto);
            userAccountRepository.save(userAccount);
            log.info("사용자 계정 업데이트 성공 - 전화번호: {}", phoneNumber);
            return convertUserAccountToUserAccountDto(userAccount);
        } catch (IllegalArgumentException e) {
            log.error("사용자 계정 업데이트 실패 - 잘못된 입력값: {}", e.getMessage());
            throw e;
        } catch (NoSuchElementException e) {
            log.error("사용자 계정 업데이트 실패 - 데이터 없음: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("사용자 계정 업데이트 중 예외 발생: {}", e.getMessage(), e);
            throw new RuntimeException("사용자 계정 업데이트에 실패했습니다.", e);
        }
    }

    @Transactional
    public void sendOtp(String phoneNumber, boolean isSignup) {
        log.debug("OTP 전송 시도 - 전화번호: {}, 회원가입 여부: {}", phoneNumber, isSignup);
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            log.error("전화번호가 누락되었습니다 - 입력값: {}", phoneNumber);
            throw new IllegalArgumentException("전화번호는 필수 입력값입니다.");
        }
        try {
            Optional<UserAccount> optionalUserAccount = userAccountRepository.findByPhoneNumber(phoneNumber);
            if (isSignup) {
                // 회원가입: 이미 등록된 전화번호인지 확인
                if (optionalUserAccount.isPresent() && !optionalUserAccount.get().getName().equals("UserName")) {
                    log.error("이미 가입된 전화번호입니다 - 전화번호: {}", phoneNumber);
                    throw new IllegalArgumentException("이미 가입된 전화번호입니다: " + phoneNumber);
                }
            } else {
                // 로그인: 등록된 전화번호인지 확인
                if (optionalUserAccount.isEmpty()) {
                    log.error("등록되지 않은 전화번호입니다 - 전화번호: {}", phoneNumber);
                    throw new NoSuchElementException("등록되지 않은 전화번호입니다: " + phoneNumber);
                } else if (optionalUserAccount.get().getName().equals("UserName")) {
                    log.error("회원가입이 정상적으로 완료되지 않은 전화번호입니다 - 전화번호: {}", phoneNumber);
                    throw new IllegalArgumentException("회원가입이 정상적으로 등록되지 않은 전화번호입니다: " + phoneNumber);
                }
            }
            String otp = generateRandomNumber(6);
            LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);
            UserAccount userAccount = optionalUserAccount.orElseGet(() -> {
                UserAccount newAccount = new UserAccount();
                newAccount.setName("UserName");
                newAccount.setPhoneNumber(phoneNumber);
                newAccount.setBirthDate(LocalDateTime.now());
                newAccount.setGender(UserAccount.Gender.Male);
                newAccount.setEmail("email@email.com");
                return newAccount;
            });
            userAccount.setOtp(otp);
            userAccount.setOtpExpiry(expiryTime);
            userAccountRepository.save(userAccount);
            Message message = new Message();
            message.setFrom("01032330241");
            message.setTo(phoneNumber);
            message.setText("[CareBridge] 인증번호는 \n[ " + otp + " ] 입니다");
            smsMessageService.sendOne(new SingleMessageSendingRequest(message));
            log.info("OTP 전송 성공 - 전화번호: {}", phoneNumber);
        } catch (IllegalArgumentException e) {
            log.error("OTP 전송 실패 - 잘못된 입력값: {}", e.getMessage());
            throw e;
        } catch (NoSuchElementException e) {
            log.error("OTP 전송 실패 - 데이터 없음: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("OTP 전송 중 예외 발생: {}", e.getMessage(), e);
            throw new RuntimeException("OTP 전송에 실패했습니다.", e);
        }
    }

    @Transactional
    public boolean verifyOtp(VerifyAccountDto verifyAccountDto) {
        log.debug("OTP 인증 시도 - VerifyAccountDto: {}", verifyAccountDto);
        if (verifyAccountDto == null || verifyAccountDto.getPhone() == null || verifyAccountDto.getPhone().trim().isEmpty()
                || verifyAccountDto.getOtp() == null || verifyAccountDto.getOtp().trim().isEmpty()) {
            log.error("OTP 인증 입력값이 누락되었습니다 - VerifyAccountDto: {}", verifyAccountDto);
            throw new IllegalArgumentException("OTP 인증에 필요한 입력값이 부족합니다.");
        }
        try {
            UserAccount userAccount = userAccountRepository.findByPhoneNumber(verifyAccountDto.getPhone())
                    .orElseThrow(() -> {
                        log.error("사용자 계정을 찾을 수 없습니다 - 전화번호: {}", verifyAccountDto.getPhone());
                        return new NoSuchElementException("해당 전화번호의 사용자 계정을 찾을 수 없습니다: " + verifyAccountDto.getPhone());
                    });
            boolean isVerified = userAccount.getOtp().equals(verifyAccountDto.getOtp()) &&
                    userAccount.getOtpExpiry().isAfter(LocalDateTime.now());
            if (isVerified) {
                log.info("OTP 인증 성공 - 전화번호: {}", verifyAccountDto.getPhone());
            } else {
                log.warn("OTP 인증 실패 - 전화번호: {}", verifyAccountDto.getPhone());
            }
            return isVerified;
        } catch (IllegalArgumentException e) {
            log.error("OTP 인증 실패 - 잘못된 입력값: {}", e.getMessage());
            throw e;
        } catch (NoSuchElementException e) {
            log.error("OTP 인증 실패 - 데이터 없음: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("OTP 인증 중 예외 발생: {}", e.getMessage(), e);
            throw new RuntimeException("OTP 인증에 실패했습니다.", e);
        }
    }

    @Transactional
    public boolean isValidUserAccount(String phoneNumber) {
        log.debug("사용자 계정 유효성 검사 시도 - 전화번호: {}", phoneNumber);
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            log.error("전화번호가 누락되었습니다 - 입력값: {}", phoneNumber);
            throw new IllegalArgumentException("전화번호는 필수 입력값입니다.");
        }
        try {
            UserAccount userAccount = userAccountRepository.findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> {
                        log.error("사용자 계정을 찾을 수 없습니다 - 전화번호: {}", phoneNumber);
                        return new NoSuchElementException("해당 전화번호의 사용자 계정을 찾을 수 없습니다: " + phoneNumber);
                    });
            boolean isValid = !userAccount.getName().equals("UserName") &&
                    !userAccount.getEmail().equals("email@email.com");
            if (isValid) {
                log.info("유효한 사용자 계정입니다 - 전화번호: {}", phoneNumber);
            } else {
                log.warn("유효하지 않은 사용자 계정입니다 - 전화번호: {}", phoneNumber);
            }
            return isValid;
        } catch (IllegalArgumentException e) {
            log.error("사용자 계정 유효성 검사 실패 - 잘못된 입력값: {}", e.getMessage());
            throw e;
        } catch (NoSuchElementException e) {
            log.error("사용자 계정 유효성 검사 실패 - 데이터 없음: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("사용자 계정 유효성 검사 중 예외 발생: {}", e.getMessage(), e);
            throw new RuntimeException("사용자 계정 유효성 검사에 실패했습니다.", e);
        }
    }

    public String generateRandomNumber(int num) {
        Random rand = new Random();
        StringBuilder numStr = new StringBuilder();
        for (int i = 0; i < num; i++) {
            numStr.append(rand.nextInt(10));
        }
        return numStr.toString();
    }

    public UserAccountDto convertUserAccountToUserAccountDto(UserAccount userAccount){
        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setUserId(userAccount.getId());
        userAccountDto.setName(userAccount.getName());
        userAccountDto.setPhoneNumber(userAccount.getPhoneNumber());
        userAccountDto.setBirthDate(userAccount.getBirthDate());
        userAccountDto.setGender(userAccount.getGender());
        return userAccountDto;
    }
}
