package com.example.carebridge.service;

import com.example.carebridge.dto.GuardianDto;
import com.example.carebridge.entity.Guardian;
import com.example.carebridge.repository.GuardianRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GuardianService {
    private final GuardianRepository guardianRepository;

    public GuardianService(GuardianRepository guardianRepository) {
        this.guardianRepository = guardianRepository;
    }

    /**
     * 보호자 정보를 조회합니다.
     *
     * @param guardianId 보호자 ID
     * @return 보호자 정보 DTO
     */
    public GuardianDto getGuardianInfo(String guardianId) {
        Guardian guardian = guardianRepository.findByGuardianId(guardianId);
        GuardianDto guardianDto = new GuardianDto();
        guardianDto.setGuardianId(guardian.getGuardianId());
        guardianDto.setPatientId(guardian.getPatientId());
        guardianDto.setName(guardian.getName());
        guardianDto.setPhoneNumber(guardian.getPhoneNumber());
        return guardianDto;
    }

    /**
     * 새로운 보호자를 추가합니다.
     *
     * @param patientId 환자 ID
     * @param name 보호자 이름
     * @param phoneNumber 보호자 전화번호
     */
    public void addGuardian(int patientId, String name, String phoneNumber) {
        Guardian guardian = new Guardian();
        guardian.setGuardianId(UUID.randomUUID().toString());
        guardian.setPatientId(patientId);
        guardian.setName(name);
        guardian.setPhoneNumber(phoneNumber);
        guardianRepository.save(guardian);
    }

    /**
     * 보호자를 삭제합니다.
     *
     * @param guardianId 보호자 ID
     */
    public void deleteGuardian(String guardianId) {
        guardianRepository.deleteByGuardianId(guardianId);
    }
}