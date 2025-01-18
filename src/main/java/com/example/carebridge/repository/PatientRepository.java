package com.example.carebridge.repository;

import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.carebridge.entity.Patient;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {

    /**
     * 환자의 ID로 환자 정보를 조회합니다.
     *
     * @param patientId 환자의 ID
     * @return 환자 객체
     */
    Patient findByPatientId(Integer patientId);

    /**
     * 모든 환자 정보를 조회합니다.
     *
     * @return 환자 리스트
     */
    @Nonnull
    List<Patient> findAll();

    /**
     * 병원 ID와 부서로 환자 정보를 조회합니다.
     *
     * @param hospitalId 병원 ID
     * @param department 부서
     * @return 해당 병원과 부서에 속한 환자 리스트
     */
    List<Patient> findByHospitalIdAndDepartment(Integer hospitalId, String department);
}