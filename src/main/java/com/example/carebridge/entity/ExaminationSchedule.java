package com.example.carebridge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
@Entity
@Table(name = "Examination_Schedule") // MySQL 의 Examination_Schedule 테이블과 매핑
public class ExaminationSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 증가 설정
    private Integer id; // 검진 일정 고유 ID

    @Column(name = "patient_id", nullable = false) // 환자 ID 컬럼과 매핑
    private Integer patientId; // 환자 ID

    @Column(name = "medical_staff_id", nullable = false) // 의료진 ID 컬럼과 매핑
    private Integer medicalStaffId; // 의료진 ID

    @Column(name = "schedule_date", nullable = false) // 검진 일정 날짜 컬럼과 매핑
    private Timestamp scheduleDate; // 검진 날짜

    @Column(name = "details") // 검진 세부사항 컬럼과 매핑
    private String details; // 검진 세부사항

    @Column(name = "category", nullable = false) // 검진 종류 코드 컬럼과 매핑
    private String category; // 검진 종류 코드

}