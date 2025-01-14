package com.example.carebridge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Request 엔티티 클래스는 요청 정보를 나타냅니다.
 */
@Entity
@Table(name = "Request_List")
@Getter
@Setter
public class RequestList {

    /**
     * 요청 ID (기본 키)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Integer requestId;

    /**
     * 환자 ID
     */
    @Column(name = "patient_id", nullable = false)
    private Integer patientId;

    /**
     * 요청 내용
     */
    @Column(name = "request_content", nullable = false)
    private String requestContent;

    /**
     * 요청 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status;

    /**
     * 요청 상태를 나타내는 열거형
     */
    public enum RequestStatus {
        PENDING,      // 대기 중
        COMPLETED,    // 완료됨
        IN_PROGRESS,  // 진행 중
        SCHEDULED     // 예약됨
    }
}