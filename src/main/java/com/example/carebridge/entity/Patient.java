package com.example.carebridge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "Patient") // MySQL의 Patient 테이블과 매핑
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment primary key
    @Column(name = "patient_id") // patient_id column mapping
    private Integer patientId; // Patient unique ID

    @Column(name = "phone_number", nullable = false, unique = true) // phone_number 컬럼과 매핑, 고유값 설정
    private String phoneNumber; // 환자 전화번호

    @Column(nullable = false) // Not Null 제약 조건 설정
    private String name; // 환자 이름

    @Column(name = "birth_date", nullable = false) // birth_date 컬럼과 매핑
    private LocalDate birthDate; // 환자 생년월일

    @Enumerated(EnumType.STRING) // Enum 타입을 문자열로 저장
    @Column(nullable = false) // Not Null 제약 조건 설정
    private Gender gender; // 환자 성별 (Male/Female)

    @Column(name = "guardian_contact") // guardian_contact 컬럼과 매핑
    private String guardianContact; // 보호자 전화번호 (참조)

    @Column(name = "hospital_location") // hospital_location 컬럼과 매핑
    private String hospitalLocation; // 병원 위치 정보

    @ManyToOne
    @JoinColumn(name = "guardian_contact", referencedColumnName = "phone_number", insertable = false, updatable = false)
    private Guardian guardian; // Guardian 엔티티와 참조 관계 설정
    
    @Column(name = "chatroom_id") // chat_room_id 컬럼과 매핑
    private String chatRoomId; // 채팅방 ID

    @Column(name = "department") // department 컬럼과 매핑
    private String department;

    // 성별을 Enum 으로 정의 (Male/Female)
    public enum Gender {
        Male, Female
    }
}
