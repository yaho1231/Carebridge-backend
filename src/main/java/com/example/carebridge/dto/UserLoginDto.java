package com.example.carebridge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDto {
    private String accessToken;

    private String refreshToken;

    private Integer userId;

    private Integer patientId;
}
