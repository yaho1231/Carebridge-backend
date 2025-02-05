package com.example.carebridge.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 병원 정보를 관리하는 엔티티 클래스
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Hospital") // MySQL 의 Hospital 테이블과 매핑
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 증가 설정
    @Column(name = "hospital_id") // 병원 고유 ID
    private Integer hospitalId; // 병원 고유 ID

    @Column(name = "name", nullable = false, length = 100) // 병원 이름
    private String name; // 병원 이름

    @Column(name = "location", nullable = false, length = 255) // 병원 위치
    private String location; // 병원 위치

    @Builder.Default
    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<HospitalInformation> hospitalInformationList = new ArrayList<>();
}
