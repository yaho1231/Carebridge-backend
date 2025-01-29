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

import java.sql.Timestamp;
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
            patientId = chatMessageDto.getSenderId();
            medicalStaffId = chatRoomRepository.findByChatRoomId(roomId).getMedicalStaffId();
        } else {
            medicalStaffId = chatMessageDto.getSenderId();
            patientId = chatRoomRepository.findByChatRoomId(roomId).getPatientId();
        }
        message.setPatientId(patientId);
        message.setMedicalStaffId(medicalStaffId);
        message.setChatRoomId(roomId);
        message.setMessageContent(chatMessageDto.getMessageContent());
        message.setSender_id(chatMessageDto.getSenderId());
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
     * 의료진 ID로 메시지 요약 정보를 조회합니다.
     *
     * @param medicalStaffId 의료진의 ID
     * @return 메시지 요약 정보 리스트
     */
    public List<MessageSummaryDto> getSummaryMessageInformation(Integer medicalStaffId) {
        // 의료진 ID로 메시지 목록을 조회합니다.
        List<Message> messages = messageRepository.findByMedicalStaffId(medicalStaffId);

        // 메시지를 환자 ID 별로 그룹화합니다.
        Map<Integer, List<Message>> messagesByPatient = messages.stream()
                .collect(Collectors.groupingBy(Message::getPatientId));

        // 메시지 요약 정보를 저장할 리스트를 생성합니다.
        List<MessageSummaryDto> summaryList = new ArrayList<>();

        // 각 환자별로 메시지 요약 정보를 생성합니다.
        for (Map.Entry<Integer, List<Message>> entry : messagesByPatient.entrySet()) {
            List<Message> patientMessages = entry.getValue();

            // 메시지를 타임스탬프 기준으로 내림차순 정렬하여 가장 최근 메시지를 가져옵니다.
            patientMessages.sort(Comparator.comparing(Message::getTimestamp).reversed());

            // 가장 최근 메시지를 가져옵니다.
            Message recentMessage = patientMessages.get(0);

            // 메시지 요약 정보를 생성합니다.
            MessageSummaryDto summary = new MessageSummaryDto(
                    patientRepository.findByPatientId(recentMessage.getPatientId()).getName(), // 발신자 이름을 문자열로 변환하여 사용
                    recentMessage.getChatRoomId(),
                    recentMessage.getMessageContent(),
                    Timestamp.valueOf(recentMessage.getTimestamp()).toLocalDateTime(),
                    recentMessage.getReadStatus()
            );

            // 메시지 요약 정보를 리스트에 추가합니다.
            summaryList.add(summary);
        }

        // 메시지 요약 정보 리스트를 반환합니다.
        return summaryList;
    }
}