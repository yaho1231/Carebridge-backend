package com.example.carebridge.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
@Data
public class ExaminationScheduleDto {

    private Integer id; // 검진 일정 고유 ID
    private Integer patientId; // 환자 ID
    private Integer medicalStaffId; // 의료진 ID
    private Timestamp scheduleDate; // 검진 날짜
    private String details; // 검진 세부사항
    private String category; // 검진 종류 코드

}