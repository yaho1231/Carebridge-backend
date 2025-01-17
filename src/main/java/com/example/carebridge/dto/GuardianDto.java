package com.example.carebridge.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class GuardianDto {

    private String guardianId; // 보호자 고유 ID

    private String name; // 보호자 이름

    private Integer patientId; // 환자 ID

    private String phoneNumber; // 보호자 전화번호
}