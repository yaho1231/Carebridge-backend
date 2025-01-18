package com.example.carebridge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Hospital_Information")
public class HospitalInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 증가 설정
    private Integer id; // 병원 정보 고유 ID

    @Column(name = "hospital_id", nullable = false)
    private Integer hospitalId; // 병원 ID

    @Column(name = "category") // 정보 카테고리
    private String category; // 정보 카테고리

    @Column(name = "title") // 정보 제목
    private String title;

    @Column(name = "information") // 병원 정보
    private String information;
}
