package com.example.carebridge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Guardian") // MySQL의 Guardian 테이블과 매핑
public class Guardian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 증가 설정
    private Integer id; // 보호자 고유 ID

    @Column(nullable = false) // 보호자 이름, Not Null 제약 조건 설정
    private String name; // 보호자 이름

    @Column(name = "phone_number", nullable = false, unique = true) // 보호자 전화번호 컬럼과 매핑 (Unique 제약 조건)
    private String phoneNumber; // 보호자 전화번호
}
