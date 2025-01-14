package com.example.carebridge.service;

import com.example.carebridge.dto.ChatMessageDto;
import com.example.carebridge.dto.MessageSummaryDto;
import com.example.carebridge.entity.Message;
import com.example.carebridge.repository.ChatRoomRepository;
import com.example.carebridge.repository.MessageRepository;
import com.example.carebridge.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final PatientRepository patientRepository;

    public MessageService(MessageRepository messageRepository, ChatRoomRepository chatRoomRepository, PatientRepository patientRepository) {
        this.messageRepository = messageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.patientRepository = patientRepository;
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
        String roomId = chatMessageDto.getChatRoomId();

        if (chatMessageDto.getIsPatient()) {
            patientId = chatMessageDto.getSender_id();
            medicalStaffId = chatRoomRepository.findByChatRoomId(roomId).getMedicalStaffId();
        } else {
            medicalStaffId = chatMessageDto.getSender_id();
            patientId = chatRoomRepository.findByChatRoomId(roomId).getPatientId();
        }
        message.setPatientId(patientId);
        message.setMedicalStaffId(medicalStaffId);
        message.setChatRoomId(roomId);
        message.setMessageContent(chatMessageDto.getMessageContent());
        message.setSender_id(chatMessageDto.getSender_id());
        message.setReadStatus(chatMessageDto.getReadStatus());
        message.setTimestamp(chatMessageDto.getTimestamp());
        messageRepository.save(message);
    }

    /**
     * 메시지의 읽음 상태를 업데이트합니다.
     *
     * @param messageId 메시지의 ID
     */
    public void updateReadStatus(Integer messageId) {
        Message message = messageRepository.findByMessageId(messageId);
        message.setReadStatus(true);
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

        // 각 메시지 리스트를 타임스탬프 기준으로 내림차순 정렬합니다.
        for (Map.Entry<Integer, List<Message>> entry : messageMap.entrySet()) {
            entry.setValue(entry.getValue().stream()
                    .sorted(Comparator.comparing(Message::getTimestamp).reversed())
                    .collect(Collectors.toList()));
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
            List<Message> messages = messageRepository.findMessageContentByPatientId(patientId);
            // 메시지 리스트를 타임스탬프 기준으로 내림차순 정렬합니다.
            return messages.stream()
                    .sorted(Comparator.comparing(Message::getTimestamp).reversed())
                    .collect(Collectors.toList());
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


    /**
     * 환자별로 가장 최근 메시지 정보를 요약하여 반환합니다.
     *
     * @return 메시지 요약 정보 리스트
     */
    public List<MessageSummaryDto> getSummaryMessageInformation(Integer medicalStaffId) {
        List<Message> messages = messageRepository.findByMedicalStaffId(medicalStaffId);
        return messages.stream()
                .collect(Collectors.groupingBy(Message::getPatientId))
                .values().stream()
                .map(patientMessages -> patientMessages.stream().max(Comparator.comparing(Message::getTimestamp)).orElse(null))
                .filter(Objects::nonNull)
                .map(message -> new MessageSummaryDto(
                        patientRepository.findByPatientId(message.getPatientId()).getName(), // 발신자 이름
                        message.getMessageContent(), // 마지막 메시지 내용
                        message.getTimestamp() // 메시지 타임스탬프
                ))
                .collect(Collectors.toList());
    }
}