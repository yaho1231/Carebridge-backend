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
        if (medicalStaffId == null) {
            log.error("의료진 ID가 null 입니다.");
            throw new IllegalArgumentException("의료진 ID는 null 일 수 없습니다.");
        }

        var medicalStaff = medicalStaffRepository.findByMedicalStaffId(medicalStaffId)
                .orElseThrow(() -> {
                    log.error("의료진을 찾을 수 없습니다. ID: {}", medicalStaffId);
                    return new IllegalArgumentException("해당 ID의 의료진을 찾을 수 없습니다: " + medicalStaffId);
                });

        String department = medicalStaff.getDepartment();
        Integer hospitalId = medicalStaff.getHospitalId();
        
        List<Patient> patients = patientRepository.findByHospitalIdAndDepartment(hospitalId, department);
        List<PatientDto> patientDtoList = new ArrayList<>();
        
        for (Patient patient : patients) {
            patientDtoList.add(convertToDto(patient));
        }
        
        log.debug("의료진 ID {}의 담당 환자 {}명 조회 완료", medicalStaffId, patientDtoList.size());
        return patientDtoList;
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
        return patientRepository.findByPatientId(patientId)
                .orElseThrow(() -> {
                    log.error("환자를 찾을 수 없습니다. ID: {}", patientId);
                    return new IllegalArgumentException("해당 ID의 환자를 찾을 수 없습니다: " + patientId);
                });
    }

    @Transactional
    public Patient getPatientByPhone(String phone) {
        return patientRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> {
                    log.error("환자를 찾을 수 없습니다. Phone Number: {}", phone);
                    return new IllegalArgumentException("해당 전화번호의 환자를 찾을 수 없습니다: " + phone);
                });
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
        if (patientDto == null) {
            log.error("환자 정보가 null 입니다.");
            throw new IllegalArgumentException("환자 정보는 null 일 수 없습니다.");
        }

        UserAccount userAccount = userAccountRepository.findByPhoneNumber(patientDto.getPhoneNumber())
                .orElseThrow(() -> {
                    log.error("사용자 계정을 찾을 수 없습니다. 전화번호: {}", patientDto.getPhoneNumber());
                    return new IllegalArgumentException("해당 전화번호의 사용자 계정을 찾을 수 없습니다: " + patientDto.getPhoneNumber());
                });

        Patient patient = new Patient();
        patient.update(patientDto, userAccount);
        Patient savedPatient = patientRepository.save(patient);
        log.info("새로운 환자 정보 생성 완료. ID: {}", savedPatient.getPatientId());
        return savedPatient;
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
        Patient patient = getPatientById(patientId);
        boolean exists = patient.getChatRoomId() != null;
        log.debug("환자 ID {}의 채팅방 존재 여부: {}", patientId, exists);
        return exists;
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
        if (phoneNumber == null || !phoneNumber.matches("^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$")) {
            log.error("잘못된 전화번호 형식: {}", phoneNumber);
            throw new IllegalArgumentException("유효하지 않은 전화번호 형식입니다: " + phoneNumber);
        }

        Patient patient = getPatientById(patientId);
        patient.setPhoneNumber(phoneNumber);
        patientRepository.save(patient);
        log.info("환자 ID {}의 전화번호 업데이트 완료: {}", patientId, phoneNumber);
    }
}