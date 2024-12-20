package com.example.carebridge.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Medical_Record") // MySQL의 Medical_Record 테이블과 매핑
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 증가 설정
    private Integer id; // 의료 기록 고유 ID

    @Column(name = "patient_phone", nullable = false) // 환자 전화번호 컬럼과 매핑 (Not Null 제약 조건)
    private String patientPhone; // 환자 전화번호

    @Column(name = "disease_info") // 질병 정보 컬럼과 매핑
    private String diseaseInfo; // 질병 정보

    @Column(name = "prescription") // 처방 내용 컬럼과 매핑
    private String prescription; // 처방 내용

    @Column(name = "exam_schedule") // 검진 일정 컬럼과 매핑
    private String examSchedule; // 검진 일정

    @Column(name = "hospital_id", nullable = false) // 병원 ID 컬럼과 매핑
    private Integer hospitalId; // 병원 ID

    // Getter와 Setter 메서드 정의
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getPatientPhone() { return patientPhone; }
    public void setPatientPhone(String patientPhone) { this.patientPhone = patientPhone; }

    public String getDiseaseInfo() { return diseaseInfo; }
    public void setDiseaseInfo(String diseaseInfo) { this.diseaseInfo = diseaseInfo; }

    public String getPrescription() { return prescription; }
    public void setPrescription(String prescription) { this.prescription = prescription; }

    public String getExamSchedule() { return examSchedule; }
    public void setExamSchedule(String examSchedule) { this.examSchedule = examSchedule; }

    public Integer getHospitalId() { return hospitalId; }
    public void setHospitalId(Integer hospitalId) { this.hospitalId = hospitalId; }
}
