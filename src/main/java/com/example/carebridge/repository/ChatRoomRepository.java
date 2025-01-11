package com.example.carebridge.repository;

import com.example.carebridge.entity.ChatRoom;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    /**
     * 모든 채팅방을 조회합니다.
     * @return 모든 채팅방 목록
     */
    @Nonnull
    List<ChatRoom> findAll();

    /**
     * 환자의 ID로 채팅방을 조회합니다.
     * @param patientId 환자의 ID
     * @return 환자의 채팅방
     */
    ChatRoom findByPatientId(Integer patientId);

    /**
     * 채팅방의 ID로 채팅방을 조회합니다.
     * @param chatroomId 채팅방의 ID
     * @return 채팅방
     */
    ChatRoom findByChatRoomId(String chatroomId);
}