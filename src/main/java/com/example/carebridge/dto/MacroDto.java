package com.example.carebridge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MacroDto는 의료진의 매크로 정보를 전송하기 위한 데이터 전송 객체입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MacroDto {
    /**
     * 매크로 ID
     */
    private Integer macroId;

    /**
     * 의료진 ID
     */
    @NotNull
    private Integer medicalStaffId;

    /**
     * 매크로 텍스트 내용
     */
    @NotBlank
    private String text;

    /**
     * 매크로 이름
     */
    @NotBlank
    private String macroName;
}
