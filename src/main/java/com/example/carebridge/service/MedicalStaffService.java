package com.example.carebridge.service;

import com.example.carebridge.entity.MedicalStaff;
import com.example.carebridge.repository.MedicalStaffRepository;
import org.springframework.stereotype.Service;

@Service
public class MedicalStaffService {
    private final MedicalStaffRepository medicalStaffRepository;

    public MedicalStaffService(MedicalStaffRepository medicalStaffRepository) {
        this.medicalStaffRepository = medicalStaffRepository;
    }

    /**
     * 특정 분과의 모든 의료진을 조회합니다.
     *
     * @param department 의료진 소속 분과
     * @return 해당 분과의 의료진 객체
     */
    public MedicalStaff findAllByDepartment(String department) {
        return medicalStaffRepository.findAllByDepartment(department);
    }
}