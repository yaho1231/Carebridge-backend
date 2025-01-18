package com.example.carebridge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Macro") // MySQL 의 Macro 테이블과 매핑
public class Macro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 증가 설정
    @Column(name = "macro_id") // 매크로 ID, Not Null 제약 조건 설정
    private Integer macroId; // 매크로 ID

    @Column(name = "medical_Staff_id") // 의료진 ID, Not Null 제약 조건 설정
    private Integer medicalStaffId; // 의료진 ID

    @Column(name = "text") // 텍스트 필드, Not Null 제약 조건 설정, 최대 길이 255
    private String text; // 텍스트

    @Column(name = "macro_name") // 매크로 이름, Not Null 제약 조건 설정, 최대 길이 255
    private String macroName; // 매크로 이름
}