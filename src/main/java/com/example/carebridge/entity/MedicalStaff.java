package com.example.carebridge.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 의료진 정보를 관리하는 엔티티 클래스
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Medical_Staff")
public class MedicalStaff {

    /**
     * 의료진의 고유 식별자
     * 자동 증가 전략 사용
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medical_staff_id", nullable = false)
    private Integer medicalStaffId;

    /**
     * 의료진 소속 분과
     * 필수 입력 값이며 최대 100자까지 저장 가능
     */
    @Column(name = "department", nullable = false, length = 100)
    private String department;

    /**
     * 소속 병원 ID
     * 필수 입력 값
     */
    @Column(name = "hospital_id", nullable = false)
    private Integer hospitalId;

    /**
     * 의료진 인사말 머리말
     * 최대 500자까지 저장 가능
     */
    @Column(name = "phrase_head", length = 500)
    private String phraseHead;

    /**
     * 의료진 인사말 꼬리말
     * 최대 500자까지 저장 가능
     */
    @Column(name = "phrase_tail", length = 500)
    private String phraseTail;

    /**
     * 의료진 정보 생성을 위한 빌더 패턴 생성자
     */
    @Builder
    public MedicalStaff(String department, Integer hospitalId, 
                       String phraseHead, String phraseTail) {
        this.department = department;
        this.hospitalId = hospitalId;
        this.phraseHead = phraseHead;
        this.phraseTail = phraseTail;
    }
}
