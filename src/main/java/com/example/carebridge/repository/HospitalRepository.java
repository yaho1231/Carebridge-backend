package com.example.carebridge.repository;

import com.example.carebridge.entity.Hospital;
import com.example.carebridge.entity.HospitalInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    Hospital findByHospitalId(Integer hospitalId);
}
