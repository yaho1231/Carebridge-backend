package com.example.carebridge.dto;

import com.example.carebridge.entity.ExaminationSchedule;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 진료 일정 정보를 전송하기 위한 DTO 클래스
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExaminationScheduleDto {

    /**
     * 진료 일정의 고유 식별자
     */
    @NotNull(message = "일정 ID는 필수입니다")
    private Integer id;

    /**
     * 환자의 고유 식별자
     */
    @NotNull(message = "환자 ID는 필수입니다")
    private Integer patientId;

    /**
     * 담당 의료진의 고유 식별자
     */
    @NotNull(message = "의료진 ID는 필수입니다")
    private Integer medicalStaffId;

    /**
     * 진료 예정 일시
     * ISO-8601 형식의 날짜/시간 문자열
     */
    @NotNull(message = "진료 일시는 필수입니다")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private String scheduleDate;

    /**
     * 진료 세부사항
     */
    private String details;

    /**
     * 진료 종류
     * SURGERY(수술), OUTPATIENT(외래), EXAMINATION(검진)
     */
    @NotNull(message = "진료 종류는 필수입니다")
    private ExaminationSchedule.Category category;

}