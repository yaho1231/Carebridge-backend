package com.example.carebridge.controller;

import com.example.carebridge.dto.NotificationRequestDto;
import com.example.carebridge.dto.NotificationTokenDto;
import com.example.carebridge.service.FcmService;
import com.example.carebridge.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Tag(name = "알림 API", description = "FCM(Firebase Cloud Messaging) 알림 관련 API - 사용자 디바이스 토큰 관리 및 푸시 알림 전송")
public class NotificationApiController {

    private final NotificationService notificationService;
    private final FcmService fcmService;

    public NotificationApiController(NotificationService notificationService, FcmService fcmService) {
        this.notificationService = notificationService;
        this.fcmService = fcmService;
    }

    @Operation(
        summary = "FCM 토큰 등록",
        description = "사용자의 FCM 토큰을 서버에 등록합니다.\n\n" +
            "### 사용 시나리오\n" +
            "- 사용자가 앱에 처음 로그인할 때\n" +
            "- 디바이스 토큰이 갱신되었을 때\n\n" +
            "### 주의사항\n" +
            "- 토큰은 디바이스와 앱 설치마다 고유하며, 재설치 시 변경될 수 있습니다.\n" +
            "- 기존에 등록된 토큰이 있다면 새로운 토큰으로 업데이트됩니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "토큰 등록 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"message\": \"토큰이 성공적으로 등록되었습니다.\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"error\": \"유효하지 않은 사용자 ID 또는 토큰입니다.\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 오류",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"error\": \"서버 내부 오류가 발생했습니다.\"}"
                )
            )
        )
    })
    @PostMapping("/register")
    public ResponseEntity<Void> register(
        @Parameter(
            description = "FCM 토큰 등록 정보",
            required = true,
            schema = @Schema(implementation = NotificationTokenDto.class),
            examples = @ExampleObject(
                value = """
                {
                    "userId": 123,
                    "token": "firebase_fcm_token_example..."
                }
                """
            )
        )
        @RequestBody NotificationTokenDto notificationTokenDto
    ) {
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

    @Operation(
        summary = "FCM 토큰 삭제",
        description = "사용자의 FCM 토큰을 서버에서 삭제합니다.\n\n" +
            "### 사용 시나리오\n" +
            "- 사용자가 앱에서 로그아웃할 때\n" +
            "- 알림 수신을 비활성화할 때\n" +
            "- 앱을 제거할 때\n\n" +
            "### 주의사항\n" +
            "- 토큰이 삭제되면 해당 디바이스로 더 이상 알림을 받을 수 없습니다.\n" +
            "- 재로그인 시 새로운 토큰을 등록해야 합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "토큰 삭제 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"message\": \"토큰이 성공적으로 삭제되었습니다.\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"error\": \"존재하지 않는 사용자 ID입니다.\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 오류",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"error\": \"서버 내부 오류가 발생했습니다.\"}"
                )
            )
        )
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        @Parameter(
            description = "FCM 토큰 삭제 정보",
            required = true,
            schema = @Schema(implementation = NotificationTokenDto.class),
            examples = @ExampleObject(
                value = """
                {
                    "userId": 123,
                    "token": "firebase_fcm_token_example..."
                }
                """
            )
        )
        @RequestBody NotificationTokenDto notificationTokenDto
    ) {
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

    @Operation(
        summary = "FCM 알림 전송",
        description = "지정된 사용자들에게 FCM 알림을 전송합니다.\n\n" +
            "### 사용 시나리오\n" +
            "- 특정 이벤트 발생 시 알림 전송\n" +
            "- 긴급 알림 전송\n" +
            "- 공지사항 전파\n\n" +
            "### 주의사항\n" +
            "- 한 번에 최대 500개의 알림까지 전송 가능\n" +
            "- 알림 제목과 내용은 각각 최대 1000자까지 지원\n" +
            "- 수신자의 FCM 토큰이 유효해야 전송 가능\n" +
            "- 네트워크 상태에 따라 전송 지연이 발생할 수 있음"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "알림 전송 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"message\": \"알림이 성공적으로 전송되었습니다.\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"error\": \"유효하지 않은 알림 내용이 포함되어 있습니다.\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 오류",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"error\": \"FCM 서버와의 통신 중 오류가 발생했습니다.\"}"
                )
            )
        )
    })
    @PostMapping("/send")
    public ResponseEntity<Void> send(
        @Parameter(
            description = "FCM 알림 전송 요청 목록",
            required = true,
            schema = @Schema(implementation = NotificationRequestDto.class),
            examples = @ExampleObject(
                value = """
                [{
                    "token": "firebase_fcm_token_example...",
                    "title": "긴급 알림",
                    "body": "새로운 메시지가 도착했습니다.",
                    "ttl": "3600"
                }]
                """
            )
        )
        @RequestBody List<NotificationRequestDto> notificationRequestDtoList
    ) {
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