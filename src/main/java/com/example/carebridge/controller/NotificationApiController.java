package com.example.carebridge.controller;

import com.example.carebridge.dto.NotificationRequestDto;
import com.example.carebridge.dto.NotificationTokenDto;
import com.example.carebridge.service.FcmService;
import com.example.carebridge.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/api/notification")
@Tag(name = "알림 API", description = "FCM 알림 관련 API")
public class NotificationApiController {

    private final NotificationService notificationService;
    private final FcmService fcmService;

    public NotificationApiController(NotificationService notificationService, FcmService fcmService) {
        this.notificationService = notificationService;
        this.fcmService = fcmService;
    }

    /**
     * FCM 토큰 등록 API
     * 사용자의 디바이스 토큰을 서버에 등록합니다.
     *
     * @param notificationTokenDto FCM 토큰 등록 요청 DTO
     *                            - userId: 사용자 고유 식별자
     *                            - token: FCM 디바이스 토큰
     * @return ResponseEntity<Void> 
     *         - 200: 토큰 등록 성공
     *         - 400: 잘못된 요청 (유효하지 않은 사용자 ID 또는 토큰)
     *         - 500: 서버 내부 오류
     */
    @Operation(summary = "FCM 토큰 등록", description = "사용자의 FCM 토큰을 서버에 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "토큰 등록 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody NotificationTokenDto notificationTokenDto) {
        try {
            notificationService.register(notificationTokenDto.getUserId(), notificationTokenDto.getToken());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("토큰 등록 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("토큰 등록 중 서버 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * FCM 토큰 삭제 API
     * 로그아웃 시 사용자의 디바이스 토큰을 서버에서 삭제합니다.
     *
     * @param notificationTokenDto FCM 토큰 삭제 요청 DTO
     *                            - userId: 토큰을 삭제할 사용자의 고유 식별자
     *                            - token: 삭제할 FCM 디바이스 토큰 (선택적)
     * @return ResponseEntity<Void>
     *         - 200: 토큰 삭제 성공
     *         - 400: 잘못된 요청 (존재하지 않는 사용자 ID)
     *         - 500: 서버 내부 오류
     */
    @Operation(summary = "FCM 토큰 삭제", description = "로그아웃 시 사용자의 FCM 토큰을 서버에서 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "토큰 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody NotificationTokenDto notificationTokenDto) {
        try {
            notificationService.deleteToken(notificationTokenDto.getUserId());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("토큰 삭제 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("토큰 삭제 중 서버 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * FCM 알림 전송 API
     * 지정된 사용자들에게 FCM 알림을 전송합니다.
     *
     * @param notificationRequestDtoList FCM 알림 전송 요청 DTO 리스트
     *                                  - userId: 알림을 받을 사용자의 고유 식별자
     *                                  - title: 알림 제목
     *                                  - body: 알림 내용
     *                                  - data: 추가 데이터 (선택적)
     * @return ResponseEntity<Void>
     *         - 200: 알림 전송 성공
     *         - 400: 잘못된 요청 (유효하지 않은 사용자 ID 또는 알림 내용)
     *         - 500: 서버 내부 오류 (FCM 서버 통신 실패 등)
     */
    @Operation(summary = "FCM 알림 전송", description = "지정된 사용자들에게 FCM 알림을 전송합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "알림 전송 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/send")
    public ResponseEntity<Void> send(@RequestBody List<NotificationRequestDto> notificationRequestDtoList) {
        try {
            fcmService.send(notificationRequestDtoList);
            return ResponseEntity.ok().build();
        } catch (InterruptedException | ExecutionException e) {
            log.error("알림 전송 실패: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        } catch (IllegalArgumentException e) {
            log.error("잘못된 알림 요청: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("알림 전송 중 서버 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}