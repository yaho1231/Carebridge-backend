package com.example.carebridge.repository;

import com.example.carebridge.entity.HospitalInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalInformationRepository extends JpaRepository<HospitalInformation, Long> {
    List<HospitalInformation> findAllByHospitalId(int hospital_id);
}
