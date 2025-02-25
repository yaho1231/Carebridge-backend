package com.example.carebridge.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

/**
 * 보호자 정보를 관리하는 엔티티 클래스
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Guardian") // MySQL의 Guardian 테이블과 매핑
public class Guardian {

    @Id
    @Column(name = "guardian_id") // 보호자 ID 컬럼과 매핑
    private String guardianId; // 보호자 고유 ID

    @Column(name = "name", nullable = false, length = 50) // 보호자 이름, Not Null 제약 조건 설정
    private String name; // 보호자 이름

    @Column(name = "patient_id", nullable = false) // 환자 ID 컬럼과 매핑
    private Integer patientId; // 환자 ID

    @Column(name = "phone_number", nullable = false, length = 15, unique = true)
    private String phoneNumber; // 보호자 전화번호
}
