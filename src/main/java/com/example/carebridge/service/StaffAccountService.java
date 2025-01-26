package com.example.carebridge.service;

import com.example.carebridge.controller.StaffAccountController;
import com.example.carebridge.dto.StaffAccountDto;
import com.example.carebridge.dto.UserAccountDto;
import com.example.carebridge.entity.StaffAccount;
import com.example.carebridge.repository.StaffAccountRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
public class StaffAccountService {

    private StaffAccountRepository staffAccountRepository;

    public StaffAccountService(StaffAccountRepository staffAccountRepository) {
        this.staffAccountRepository = staffAccountRepository;
    }

    public Boolean veriftStaffAccount(StaffAccountDto staffAccountDto) {
        StaffAccount staffAccount1 = staffAccountRepository.getStaffAccountByUserId(staffAccountDto.getUserId());
        return staffAccountDto.getUserId().equals(staffAccount1.getUserId()) &&
                staffAccountDto.getPassword().equals(staffAccount1.getPassword());
    }

    public StaffAccountDto convertStaffAccountToStaffAccountDto(StaffAccount staffAccount) {
        StaffAccountDto staffAccountDto = new StaffAccountDto();
        staffAccountDto.setUserId(staffAccount.getUserId());
        staffAccountDto.setPassword(staffAccount.getPassword());
        return staffAccountDto;
    }
}
