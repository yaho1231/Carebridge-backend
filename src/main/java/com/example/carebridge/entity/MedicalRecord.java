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
@Table(name = "Medical_Record")
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
     * 환자 ID
     * 필수 입력 값
     */
    @Column(name = "patient_id", nullable = false)
    private Integer patientId;

    /**
     * 질병 정보
     * 최대 500자까지 저장 가능
     */
    @Column(name = "disease_info", length = 500)
    private String diseaseInfo;

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
    public MedicalRecord(Integer patientId, String diseaseInfo, Integer hospitalId) {
        this.patientId = patientId;
        this.diseaseInfo = diseaseInfo;
        this.hospitalId = hospitalId;
    }
}
