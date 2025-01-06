package com.example.carebridge.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ChatRoom {

    /**
     * 채팅방의 고유 ID
     */
    private String roomId;

    /**
     * 채팅방의 이름
     */
    private String name;

    /**
     * 주어진 이름으로 새로운 채팅방을 생성하는 정적 메서드입니다.
     *
     * @param name 생성할 채팅방의 이름
     * @return 생성된 ChatRoom 객체
     */
    public static ChatRoom create(String name) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = UUID.randomUUID().toString(); // 고유한 ID를 생성하여 설정
        chatRoom.name = name; // 채팅방 이름 설정
        return chatRoom;
    }
}