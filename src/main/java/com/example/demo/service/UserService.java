package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    public String getUsers() {
        // 비즈니스 로직 처리 (예: 데이터베이스에서 사용자 목록 조회)
        return "User list";
    }

    public String getUserById(Long id) {
        // 비즈니스 로직 (예: 특정 ID의 사용자 조회)
        return "User details for ID: " + id;
    }

}
