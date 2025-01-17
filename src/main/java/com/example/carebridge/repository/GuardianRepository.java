package com.example.carebridge.repository;

import com.example.carebridge.entity.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuardianRepository extends JpaRepository<Guardian, Integer> {

    /**
     * 보호자 ID로 보호자 정보를 조회합니다.
     *
     * @param guardianId 보호자 ID
     * @return 보호자 엔티티
     */
    Guardian findByGuardianId(String guardianId);

    /**
     * 보호자 ID로 보호자 정보를 삭제합니다.
     *
     * @param guardianId 보호자 ID
     */
    void deleteByGuardianId(String guardianId);
}