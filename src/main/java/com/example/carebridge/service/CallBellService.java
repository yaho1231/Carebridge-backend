package com.example.carebridge.service;

import com.example.carebridge.dto.ChatRoomDto;
import com.example.carebridge.entity.ChatRoom;
import com.example.carebridge.repository.ChatRoomRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Getter
@Setter
public class CallBellService {

    private final ChatRoomRepository chatRoomRepository;
    private MedicalStaffService medicalStaffService;

    @Autowired
    public CallBellService(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    /**
     * 특정 환자의 ID로 채팅방을 조회합니다.
     *
     * @param patientId 환자의 ID
     * @return 채팅방 DTO
     */
    public ChatRoomDto findChatRoomByPatientId(Integer patientId) {
        ChatRoom chatRoom = chatRoomRepository.findByPatientId(patientId);
        ChatRoomDto chatRoomDto = new ChatRoomDto();
        chatRoomDto.setRoomId(chatRoom.getChatRoomId());
        chatRoomDto.setPatientId(chatRoom.getPatientId());
        chatRoomDto.setMedicalStaffId(chatRoom.getMedicalStaffId());
        return chatRoomDto;
    }

    /**
     * 새로운 채팅방을 생성합니다.
     *
     * @param patientId 환자의 ID
     * @param department 의료진 소속 분과
     * @return 생성된 채팅방 DTO
     */
    public ChatRoomDto createChatRoom(Integer patientId, String department) {
        ChatRoomDto chatRoomDto = new ChatRoomDto();
        chatRoomDto.setRoomId(UUID.randomUUID().toString());
        chatRoomDto.setPatientId(patientId);
        chatRoomDto.setMedicalStaffId(medicalStaffService.findAllByDepartment(department).getMedicalStaffId());

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setChatRoomId(chatRoomDto.getRoomId());
        chatRoom.setPatientId(chatRoomDto.getPatientId());
        chatRoom.setMedicalStaffId(chatRoomDto.getMedicalStaffId());
        chatRoomRepository.save(chatRoom);

        return chatRoomDto;
    }
}