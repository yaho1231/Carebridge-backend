package com.example.carebridge.dto;

import com.example.carebridge.entity.Patient;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * PatientDto 는 환자 정보를 전송하기 위한 데이터 전송 객체입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDto {
    /**
     * 환자의 고유 식별자
     * 필수 값으로, null이 허용되지 않습니다
     */
    @NotNull(message = "환자 ID는 필수입니다")
    private Integer patientId;

    /**
     * 환자의 전화번호
     * 형식: XXX-XXXX-XXXX 또는 XXXXXXXXXXX
     * (예: 010-1234-5678 또는 01012345678)
     */
    @Pattern(regexp = "^\\d{2,3}[-]?\\d{3,4}[-]?\\d{4}$", 
            message = "전화번호는 'XXX-XXXX-XXXX' 형식 또는 숫자만 입력 가능합니다")
    private String phoneNumber;

    /**
     * 환자의 이름
     * 필수 값입니다
     */
    @NotNull(message = "이름은 필수입니다")
    private String name;

    /**
     * 환자의 생년월일
     * ISO-8601 형식의 날짜/시간 문자열
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private String birthDate;

    /**
     * 환자의 성별
     * MALE 또는 FEMALE 값만 가능
     */
    @NotNull(message = "성별은 필수입니다")
    private Patient.Gender gender;

    /**
     * 보호자 연락처
     * 형식: XXX-XXXX-XXXX 또는 XXXXXXXXXXX
     * (예: 010-1234-5678 또는 01012345678)
     */
    @Pattern(regexp = "^\\d{2,3}[-]?\\d{3,4}[-]?\\d{4}$", 
            message = "보호자 전화번호는 'XXX-XXXX-XXXX' 형식 또는 숫자만 입력 가능합니다")
    private String guardianContact;

    /**
     * 병원 고유 식별자
     * 필수 값입니다
     */
    @NotNull(message = "병원 ID는 필수입니다")
    private Integer hospitalId;

    /**
     * 병원의 위치 정보
     * 선택적 값입니다
     */
    private String hospitalLocation;

    /**
     * 환자와 관련된 채팅방 식별자
     * 선택적 값입니다
     */
    private String chatRoomId;

    /**
     * 환자가 속한 진료과
     * 선택적 값입니다
     */
    private String department;

    /**
     * 환자의 이메일 주소
     * 유효한 이메일 형식이어야 합니다
     */
    @Email(message = "유효한 이메일 주소를 입력해주세요")
    private String email;

    /**
     * 환자의 입원 일자
     * ISO-8601 형식의 날짜/시간 문자열
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private String hospitalizationDate;

    /**
     * 사용자 계정 식별자
     * 
     */
    private Integer userId;
}