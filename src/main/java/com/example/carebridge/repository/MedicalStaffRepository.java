package com.example.carebridge.repository;

import com.example.carebridge.entity.MedicalStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalStaffRepository extends JpaRepository<MedicalStaff, Integer> {

    /**
     * 부서 이름으로 모든 의료진을 조회합니다.
     *
     * @param department 부서 이름
     * @return 해당 부서의 모든 의료진 목록
     */
    MedicalStaff findAllByDepartment(String department);

    /**
     * 의료진 ID로 의료진을 조회합니다.
     *
     * @param medicalStaffId 의료진 ID
     * @return 의료진 객체
     */
    MedicalStaff findByMedicalStaffId(Integer medicalStaffId);
}