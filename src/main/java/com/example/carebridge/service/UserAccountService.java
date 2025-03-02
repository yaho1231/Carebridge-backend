package com.example.carebridge.service;

import com.example.carebridge.dto.UserAccountDto;
import com.example.carebridge.dto.VerifyAccountDto;
import com.example.carebridge.entity.UserAccount;
import com.example.carebridge.repository.UserAccountRepository;
import lombok.Getter;
import lombok.Setter;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import net.nurigo.sdk.message.service.DefaultMessageService;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;

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

    public UserAccountDto getUserAccount(String phone_number){
        UserAccount userAccount = userAccountRepository.findByPhoneNumber(phone_number)
                .orElseThrow(() -> new IllegalArgumentException("해당 전화번호의 사용자를 찾을 수 없습니다."));
        return convertUserAccountToUserAccountDto(userAccount);
    }

    public UserAccountDto getUserAccountByEmail(String email){
        UserAccount userAccount = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자를 찾을 수 없습니다."));
        return convertUserAccountToUserAccountDto(userAccount);
    }

    public void createUserAccount(UserAccountDto userAccountDto){
        Optional<UserAccount> userAccount = userAccountRepository.findByPhoneNumber(userAccountDto.getPhoneNumber());
        if(userAccount.isPresent())
            throw new DuplicateKeyException("이미 등록된 전화번호입니다.");
        UserAccount newUserAccount = new UserAccount();
        newUserAccount.update(userAccountDto);
        userAccountRepository.save(newUserAccount);
    }

    public UserAccountDto updateUserAccount(String phone_number, UserAccountDto userAccountDto){
        UserAccount userAccount = userAccountRepository.findByPhoneNumber(phone_number)
                .orElseThrow(() -> new IllegalArgumentException("해당 전화번호의 사용자를 찾을 수 없습니다."));
        userAccount.update(userAccountDto);
        userAccountRepository.save(userAccount);
        return convertUserAccountToUserAccountDto(userAccount);
    }

    public void sendOtp(String phone_number, boolean isSignup){

        Optional<UserAccount> optionalUserAccount = userAccountRepository.findByPhoneNumber(phone_number);

        if (isSignup){
            // 회원가입: 이미 등록된 전화번호인지 확인
            if (optionalUserAccount.isPresent() && !optionalUserAccount.get().getName().equals("UserName"))
                throw new IllegalArgumentException("이미 가입된 전화번호입니다.");
        }
        else {
            // 로그인: 등록된 전화번호인지 확인
            if (optionalUserAccount.isEmpty())
                throw new NoSuchElementException("등록되지 않은 전화번호입니다.");
            else if(optionalUserAccount.get().getName().equals("UserName"))
                throw new IllegalStateException("회원가입이 정상적으로 등록되지 않았습니다.");
        }

        String otp = generateRandomNumber(6);
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        UserAccount userAccount = optionalUserAccount.orElseGet(() -> {
            UserAccount newAccount = new UserAccount();
            newAccount.setName("UserName");
            newAccount.setPhoneNumber(phone_number);
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
        message.setTo(phone_number);
        message.setText("[CareBridge] 인증번호는 \n[ " + otp + " ] 입니다");

        this.smsMessageService.sendOne(new SingleMessageSendingRequest(message));
    }

    public boolean verifyOtp(VerifyAccountDto verifyAccountDto) {
        UserAccount userAccount = userAccountRepository.findByPhoneNumber(verifyAccountDto.getPhone())
                .orElseThrow(() -> new NoSuchElementException("해당 전화번호의 사용자를 찾을 수 없습니다."));

        return userAccount.getOtp().equals(verifyAccountDto.getOtp()) &&
                userAccount.getOtpExpiry().isAfter(LocalDateTime.now());
    }

    public boolean isValidUserAccount(String phone_number){
        UserAccount userAccount = userAccountRepository.findByPhoneNumber(phone_number)
                .orElseThrow(() -> new NoSuchElementException("해당 전화번호의 사용자를 찾을 수 없습니다."));
        return !userAccount.getName().equals("UserName") &&
                !userAccount.getEmail().equals("email@email.com");
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
