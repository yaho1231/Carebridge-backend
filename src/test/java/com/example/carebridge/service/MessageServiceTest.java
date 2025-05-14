package com.example.carebridge.service;

import com.example.carebridge.dto.ChatCompletionDto;
import com.example.carebridge.dto.ChatMessageDto;
import com.example.carebridge.entity.ChatRoom;
import com.example.carebridge.entity.Hospital;
import com.example.carebridge.entity.HospitalInformation;
import com.example.carebridge.entity.Message;
import com.example.carebridge.repository.ChatRoomRepository;
import com.example.carebridge.repository.HospitalRepository;
import com.example.carebridge.repository.MessageRepository;
import com.example.carebridge.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * MessageService 클래스에 대한 단위 테스트
 * 모든 서비스 메소드의 기능을 검증합니다.
 */
@Tag("service")
@DisplayName("MessageService 테스트")
@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private ChatGPTService chatGPTService;

    @Mock
    private HospitalInformationService hospitalInformationService;

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private MessageService messageService;
    
    // 공통으로 사용되는 테스트 데이터
    protected Integer defaultPatientId;
    protected Integer defaultMedicalStaffId;
    protected Integer defaultHospitalId;
    protected String defaultChatRoomId;
    protected ChatRoom defaultChatRoom;
    protected LocalDateTime defaultTimestamp;
    
    /**
     * 각 테스트 실행 전에 공통으로 사용되는 테스트 데이터를 초기화합니다.
     */
    @BeforeEach
    void globalSetUp() {
        // 공통 테스트 데이터 초기화
        defaultPatientId = 2;
        defaultMedicalStaffId = 1;
        defaultHospitalId = 1;
        defaultChatRoomId = defaultMedicalStaffId + "_" + defaultPatientId;
        defaultTimestamp = LocalDateTime.now();
        
        // 기본 채팅방 설정
        defaultChatRoom = new ChatRoom();
        defaultChatRoom.setChatRoomId(defaultChatRoomId);
        defaultChatRoom.setPatientId(defaultPatientId);
        defaultChatRoom.setMedicalStaffId(defaultMedicalStaffId);
    }
    
    /**
     * 테스트용 ChatMessageDto 생성 헬퍼 메소드
     * 
     * @param isPatient 환자 여부
     * @param content 메시지 내용
     * @return 생성된 ChatMessageDto 객체
     */
    private ChatMessageDto createChatMessageDto(boolean isPatient, String content) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setChatRoomId(defaultChatRoomId);
        dto.setMessageContent(content);
        dto.setSenderId(isPatient ? defaultPatientId : defaultMedicalStaffId);
        dto.setIsPatient(isPatient);
        dto.setReadStatus(false);
        dto.setTimestamp(defaultTimestamp.toString());
        dto.setHospitalId(defaultHospitalId);
        dto.setPatientId(defaultPatientId);
        dto.setMedicalStaffId(defaultMedicalStaffId);
        return dto;
    }

    @Nested
    @DisplayName("saveMessage 메소드 테스트")
    class SaveMessageTest {

        @Test
        @DisplayName("환자가 메시지를 저장하는 경우 성공 테스트")
        void saveMessage_PatientMessageSuccess() {
            // given
            ChatMessageDto chatMessageDto = createChatMessageDto(true, "안녕하세요, 문의가 있습니다.");
            
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("content", "정보성 질문");
            Map<String, Object> choiceMap = new HashMap<>();
            choiceMap.put("message", messageMap);
            List<Map<String, Object>> choices = Collections.singletonList(choiceMap);
            Map<String, Object> result = new HashMap<>();
            result.put("choices", choices);

            when(chatRoomRepository.findByChatRoomId(chatMessageDto.getChatRoomId())).thenReturn(Optional.of(defaultChatRoom));
            when(chatGPTService.prompt(any(ChatCompletionDto.class))).thenReturn(result);
            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            Message savedMessage = messageService.saveMessage(chatMessageDto);

            // then
            assertNotNull(savedMessage);
            assertEquals(chatMessageDto.getMessageContent(), savedMessage.getMessageContent());
            assertEquals(chatMessageDto.getSenderId(), savedMessage.getSenderId());
            assertEquals(defaultChatRoom.getPatientId(), savedMessage.getPatientId());
            assertEquals(defaultChatRoom.getMedicalStaffId(), savedMessage.getMedicalStaffId());
            assertEquals("정보성 질문", savedMessage.getCategory());
            assertTrue(savedMessage.getIsPatient());
            assertEquals(Message.MessageType.MESSAGE, savedMessage.getType());

            verify(chatRoomRepository, times(1)).findByChatRoomId(chatMessageDto.getChatRoomId());
            verify(chatGPTService, times(1)).prompt(any(ChatCompletionDto.class));
            verify(messageRepository, times(1)).save(any(Message.class));
        }

        @Test
        @DisplayName("의료진이 메시지를 저장하는 경우 성공 테스트")
        void saveMessage_MedicalStaffMessageSuccess() {
            // given
            ChatMessageDto chatMessageDto = createChatMessageDto(false, "안녕하세요, 답변 드립니다.");

            when(chatRoomRepository.findByChatRoomId(chatMessageDto.getChatRoomId())).thenReturn(Optional.of(defaultChatRoom));
            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            Message savedMessage = messageService.saveMessage(chatMessageDto);

            // then
            assertNotNull(savedMessage);
            assertEquals(chatMessageDto.getMessageContent(), savedMessage.getMessageContent());
            assertEquals(chatMessageDto.getSenderId(), savedMessage.getSenderId());
            assertEquals(defaultChatRoom.getPatientId(), savedMessage.getPatientId());
            assertEquals(defaultChatRoom.getMedicalStaffId(), savedMessage.getMedicalStaffId());
            assertEquals("의료진 메세지", savedMessage.getCategory());
            assertFalse(savedMessage.getIsPatient());
            assertEquals(Message.MessageType.MESSAGE, savedMessage.getType());

            verify(chatRoomRepository, times(1)).findByChatRoomId(chatMessageDto.getChatRoomId());
            verify(chatGPTService, never()).prompt(any(ChatCompletionDto.class));
            verify(messageRepository, times(1)).save(any(Message.class));
        }

        @Test
        @DisplayName("채팅방을 찾을 수 없는 경우 예외 발생")
        void saveMessage_ChatRoomNotFound() {
            // given
            ChatMessageDto chatMessageDto = createChatMessageDto(true, "테스트 메시지");
            chatMessageDto.setChatRoomId("999_999");

            when(chatRoomRepository.findByChatRoomId(chatMessageDto.getChatRoomId())).thenReturn(Optional.empty());

            // when & then
            assertThrows(IllegalArgumentException.class, () -> messageService.saveMessage(chatMessageDto));

            verify(chatRoomRepository, times(1)).findByChatRoomId(chatMessageDto.getChatRoomId());
            verify(chatGPTService, never()).prompt(any(ChatCompletionDto.class));
            verify(messageRepository, never()).save(any(Message.class));
        }
    }

    @Nested
    @DisplayName("chatGptMessage 메소드 테스트")
    class ChatGptMessageTest {

        @Test
        @DisplayName("ChatGPT를 이용한 메시지 생성 성공 테스트")
        void chatGptMessage_Success() {
            // given
            ChatMessageDto chatMessageDto = new ChatMessageDto();
            chatMessageDto.setChatRoomId("1_2");
            chatMessageDto.setMessageContent("입원 절차가 어떻게 되나요?");
            chatMessageDto.setSenderId(2);
            chatMessageDto.setReadStatus(false);
            chatMessageDto.setHospitalId(1);

            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setChatRoomId("1_2");
            chatRoom.setPatientId(2);
            chatRoom.setMedicalStaffId(1);

            Hospital hospital = Hospital.builder()
                    .hospitalId(1)
                    .name("케어브릿지 병원")
                    .location("서울시 강남구")
                    .build();

            HospitalInformation hospitalInformation = new HospitalInformation();
            hospitalInformation.setInformation("입원 절차는 접수, 진료, 입원 결정, 병실 배정의 순서로 진행됩니다.");

            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("content", "입원 절차는 접수, 진료, 입원 결정, 병실 배정의 순서로 진행됩니다. 접수는 1층 접수창구에서 가능하며, 보험증과 신분증이 필요합니다.");
            Map<String, Object> choiceMap = new HashMap<>();
            choiceMap.put("message", messageMap);
            List<Map<String, Object>> choices = Collections.singletonList(choiceMap);
            Map<String, Object> result = new HashMap<>();
            result.put("choices", choices);

            when(chatRoomRepository.findByChatRoomId(chatMessageDto.getChatRoomId())).thenReturn(Optional.of(chatRoom));
            when(hospitalRepository.findByHospitalId(chatMessageDto.getHospitalId())).thenReturn(Optional.of(hospital));
            when(hospitalInformationService.findMostSimilarHospitalInformation(chatMessageDto.getMessageContent(), chatMessageDto.getHospitalId())).thenReturn(hospitalInformation);
            when(chatGPTService.prompt(any(ChatCompletionDto.class))).thenReturn(result);
            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            Message savedMessage = messageService.chatGptMessage(chatMessageDto);

            // then
            assertNotNull(savedMessage);
            assertTrue(savedMessage.getMessageContent().startsWith("[ChatGPT로 자동 생성된 답변 입니다.]"));
            assertEquals(chatRoom.getMedicalStaffId(), savedMessage.getSenderId());
            assertEquals(chatRoom.getPatientId(), savedMessage.getPatientId());
            assertEquals(chatRoom.getMedicalStaffId(), savedMessage.getMedicalStaffId());
            assertEquals("정보성 질문 답변자동생성", savedMessage.getCategory());
            assertFalse(savedMessage.getIsPatient());
            assertEquals(Message.MessageType.MESSAGE, savedMessage.getType());

            verify(chatRoomRepository, times(1)).findByChatRoomId(chatMessageDto.getChatRoomId());
            verify(hospitalRepository, times(1)).findByHospitalId(chatMessageDto.getHospitalId());
            verify(hospitalInformationService, times(1)).findMostSimilarHospitalInformation(chatMessageDto.getMessageContent(), chatMessageDto.getHospitalId());
            verify(chatGPTService, times(1)).prompt(any(ChatCompletionDto.class));
            verify(messageRepository, times(1)).save(any(Message.class));
        }

        @Test
        @DisplayName("채팅방을 찾을 수 없는 경우 예외 발생")
        void chatGptMessage_ChatRoomNotFound() {
            // given
            ChatMessageDto chatMessageDto = new ChatMessageDto();
            chatMessageDto.setChatRoomId("999_999");
            chatMessageDto.setHospitalId(1);

            when(chatRoomRepository.findByChatRoomId(chatMessageDto.getChatRoomId())).thenReturn(Optional.empty());

            // when & then
            assertThrows(IllegalArgumentException.class, () -> messageService.chatGptMessage(chatMessageDto));

            verify(chatRoomRepository, times(1)).findByChatRoomId(chatMessageDto.getChatRoomId());
            verify(hospitalRepository, never()).findByHospitalId(anyInt());
            verify(hospitalInformationService, never()).findMostSimilarHospitalInformation(anyString(), anyInt());
            verify(chatGPTService, never()).prompt(any(ChatCompletionDto.class));
            verify(messageRepository, never()).save(any(Message.class));
        }

        @Test
        @DisplayName("병원을 찾을 수 없는 경우 예외 발생")
        void chatGptMessage_HospitalNotFound() {
            // given
            ChatMessageDto chatMessageDto = new ChatMessageDto();
            chatMessageDto.setChatRoomId("1_2");
            chatMessageDto.setHospitalId(999);

            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setChatRoomId("1_2");
            chatRoom.setPatientId(2);
            chatRoom.setMedicalStaffId(1);

            when(chatRoomRepository.findByChatRoomId(chatMessageDto.getChatRoomId())).thenReturn(Optional.of(chatRoom));
            when(hospitalRepository.findByHospitalId(chatMessageDto.getHospitalId())).thenReturn(Optional.empty());

            // when & then
            assertThrows(IllegalArgumentException.class, () -> messageService.chatGptMessage(chatMessageDto));

            verify(chatRoomRepository, times(1)).findByChatRoomId(chatMessageDto.getChatRoomId());
            verify(hospitalRepository, times(1)).findByHospitalId(chatMessageDto.getHospitalId());
            verify(hospitalInformationService, never()).findMostSimilarHospitalInformation(anyString(), anyInt());
            verify(chatGPTService, never()).prompt(any(ChatCompletionDto.class));
            verify(messageRepository, never()).save(any(Message.class));
        }

        @Test
        @DisplayName("관련 병원 정보를 찾을 수 없는 경우 예외 발생")
        void chatGptMessage_HospitalInformationNotFound() {
            // given
            ChatMessageDto chatMessageDto = new ChatMessageDto();
            chatMessageDto.setChatRoomId("1_2");
            chatMessageDto.setMessageContent("존재하지 않는 정보");
            chatMessageDto.setHospitalId(1);

            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setChatRoomId("1_2");
            chatRoom.setPatientId(2);
            chatRoom.setMedicalStaffId(1);

            Hospital hospital = Hospital.builder()
                    .hospitalId(1)
                    .name("케어브릿지 병원")
                    .location("서울시 강남구")
                    .build();

            when(chatRoomRepository.findByChatRoomId(chatMessageDto.getChatRoomId())).thenReturn(Optional.of(chatRoom));
            when(hospitalRepository.findByHospitalId(chatMessageDto.getHospitalId())).thenReturn(Optional.of(hospital));
            when(hospitalInformationService.findMostSimilarHospitalInformation(chatMessageDto.getMessageContent(), chatMessageDto.getHospitalId())).thenReturn(null);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> messageService.chatGptMessage(chatMessageDto));

            verify(chatRoomRepository, times(1)).findByChatRoomId(chatMessageDto.getChatRoomId());
            verify(hospitalRepository, times(1)).findByHospitalId(chatMessageDto.getHospitalId());
            verify(hospitalInformationService, times(1)).findMostSimilarHospitalInformation(chatMessageDto.getMessageContent(), chatMessageDto.getHospitalId());
            verify(chatGPTService, never()).prompt(any(ChatCompletionDto.class));
            verify(messageRepository, never()).save(any(Message.class));
        }
    }

    @Nested
    @DisplayName("makeReqMessage 메소드 테스트")
    class MakeReqMessageTest {

        @Test
        @DisplayName("요청 사항 생성 확인 메시지 생성 성공 테스트")
        void makeReqMessage_Success() {
            // given
            ChatMessageDto chatMessageDto = new ChatMessageDto();
            chatMessageDto.setPatientId(2);
            chatMessageDto.setMedicalStaffId(1);
            chatMessageDto.setMessageContent("물 좀 갖다주세요");
            chatMessageDto.setReadStatus(false);
            chatMessageDto.setChatRoomId("1_2");
            chatMessageDto.setSenderId(2);
            chatMessageDto.setHospitalId(1);
            chatMessageDto.setCategory("의료진 도움요청");
            chatMessageDto.setIsPatient(true);

            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            Message savedMessage = messageService.makeReqMessage(chatMessageDto);

            // then
            assertNotNull(savedMessage);
            assertTrue(savedMessage.getMessageContent().startsWith("[요청 사항 생성 완료]"));
            assertTrue(savedMessage.getMessageContent().contains(chatMessageDto.getMessageContent()));
            assertEquals(chatMessageDto.getPatientId(), savedMessage.getPatientId());
            assertEquals(chatMessageDto.getMedicalStaffId(), savedMessage.getMedicalStaffId());
            assertEquals(chatMessageDto.getMedicalStaffId(), savedMessage.getSenderId());
            assertEquals(chatMessageDto.getCategory(), savedMessage.getCategory());
            assertFalse(savedMessage.getIsPatient());
            assertEquals(Message.MessageType.MESSAGE, savedMessage.getType());

            verify(messageRepository, times(1)).save(any(Message.class));
        }
    }

    @Nested
    @DisplayName("updateReadStatus 메소드 테스트")
    class UpdateReadStatusTest {

        @Test
        @DisplayName("메시지 읽음 상태 업데이트 성공 테스트")
        void updateReadStatus_Success() {
            // given
            Integer messageId = 1;
            Message message = new Message();
            message.setMessageId(messageId);
            message.setReadStatus(false);

            when(messageRepository.findByMessageId(messageId)).thenReturn(Optional.of(message));
            when(messageRepository.save(any(Message.class))).thenReturn(message);

            // when
            messageService.updateReadStatus(messageId);

            // then
            assertTrue(message.getReadStatus());
            verify(messageRepository, times(1)).findByMessageId(messageId);
            verify(messageRepository, times(1)).save(message);
        }

        @Test
        @DisplayName("메시지를 찾을 수 없는 경우 예외 발생")
        void updateReadStatus_MessageNotFound() {
            // given
            Integer messageId = 999;
            when(messageRepository.findByMessageId(messageId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(IllegalArgumentException.class, () -> messageService.updateReadStatus(messageId));

            verify(messageRepository, times(1)).findByMessageId(messageId);
            verify(messageRepository, never()).save(any(Message.class));
        }
    }

    @Nested
    @DisplayName("getAll 메소드 테스트")
    class GetAllTest {

        @Test
        @DisplayName("모든 환자의 메시지 목록을 성공적으로 조회하는 경우")
        void getAll_Success() {
            // given
            Message message1 = new Message();
            message1.setMessageId(1);
            message1.setPatientId(1);
            message1.setTimestamp(LocalDateTime.now());

            Message message2 = new Message();
            message2.setMessageId(2);
            message2.setPatientId(1);
            message2.setTimestamp(LocalDateTime.now().minusHours(1));

            Message message3 = new Message();
            message3.setMessageId(3);
            message3.setPatientId(2);
            message3.setTimestamp(LocalDateTime.now());

            List<Message> messages = Arrays.asList(message1, message2, message3);
            when(messageRepository.findAllMessage()).thenReturn(Optional.of(messages));

            // when
            Map<Integer, List<Message>> result = messageService.getAll();

            // then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertTrue(result.containsKey(1));
            assertTrue(result.containsKey(2));
            assertEquals(2, result.get(1).size());
            assertEquals(1, result.get(2).size());
            assertEquals(message1.getMessageId(), result.get(1).get(0).getMessageId());
            assertEquals(message2.getMessageId(), result.get(1).get(1).getMessageId());
            assertEquals(message3.getMessageId(), result.get(2).get(0).getMessageId());

            verify(messageRepository, times(1)).findAllMessage();
        }

        @Test
        @DisplayName("메시지가 존재하지 않는 경우 예외 발생")
        void getAll_MessagesNotFound() {
            // given
            when(messageRepository.findAllMessage()).thenReturn(Optional.empty());

            // when & then
            assertThrows(IllegalArgumentException.class, () -> messageService.getAll());

            verify(messageRepository, times(1)).findAllMessage();
        }
    }

    @Nested
    @DisplayName("getMessagesByPatientId 메소드 테스트")
    class GetMessagesByPatientIdTest {

        @Test
        @DisplayName("환자 ID로 메시지를 성공적으로 조회하는 경우")
        void getMessagesByPatientId_Success() {
            // given
            Integer patientId = 1;
            Message message1 = new Message();
            message1.setMessageId(1);
            message1.setPatientId(patientId);
            message1.setTimestamp(LocalDateTime.now());

            Message message2 = new Message();
            message2.setMessageId(2);
            message2.setPatientId(patientId);
            message2.setTimestamp(LocalDateTime.now().minusHours(1));

            List<Message> messages = Arrays.asList(message1, message2);
            when(messageRepository.findMessageContentByPatientId(patientId)).thenReturn(Optional.of(messages));

            // when
            List<Message> result = messageService.getMessagesByPatientId(patientId);

            // then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(message1.getMessageId(), result.get(0).getMessageId());
            assertEquals(message2.getMessageId(), result.get(1).getMessageId());

            verify(messageRepository, times(1)).findMessageContentByPatientId(patientId);
        }

        @Test
        @DisplayName("환자의 메시지가 존재하지 않는 경우 예외 발생")
        void getMessagesByPatientId_MessagesNotFound() {
            // given
            Integer patientId = 999;
            lenient().when(messageRepository.findMessageContentByPatientId(patientId)).thenReturn(Optional.empty());

            // when
            List<Message> result = messageService.getMessagesByPatientId(patientId);
            
            // then
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(messageRepository, times(1)).findMessageContentByPatientId(patientId);
        }

        @Test
        @DisplayName("예외 발생 시 빈 리스트 반환")
        void getMessagesByPatientId_ExceptionHandling() {
            // given
            Integer patientId = 1;
            when(messageRepository.findMessageContentByPatientId(patientId)).thenThrow(new RuntimeException("Database error"));

            // when
            List<Message> result = messageService.getMessagesByPatientId(patientId);

            // then
            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(messageRepository, times(1)).findMessageContentByPatientId(patientId);
        }
    }

    @Nested
    @DisplayName("getMessageById 메소드 테스트")
    class GetMessageByIdTest {

        @Test
        @DisplayName("메시지 ID로 메시지를 성공적으로 조회하는 경우")
        void getMessageById_Success() {
            // given
            Integer messageId = 1;
            Message message = new Message();
            message.setMessageId(messageId);
            message.setMessageContent("테스트 메시지");

            when(messageRepository.findByMessageId(messageId)).thenReturn(Optional.of(message));

            // when
            Message result = messageService.getMessageById(messageId);

            // then
            assertNotNull(result);
            assertEquals(messageId, result.getMessageId());
            assertEquals("테스트 메시지", result.getMessageContent());

            verify(messageRepository, times(1)).findByMessageId(messageId);
        }

        @Test
        @DisplayName("메시지를 찾을 수 없는 경우 예외 발생")
        void getMessageById_MessageNotFound() {
            // given
            Integer messageId = 999;
            lenient().when(messageRepository.findByMessageId(messageId)).thenReturn(Optional.empty());

            // when & then
            NoSuchElementException exception = assertThrows(NoSuchElementException.class, 
                () -> messageService.getMessageById(messageId));
            
            // 예외 메시지 검증
            assertTrue(exception.getMessage().contains("메시지를 찾을 수 없습니다"));
            verify(messageRepository, times(1)).findByMessageId(messageId);
        }
    }
} 