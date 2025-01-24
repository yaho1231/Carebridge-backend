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

    public String login(StaffAccountDto staffAccountDto) {
        try {
            StaffAccountDto staffAccountDto1 = staffAccountRepository.getStaffAccountByUserId(staffAccountDto.getUserId());
            return "Login successful";
        }
        catch (Exception e) {
            return "Login failed";
        }
    }

    public StaffAccountDto convertStaffAccountToStaffAccountDto(StaffAccount staffAccount) {
        StaffAccountDto staffAccountDto = new StaffAccountDto();
        staffAccountDto.setUserId(staffAccount.getUserId());
        staffAccountDto.setPassword(staffAccount.getPassword());
        return staffAccountDto;
    }
}
