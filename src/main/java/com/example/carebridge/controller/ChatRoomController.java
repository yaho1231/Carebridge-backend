package com.example.carebridge.controller;

import com.example.carebridge.dto.ChatRoomDto;
import com.example.carebridge.service.CallBellService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/chat")
public class ChatRoomController {

    private final CallBellService callBellService;

    /**
     * 새로운 채팅방을 생성합니다.
     *
     * @param patientId 환자의 ID
     * @param department 의료진 소속 분과
     * @return 생성된 채팅방 객체
     */
    @PostMapping("/post/room")
    @ResponseBody
    public ChatRoomDto createRoom(@RequestParam Integer patientId, @RequestParam String department) {
        return callBellService.createChatRoom(patientId, department);
    }

    /**
     * 특정 환자의 ID를 가진 채팅방의 정보를 반환합니다.
     *
     * @param patientId 환자의 ID
     * @return 해당 ID를 가진 채팅방 객체
     */
    @GetMapping("/get/room/{patientId}")
    @ResponseBody
    public ChatRoomDto roomInfo(@PathVariable Integer patientId) {
        return callBellService.findChatRoomByPatientId(patientId);
    }
}