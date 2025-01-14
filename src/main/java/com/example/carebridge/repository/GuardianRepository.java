package com.example.carebridge.repository;

import com.example.carebridge.entity.Guardian;
import com.example.carebridge.entity.MedicalStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuardianRepository extends JpaRepository<Guardian, Integer> {
    Guardian findByPatientId(Integer patientId);
}