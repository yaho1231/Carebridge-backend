package com.example.carebridge.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 의료 기록을 관리하는 엔티티 클래스
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "medical_record")
public class MedicalRecord {

    /**
     * 의료 기록의 고유 식별자
     * 자동 증가 전략 사용
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /**
     * 환자 전화번호
     * 필수 입력 값이며 최대 20자까지 저장 가능
     */
    @Column(name = "patient_phone", nullable = false, length = 20)
    private String patientPhone;

    /**
     * 질병 정보
     * 최대 500자까지 저장 가능
     */
    @Column(name = "disease_info", length = 500)
    private String diseaseInfo;

    /**
     * 처방 내용
     * 최대 500자까지 저장 가능
     */
    @Column(name = "prescription", length = 500)
    private String prescription;

    /**
     * 검진 일정
     * 최대 255자까지 저장 가능
     */
    @Column(name = "exam_schedule", length = 255)
    private String examSchedule;

    /**
     * 병원 ID
     * 필수 입력 값
     */
    @Column(name = "hospital_id", nullable = false)
    private Integer hospitalId;

    /**
     * 의료 기록 생성을 위한 빌더 패턴 생성자
     */
    @Builder
    public MedicalRecord(String patientPhone, String diseaseInfo, 
                        String prescription, String examSchedule, Integer hospitalId) {
        this.patientPhone = patientPhone;
        this.diseaseInfo = diseaseInfo;
        this.prescription = prescription;
        this.examSchedule = examSchedule;
        this.hospitalId = hospitalId;
    }
}
