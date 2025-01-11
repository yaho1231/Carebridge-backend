package com.example.carebridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.carebridge.entity.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {

    /**
     * 환자의 ID로 환자 정보를 조회합니다.
     * @param patientId 환자의 ID
     * @return 환자 객체
     */
    Patient findByPatientId(Integer patientId);
}