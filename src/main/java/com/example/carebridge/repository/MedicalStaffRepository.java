package com.example.carebridge.repository;

import com.example.carebridge.entity.MedicalStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 의료진 정보 관리를 위한 레포지토리 인터페이스
 * 의료진의 기본 정보 조회 및 관리 기능을 제공합니다.
 */
@Repository
public interface MedicalStaffRepository extends JpaRepository<MedicalStaff, Integer> {

    /**
     * 부서 이름으로 의료진을 조회합니다.
     * 각 부서에는 한 명의 의료진만 존재합니다.
     *
     * @param department 부서 이름
     * @return 해당 부서의 의료진 정보를 담은 Optional 객체
     */
    @Query("SELECT ms FROM MedicalStaff ms WHERE ms.department = :department")
    Optional<MedicalStaff> findByDepartment(@Param("department") String department);

    /**
     * 의료진 ID로 의료진을 조회합니다.
     * Optional을 사용하여 null 안전성을 보장합니다.
     *
     * @param medicalStaffId 의료진 ID
     * @return 의료진 정보를 담은 Optional 객체
     */
    @Query("SELECT ms FROM MedicalStaff ms WHERE ms.medicalStaffId = :medicalStaffId")
    Optional<MedicalStaff> findByMedicalStaffId(@Param("medicalStaffId") Integer medicalStaffId);
}