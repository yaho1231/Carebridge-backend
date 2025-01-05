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

    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoom> room(){
        List<ChatRoom> list = chatRoomRepository.findAllRoom();
        return list;
    }

    @PostMapping("/room")
    @ResponseBody
    public ChatRoom createRoom(@RequestParam("user1") String user1,@RequestParam("user2") String user2){
        return chatRoomRepository.createChatRoom(user1, user2);
    }

    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoom roomInfo(@PathVariable String roomId){
        return chatRoomRepository.findRoomById(roomId);
    }
}
