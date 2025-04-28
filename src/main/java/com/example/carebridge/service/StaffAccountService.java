package com.example.carebridge.service;

import com.example.carebridge.dto.StaffAccountDto;
import com.example.carebridge.entity.StaffAccount;
import com.example.carebridge.repository.StaffAccountRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Service
@Getter
@Setter
public class StaffAccountService {

    private StaffAccountRepository staffAccountRepository;

    public StaffAccountService(StaffAccountRepository staffAccountRepository) {
        this.staffAccountRepository = staffAccountRepository;
    }

    @Transactional
    public Boolean verifyStaffAccount(StaffAccountDto staffAccountDto) {
        log.debug("스태프 계정 검증 시도 - StaffAccountDto: {}", staffAccountDto);
        if (staffAccountDto == null || staffAccountDto.getUserId() == null || staffAccountDto.getPassword() == null) {
            log.error("스태프 계정 정보가 누락되었습니다: {}", staffAccountDto);
            throw new IllegalArgumentException("스태프 계정 정보는 필수 입력값입니다.");
        }
        try {
            StaffAccount staffAccount = staffAccountRepository.getStaffAccountByUserId(staffAccountDto.getUserId())
                    .orElseThrow(() -> {
                        log.error("스태프 계정을 찾을 수 없습니다 - 사용자 ID: {}", staffAccountDto.getUserId());
                        return new NoSuchElementException("해당 사용자 ID의 스태프 계정을 찾을 수 없습니다: " + staffAccountDto.getUserId());
                    });
            boolean isVerified = staffAccountDto.getUserId().equals(staffAccount.getUserId()) &&
                    staffAccountDto.getPassword().equals(staffAccount.getPassword());
            log.info("스태프 계정 검증 결과 - 사용자 ID: {}, 검증 성공 여부: {}", staffAccountDto.getUserId(), isVerified);
            return isVerified;
        } catch (IllegalArgumentException e) {
            log.error("스태프 계정 검증 실패 - 잘못된 입력값: {}", e.getMessage());
            throw e;
        } catch (NoSuchElementException e) {
            log.error("스태프 계정 검증 실패 - 데이터 없음: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("스태프 계정 검증 중 예외 발생: {}", e.getMessage(), e);
            throw new RuntimeException("스태프 계정 검증에 실패했습니다.", e);
        }
    }

    public StaffAccountDto convertStaffAccountToStaffAccountDto(StaffAccount staffAccount) {
        StaffAccountDto staffAccountDto = new StaffAccountDto();
        staffAccountDto.setUserId(staffAccount.getUserId());
        staffAccountDto.setPassword(staffAccount.getPassword());
        return staffAccountDto;
    }

    @Transactional
    public String findPassword(String id) {
        log.debug("비밀번호 찾기 시도 - 사용자 ID: {}", id);
        if (id == null || id.trim().isEmpty()) {
            log.error("아이디가 null 이거나 비어 있습니다: {}", id);
            throw new IllegalArgumentException("아이디는 필수 입력값입니다.");
        }
        try {
            StaffAccount staffAccount = staffAccountRepository.getStaffAccountByUserId(id)
                    .orElseThrow(() -> {
                        log.error("스태프 계정을 찾을 수 없습니다 - 사용자 ID: {}", id);
                        return new NoSuchElementException("해당 사용자 ID의 스태프 계정을 찾을 수 없습니다: " + id);
                    });
            String password = staffAccount.getPassword();
            log.info("비밀번호 찾기 성공 - 사용자 ID: {}", id);
            return password;
        } catch (IllegalArgumentException e) {
            log.error("비밀번호 찾기 실패 - 잘못된 입력값: {}", e.getMessage());
            throw e;
        } catch (NoSuchElementException e) {
            log.error("비밀번호 찾기 실패 - 데이터 없음: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("비밀번호 찾기 중 예외 발생: {}", e.getMessage(), e);
            throw new RuntimeException("비밀번호 찾기에 실패했습니다.", e);
        }
    }

    @Transactional
    public void resetPassword(StaffAccountDto staffAccountDto, String newPassword) {
        log.debug("비밀번호 초기화 시도 - StaffAccountDto: {}, 새로운 비밀번호: {}", staffAccountDto, newPassword);
        if (staffAccountDto == null || staffAccountDto.getUserId() == null || staffAccountDto.getPassword() == null ||
                newPassword == null || newPassword.trim().isEmpty()) {
            log.error("비밀번호 초기화 입력값이 누락되었습니다 - StaffAccountDto: {}, 새로운 비밀번호: {}", staffAccountDto, newPassword);
            throw new IllegalArgumentException("비밀번호 초기화에 필요한 입력값이 부족합니다.");
        }
        try {
            StaffAccount staffAccount = staffAccountRepository.getStaffAccountByUserId(staffAccountDto.getUserId())
                    .orElseThrow(() -> {
                        log.error("스태프 계정을 찾을 수 없습니다 - 사용자 ID: {}", staffAccountDto.getUserId());
                        return new NoSuchElementException("해당 사용자 ID의 스태프 계정을 찾을 수 없습니다: " + staffAccountDto.getUserId());
                    });
            if (!staffAccountDto.getPassword().equals(staffAccount.getPassword())) {
                log.error("기존 비밀번호가 일치하지 않습니다 - 사용자 ID: {}", staffAccountDto.getUserId());
                throw new IllegalArgumentException("기존 비밀번호와 일치하지 않습니다.");
            }
            if (newPassword.equals(staffAccount.getPassword())) {
                log.error("새로운 비밀번호가 기존 비밀번호와 동일합니다 - 사용자 ID: {}", staffAccountDto.getUserId());
                throw new IllegalArgumentException("새로운 비밀번호가 기존의 비밀번호와 일치합니다.");
            }
            staffAccount.setPassword(newPassword);
            staffAccountRepository.save(staffAccount);
            log.info("비밀번호 초기화 성공 - 사용자 ID: {}", staffAccountDto.getUserId());
        } catch (IllegalArgumentException e) {
            log.error("비밀번호 초기화 실패 - 잘못된 입력값: {}", e.getMessage());
            throw e;
        } catch (NoSuchElementException e) {
            log.error("비밀번호 초기화 실패 - 데이터 없음: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("비밀번호 초기화 중 예외 발생: {}", e.getMessage(), e);
            throw new RuntimeException("비밀번호 초기화에 실패했습니다.", e);
        }
    }

    @Transactional
    public StaffAccount findStaffAccountByUserId(String userId) {
        log.debug("사용자 ID로 스태프 계정 조회 시도 - 사용자 ID: {}", userId);
        if (userId == null || userId.trim().isEmpty()) {
            log.error("사용자 ID가 누락되었습니다 - 입력값: {}", userId);
            throw new IllegalArgumentException("사용자 ID는 필수 입력값입니다.");
        }
        try {
            return staffAccountRepository.getStaffAccountByUserId(userId)
                    .orElseThrow(() -> {
                        log.error("스태프 계정을 찾을 수 없습니다 - 사용자 ID: {}", userId);
                        return new NoSuchElementException("해당 사용자 ID의 스태프 계정을 찾을 수 없습니다: " + userId);
                    });
        } catch (IllegalArgumentException e) {
            log.error("스태프 계정 조회 실패 - 잘못된 입력값: {}", e.getMessage());
            throw e;
        } catch (NoSuchElementException e) {
            log.error("스태프 계정 조회 실패 - 데이터 없음: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("스태프 계정 조회 중 예외 발생: {}", e.getMessage(), e);
            throw new RuntimeException("스태프 계정 조회에 실패했습니다.", e);
        }
    }
}
