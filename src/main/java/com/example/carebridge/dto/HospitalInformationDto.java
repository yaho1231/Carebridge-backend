package com.example.carebridge.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 병원 정보를 전송하기 위한 DTO 클래스
 * 병원의 상세 정보를 담고 있습니다
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HospitalInformationDto {
    
    /**
     * 병원 정보의 고유 식별자
     * 
     */
    private Integer id;

    /**
     * 병원의 고유 식별자
     * 필수 값으로, null이 허용되지 않습니다
     */
    @NotNull(message = "병원 ID는 필수입니다")
    private Integer hospitalId;

    /**
     * 정보의 카테고리
     * 예: 진료과목, 시설안내, 의료진 소개 등
     * 최대 50자까지 입력 가능
     */
    @NotNull(message = "카테고리는 필수입니다")
    @Size(max = 50, message = "카테고리는 50자를 초과할 수 없습니다")
    private String category;

    /**
     * 정보의 제목
     * 최대 100자까지 입력 가능
     */
    @NotNull(message = "제목은 필수입니다")
    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다")
    private String title;

    /**
     * 상세 정보 내용
     * 최대 1000자까지 입력 가능
     */
    @Size(max = 1000, message = "정보 내용은 1000자를 초과할 수 없습니다")
    private String information;
}
