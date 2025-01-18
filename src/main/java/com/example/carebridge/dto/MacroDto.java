package com.example.carebridge.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class MacroDto {
    private Integer macroId; // 매크로 ID

    private Integer medicalStaffId; // 의료진 ID

    private String text; // 텍스트

    private String macroName; // 매크로 이름
}
