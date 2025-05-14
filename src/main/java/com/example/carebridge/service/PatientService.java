package com.example.carebridge.service;

import com.example.carebridge.entity.Patient;
import com.example.carebridge.dto.PatientDto;
import com.example.carebridge.entity.UserAccount;
import com.example.carebridge.repository.MedicalStaffRepository;
import com.example.carebridge.repository.PatientRepository;
import com.example.carebridge.repository.UserAccountRepository;
import com.example.carebridge.mapper.PatientMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 환자 정보 관리 서비스
 * 환자의 기본 정보 조회, 생성, 수정 등을 처리하는 서비스 클래스입니다.
 */
@Slf4j
@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final MedicalStaffRepository medicalStaffRepository;
    private final UserAccountRepository userAccountRepository;
    private final PatientMapper patientMapper;

    /**
     * 필요한 레포지토리들을 주입받는 생성자입니다.
     *
     * @param patientRepository 환자 정보 레포지토리
     * @param medicalStaffRepository 의료진 정보 레포지토리
     * @param userAccountRepository 사용자 계정 레포지토리
     * @param patientMapper Patient 엔티티와 DTO 간의 변환을 처리하는 매퍼
     */
    public PatientService(PatientRepository patientRepository, 
                         MedicalStaffRepository medicalStaffRepository, 
                         UserAccountRepository userAccountRepository,
                         PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.medicalStaffRepository = medicalStaffRepository;
        this.userAccountRepository = userAccountRepository;
        this.patientMapper = patientMapper;
    }

    /**
     * 의료진 ID로 담당 환자 목록을 조회합니다.
     * 의료진의 소속 병원과 부서를 기준으로 환자 목록을 필터링합니다.
     *
     * @param medicalStaffId 의료진 ID
     * @return 환자 정보 목록
     * @throws IllegalArgumentException 의료진 ID가 null 이거나 해당 의료진을 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public List<PatientDto> getPatientList(Integer medicalStaffId) {
        log.debug("담당 환자 리스트 조회 시도 - 의료진 ID: {}", medicalStaffId);
        if (medicalStaffId == null) {
            log.error("의료진 ID가 null 입니다.");
            throw new IllegalArgumentException("의료진 ID는 필수 입력값입니다.");
        }
        try {
            var medicalStaff = medicalStaffRepository.findByMedicalStaffId(medicalStaffId)
                    .orElseThrow(() -> {
                        log.error("의료진을 찾을 수 없습니다 - ID: {}", medicalStaffId);
                        return new NoSuchElementException("해당 ID의 의료진을 찾을 수 없습니다: " + medicalStaffId);
                    });
            String department = medicalStaff.getDepartment();
            Integer hospitalId = medicalStaff.getHospitalId();
            List<Patient> patients = patientRepository.findByHospitalIdAndDepartment(hospitalId, department)
                    .orElseThrow(() -> {
                        log.error("환자 목록을 찾을 수 없습니다 - 병원 ID: {}, 부서: {}", hospitalId, department);
                        return new NoSuchElementException("해당 병원 및 부서의 환자 목록이 존재하지 않습니다.");
                    });
            List<PatientDto> patientDtoList = new ArrayList<>();
            for (Patient patient : patients) {
                patientDtoList.add(convertToDto(patient));
            }
            log.info("담당 환자 리스트 조회 성공 - 의료진 ID: {}, 환자 수: {}", medicalStaffId, patientDtoList.size());
            return patientDtoList;
        } catch (IllegalArgumentException e) {
            log.error("담당 환자 리스트 조회 실패 - 입력값 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("담당 환자 리스트 조회 실패 - 데이터 없음: {}", e.getMessage());
            throw new NoSuchElementException("해당 환자를 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("담당 환자 리스트 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("담당 환자 리스트 조회에 실패했습니다.", e);
        }
    }

    /**
     * 환자 ID로 환자 정보를 조회합니다.
     *
     * @param patientId 환자 ID
     * @return 환자 정보
     * @throws IllegalArgumentException 해당 ID의 환자를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public Patient getPatientById(int patientId) {
        log.debug("환자 조회 시도 - 환자 ID: {}", patientId);
        if (patientId <= 0) {
            log.error("유효하지 않은 환자 ID: {}", patientId);
            throw new IllegalArgumentException("환자 ID는 0보다 큰 양수여야 합니다.");
        }
        try {
            return patientRepository.findByPatientId(patientId)
                    .orElseThrow(() -> {
                        log.error("환자를 찾을 수 없습니다 - ID: {}", patientId);
                        return new NoSuchElementException("해당 ID의 환자를 찾을 수 없습니다: " + patientId);
                    });
        } catch (IllegalArgumentException e) {
            log.error("환자 조회 실패 - 입력값 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("환자 조회 실패 - 데이터 없음: {}", e.getMessage());
            throw new NoSuchElementException("해당 환자를 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("환자 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("환자 조회에 실패했습니다.", e);
        }
    }

    @Transactional
    public Patient getPatientByPhone(String phone) {
        log.debug("환자 조회 시도 - 전화번호: {}", phone);
        if (phone == null || phone.trim().isEmpty()) {
            log.error("전화번호가 null 이거나 빈 문자열입니다.");
            throw new IllegalArgumentException("전화번호는 필수 입력값입니다.");
        }
        try {
            return patientRepository.findByPhoneNumber(phone)
                    .orElseThrow(() -> {
                        log.error("환자를 찾을 수 없습니다 - 전화번호: {}", phone);
                        return new NoSuchElementException("해당 전화번호의 환자를 찾을 수 없습니다: " + phone);
                    });
        } catch (IllegalArgumentException e) {
            log.error("환자 조회 실패 - 입력값 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("환자 조회 실패 - 데이터 없음: {}", e.getMessage());
            throw new NoSuchElementException("해당 환자를 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("환자 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("환자 조회에 실패했습니다.", e);
        }
    }

    @Transactional
    public Patient getPatientByEmail(String email) {
        log.debug("환자 조회 시도 - 이메일: {}", email);
        if (email == null || email.trim().isEmpty()) {
            log.error("이메일이 null 이거나 빈 문자열입니다.");
            throw new IllegalArgumentException("이메일은 필수 입력값입니다.");
        }
        try {
            return patientRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.error("환자를 찾을 수 없습니다 - 이메일: {}", email);
                        return new NoSuchElementException("해당 이메일을 가진 환자를 찾을 수 없습니다: " + email);
                    });
        } catch (IllegalArgumentException e) {
            log.error("환자 조회 실패 - 입력값 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("환자 조회 실패 - 데이터 없음: {}", e.getMessage());
            throw new NoSuchElementException("해당 환자를 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("환자 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("환자 조회에 실패했습니다.", e);
        }
    }

    /**
     * 새로운 환자 정보를 생성합니다.
     * 환자 정보 생성 전 사용자 계정이 존재하는지 확인합니다.
     *
     * @param patientDto 생성할 환자 정보
     * @return 생성된 환자 정보
     * @throws IllegalArgumentException 환자 정보가 null 이거나 해당 전화번호의 사용자 계정이 없는 경우
     */
    @Transactional
    public Patient createPatient(PatientDto patientDto) {
        log.debug("환자 생성 시도 - PatientDto: {}", patientDto);
        if (patientDto == null) {
            log.error("환자 정보가 null 입니다.");
            throw new IllegalArgumentException("환자 정보는 필수 입력값입니다.");
        }
        try {
            UserAccount userAccount = userAccountRepository.findByPhoneNumber(patientDto.getPhoneNumber())
                    .orElseThrow(() -> {
                        log.error("사용자 계정을 찾을 수 없습니다 - 전화번호: {}", patientDto.getPhoneNumber());
                        return new NoSuchElementException("해당 전화번호의 사용자 계정을 찾을 수 없습니다: " + patientDto.getPhoneNumber());
                    });
            
            Patient patient = new Patient();
            patient.update(patientDto, userAccount);

            Patient savedPatient = patientRepository.save(patient);
            log.info("환자 생성 성공 - 환자 ID: {}", savedPatient.getPatientId());
            return savedPatient;
        } catch (IllegalArgumentException e) {
            log.error("환자 생성 실패 - 입력값 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("환자 생성 실패 - 데이터 없음: {}", e.getMessage());
            throw new NoSuchElementException("해당 환자를 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("환자 생성 중 예외 발생: {}", e.getMessage(), e);
            throw new RuntimeException("환자 생성에 실패했습니다.", e);
        }
    }

    /**
     * Patient 엔티티를 PatientDto 로 변환합니다.
     *
     * @param patient 변환할 Patient 엔티티
     * @return 변환된 PatientDto 객체
     */
    public PatientDto convertToDto(Patient patient) {
        return patientMapper.toDto(patient);
    }

    /**
     * 환자의 채팅방 존재 여부를 확인합니다.
     *
     * @param patientId 환자 ID
     * @return 채팅방 존재 여부 (true: 존재함, false: 존재하지 않음)
     * @throws IllegalArgumentException 해당 ID의 환자를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public Boolean isChatRoomExist(Integer patientId) {
        log.debug("환자 ID {}의 채팅방 존재 여부 조회 시도", patientId);
        if (patientId == null) {
            log.error("환자 ID가 null입니다.");
            throw new IllegalArgumentException("환자 ID는 필수 입력값입니다.");
        }
        try {
            Patient patient = getPatientById(patientId);
            if (patient == null) {
                log.error("ID {}에 해당하는 환자를 찾을 수 없습니다.", patientId);
                throw new NoSuchElementException("해당 ID의 환자를 찾을 수 없습니다: " + patientId);
            }
            boolean exists = patient.getChatRoomId() != null;
            log.debug("환자 ID {}의 채팅방 존재 여부: {}", patientId, exists);
            return exists;
        } catch (IllegalArgumentException e) {
            log.error("채팅방 존재 여부 조회 실패 - 입력값 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("채팅방 존재 여부 조회 실패 - 데이터 없음: {}", e.getMessage());
            throw new NoSuchElementException("해당 환자를 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("채팅방 존재 여부 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("채팅방 존재 여부 조회에 실패했습니다.", e);
        }
    }

    /**
     * 환자의 전화번호를 업데이트합니다.
     * 전화번호는 한국 휴대전화 번호 형식이어야 합니다.
     *
     * @param patientId 환자 ID
     * @param phoneNumber 새로운 전화번호 (형식: 010-1234-5678 또는 01012345678)
     * @throws IllegalArgumentException 환자를 찾을 수 없거나 전화번호 형식이 잘못된 경우
     */
    @Transactional
    public void updatePhoneNumber(Integer patientId, String phoneNumber) {
        log.debug("전화번호 업데이트 시도 - 환자 ID: {}, 전화번호: {}", patientId, phoneNumber);
        if (phoneNumber == null || !phoneNumber.matches("^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$")) {
            log.error("잘못된 전화번호 형식: {}", phoneNumber);
            throw new IllegalArgumentException("유효하지 않은 전화번호 형식입니다: " + phoneNumber);
        }
        try {
            Patient patient = patientRepository.findByPatientId(patientId)
                    .orElseThrow(() -> {
                        log.error("환자 정보를 찾을 수 없습니다 - 환자 ID: {}", patientId);
                        return new NoSuchElementException("해당 환자 ID의 환자 정보를 찾을 수 없습니다: " + patientId);
                    });
            patient.setPhoneNumber(phoneNumber);
            patientRepository.save(patient);
            log.info("환자 ID {}의 전화번호 업데이트 완료: {}", patientId, phoneNumber);
        } catch (IllegalArgumentException e) {
            log.error("전화번호 업데이트 실패 - 잘못된 입력값: {}", e.getMessage());
            throw e;
        } catch (NoSuchElementException e) {
            log.error("전화번호 업데이트 실패 - 데이터 없음: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("전화번호 업데이트 중 예외 발생: {}", e.getMessage(), e);
            throw new RuntimeException("전화번호 업데이트에 실패했습니다.", e);
        }
    }
}