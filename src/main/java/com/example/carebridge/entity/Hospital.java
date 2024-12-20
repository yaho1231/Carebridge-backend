package com.example.carebridge.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Hospital") // MySQL의 Hospital 테이블과 매핑
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 증가 설정
    private Integer id; // 병원 고유 ID

    @Column(nullable = false) // 병원 이름, Not Null 제약 조건 설정
    private String name; // 병원 이름

    @Column(nullable = false) // 병원 위치 정보, Not Null 제약 조건 설정
    private String location; // 병원 위치

    // Getter와 Setter 메서드 정의
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
