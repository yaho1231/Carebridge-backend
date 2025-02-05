package com.example.carebridge.repository;

import com.example.carebridge.entity.ChatRoom;
import org.springframework.lang.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    /**
     * 모든 채팅방을 조회합니다.
     * @return 모든 채팅방 목록
     */
    @Override
    @NonNull
    List<ChatRoom> findAll();

    /**
     * 환자의 ID로 채팅방을 조회합니다.
     * @param patientId 환자의 ID
     * @return 환자의 채팅방
     */
    @Query("SELECT c FROM ChatRoom c WHERE c.patientId = :patientId")
    Optional<ChatRoom> findByPatientId(@Param("patientId") Integer patientId);

    /**
     * 채팅방의 ID로 채팅방을 조회합니다.
     * @param chatRoomId 채팅방의 ID
     * @return 채팅방
     */
    @Query("SELECT c FROM ChatRoom c WHERE c.chatRoomId = :chatRoomId")
    Optional<ChatRoom> findByChatRoomId(@Param("chatRoomId") String chatRoomId);
}