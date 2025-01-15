package com.example.carebridge.dto;

import lombok.Getter;

@Getter
public class VerifyResponseDto {
    private final int verify_value;
    private final String phone;

    public VerifyResponseDto(int value, String phone) {
        this.verify_value = value;
        this.phone = phone;
    }
}
