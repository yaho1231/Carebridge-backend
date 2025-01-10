package com.example.carebridge.repository;

import com.example.carebridge.dto.ChatRoom;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ChatRoomRepository {

    private Map<String, ChatRoom> chatRoomMap;

    /**
     * 초기화 메서드, chatRoomMap을 LinkedHashMap으로 초기화합니다.
     */
    @PostConstruct
    private void init() {
        chatRoomMap = new LinkedHashMap<>();
    }

    /**
     * 모든 채팅방을 최신 순으로 반환합니다.
     *
     * @return 최신 순으로 정렬된 모든 채팅방 목록
     */
    public List<ChatRoom> findAllRoom() {
        List<ChatRoom> chatRooms = new ArrayList<>(chatRoomMap.values());
        Collections.reverse(chatRooms); // 최신 순으로 정렬
        return chatRooms;
    }

    /**
     * ID로 채팅방을 찾습니다.
     *
     * @param id 찾고자 하는 채팅방의 ID
     * @return 해당 ID를 가진 채팅방, 없으면 null
     */
    public ChatRoom findRoomById(String id) {
        return chatRoomMap.get(id);
    }

    /**
     * 두 사용자의 이름으로 새로운 채팅방을 생성합니다.
     *
     * @param user1 첫 번째 사용자의 이름
     * @param user2 두 번째 사용자의 이름
     * @return 생성된 채팅방 객체
     */
    public ChatRoom createChatRoom(String user1, String user2) {
        String roomName = user1 + " and " + user2;
        ChatRoom chatRoom = ChatRoom.create(roomName);
        chatRoomMap.put(chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }
}