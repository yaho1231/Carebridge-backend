package com.example.carebridge.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 환자의 보호자 정보를 전송하기 위한 DTO 클래스
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuardianDto {

    /**
     * 보호자의 고유 식별자
     * UUID 형식으로 저장됩니다
     */
    @NotNull(message = "보호자 ID는 필수입니다")
    private String guardianId;

    /**
     * 보호자의 이름
     * 최대 50자까지 입력 가능합니다
     */
    @NotNull(message = "보호자 이름은 필수입니다")
    @Size(max = 50, message = "이름은 50자를 초과할 수 없습니다")
    private String name;

    /**
     * 담당하는 환자의 고유 식별자
     * 필수 값입니다
     */
    @NotNull(message = "환자 ID는 필수입니다")
    private Integer patientId;

    /**
     * 보호자의 연락처
     * 형식: XXX-XXXX-XXXX 또는 XXXXXXXXXXX
     * (예: 010-1234-5678 또는 01012345678)
     */
    @NotNull(message = "전화번호는 필수입니다")
    @Pattern(regexp = "^\\d{2,3}[-]?\\d{3,4}[-]?\\d{4}$", 
            message = "전화번호는 'XXX-XXXX-XXXX' 형식 또는 숫자만 입력 가능합니다")
    private String phoneNumber;
}