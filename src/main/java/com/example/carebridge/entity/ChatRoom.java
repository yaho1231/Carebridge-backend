package com.example.carebridge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Chat_Room") // MySQL의 Chat_Room 테이블과 매핑
public class ChatRoom {

    @Id
    @Column(name = "chatroom_id", nullable = false) // chatroom_id 컬럼과 매핑
    private String chatRoomId; // 채팅방 고유 ID

    @Column(name = "patient_id", nullable = false) // patient_id 컬럼과 매핑
    private Integer patientId; // 환자 ID

    @Column(name = "medical_staff_id", nullable = false) // medical_staff_id 컬럼과 매핑
    private Integer medicalStaffId; // 의료진 ID
}