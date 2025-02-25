package com.example.carebridge.service;

import com.example.carebridge.entity.MedicalStaff;
import com.example.carebridge.repository.MedicalStaffRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 의료진 정보 관리 서비스
 * 의료진의 기본 정보 조회 및 관리를 담당하는 서비스 클래스입니다.
 */
@Slf4j
@Service
public class MedicalStaffService {
    private final MedicalStaffRepository medicalStaffRepository;

    /**
     * MedicalStaffRepository 를 주입받는 생성자입니다.
     *
     * @param medicalStaffRepository 의료진 정보 레포지토리
     */
    public MedicalStaffService(MedicalStaffRepository medicalStaffRepository) {
        this.medicalStaffRepository = medicalStaffRepository;
    }

    /**
     * 특정 분과의 모든 의료진을 조회합니다.
     *
     * @param department 의료진 소속 분과
     * @return 해당 분과의 의료진 객체
     * @throws IllegalArgumentException 부서가 null 이거나 빈 문자열인 경우, 또는 해당 부서의 의료진을 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public MedicalStaff findAllByDepartment(String department) {
        // 부서명 유효성 검사
        if (department == null || department.trim().isEmpty()) {
            log.error("부서명이 null 이거나 빈 문자열입니다.");
            throw new IllegalArgumentException("부서명은 필수 입력값입니다.");
        }

        // 의료진 조회
        return medicalStaffRepository.findByDepartment(department)
                .stream()
                .findFirst()
                .orElseThrow(() -> {
                    log.error("부서 {}에 해당하는 의료진을 찾을 수 없습니다.", department);
                    return new IllegalArgumentException("해당 부서의 의료진을 찾을 수 없습니다: " + department);
                });
    }

    @Transactional(readOnly = true)
    public List<MedicalStaff> findAllByHospitalId(Integer hospitalId) {
        if (hospitalId == null) {
            log.error("병원 Id가 null 이거나 빈 문자열입니다.");
            throw new IllegalArgumentException("병원 ID는 필수 입력값입니다.");
        }
        List<MedicalStaff> medicalStaffList = medicalStaffRepository.findByHospitalId(hospitalId);
        if (medicalStaffList == null || medicalStaffList.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 병원 ID");
        }
        return medicalStaffList;
    }
}