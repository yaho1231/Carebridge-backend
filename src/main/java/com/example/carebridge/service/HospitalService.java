package com.example.carebridge.service;

import com.example.carebridge.entity.Hospital;
import com.example.carebridge.repository.HospitalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

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
        log.debug("병원 이름 조회 시도 - 병원 ID: {}", hospitalId);
        try {
            if (hospitalId == null) {
                log.error("병원 ID가 null입니다.");
                throw new IllegalArgumentException("병원 ID는 필수 입력값입니다.");
            }
            Hospital hospital = hospitalRepository.findByHospitalId(hospitalId)
                    .orElseThrow(() -> {
                        log.error("병원을 찾을 수 없습니다 - 병원 ID: {}", hospitalId);
                        return new NoSuchElementException("해당 ID의 병원을 찾을 수 없습니다.");
                    });
            log.info("병원 이름 조회 성공 - 병원 ID: {}, 병원명: {}", hospitalId, hospital.getName());
            return hospital.getName();
        } catch (IllegalArgumentException e) {
            log.error("병원 이름 조회 실패 - 입력값 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("병원 이름 조회 실패 - 병원 정보 조회 실패: {}", e.getMessage());
            throw new NoSuchElementException("해당 병원 정보를 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("병원 이름 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("병원 이름 조회에 실패했습니다.", e);
        }
    }

    /**
     * 병원 ID로 병원 존재 여부를 확인합니다.
     * 
     * @param hospitalId 확인할 병원 ID
     * @return 병원 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean isHospitalExist(Integer hospitalId) {
        log.debug("병원 존재 여부 확인 시도 - 병원 ID: {}", hospitalId);
        try {
            if (hospitalId == null) {
                log.error("병원 ID가 null입니다.");
                throw new IllegalArgumentException("병원 ID는 필수 입력값입니다.");
            }
            boolean exists = hospitalRepository.findByHospitalId(hospitalId).isPresent();
            log.info("병원 존재 여부 확인 완료 - 병원 ID: {}, 존재 여부: {}", hospitalId, exists);
            return exists;
        } catch (IllegalArgumentException e) {
            log.error("병원 존재 여부 확인 실패 - 입력값 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("병원 존재 여부 확인 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("병원 존재 여부 확인에 실패했습니다.", e);
        }
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
        log.debug("병원 정보 안전 조회 시도 - 병원 ID: {}", hospitalId);

        try {
            if (hospitalId == null) {
                log.error("병원 ID가 null입니다.");
                throw new IllegalArgumentException("병원 ID는 필수 입력값입니다.");
            }
            return hospitalRepository.findByHospitalId(hospitalId)
                    .map(hospital -> {
                        log.info("병원 정보 조회 성공 - 병원 ID: {}, 병원명: {}", hospitalId, hospital.getName());
                        return hospital;
                    })
                    .orElseGet(() -> {
                        log.warn("병원을 찾을 수 없습니다. 병원 ID: {}", hospitalId);
                        throw new NoSuchElementException("해당 ID의 병원을 찾을 수 없습니다.");
                    });
        } catch (IllegalArgumentException e) {
            log.error("병원 정보 안전 조회 실패 - 입력값 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("병원 정보 안전 조회 실패 - 병원 정보 조회 실패: {}", e.getMessage());
            throw new NoSuchElementException("해당 병원 정보를 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("병원 정보 안전 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("병원 정보 안전 조회에 실패했습니다.", e);
        }
    }
}
