package com.example.carebridge.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class VerifyAccountDto {
    private final String otp;
    private final String phone;

    public VerifyAccountDto(String value, String phone) {
        this.otp = value;
        this.phone = phone;
    }
}
