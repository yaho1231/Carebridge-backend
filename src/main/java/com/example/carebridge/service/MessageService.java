package com.example.carebridge.service;

import com.example.carebridge.dto.ChatMessageDto;
import com.example.carebridge.entity.Message;
import com.example.carebridge.repository.ChatRoomRepository;
import com.example.carebridge.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;

    public MessageService(MessageRepository messageRepository, ChatRoomRepository chatRoomRepository) {
        this.messageRepository = messageRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    /**
     * 새로운 메시지를 저장합니다.
     *
     * @param chatMessageDto 저장할 메시지 객체
     */
    public void saveMessage(ChatMessageDto chatMessageDto) {
        Message message = new Message();
        Integer patientId;
        Integer medicalStaffId;
        String roomId = chatMessageDto.getRoomId();

        if (chatMessageDto.getIsPatient()) {
            patientId = chatMessageDto.getSenderId();
            medicalStaffId = chatRoomRepository.findByChatRoomId(roomId).getMedicalStaffId();
        } else {
            medicalStaffId = chatMessageDto.getSenderId();
            patientId = chatRoomRepository.findByChatRoomId(roomId).getPatientId();
        }
        message.setPatientId(patientId);
        message.setMedicalStaffId(medicalStaffId);
        message.setChatRoomId(roomId);
        message.setMessageContent(chatMessageDto.getMessage());
        messageRepository.save(message);
    }

    /**
     * 모든 환자의 메시지 목록을 환자 ID를 키로, 메시지 리스트를 값으로 가지는 맵으로 반환합니다.
     *
     * @return 환자 ID를 키로 하고 메시지 리스트를 값으로 가지는 맵
     */
    public Map<Integer, List<Message>> getAll() {
        List<Message> messages = messageRepository.findAll();
        Map<Integer, List<Message>> messageMap = new HashMap<>();

        for (Message message : messages) {
            Integer patientId = message.getPatientId();
            messageMap.computeIfAbsent(patientId, k -> new ArrayList<>()).add(message);
        }

        return messageMap;
    }

    /**
     * 특정 환자의 메시지 목록을 반환합니다.
     *
     * @param patientId 환자의 ID
     * @return 메시지 목록
     */
    public List<Message> getMessagesByPatientId(Integer patientId) {
        try {
            return messageRepository.findMessageContentByPatientId(patientId);
        } catch (Exception e) {
            logger.error("Error fetching messages for patientId: {}", patientId, e);
            return new ArrayList<>();
        }
    }

    /**
     * 특정 메시지의 읽음 상태를 반환합니다.
     *
     * @param patientId 환자의 ID
     * @param messageId 메시지의 ID
     * @return 읽음 상태
     */
    public Boolean getReadStatus(Integer patientId, Integer messageId) {
        try {
            List<Message> patientMessageList = getMessagesByPatientId(patientId);
            for (Message message : patientMessageList) {
                if (message.getMessageId().equals(messageId)) {
                    return message.getReadStatus();
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching read status for patientId: {} and messageId: {}", patientId, messageId, e);
        }
        return false;
    }

    /**
     * 특정 텍스트를 포함하는 메시지 목록을 반환합니다.
     *
     * @param patientId 환자의 ID
     * @param text 검색할 텍스트
     * @return 메시지 목록
     */
    public List<Message> getMessagesContainingText(Integer patientId, String text) {
        try {
            List<Message> patientMessageList = getMessagesByPatientId(patientId);
            List<Message> messagesContainingText = new ArrayList<>();
            for (Message message : patientMessageList) {
                if (message.getMessageContent().contains(text)) {
                    messagesContainingText.add(message);
                }
            }
            return messagesContainingText;
        } catch (Exception e) {
            logger.error("Error fetching messages containing text for patientId: {}", patientId, e);
            return new ArrayList<>();
        }
    }
}