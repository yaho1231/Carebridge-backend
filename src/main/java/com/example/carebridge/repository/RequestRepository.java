package com.example.carebridge.repository;

import com.example.carebridge.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {

    /**
     * 요청 ID로 요청 정보를 조회합니다.
     *
     * @param requestId 요청 ID
     * @return 요청 엔티티
     */
    Request findByRequestId(Integer requestId);

    /**
     * 의료진 ID로 요청 목록을 조회합니다.
     *
     * @param medicalStaffId 의료진 ID
     * @return 요청 목록
     */
    List<Request> findByMedicalStaffId(Integer medicalStaffId);

    /**
     * 환자 ID로 요청 목록을 요청 시간 순으로 조회합니다.
     *
     * @param patientId 환자 ID
     * @return 요청 목록
     */
    List<Request> findByPatientIdOrderByRequestTime(Integer patientId);

    /**
     * 요청 ID로 요청 정보를 삭제합니다.
     *
     * @param requestId 요청 ID
     */
    void deleteByRequestId(Integer requestId);
}