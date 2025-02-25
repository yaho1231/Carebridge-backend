package com.example.carebridge.repository;

import com.example.carebridge.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 요청 관리를 위한 레포지토리 인터페이스
 * 의료진과 환자 간의 요청 정보를 관리하고 조회하는 기능을 제공합니다.
 */
@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {

    /**
     * 요청 ID로 요청 정보를 조회합니다.
     * Optional을 사용하여 null 안전성을 보장합니다.
     *
     * @param requestId 요청 ID
     * @return 요청 정보를 담은 Optional 객체
     */
    @Query("SELECT r FROM Request r WHERE r.requestId = :requestId")
    Optional<Request> findByRequestId(@Param("requestId") Integer requestId);

    /**
     * 의료진 ID로 요청 목록을 조회합니다.
     * 요청 시간 순으로 정렬하여 반환합니다.
     *
     * @param medicalStaffId 의료진 ID
     * @return 정렬된 요청 목록
     */
    @Query("SELECT r FROM Request r WHERE r.medicalStaffId = :medicalStaffId ORDER BY r.requestTime DESC")
    List<Request> findByMedicalStaffId(@Param("medicalStaffId") Integer medicalStaffId);

    /**
     * 환자 ID로 요청 목록을 조회합니다.
     * 요청 시간 순으로 정렬하여 반환합니다.
     *
     * @param patientId 환자 ID
     * @return 정렬된 요청 목록
     */
    @Query("SELECT r FROM Request r WHERE r.patientId = :patientId ORDER BY r.requestTime DESC")
    List<Request> findByPatientIdOrderByRequestTime(@Param("patientId") Integer patientId);

    /**
     * 요청 ID로 요청 정보를 삭제합니다.
     *
     * @param requestId 요청 ID
     */
    @Query("DELETE FROM Request r WHERE r.requestId = :requestId")
    void deleteByRequestId(@Param("requestId") Integer requestId);
}