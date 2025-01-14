package com.example.carebridge.service;

import com.example.carebridge.entity.Patient;
import com.example.carebridge.dto.PatientDto;
import com.example.carebridge.repository.GuardianRepository;
import com.example.carebridge.repository.PatientRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Getter
@Setter
public class PatientService {
    private final PatientRepository patientRepository;
    private final GuardianRepository guardianRepository;

    // 생성자 주입을 통해 PatientRepository 와 GuardianRepository 를 초기화합니다.
    public PatientService(PatientRepository patientRepository, GuardianRepository guardianRepository) {
        this.patientRepository = patientRepository;
        this.guardianRepository = guardianRepository;
    }

    /**
     * 모든 환자 정보를 조회하여 PatientDto 리스트로 반환합니다.
     *
     * @return 환자 정보가 담긴 PatientDto 리스트
     */
    public List<PatientDto> getPatientList() {
        List<PatientDto> patientDtoList = new ArrayList<>();
        List<Patient> patients = patientRepository.findAll();
        for (Patient patient : patients) {
            patientDtoList.add(convertToDto(patient));
        }
        return patientDtoList;
    }

    /**
     * 환자의 ID로 환자 정보를 조회하여 PatientDto 로 반환합니다.
     *
     * @param patientId 환자의 ID
     * @return 환자 정보가 담긴 PatientDto 객체
     */
    public PatientDto getPatientById(int patientId) {
        Patient patient = patientRepository.findByPatientId(patientId);
        return convertToDto(patient);
    }

    /**
     * Patient 엔티티를 PatientDto 로 변환합니다.
     *
     * @param patient 변환할 Patient 엔티티
     * @return 변환된 PatientDto 객체
     */
    private PatientDto convertToDto(Patient patient) {
        PatientDto patientDto = new PatientDto();
        patientDto.setPatientId(patient.getPatientId());
        patientDto.setName(patient.getName());
        patientDto.setPhoneNumber(patient.getPhoneNumber());
        patientDto.setBirthDate(patient.getBirthDate());
        patientDto.setGender(patient.getGender());
        patientDto.setGuardianContact(patient.getGuardianContact());
        patientDto.setHospitalLocation(patient.getHospitalLocation());
        patientDto.setChatRoomId(patient.getChatRoomId());
        patientDto.setDepartment(patient.getDepartment());
        return patientDto;
    }
}