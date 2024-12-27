package com.example.carebridge.repository;

import com.example.carebridge.entity.HospitalInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalInfoRepository extends JpaRepository<HospitalInfo, Long> {

    // Find hospital information by category
    List<HospitalInfo> findByCategory(String category);

    // Find hospital information by hospitalId
    List<HospitalInfo> findByHospitalId(Long hospitalId);

    // Find hospital information by category and hospitalId
    List<HospitalInfo> findByCategoryAndHospitalId(String category, Long hospitalId);
}

