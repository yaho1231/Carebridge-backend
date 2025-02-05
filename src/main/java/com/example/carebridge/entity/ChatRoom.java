package com.example.carebridge.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * STOMP 프로토콜을 사용한 채팅방 채널 정보를 저장하는 엔티티
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Chat_Room")
public class ChatRoom {

    /**
     * 채팅방(채널)의 고유 식별자
     * STOMP의 destination으로 사용됨
     */
    @Id
    @Column(name = "chatroom_id", nullable = false)
    private String chatRoomId;

    /**
     * 채팅 참여자인 환자의 고유 식별자
     */
    @Column(name = "patient_id", nullable = false)
    private Integer patientId;

    /**
     * 채팅 참여자인 의료진의 고유 식별자
     */
    @Column(name = "medical_staff_id", nullable = false)
    private Integer medicalStaffId;
}