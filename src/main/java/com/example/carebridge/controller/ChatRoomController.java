package com.example.carebridge.controller;

import com.example.carebridge.dto.ChatRoom;
import com.example.carebridge.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;

    /**
     * 모든 채팅방 목록을 반환합니다.
     *
     * @return 모든 채팅방 목록
     */
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoom> room() {
        List<ChatRoom> list = chatRoomRepository.findAllRoom();
        return list;
    }

    /**
     * 새로운 채팅방을 생성합니다.
     *
     * @param user1 첫 번째 사용자의 이름
     * @param user2 두 번째 사용자의 이름
     * @return 생성된 채팅방 객체
     */
    @PostMapping("/room")
    @ResponseBody
    public ChatRoom createRoom(@RequestParam("user1") String user1, @RequestParam("user2") String user2) {
        return chatRoomRepository.createChatRoom(user1, user2);
    }

    /**
     * 특정 ID를 가진 채팅방의 정보를 반환합니다.
     *
     * @param roomId 찾고자 하는 채팅방의 ID
     * @return 해당 ID를 가진 채팅방 객체
     */
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoom roomInfo(@PathVariable String roomId) {
        return chatRoomRepository.findRoomById(roomId);
    }
}