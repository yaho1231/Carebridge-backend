package com.example.carebridge.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Medical_Staff") // MySQL의 Medical_Staff 테이블과 매핑
public class MedicalStaff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 증가 설정
    private Integer id; // 의료진 고유 ID

    @Column(nullable = false) // 의료진 소속 분과, Not Null 제약 조건 설정
    private String department; // 의료진 소속 분과

    @Column(name = "hospital_id", nullable = false) // 소속 병원 ID 컬럼과 매핑
    private Integer hospitalId; // 소속 병원 ID

    // Getter와 Setter 메서드 정의
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getHospitalId() { return hospitalId; }
    public void setHospitalId(Integer hospitalId) { this.hospitalId = hospitalId; }
}
