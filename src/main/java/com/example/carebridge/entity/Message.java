package com.example.carebridge.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Message") // MySQL의 Message 테이블과 매핑
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 증가 설정
    private Integer messageId; // 메시지 고유 ID

    @Column(name = "patient_id", nullable = false) // 환자 ID 컬럼과 매핑
    private Integer patientId; // 환자 ID

    @Column(name = "medical_staff_id", nullable = false) // 의료진 ID 컬럼과 매핑
    private Integer medicalStaffId; // 의료진 ID

    @Column(name = "messageContent", nullable = false) // 메시지 내용 컬럼과 매핑
    private String messageContent; // 메시지 내용

    @Column(name = "timestamp", nullable = false) // 타임스탬프 컬럼과 매핑
    private String timestamp; // 메시지 발송 시간

    @Column(name = "readStatus", nullable = false) // 읽음 여부 컬럼과 매핑 (기본값 false)
    private Boolean readStatus; // 메시지 읽음 여부

    // Getter와 Setter 메서드 정의
    public Integer getMessageId() { return messageId; } // 메시지 ID 반환
    public void setMessageId(Integer messageId) { this.messageId = messageId; } // 메시지 ID 설정

    public Integer getPatientId() { return patientId; } // 환자 ID 반환
    public void setPatientId(Integer patientId) { this.patientId = patientId; } // 환자 ID 설정

    public Integer getMedicalStaffId() { return medicalStaffId; } // 의료진 ID 반환
    public void setMedicalStaffId(Integer medicalStaffId) { this.medicalStaffId = medicalStaffId; } // 의료진 ID 설정

    public String getMessageContent() { return messageContent; } // 메시지 내용 반환
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; } // 메시지 내용 설정

    public String getTimestamp() { return timestamp; } // 메시지 발송 시간 반환
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; } // 메시지 발송 시간 설정

    public Boolean getReadStatus() { return readStatus; } // 메시지 읽음 여부 반환
    public void setReadStatus(Boolean readStatus) { this.readStatus = readStatus; } // 메시지 읽음 여부 설정
}