package com.example.carebridge.service;

import com.example.carebridge.entity.Patient;
import com.example.carebridge.dto.PatientDto;
import com.example.carebridge.entity.UserAccount;
import com.example.carebridge.repository.GuardianRepository;
import com.example.carebridge.repository.MedicalStaffRepository;
import com.example.carebridge.repository.PatientRepository;
import com.example.carebridge.repository.UserAccountRepository;
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
    private final MedicalStaffRepository medicalStaffRepository;
    private final UserAccountRepository userAccountRepository;

    // 생성자 주입을 통해 PatientRepository 와 GuardianRepository 를 초기화합니다.
    public PatientService(PatientRepository patientRepository, GuardianRepository guardianRepository, MedicalStaffRepository medicalStaffRepository, UserAccountRepository userAccountRepository) {
        this.patientRepository = patientRepository;
        this.guardianRepository = guardianRepository;
        this.medicalStaffRepository = medicalStaffRepository;
        this.userAccountRepository = userAccountRepository;
    }


    /**
     * 모든 담당 환자 정보를 조회하여 PatientDto 리스트로 반환합니다.
     *
     * @return 환자 정보가 담긴 PatientDto 리스트
     */
    public List<PatientDto> getPatientList(Integer MedicalStaffId) {
        List<PatientDto> patientDtoList = new ArrayList<>();
        String department = medicalStaffRepository.findByMedicalStaffId(MedicalStaffId).getDepartment();
        Integer hospitalId = medicalStaffRepository.findByMedicalStaffId(MedicalStaffId).getHospitalId();
        List<Patient> patients = patientRepository.findByHospitalIdAndDepartment(hospitalId, department);
        for (Patient patient : patients) {
            patientDtoList.add(convertToDto(patient));
        }
        return patientDtoList;
    }

    /**
     * 환자의 ID로 환자 정보를 조회하여 Patient 로 반환합니다.
     *
     * @param patientId 환자의 ID
     * @return 환자 정보가 담긴 PatientDto 객체
     */
    public Patient getPatientById(int patientId) {
        return patientRepository.findByPatientId(patientId);
    }

    public Patient createPatient(PatientDto patient) {
        Patient patient1 = new Patient();
        UserAccount userAccount = userAccountRepository.findByPhoneNumber(patient.getPhoneNumber());
        patient1.update(patient, userAccount);
        return patientRepository.save(patient1);
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
        patientDto.setHospitalId(patient.getHospitalId());
        patientDto.setChatRoomId(patient.getChatRoomId());
        patientDto.setDepartment(patient.getDepartment());
        patientDto.setHospitalizationDate(patient.getHospitalizationDate());
        return patientDto;
    }

    /**
     * 환자의 채팅방 존재 여부를 확인합니다.
     *
     * @param patientId 환자의 ID
     * @return 채팅방 존재 여부
     */
    public Boolean isChatRoomExist(Integer patientId) {
        return patientRepository.findByPatientId(patientId).getChatRoomId() != null;
    }
}