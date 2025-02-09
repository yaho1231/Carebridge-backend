package com.example.carebridge.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.example.carebridge.repository.MedicalRecordRepository;
import com.example.carebridge.entity.MedicalRecord;

/**
 * 의료 기록 서비스
 * 환자의 의료 기록 정보를 관리하는 비즈니스 로직을 처리하는 서비스입니다.
 */
@Service
public class MedicalRecordService {
    
    /**
     * 의료 기록 레포지토리
     */
    private final MedicalRecordRepository medicalRecordRepository;

    /**
     * 의료 기록 서비스 생성자
     * @param medicalRecordRepository 의료 기록 레포지토리
     */
    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }

    /**
     * 환자의 질병 정보를 조회합니다.
     * 
     * @param patientId 환자 ID (필수)
     * @return 환자의 질병 정보
     * @throws IllegalArgumentException 환자 ID가 유효하지 않은 경우
     * @throws IllegalStateException 환자의 질병 정보를 찾을 수 없는 경우
     */
    public String getDiseaseInfo(Integer patientId) {
        if (patientId == null || patientId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 환자 ID입니다.");
        }

        try {
            return medicalRecordRepository.findByPatientId(patientId)
                .map(MedicalRecord::getDiseaseInfo)
                .filter(StringUtils::hasText)
                .orElseThrow(() -> new IllegalStateException(
                    String.format("환자 ID %d의 질병 정보를 찾을 수 없습니다.", patientId)
                ));
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("질병 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}