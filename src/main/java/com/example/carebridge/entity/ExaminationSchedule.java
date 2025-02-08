package com.example.carebridge.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 진료 일정 정보를 저장하는 엔티티
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Examination_Schedule")
public class ExaminationSchedule {

    /**
     * 진료 일정의 고유 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 의료진 정보와의 연관 관계
     */
    @Column(name = "medical_staff_id", nullable = false)
    private Integer medicalStaffId;

    /**
     * 환자 정보와의 연관 관계
     */
    @Column(name = "patient_id", nullable = false)
    private Integer patientId;

    /**
     * 진료 예정 일시
     */
    @Column(name = "schedule_date", nullable = false)
    private LocalDateTime scheduleDate;

    /**
     * 진료 세부사항
     */
    @Column(name = "details")
    private String details;

    /**
     * 진료 종류
     * SURGERY(수술), OUTPATIENT(외래), EXAMINATION(검진)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    @Builder
    public ExaminationSchedule(Integer medicalStaffId, Integer patientId, 
                             LocalDateTime scheduleDate, String details) {
        this.medicalStaffId = medicalStaffId;
        this.patientId = patientId;
        this.scheduleDate = scheduleDate;
        this.details = details;
    }

    /**
     * 진료 종류를 정의하는 열거형
     */
    public enum Category {
        SURGERY("수술"),
        OUTPATIENT("외래"),
        EXAMINATION("검진");

        private final String description;

        Category(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}