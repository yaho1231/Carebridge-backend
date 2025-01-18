package com.example.carebridge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Medical_Staff") // MySQL의 Medical_Staff 테이블과 매핑
public class MedicalStaff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 증가 설정
    @Column(name = "medical_staff_id", nullable = false) //Not Null 제약 조건 설정
    private Integer medicalStaffId; // 의료진 고유 ID

    @Column(nullable = false) // 의료진 소속 분과, Not Null 제약 조건 설정
    private String department; // 의료진 소속 분과

    @Column(name = "hospital_id", nullable = false) // 소속 병원 ID 컬럼과 매핑
    private Integer hospitalId; // 소속 병원 ID

    @Column(name = "phrase_head") // 의료진 인사말 컬럼과 매핑
    private String phrase_head; // 의료진 인사말

    @Column(name = "phrase_tail") // 의료진 인사말 컬럼과 매핑
    private String phrase_tail;
}
