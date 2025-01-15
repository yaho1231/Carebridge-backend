package com.example.carebridge.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ExaminationScheduleDto {

    // Getters and Setters
    private Integer id;
    private String patientPhone;
    private Integer medicalStaffId;
    private String scheduleDate;
    private String details;
    private String code;

}
