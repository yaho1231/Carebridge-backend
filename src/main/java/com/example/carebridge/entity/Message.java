package com.example.carebridge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "Message") // MySQL의 Message 테이블과 매핑
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 증가 설정
    private Integer messageId; // 메시지 고유 ID

    @Column(name = "patient_id", nullable = false) // 환자 ID 컬럼과 매핑
    private Integer patientId; // 환자 ID

    @Column(name = "medical_staff_id", nullable = false) // 의료진 ID 컬럼과 매핑
    private Integer medicalStaffId; // 의료진 ID

    @Column(name = "message_content", nullable = false) // 메시지 내용 컬럼과 매핑
    private String messageContent; // 메시지 내용

    @Column(name = "message_timestamp", nullable = false) // 타임스탬프 컬럼과 매핑
    private LocalDateTime timestamp; // 메시지 발송 시간

    @Column(name = "readstatus", nullable = false) // 읽음 여부 컬럼과 매핑 (기본값 false)
    private Boolean readStatus; // 메시지 읽음 여부

    @Column(name = "chatroom_id", nullable = false) // 채팅방 ID 컬럼과 매핑
    private String chatRoomId; // 채팅방 ID

    @Column(name = "sender_id", nullable = false) // 발신자 ID 컬럼과 매핑
    private Integer senderId; // 발신자 ID

    @Column(name = "hospital_id")
    private Integer hospitalId;

    @Column(name = "category")
    private String category;
}