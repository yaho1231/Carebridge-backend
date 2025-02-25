package com.example.carebridge.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * RequestDto는 의료 서비스 요청 정보를 전송하기 위한 데이터 전송 객체입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {

    /**
     * 요청 ID (기본 키)
     */
    @NotNull
    private Integer requestId;

    /**
     * 환자 ID
     */
    @NotNull
    private Integer patientId;

    /**
     * 의료진 ID
     */
    @NotNull
    private Integer medicalStaffId;

    /**
     * 요청 내용
     */
    @NotBlank
    private String requestContent;

    /**
     * 요청 상태
     */
    @NotBlank
    private String status;

    /**
     * 요청 시간
     */
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private String requestTime;

    /**
     * 요청 수락 시간
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private String acceptTime;
}
