package com.example.carebridge.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Chat_Room") // MySQL의 Chat_Room 테이블과 매핑
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 증가 설정
    @Column(name = "chat_room_id") // chat_room_id 컬럼과 매핑
    private Integer chatRoomId; // 채팅방 고유 ID

    @Column(name = "patient_id", nullable = false) // patient_id 컬럼과 매핑
    private Integer patientId; // 환자 ID

    @Column(name = "medical_staff_id", nullable = false) // medical_staff_id 컬럼과 매핑
    private Integer medicalStaffId; // 의료진 ID

    // Getter와 Setter 메서드 정의

    /**
     * 채팅방 고유 ID를 반환합니다.
     * @return chatRoomId 채팅방 고유 ID
     */
    public Integer getChatRoomId() {
        return chatRoomId;
    }

    /**
     * 채팅방 고유 ID를 설정합니다.
     * @param chatRoomId 채팅방 고유 ID
     */
    public void setChatRoomId(Integer chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    /**
     * 환자 ID를 반환합니다.
     * @return patientId 환자 ID
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * 환자 ID를 설정합니다.
     * @param patientId 환자 ID
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * 의료진 ID를 반환합니다.
     * @return medicalStaffId 의료진 ID
     */
    public Integer getMedicalStaffId() {
        return medicalStaffId;
    }

    /**
     * 의료진 ID를 설정합니다.
     * @param medicalStaffId 의료진 ID
     */
    public void setMedicalStaffId(Integer medicalStaffId) {
        this.medicalStaffId = medicalStaffId;
    }
}