package com.example.carebridge.dto;

import com.example.carebridge.entity.Patient;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * PatientDto 는 환자 정보를 전송하기 위한 데이터 전송 객체입니다.
 */
@Data
@Getter
@Setter
public class PatientDto {
    /**
     * 환자의 고유 ID
     */
    private Integer patientId; // Patient unique ID

    /**
     * 환자의 전화번호
     */
    private String phoneNumber; // 환자 전화번호

    /**
     * 환자의 이름
     */
    private String name; // 환자 이름

    /**
     * 환자의 생년월일
     */
    private LocalDate birthDate; // 환자 생년월일

    /**
     * 환자의 성별 (남성/여성)
     */
    private Patient.Gender gender; // 환자 성별 (Male/Female)

    /**
     * 보호자의 전화번호 (참조)
     */
    private String guardianContact; // 보호자 전화번호 (참조)

    /**
     * 병원의 ID
     */
    private Integer hospitalId; // 병원 아이디

    /**
     * 병원의 위치 정보
     */
    private String hospitalLocation; // 병원 위치 정보

    /**
     * 채팅방 ID
     */
    private String chatRoomId; // 채팅방 ID

    /**
     * 부서 정보
     */
    private String department; // 부서
}