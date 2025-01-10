package com.example.carebridge.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
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
    
    @Column(name = "chat_room_id") // chat_room_id 컬럼과 매핑
    private Integer chatRoomId; // 채팅방 ID

    // Getter와 Setter 메서드 정의
    public Integer getPatientId() { return patientId; }
    public void setId(Integer patientId) { this.patientId = patientId; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getGuardianContact() { return guardianContact; }
    public void setGuardianContact(String guardianContact) { this.guardianContact = guardianContact; }

    public String getHospitalLocation() { return hospitalLocation; }
    public void setHospitalLocation(String hospitalLocation) { this.hospitalLocation = hospitalLocation; }

    public Guardian getGuardian() { return guardian; }
    public void setGuardian(Guardian guardian) { this.guardian = guardian; }

    public Integer getChatRoomId() { return chatRoomId; }
    public void setChatRoomId(Integer chatRoomId) { this.chatRoomId = chatRoomId; }

    // 성별을 Enum으로 정의 (Male/Female)
    public enum Gender {
        Male, Female
    }
}
