package com.example.carebridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.carebridge.entity.MedicalRecord;
import java.util.Optional;

/**
 * 의료 기록 레포지토리
 * 환자의 의료 기록 정보를 데이터베이스에서 조회하고 관리하는 인터페이스입니다.
 */
@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Integer> {
    
    /**
     * 환자 ID로 의료 기록을 조회합니다.
     * Optional을 사용하여 null 안전성을 보장합니다.
     * 
     * @param patientId 조회할 환자의 ID
     * @return 환자의 의료 기록 정보를 담은 Optional 객체
     */
    @Query("SELECT m FROM MedicalRecord m WHERE m.patientId = :patientId")
    Optional<MedicalRecord> findByPatientId(@Param("patientId") Integer patientId);
}
