package com.example.carebridge.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

/**
 * 병원 상세 정보를 관리하는 엔티티 클래스
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Hospital_Information")
public class HospitalInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // 병원 정보 고유 식별자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;  // 해당 정보가 속한 병원

    @Column(name = "category", length = 50)
    private String category;  // 정보 분류 (예: 진료과목, 운영시간, 특수장비 등)

    @Column(name = "title", length = 100)
    private String title;  // 정보 제목

    @Column(name = "information", columnDefinition = "TEXT")
    private String information;  // 상세 정보 내용
}
