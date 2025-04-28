package com.example.carebridge.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.example.carebridge.repository.MedicalRecordRepository;
import com.example.carebridge.entity.MedicalRecord;
import java.util.NoSuchElementException;

/**
 * 의료 기록 서비스
 * 환자의 의료 기록 정보를 관리하는 비즈니스 로직을 처리하는 서비스입니다.
 */
@Slf4j
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
    @Transactional(readOnly = true)
    public String getDiseaseInfo(Integer patientId) {
        log.debug("질병 정보 조회 시도 - 환자 ID: {}", patientId);
        try {
            if (patientId == null || patientId <= 0) {
                log.error("유효하지 않은 환자 ID입니다: {}", patientId);
                throw new IllegalArgumentException("환자 ID는 필수이며, 0보다 커야 합니다.");
            }
            return medicalRecordRepository.findByPatientId(patientId)
                    .map(MedicalRecord::getDiseaseInfo)
                    .filter(StringUtils::hasText)
                    .orElseThrow(() -> {
                        log.error("질병 정보를 찾을 수 없습니다 - 환자 ID: {}", patientId);
                        return new NoSuchElementException(String.format("환자 ID %d의 질병 정보를 찾을 수 없습니다.", patientId));
                    });
        } catch (IllegalArgumentException e) {
            log.error("질병 정보 조회 실패 - 입력값 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("질병 정보 조회 실패 - 데이터 없음: {}", e.getMessage());
            throw new NoSuchElementException("해당 환자의 질병 정보를 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("질병 정보 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("질병 정보 조회에 실패했습니다.", e);
        }
    }
}