package com.example.carebridge.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class HospitalInformationDto {
    private Integer id; // 병원 정보 고유 ID

    private Integer hospitalId; // 병원 ID

    private String category; // 정보 카테고리

    private String title;

    private String information;
}
