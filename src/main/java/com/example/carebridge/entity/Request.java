package com.example.carebridge.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 의료진에 대한 환자의 요청 정보를 관리하는 엔티티 클래스
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "request")
public class Request {

    /**
     * 요청의 고유 식별자
     * 자동 증가 전략 사용
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Integer requestId;

    /**
     * 요청한 환자의 ID
     * 필수 입력 값
     */
    @Column(name = "patient_id", nullable = false)
    private Integer patientId;

    /**
     * 요청받은 의료진의 ID
     * 필수 입력 값
     */
    @Column(name = "medical_staff_id", nullable = false)
    private Integer medicalStaffId;

    /**
     * 요청 내용
     * 필수 입력 값이며 최대 500자까지 저장 가능
     */
    @Column(name = "request_content", nullable = false, length = 500)
    private String requestContent;

    /**
     * 요청 시간
     * ISO-8601 형식으로 저장
     * 필수 입력 값
     */
    @Column(name = "request_time", nullable = false)
    private LocalDateTime requestTime;

    /**
     * 요청 수락 시간
     * ISO-8601 형식으로 저장
     */
    @Column(name = "accept_time")
    private LocalDateTime acceptTime;

    /**
     * 요청 상태
     * 필수 입력 값
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status;

    /**
     * 요청 상태를 나타내는 열거형
     */
    public enum RequestStatus {
        PENDING("대기 중"),
        COMPLETED("완료됨"),
        IN_PROGRESS("진행 중"),
        SCHEDULED("예약됨");

        private final String description;

        RequestStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 요청 정보 생성을 위한 빌더 패턴 생성자
     */
    @Builder
    public Request(Integer patientId, Integer medicalStaffId, String requestContent,
                  LocalDateTime requestTime, RequestStatus status) {
        this.patientId = patientId;
        this.medicalStaffId = medicalStaffId;
        this.requestContent = requestContent;
        this.requestTime = requestTime;
        this.status = status;
    }
}