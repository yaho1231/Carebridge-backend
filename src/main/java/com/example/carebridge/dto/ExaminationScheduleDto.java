package com.example.carebridge.dto;

public class ExaminationScheduleDto {

    private Integer id;
    private String patientPhone;
    private Integer medicalStaffId;
    private String scheduleDate;
    private String details;
    private String code;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getPatientPhone() { return patientPhone; }
    public void setPatientPhone(String patientPhone) { this.patientPhone = patientPhone; }

    public Integer getMedicalStaffId() { return medicalStaffId; }
    public void setMedicalStaffId(Integer medicalStaffId) { this.medicalStaffId = medicalStaffId; }

    public String getScheduleDate() { return scheduleDate; }
    public void setScheduleDate(String scheduleDate) { this.scheduleDate = scheduleDate; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
