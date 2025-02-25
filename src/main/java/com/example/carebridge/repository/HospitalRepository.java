package com.example.carebridge.repository;

import com.example.carebridge.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 병원 정보 관리를 위한 레포지토리 인터페이스
 * JpaRepository를 상속받아 기본적인 CRUD 기능을 제공합니다.
 */
@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    
    /**
     * 병원 ID로 병원 정보를 조회합니다.
     * Optional을 사용하여 null 안전성을 보장합니다.
     * 
     * @param hospitalId 조회할 병원 ID
     * @return 병원 정보를 담은 Optional 객체
     * @throws IllegalArgumentException 유효하지 않은 hospitalId가 입력된 경우
     */
    @Query("SELECT h FROM Hospital h WHERE h.hospitalId = :hospitalId")
    Optional<Hospital> findByHospitalId(@Param("hospitalId") Integer hospitalId);
}
