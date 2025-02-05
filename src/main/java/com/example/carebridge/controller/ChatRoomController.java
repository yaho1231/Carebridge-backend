package com.example.carebridge.controller;

import com.example.carebridge.dto.ChatRoomDto;
import com.example.carebridge.service.CallBellService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 채팅방 관리 컨트롤러
 * 채팅방의 생성, 조회 등 채팅방 관련 기능을 제공하는 REST API 컨트롤러입니다.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/chat")
@Tag(name = "Chat Room Controller", description = "채팅방 관리 API")
public class ChatRoomController {

    private final CallBellService callBellService;

    /**
     * 새로운 채팅방을 생성합니다.
     * 환자 ID와 의료진 소속 분과를 기반으로 새로운 채팅방을 생성합니다.
     *
     * @param patientId 환자의 ID (필수)
     * @param department 의료진 소속 분과 (필수)
     * @return ResponseEntity<ChatRoomDto> 생성된 채팅방 정보와 HTTP 상태
     * @throws IllegalArgumentException 환자 ID가 존재하지 않거나 부서 정보가 잘못된 경우
     */
    @Operation(summary = "채팅방 생성", description = "환자와 의료진 간의 새로운 채팅방을 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "채팅방 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (환자 ID 또는 부서 정보 오류)"),
        @ApiResponse(responseCode = "404", description = "환자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/room")
    public ResponseEntity<ChatRoomDto> createRoom(
            @Parameter(description = "환자 ID", required = true) 
            @RequestParam Integer patientId,
            @Parameter(description = "의료진 소속 분과", required = true) 
            @RequestParam String department) {
        try {
            log.debug("채팅방 생성 요청 - 환자 ID: {}, 부서: {}", patientId, department);
            ChatRoomDto chatRoom = callBellService.createChatRoom(patientId, department);
            log.info("채팅방 생성 성공 - 방 ID: {}", chatRoom.getRoomId());
            return new ResponseEntity<>(chatRoom, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.error("채팅방 생성 실패 - 잘못된 요청: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("채팅방 생성 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 특정 환자의 채팅방 정보를 조회합니다.
     * 환자 ID를 기반으로 해당 환자의 채팅방 정보를 반환합니다.
     *
     * @param patientId 환자의 ID (필수)
     * @return ResponseEntity<ChatRoomDto> 조회된 채팅방 정보와 HTTP 상태
     * @throws IllegalArgumentException 환자 ID가 존재하지 않는 경우
     */
    @Operation(summary = "채팅방 조회", description = "특정 환자의 채팅방 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "채팅방 조회 성공"),
        @ApiResponse(responseCode = "404", description = "채팅방 또는 환자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/room/{patientId}")
    public ResponseEntity<ChatRoomDto> roomInfo(
            @Parameter(description = "환자 ID", required = true)
            @PathVariable Integer patientId) {
        try {
            log.debug("채팅방 조회 요청 - 환자 ID: {}", patientId);
            ChatRoomDto chatRoom = callBellService.findChatRoomByPatientId(patientId);
            
            if (chatRoom == null) {
                log.info("채팅방을 찾을 수 없음 - 환자 ID: {}", patientId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            log.debug("채팅방 조회 성공 - 방 ID: {}", chatRoom.getRoomId());
            return new ResponseEntity<>(chatRoom, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("채팅방 조회 실패 - 잘못된 요청: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("채팅방 조회 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}