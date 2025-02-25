package com.example.carebridge.repository;

import com.example.carebridge.entity.HospitalInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 병원 정보 관리를 위한 레포지토리 인터페이스
 * JpaRepository를 상속받아 기본적인 CRUD 기능을 제공합니다.
 */
@Repository
public interface HospitalInformationRepository extends JpaRepository<HospitalInformation, Integer> {
    
    /**
     * 특정 병원의 모든 정보를 조회합니다.
     * 
     * @param hospitalId 조회할 병원 ID
     * @return 해당 병원의 모든 정보 목록
     */
    @Query("SELECT hi FROM HospitalInformation hi WHERE hi.hospital.hospitalId = :hospitalId ORDER BY hi.title")
    List<HospitalInformation> findAllByHospitalId(@Param("hospitalId") Integer hospitalId);
    
    /**
     * 특정 병원의 특정 제목에 해당하는 정보를 조회합니다.
     * 
     * @param hospitalId 조회할 병원 ID
     * @param title 조회할 정보의 제목
     * @return 해당하는 병원 정보
     */
    @Query("SELECT hi FROM HospitalInformation hi WHERE hi.hospital.hospitalId = :hospitalId AND hi.title = :title")
    Optional<HospitalInformation> findByHospitalIdAndTitle(
        @Param("hospitalId") Integer hospitalId, 
        @Param("title") String title
    );

    @Query("SELECT hi FROM HospitalInformation hi WHERE hi.hospital.hospitalId = :hospitalId AND hi.id = :id")
    Optional<HospitalInformation> findByHospitalIdAndId(Integer hospitalId, Integer id);
}
