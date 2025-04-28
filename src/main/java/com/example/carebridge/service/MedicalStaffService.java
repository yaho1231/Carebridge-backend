package com.example.carebridge.service;

import com.example.carebridge.entity.MedicalStaff;
import com.example.carebridge.repository.MedicalStaffRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

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
        log.debug("부서별 의료진 조회 시도 - 부서명: {}", department);
        try {
            if (department == null || department.trim().isEmpty()) {
                log.error("부서명이 null 이거나 빈 문자열입니다.");
                throw new IllegalArgumentException("부서명은 필수 입력값입니다.");
            }
            return medicalStaffRepository.findByDepartment(department)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> {
                        log.error("부서 {}에 해당하는 의료진을 찾을 수 없습니다.", department);
                        return new NoSuchElementException("해당 부서의 의료진을 찾을 수 없습니다: " + department);
                    });
        } catch (IllegalArgumentException e) {
            log.error("부서별 의료진 조회 실패 - 입력값 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("부서별 의료진 조회 실패 - 데이터 없음: {}", e.getMessage());
            throw new NoSuchElementException("해당 부서의 의료진을 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("부서별 의료진 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("부서별 의료진 조회에 실패했습니다.", e);
        }
    }

    @Transactional(readOnly = true)
    public List<MedicalStaff> findAllByHospitalId(Integer hospitalId) {
        log.debug("병원 ID로 의료진 조회 시도 - 병원 ID: {}", hospitalId);
        try {
            if (hospitalId == null) {
                log.error("병원 ID가 null 입니다.");
                throw new IllegalArgumentException("병원 ID는 필수 입력값입니다.");
            }
            List<MedicalStaff> medicalStaffList = medicalStaffRepository.findByHospitalId(hospitalId)
                    .orElseThrow(() -> {
                        log.error("병원 ID {}에 해당하는 의료진을 찾을 수 없습니다.", hospitalId);
                        return new NoSuchElementException("해당 병원 ID의 의료진을 찾을 수 없습니다: " + hospitalId);
                    });
            log.info("병원 ID로 의료진 조회 성공 - 병원 ID: {}, 의료진 수: {}", hospitalId, medicalStaffList.size());
            return medicalStaffList;
        } catch (IllegalArgumentException e) {
            log.error("병원 ID로 의료진 조회 실패 - 입력값 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("병원 ID로 의료진 조회 실패 - 데이터 없음: {}", e.getMessage());
            throw new NoSuchElementException("해당 병원 ID의 의료진을 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("병원 ID로 의료진 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("병원 ID로 의료진 조회에 실패했습니다.", e);
        }
    }
}