package com.example.carebridge.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RequestDto {

    /**
     * 요청 ID (기본 키)
     */
    private Integer requestId;

    /**
     * 환자 ID
     */
    private Integer patientId;

    /**
     * 의료진 ID
     */
    private Integer medicalStaffId;

    /**
     * 요청 내용
     */
    private String requestContent;

    /**
     * 요청 상태
     */
    private String status;

    /**
     * 요청 시간
     */
    private String requestTime;

    /**
     * 요청 수락 시간
     */
    private String acceptTime;
}
