package com.example.carebridge.service;

import com.example.carebridge.controller.StaffAccountController;
import com.example.carebridge.dto.StaffAccountDto;
import com.example.carebridge.dto.UserAccountDto;
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

    public StaffAccount login(StaffAccountDto staffAccountDto) {
        return staffAccountRepository.getStaffAccountByUserId(staffAccountDto.getUserId());
    }

    public StaffAccountDto convertStaffAccountToStaffAccountDto(StaffAccount staffAccount) {
        StaffAccountDto staffAccountDto = new StaffAccountDto();
        staffAccountDto.setUserId(staffAccount.getUserId());
        staffAccountDto.setPassword(staffAccount.getPassword());
        return staffAccountDto;
    }
}
