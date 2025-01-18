package com.example.carebridge.dto;

import com.example.carebridge.entity.UserAccount;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Getter
@Setter
public class UserAccountDto {

    private Integer userId;

    private String name;

    private String phoneNumber;

    private Date birthDate;

    private UserAccount.Gender gender;

}
