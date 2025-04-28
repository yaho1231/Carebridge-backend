package com.example.carebridge.service;

import com.example.carebridge.entity.UserAccount;
import com.example.carebridge.repository.UserAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/**
 * 알림 서비스
 * 사용자의 FCM 토큰을 관리하고 알림 기능을 제공하는 서비스 클래스입니다.
 * 
 * 주요 기능:
 * 1. FCM 토큰 등록
 * 2. FCM 토큰 삭제
 * 
 * 사용 예시:
 * Long userId = 사용자ID;
 * String fcmToken = "Firebase에서_발급받은_토큰";
 * notificationService.register(userId, fcmToken);
 */
@Slf4j
@Service
public class NotificationService {

    /**
     * 사용자 계정 정보에 접근하기 위한 레포지토리
     */
    private final UserAccountRepository userAccountRepository;

    /**
     * 생성자를 통한 의존성 주입
     * 
     * @param userAccountRepository 사용자 계정 레포지토리
     */
    public NotificationService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    /**
     * 사용자의 FCM 토큰을 등록하는 메소드
     * 
     * @param userId 토큰을 등록할 사용자의 ID
     * @param token Firebase에서 발급받은 FCM 토큰
     * @throws RuntimeException 사용자를 찾을 수 없는 경우 발생
     * 
     * 처리 과정:
     * 1. 사용자 ID로 계정 정보 조회
     * 2. FCM 토큰 업데이트
     * 3. 변경된 정보 저장
     */
    @Transactional
    public void register(final Integer userId, final String token) {
        log.debug("FCM 토큰 등록 시도 - 사용자 ID: {}", userId);
        if (userId == null) {
            log.error("사용자 ID가 누락되었습니다 - 입력값: {}", userId);
            throw new IllegalArgumentException("사용자 ID는 필수 입력값입니다.");
        }
        if (token == null || token.trim().isEmpty()) {
            log.error("FCM 토큰이 누락되었습니다 - 입력값: {}", token);
            throw new IllegalArgumentException("FCM 토큰은 필수 입력값입니다.");
        }
        try {
            UserAccount userAccount = userAccountRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("사용자 계정을 찾을 수 없습니다 - 사용자 ID: {}", userId);
                        return new NoSuchElementException("해당 ID의 사용자 계정을 찾을 수 없습니다: " + userId);
                    });
            userAccount.setFcmToken(token);
            userAccountRepository.save(userAccount);
            log.info("FCM 토큰 등록 성공 - 사용자 ID: {}", userId);
        } catch (IllegalArgumentException e) {
            log.error("FCM 토큰 등록 실패 - 잘못된 입력값: {}", e.getMessage());
            throw e;
        } catch (NoSuchElementException e) {
            log.error("FCM 토큰 등록 실패 - 데이터 없음: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("FCM 토큰 등록 중 예외 발생: {}", e.getMessage(), e);
            throw new RuntimeException("FCM 토큰 등록에 실패했습니다.", e);
        }
    }

    /**
     * 사용자의 FCM 토큰을 삭제하는 메소드
     * 로그아웃 또는 알림 서비스 해제 시 호출됩니다.
     * 
     * @param userId 토큰을 삭제할 사용자의 ID
     * @throws RuntimeException 사용자를 찾을 수 없는 경우 발생
     * 
     * 처리 과정:
     * 1. 사용자 ID로 계정 정보 조회
     * 2. FCM 토큰을 null로 설정하여 제거
     * 3. 변경된 정보 저장
     * 
     * 주의사항:
     * - 토큰 삭제 후에는 해당 사용자에게 푸시 알림을 보낼 수 없습니다.
     * - 알림 서비스 재사용 시 새로운 토큰을 등록해야 합니다.
     */
    @Transactional
    public void deleteToken(final Integer userId) {
        log.debug("FCM 토큰 삭제 시도 - 사용자 ID: {}", userId);
        try {
            if (userId == null) {
                log.error("사용자 ID가 null 입니다.");
                throw new IllegalArgumentException("사용자 ID는 필수 입력값입니다.");
            }
            UserAccount userAccount = userAccountRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("사용자를 찾을 수 없습니다 - 사용자 ID: {}", userId);
                        return new NoSuchElementException("해당 ID의 사용자를 찾을 수 없습니다: " + userId);
                    });
            userAccount.setFcmToken(null);
            userAccountRepository.save(userAccount);
            log.info("FCM 토큰 삭제 성공 - 사용자 ID: {}", userId);
        } catch (IllegalArgumentException e) {
            log.error("FCM 토큰 삭제 실패 - 잘못된 입력값: {}", e.getMessage());
            throw e;
        } catch (NoSuchElementException e) {
            log.error("FCM 토큰 삭제 실패 - 데이터 없음: {}", e.getMessage());
            throw e;
        }  catch (Exception e) {
            log.error("FCM 토큰 삭제 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("FCM 토큰 삭제에 실패했습니다.", e);
        }
    }
}