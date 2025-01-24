package com.example.carebridge.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class StaffAccountDto {
    private String userId;

    private String password;
}
