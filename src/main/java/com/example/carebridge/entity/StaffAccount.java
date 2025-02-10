package com.example.carebridge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Staff_Account") // MySQL 의 Patient 테이블과 매핑
public class StaffAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 설정
    private Long id;

    @Column(name = "user_id",nullable = false, unique = true, length = 50) // 로그인 아이디
    private String userId;

    @Column(nullable = false) // 비밀번호
    private String password;

    @Column(name = "hospital_id" ,nullable = false) // 병원 아이디
    private int hospitalId;

}
