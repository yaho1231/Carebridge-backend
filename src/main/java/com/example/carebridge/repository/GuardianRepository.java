package com.example.carebridge.repository;

import com.example.carebridge.entity.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuardianRepository extends JpaRepository<Guardian, Integer> {

    /**
     * 전화번호로 보호자 정보를 삭제합니다.
     *
     * @param phoneNumber 보호자 전화번호
     */
    void deleteByPhoneNumber(String phoneNumber);

    /**
     * 전화번호로 보호자 정보를 조회합니다.
     *
     * @param phoneNumber 보호자 전화번호
     * @return 보호자 엔티티
     */
    Guardian findByPhoneNumber(String phoneNumber);

    /**
     * 환자 ID로 모든 보호자 정보를 조회합니다.
     *
     * @param patientId 환자 ID
     * @return 보호자 엔티티 리스트
     */
    List<Guardian> findAllByPatientId(Integer patientId);
}