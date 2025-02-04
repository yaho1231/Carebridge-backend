package com.example.carebridge.service;

import com.example.carebridge.dto.UserAccountDto;
import com.example.carebridge.dto.VerifyAccountDto;
import com.example.carebridge.entity.UserAccount;
import com.example.carebridge.repository.UserAccountRepository;
import lombok.Getter;
import lombok.Setter;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import org.springframework.stereotype.Service;
import net.nurigo.sdk.message.service.DefaultMessageService;

import java.time.LocalDateTime;
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
        UserAccount userAccount = userAccountRepository.findByPhoneNumber(phone_number);
        return convertUserAccountToUserAccountDto(userAccount);
    }

    public void createUserAccount(UserAccountDto userAccountDto){
        UserAccount userAccount = userAccountRepository.findByPhoneNumber(userAccountDto.getPhoneNumber());
        if(userAccount == null)
            userAccount = new UserAccount();
        userAccount.update(userAccountDto);
        userAccountRepository.save(userAccount);
    }

    public UserAccountDto updateUserAccount(String phone_number, UserAccountDto userAccountDto){
        UserAccount userAccount = userAccountRepository.findByPhoneNumber(phone_number);
        userAccount.update(userAccountDto);
        userAccountRepository.save(userAccount);
        return convertUserAccountToUserAccountDto(userAccount);
    }

    public void sendOtp(String phone_number){
        String otp = generateRandomNumber(6);
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        UserAccount userAccount = userAccountRepository.findByPhoneNumber(phone_number);
        if(userAccount == null){
            userAccount = new UserAccount();
            userAccount.setName("UserName");
            userAccount.setPhoneNumber(phone_number);
            userAccount.setBirthDate(LocalDateTime.now());
            userAccount.setGender(UserAccount.Gender.Male);
            userAccount.setEmail("email@email.com");
        }
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
        UserAccount userAccount = userAccountRepository.findByPhoneNumber(verifyAccountDto.getPhone());

        return userAccount.getOtp().equals(verifyAccountDto.getOtp()) &&
                userAccount.getOtpExpiry().isAfter(LocalDateTime.now());
    }

    public boolean isValidUserAccount(String phone_number){
        UserAccount userAccount = userAccountRepository.findByPhoneNumber(phone_number);
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
