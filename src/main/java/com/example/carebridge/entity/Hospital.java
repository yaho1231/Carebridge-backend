package com.example.carebridge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "Hospital") // MySQL 의 Hospital 테이블과 매핑
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 증가 설정
    @Column(name = "hospital_id") // 병원 고유 ID
    private Integer hospitalId; // 병원 고유 ID

    @Column(name = "name", nullable = false) // 병원 이름
    private String name; // 병원 이름

    @Column(name = "location", nullable = false) // 병원 위치
    private String location; // 병원 위치

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<HospitalInformation> hospitalInformationList = new ArrayList<>();
}
