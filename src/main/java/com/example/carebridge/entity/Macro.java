package com.example.carebridge.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "Macro")
public class Macro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "macro_id")
    private Integer macroId;

    @Column(name = "medical_staff_id", nullable = false)
    private Integer medicalStaffId;

    @Column(name = "text", nullable = false, length = 255)
    private String text;

    @Column(name = "macro_name", nullable = false, length = 255)
    private String macroName;

    @Builder
    public Macro(Integer medicalStaffId, String text, String macroName) {
        this.medicalStaffId = medicalStaffId;
        this.text = text;
        this.macroName = macroName;
    }
}