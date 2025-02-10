package com.example.carebridge.service;

import com.example.carebridge.dto.ChatCompletionDto;
import com.example.carebridge.dto.ChatMessageDto;
import com.example.carebridge.dto.ChatRequestMsgDto;
import com.example.carebridge.dto.MessageSummaryDto;
import com.example.carebridge.entity.Message;
import com.example.carebridge.repository.ChatRoomRepository;
import com.example.carebridge.repository.HospitalRepository;
import com.example.carebridge.repository.MessageRepository;
import com.example.carebridge.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final PatientRepository patientRepository;
    private final ChatGPTService chatGPTService;
    private final HospitalInformationService hospitalInformationService;
    private final HospitalRepository hospitalRepository;

    public MessageService(MessageRepository messageRepository, ChatRoomRepository chatRoomRepository, PatientRepository patientRepository, ChatGPTService chatGPTService, HospitalInformationService hospitalInformationService, HospitalRepository hospitalRepository) {
        this.messageRepository = messageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.patientRepository = patientRepository;
        this.chatGPTService = chatGPTService;
        this.hospitalInformationService = hospitalInformationService;
        this.hospitalRepository = hospitalRepository;
    }

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 새로운 메시지를 저장합니다.
     *
     * @param chatMessageDto 저장할 메시지 객체
     * @return
     */
    public Message saveMessage(ChatMessageDto chatMessageDto) {
        Message message = new Message();
        Integer patientId;
        Integer medicalStaffId;
        String roomId = chatMessageDto.getChatRoomId();
        String category = chatMessageDto.getCategory();

        if (chatMessageDto.getIsPatient()) {
            message.setIsPatient(true);
            patientId = chatMessageDto.getSenderId();
            medicalStaffId = chatRoomRepository.findByChatRoomId(roomId)
                .orElseThrow(() -> {
                    logger.error("채팅방을 찾을 수 없습니다 - 방 ID: {}", roomId);
                    return new IllegalArgumentException("해당 채팅방이 존재하지 않습니다.");
                }).getMedicalStaffId();

            ChatCompletionDto chatCompletionDto = new ChatCompletionDto(
                    "gpt-4o-mini-2024-07-18",
                    Collections.singletonList(new ChatRequestMsgDto("user",
                            "다음 메시지를 반드시 정보성 질문, 의료진 도움요청, 기타 3개의 카테고리 중 하나로만 단답으로 분류하라. " +
                                    "대부분의 메세지를 정보성 질문 혹은 의료진 도움요청이 되도록 하라." +
                                    "단순히 웹에서 정보 제공을 통해 처리 가능한 요청사항의 경우 정보성 질문에 해당한다. " +
                                    "웹에서 처리 불가능하며 간호 간병 의료진이 필요한(~하고 싶다, ~해달라, ~하고 싶어요 등 의료진의 도움을 바라는) 요청사항의 경우 의료진 도움요청에 해당한다." +
                                    "그리고 알겠습니다, 네 등과 같이 사용자의 답변을 필요로하지 않는 일부 메세지만 기타에 해당한다." +
                                    "메시지 :" + chatMessageDto.getMessageContent()))
            );
            Map<String, Object> result = chatGPTService.prompt(chatCompletionDto);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) result.get("choices");
            Map<String, Object> getMessage = (Map<String, Object>) choices.get(0).get("message");
            category = (String) getMessage.get("content");
//            System.out.println(category);
        } else {
            message.setIsPatient(false);
            medicalStaffId = chatMessageDto.getSenderId();
            patientId = chatRoomRepository.findByChatRoomId(roomId)
                .orElseThrow(() -> {
                    logger.error("채팅방을 찾을 수 없습니다 - 방 ID: {}", roomId);
                    return new IllegalArgumentException("해당 채팅방이 존재하지 않습니다.");
                }).getPatientId();
        }
        message.setPatientId(patientId);
        message.setMedicalStaffId(medicalStaffId);
        message.setChatRoomId(roomId);
        message.setMessageContent(chatMessageDto.getMessageContent());
        message.setSenderId(chatMessageDto.getSenderId());
        message.setReadStatus(chatMessageDto.getReadStatus());
        message.setTimestamp(LocalDateTime.parse(chatMessageDto.getTimestamp()));
        message.setHospitalId(chatMessageDto.getHospitalId());
        message.setCategory(category);
        message.setIsRequest(false);
        messageRepository.save(message);

        return message;
    }

    public Message chatGptMessage(ChatMessageDto chatMessageDto) {
        Message message = new Message();
        Integer patientId = chatMessageDto.getSenderId();
        String roomId = chatMessageDto.getChatRoomId();
        Integer medicalStaffId = chatRoomRepository.findByChatRoomId(roomId)
            .orElseThrow(() -> {
                logger.error("채팅방을 찾을 수 없습니다 - 방 ID: {}", roomId);
                return new IllegalArgumentException("해당 채팅방이 존재하지 않습니다.");
            }).getMedicalStaffId();
        String hospitalName = hospitalRepository.findByHospitalId(chatMessageDto.getHospitalId())
                .orElseThrow(() -> new IllegalArgumentException("해당 병원을 찾을 수 없습니다."))
                .getName();

        String mostSimilarInfo = Optional.ofNullable(hospitalInformationService.findMostSimilarHospitalInformation(
                chatMessageDto.getMessageContent(), chatMessageDto.getHospitalId()))
                .orElseThrow(() -> new IllegalArgumentException("관련된 병원 정보를 찾을 수 없습니다."))
                .getInformation();
        ChatCompletionDto chatCompletionDto = new ChatCompletionDto(
                "gpt-4o-mini-2024-07-18",
                Collections.singletonList(new ChatRequestMsgDto("user",
                        "지금부터 너는 " + hospitalName +
                                "병원에 소속된 의료진이다." +
                                "다음 내용을 기반으로 varchar(255) 크기를 넘지 않게 답변하라 " + mostSimilarInfo +
                                "답변해야할 메시지 :" + chatMessageDto.getMessageContent()))
        );

        Map<String, Object> result = chatGPTService.prompt(chatCompletionDto);
        List<Map<String, Object>> choices = (List<Map<String, Object>>) result.get("choices");
        Map<String, Object> getMessage = (Map<String, Object>) choices.get(0).get("message");
        String content = (String) getMessage.get("content");

        message.setPatientId(patientId);
        message.setMedicalStaffId(medicalStaffId);
        message.setChatRoomId(roomId);
        message.setMessageContent(content);
        message.setSenderId(medicalStaffId);
        message.setReadStatus(chatMessageDto.getReadStatus());
        message.setTimestamp(LocalDateTime.now());
        message.setHospitalId(chatMessageDto.getHospitalId());
        message.setCategory("정보성 질문 답변자동생성");
        message.setIsRequest(false);
        message.setIsPatient(false);
        messageRepository.save(message);

        return message;
    }

    /**
     * 메시지의 읽음 상태를 업데이트합니다.
     *
     * @param messageId 메시지의 ID
     */
    public void updateReadStatus(Integer messageId) {
        Message message = messageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메시지를 찾을 수 없습니다."));
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
        List<Message> messages = messageRepository.findByMedicalStaffId(medicalStaffId);

        Map<Integer, List<Message>> messagesByPatient = messages.stream()
                .collect(Collectors.groupingBy(Message::getPatientId));

        List<MessageSummaryDto> summaryList = new ArrayList<>();

        for (Map.Entry<Integer, List<Message>> entry : messagesByPatient.entrySet()) {
            List<Message> patientMessages = entry.getValue();

            patientMessages.sort(Comparator.comparing(Message::getTimestamp).reversed());

            Message recentMessage = patientMessages.get(0);

            MessageSummaryDto summary = new MessageSummaryDto(
                    patientRepository.findByPatientId(recentMessage.getPatientId())
                            .orElseThrow(() -> new IllegalArgumentException("해당 환자를 찾을 수 없습니다."))
                            .getName(),
                    recentMessage.getChatRoomId(),
                    recentMessage.getMessageContent(),
                    Timestamp.valueOf(recentMessage.getTimestamp()).toLocalDateTime(),
                    recentMessage.getReadStatus()
            );

            summaryList.add(summary);
        }

        return summaryList;
    }
}