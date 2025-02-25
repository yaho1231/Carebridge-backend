package com.example.carebridge.service;

import com.example.carebridge.entity.Hospital;
import com.example.carebridge.repository.HospitalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 병원 관련 서비스
 * 병원 정보 조회 및 관리를 담당하는 서비스 클래스입니다.
 */
@Slf4j
@Service
public class HospitalService {
    
    private final HospitalRepository hospitalRepository;

    /**
     * HospitalRepository를 주입받는 생성자
     * 
     * @param hospitalRepository 병원 레포지토리
     */
    public HospitalService(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    /**
     * 병원 ID로 병원 이름을 조회합니다.
     * 
     * @param hospitalId 조회할 병원 ID
     * @return 병원 이름
     * @throws IllegalArgumentException 해당 ID의 병원을 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public String getHospitalName(Integer hospitalId) {
        return hospitalRepository.findByHospitalId(hospitalId)
                .orElseThrow(() -> {
                    log.error("병원을 찾을 수 없습니다. ID: {}", hospitalId);
                    return new IllegalArgumentException("해당 ID의 병원을 찾을 수 없습니다: " + hospitalId);
                })
                .getName();
    }

    /**
     * 병원 ID로 병원 존재 여부를 확인합니다.
     * 
     * @param hospitalId 확인할 병원 ID
     * @return 병원 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean isHospitalExist(Integer hospitalId) {
        return hospitalRepository.findByHospitalId(hospitalId).isPresent();
    }

    /**
     * 안전하게 병원 정보를 조회합니다.
     * 병원이 존재하지 않는 경우 null을 반환합니다.
     * 
     * @param hospitalId 조회할 병원 ID
     * @return 병원 엔티티 또는 null
     */
    @Transactional(readOnly = true)
    public Hospital getHospitalSafely(Integer hospitalId) {
        return hospitalRepository.findByHospitalId(hospitalId)
                .map(hospital -> {
                    log.debug("병원 정보 조회 성공 - ID: {}, 이름: {}", hospitalId, hospital.getName());
                    return hospital;
                })
                .orElseGet(() -> {
                    log.warn("병원을 찾을 수 없습니다. ID: {}", hospitalId);
                    return null;
                });
    }
}
