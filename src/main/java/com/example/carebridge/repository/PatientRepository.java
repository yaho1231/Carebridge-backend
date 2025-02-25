package com.example.carebridge.repository;

import com.example.carebridge.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 환자 정보 관리를 위한 레포지토리 인터페이스
 * 환자의 기본 정보 조회 및 관리 기능을 제공합니다.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {

    /**
     * 환자 ID로 환자 정보를 조회합니다.
     * Optional을 사용하여 null 안전성을 보장합니다.
     *
     * @param patientId 환자의 ID
     * @return 환자 정보를 담은 Optional 객체
     */
    @Query("SELECT p FROM Patient p WHERE p.patientId = :patientId")
    Optional<Patient> findByPatientId(@Param("patientId") Integer patientId);

    /**
     * 모든 환자 정보를 이름순으로 조회합니다.
     *
     * @return 정렬된 환자 목록
     */
    @NonNull
    @Query("SELECT p FROM Patient p ORDER BY p.name")
    List<Patient> findAll();

    /**
     * 병원 ID와 부서로 환자 정보를 조회합니다.
     * 환자 이름순으로 정렬하여 반환합니다.
     *
     * @param hospitalId 병원 ID
     * @param department 부서명
     * @return 해당 병원과 부서에 속한 환자 목록
     */
    @Query("SELECT p FROM Patient p WHERE p.hospitalId = :hospitalId AND p.department = :department ORDER BY p.name")
    List<Patient> findByHospitalIdAndDepartment(
        @Param("hospitalId") Integer hospitalId, 
        @Param("department") String department
    );

    @Query("SELECT p FROM Patient p WHERE p.phoneNumber = :phone")
    Optional<Patient> findByPhoneNumber(@Param("phone") String phone);
}