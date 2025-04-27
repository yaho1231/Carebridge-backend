package com.example.carebridge.service;

import com.example.carebridge.dto.StaffAccountDto;
import com.example.carebridge.entity.StaffAccount;
import com.example.carebridge.repository.StaffAccountRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
public class StaffAccountService {

    private StaffAccountRepository staffAccountRepository;

    public StaffAccountService(StaffAccountRepository staffAccountRepository) {
        this.staffAccountRepository = staffAccountRepository;
    }

    public Boolean verifyStaffAccount(StaffAccountDto staffAccountDto) {
        StaffAccount staffAccount1 = staffAccountRepository.getStaffAccountByUserId(staffAccountDto.getUserId())
//                .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 사용자를 찾을 수 없습니다."));
                .orElseThrow(() -> {
                    return new IllegalArgumentException("해당 아이디의 사용자를 찾을 수 없습니다 : " + staffAccountDto.getUserId());
                });
        return staffAccountDto.getUserId().equals(staffAccount1.getUserId()) &&
                staffAccountDto.getPassword().equals(staffAccount1.getPassword());
    }

    public StaffAccountDto convertStaffAccountToStaffAccountDto(StaffAccount staffAccount) {
        StaffAccountDto staffAccountDto = new StaffAccountDto();
        staffAccountDto.setUserId(staffAccount.getUserId());
        staffAccountDto.setPassword(staffAccount.getPassword());
        return staffAccountDto;
    }

    public String findPassword(String id){
        StaffAccount staffAccount  = staffAccountRepository.getStaffAccountByUserId(id)
                .orElseThrow(() -> {
                    return new IllegalArgumentException("해당 아이디의 사용자를 찾을 수 없습니다 : " + id);
                });
        return staffAccount.getPassword();
    }

    public void resetPassword(StaffAccountDto staffAccountDto, String newPassword) {
        StaffAccount staffAccount = staffAccountRepository.getStaffAccountByUserId(staffAccountDto.getUserId())
                .orElseThrow(() -> {
                    return new IllegalArgumentException("해당 아이디의 사용자를 찾을 수 없습니다 : " + staffAccountDto.getUserId());
                });
        if(!staffAccountDto.getPassword().equals(staffAccount.getPassword()))
            throw new IllegalArgumentException("기존 비밀번호와 일치하지 않습니다.");
        if(newPassword.equals(staffAccount.getPassword()))
            throw new IllegalArgumentException("새로운 비밀번호가 기존의 비밀번호와 일치합니다.");
        staffAccount.setPassword(newPassword);
        staffAccountRepository.save(staffAccount);
    }

    public StaffAccount findStaffAccountByUserId(String userId) {
        return staffAccountRepository
                .getStaffAccountByUserId(userId)
                .orElseThrow(() -> {
                    return new IllegalArgumentException("해당 아이디의 사용자를 찾을 수 없습니다 : " + userId);
                });
    }
}
