package com.example.carebridge.repository;

import com.example.carebridge.entity.MedicalStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalStaffRepository extends JpaRepository<MedicalStaff, Integer> {
    MedicalStaff findAllByDepartment(String department);
}