package com.example.carebridge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Hospital") // MySQL 의 Hospital 테이블과 매핑
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 증가 설정
    @Column(name = "hospital_id") // 병원 고유 ID, Not Null 제약 조건 설정
    private Integer HospitalId; // 병원 고유 ID

    @Column(name = "name") // 병원 이름, Not Null 제약 조건 설정
    private String name; // 병원 이름

    @Column(name = "location") // 병원 위치 정보, Not Null 제약 조건 설정
    private String location; // 병원 위치
}
