package com.example.carebridge.service;

import com.example.carebridge.dto.GuardianDto;
import com.example.carebridge.entity.Guardian;
import com.example.carebridge.mapper.GuardianMapper;
import com.example.carebridge.repository.GuardianRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 보호자 정보 관리 서비스
 * 보호자의 등록, 조회, 삭제 등 보호자 관련 기능을 제공하는 서비스 클래스입니다.
 */
@Slf4j
@Service
public class GuardianService {
    private final GuardianRepository guardianRepository;
    private final GuardianMapper guardianMapper;

    /**
     * GuardianRepository 와 GuardianMapper 를 주입받는 생성자
     */
    public GuardianService(GuardianRepository guardianRepository, GuardianMapper guardianMapper) {
        this.guardianRepository = guardianRepository;
        this.guardianMapper = guardianMapper;
    }

    /**
     * 보호자 정보를 조회합니다.
     *
     * @param phoneNumber 보호자 전화번호
     * @return 보호자 정보 DTO
     * @throws IllegalArgumentException 보호자를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public GuardianDto getGuardianInfo(String phoneNumber) {
        log.debug("보호자 정보 조회 시도 - 전화번호: {}", phoneNumber);
        try {
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                log.error("전화번호가 비어있습니다.");
                throw new IllegalArgumentException("전화번호는 필수입니다.");
            }
            Guardian guardian = guardianRepository.findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> {
                        log.error("보호자를 찾을 수 없습니다 - 전화번호: {}", phoneNumber);
                        return new NoSuchElementException("해당 보호자를 찾을 수 없습니다.");
                    });
            log.info("보호자 정보 조회 성공 - 보호자 ID: {}, 전화번호: {}", guardian.getGuardianId(), phoneNumber);
            return guardianMapper.toDto(guardian);
        } catch (IllegalArgumentException e) {
            log.error("보호자 정보 조회 실패 - 전화번호: {}, 오류: {}", phoneNumber, e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("보호자 정보 조회 실패 - 전화번호: {}, 오류: {}", phoneNumber, e.getMessage());
            throw new NoSuchElementException("해당 환자의 보호자 정보가 존재하지 않습니다.");
        } catch (Exception e) {
            log.error("보호자 정보 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("보호자 정보 조회에 실패했습니다.", e);
        }
    }

    /**
     * 특정 환자의 모든 보호자 정보를 조회합니다.
     *
     * @param patientId 환자 ID
     * @return 보호자 정보 리스트
     * @throws IllegalArgumentException 환자 ID가 유효하지 않은 경우
     */
    @Transactional(readOnly = true)
    public List<GuardianDto> getGuardianList(Integer patientId) {
        log.debug("보호자 목록 조회 시도 - 환자 ID: {}", patientId);
        try {
            if (patientId == null) {
                log.error("환자 ID가 null입니다.");
                throw new IllegalArgumentException("환자 ID는 필수입니다.");
            }
            List<Guardian> guardianList = guardianRepository.findAllByPatientId(patientId)
                    .orElseThrow(() -> {
                        log.error("보호자를 찾을 수 없습니다 - 환자 ID: {}", patientId);
                        return new NoSuchElementException("해당 환자의 보호자 목록이 존재하지 않습니다.");
                    });
            log.info("보호자 목록 조회 성공 - 환자 ID: {}, 보호자 수: {}", patientId, guardianList.size());
            return guardianList.stream()
                    .map(guardianMapper::toDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            log.error("보호자 목록 조회 실패 - 환자 ID: {}, 오류: {}", patientId, e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("보호자 목록 조회 실패 - 환자 ID: {}, 오류: {}", patientId, e.getMessage());
            throw new NoSuchElementException("해당 환자의 보호자 목록이 존재하지 않습니다.");
        } catch (Exception e) {
            log.error("보호자 목록 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("보호자 목록 조회에 실패했습니다.", e);
        }
    }


    /**
     * 새로운 보호자를 추가합니다.
     *
     * @param patientId 환자 ID
     * @param name 보호자 이름
     * @param phoneNumber 보호자 전화번호
     * @throws IllegalArgumentException 필수 정보가 누락된 경우
     */
    @Transactional
    public void addGuardian(int patientId, String name, String phoneNumber) {
        log.debug("보호자 추가 시도 - 환자 ID: {}, 보호자 이름: {}, 전화번호: {}", patientId, name, phoneNumber);
        try {
            if (name == null || name.trim().isEmpty()) {
                log.error("보호자 이름이 비어있습니다.");
                throw new IllegalArgumentException("보호자 이름은 필수입니다.");
            }
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                log.error("전화번호가 비어있습니다.");
                throw new IllegalArgumentException("전화번호는 필수입니다.");
            }
            Guardian guardian = new Guardian();
            guardian.setGuardianId(UUID.randomUUID().toString());
            guardian.setPatientId(patientId);
            guardian.setName(name);
            guardian.setPhoneNumber(phoneNumber);
            guardianRepository.save(guardian);
            log.info("보호자 추가 성공 - 환자 ID: {}, 보호자 ID: {}, 보호자 이름: {}", patientId, guardian.getGuardianId(), name);
        } catch (IllegalArgumentException e) {
            log.error("보호자 추가 실패 - 환자 ID: {}, 오류: {}", patientId, e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("보호자 추가 중 예상치 못한 오류 발생 - 환자 ID: {}, 오류: {}", patientId, e.getMessage(), e);
            throw new RuntimeException("보호자 추가에 실패했습니다.", e);
        }
    }

    /**
     * 보호자를 삭제합니다.
     *
     * @param phoneNumber 보호자 전화번호
     * @throws IllegalArgumentException 보호자를 찾을 수 없는 경우
     */
    @Transactional
    public void deleteGuardian(String phoneNumber) {
        log.debug("보호자 삭제 시도 - 전화번호: {}", phoneNumber);
        try {
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                log.error("전화번호가 비어있습니다.");
                throw new IllegalArgumentException("전화번호는 필수입니다.");
            }
            guardianRepository.findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> {
                        log.error("삭제할 보호자를 찾을 수 없습니다 - 전화번호: {}", phoneNumber);
                        return new NoSuchElementException("삭제할 보호자를 찾을 수 없습니다.");
                    });
            guardianRepository.deleteByPhoneNumber(phoneNumber);
            log.info("보호자 삭제 성공 - 전화번호: {}", phoneNumber);
        } catch (IllegalArgumentException e) {
            log.error("보호자 삭제 실패 - 전화번호: {}, 오류: {}", phoneNumber, e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("보호자 삭제 실패 - 전화번호: {}, 오류: {}", phoneNumber, e.getMessage());
            throw new NoSuchElementException("해당 보호자 정보가 존재하지 않습니다.");
        } catch (Exception e) {
            log.error("보호자 삭제 중 예상치 못한 오류 발생 - 전화번호: {}, 오류: {}", phoneNumber, e.getMessage(), e);
            throw new RuntimeException("보호자 삭제에 실패했습니다.", e);
        }
    }
}