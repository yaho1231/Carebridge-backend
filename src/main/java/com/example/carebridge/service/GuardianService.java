package com.example.carebridge.service;

import com.example.carebridge.dto.GuardianDto;
import com.example.carebridge.entity.Guardian;
import com.example.carebridge.repository.GuardianRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GuardianService {
    private final GuardianRepository guardianRepository;

    // GuardianRepository 를 주입받는 생성자
    public GuardianService(GuardianRepository guardianRepository) {
        this.guardianRepository = guardianRepository;
    }

    /**
     * 보호자 정보를 조회합니다.
     *
     * @param phone_number 보호자 전화번호
     * @return 보호자 정보 DTO
     */
    public GuardianDto getGuardianInfo(String phone_number) {
        Guardian guardian = guardianRepository.findByPhoneNumber(phone_number); // 전화번호로 보호자 정보 조회
        GuardianDto guardianDto = new GuardianDto(); // 보호자 정보 DTO 생성
        guardianDto.setGuardianId(guardian.getGuardianId()); // 보호자 ID 설정
        guardianDto.setPatientId(guardian.getPatientId()); // 환자 ID 설정
        guardianDto.setName(guardian.getName()); // 보호자 이름 설정
        guardianDto.setPhoneNumber(guardian.getPhoneNumber()); // 보호자 전화번호 설정
        return guardianDto; // 보호자 정보 DTO 반환
    }

    /**
     * 특정 환자의 모든 보호자 정보를 조회합니다.
     *
     * @param patientId 환자 ID
     * @return 보호자 정보 리스트
     */
    public List<GuardianDto> getGuardianList(Integer patientId) {
        List<Guardian> guardianList = guardianRepository.findAllByPatientId(patientId); // 환자 ID로 보호자 리스트 조회
        List<GuardianDto> guardianDtoList = new ArrayList<>(); // 보호자 정보 DTO 리스트 생성
        for (Guardian guardian : guardianList) {
            GuardianDto guardianDto = new GuardianDto(); // 보호자 정보 DTO 생성
            guardianDto.setGuardianId(guardian.getGuardianId()); // 보호자 ID 설정
            guardianDto.setPatientId(guardian.getPatientId()); // 환자 ID 설정
            guardianDto.setName(guardian.getName()); // 보호자 이름 설정
            guardianDto.setPhoneNumber(guardian.getPhoneNumber()); // 보호자 전화번호 설정
            guardianDtoList.add(guardianDto); // 보호자 정보 DTO 리스트에 추가
        }
        return guardianDtoList; // 보호자 정보 DTO 리스트 반환
    }

    /**
     * 새로운 보호자를 추가합니다.
     *
     * @param patientId 환자 ID
     * @param name 보호자 이름
     * @param phoneNumber 보호자 전화번호
     */
    public void addGuardian(int patientId, String name, String phoneNumber) {
        Guardian guardian = new Guardian(); // 새로운 보호자 엔티티 생성
        guardian.setGuardianId(UUID.randomUUID().toString()); // 보호자 ID 설정
        guardian.setPatientId(patientId); // 환자 ID 설정
        guardian.setName(name); // 보호자 이름 설정
        guardian.setPhoneNumber(phoneNumber); // 보호자 전화번호 설정
        guardianRepository.save(guardian); // 보호자 정보 저장
    }

    /**
     * 보호자를 삭제합니다.
     *
     * @param phone_number 보호자 전화번호
     */
    public void deleteGuardian(String phone_number) {
        guardianRepository.deleteByPhoneNumber(phone_number); // 전화번호로 보호자 정보 삭제
    }
}