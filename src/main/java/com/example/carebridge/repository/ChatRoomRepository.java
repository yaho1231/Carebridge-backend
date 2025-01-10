package com.example.carebridge.repository;

import com.example.carebridge.entity.ChatRoom;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    @Nonnull
    List<ChatRoom> findAll();
    ChatRoom findByPatientId(Integer patientId);
    ChatRoom findByChatRoomId(String chatroomId);
}