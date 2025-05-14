package com.example.carebridge.service;

import com.example.carebridge.entity.MedicalStaff;

import com.example.carebridge.dto.ChatRoomDto;
import com.example.carebridge.dto.RequestDto;
import com.example.carebridge.entity.ChatRoom;
import com.example.carebridge.entity.Message;
import com.example.carebridge.entity.Request;
import com.example.carebridge.mapper.ChatRoomMapper;
import com.example.carebridge.mapper.RequestMapper;
import com.example.carebridge.repository.ChatRoomRepository;
import com.example.carebridge.repository.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * CallBellService 클래스에 대한 단위 테스트
 * 콜벨 서비스의 모든 주요 기능을 검증하는 테스트 클래스입니다.
 * 각 테스트는 독립적으로 실행되며 외부 의존성은 모두 모킹됩니다.
 */
@Tag("service")
@DisplayName("CallBellService 테스트")
@ExtendWith(MockitoExtension.class)
class CallBellServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private ChatRoomMapper chatRoomMapper;

    @Mock
    private RequestMapper requestMapper;

    @Mock
    private MedicalStaffService medicalStaffService;

    @InjectMocks
    private CallBellService callBellService;
    
    // 공통으로 사용되는 테스트 데이터
    protected Integer defaultPatientId;
    protected Integer defaultMedicalStaffId;
    protected String defaultChatRoomId;
    protected String defaultDepartment;
    protected LocalDateTime defaultTime;
    protected MedicalStaff defaultMedicalStaff;

    /**
     * 각 테스트 실행 전에 공통으로 사용되는 테스트 데이터를 초기화합니다.
     */
    @BeforeEach
    void globalSetUp() {
        // 공통 테스트 데이터 초기화
        defaultPatientId = 1;
        defaultMedicalStaffId = 2;
        defaultChatRoomId = defaultMedicalStaffId + "_" + defaultPatientId;
        defaultDepartment = "내과";
        defaultTime = LocalDateTime.now();
        
        // 공통 MedicalStaff 객체 초기화 - 빌더 패턴 사용
        defaultMedicalStaff = MedicalStaff.builder()
                .department(defaultDepartment)
                .hospitalId(1)
                .build();
        
        // medicalStaffId 설정
        ReflectionTestUtils.setField(defaultMedicalStaff, "medicalStaffId", defaultMedicalStaffId);
    }

    @Nested
    @DisplayName("createRequestByMessage 메소드 테스트")
    class CreateRequestByMessageTest {
        private Message message;
        private Request expectedRequest;

        @BeforeEach
        void setUp() {
            message = new Message();
            message.setPatientId(defaultPatientId);
            message.setMessageContent("도움이 필요합니다");
            message.setMedicalStaffId(defaultMedicalStaffId);

            expectedRequest = new Request();
            expectedRequest.setRequestId(1);
            expectedRequest.setPatientId(defaultPatientId);
            expectedRequest.setRequestContent("도움이 필요합니다");
            expectedRequest.setStatus(Request.RequestStatus.PENDING);
            expectedRequest.setMedicalStaffId(defaultMedicalStaffId);
            expectedRequest.setType(Request.MessageType.REQUEST);
        }

        @Test
        @DisplayName("메시지로부터 요청을 성공적으로 생성하는 경우")
        void createRequestByMessage_Success() {
            // given
            lenient().when(requestRepository.save(any(Request.class))).thenReturn(expectedRequest);

            // when
            Request result = callBellService.createRequestByMessage(message);

            // then
            assertNotNull(result);
            assertEquals(expectedRequest.getRequestId(), result.getRequestId());
            assertEquals(expectedRequest.getPatientId(), result.getPatientId());
            assertEquals(expectedRequest.getRequestContent(), result.getRequestContent());
            assertEquals(expectedRequest.getStatus(), result.getStatus());
            assertEquals(expectedRequest.getMedicalStaffId(), result.getMedicalStaffId());
            assertEquals(expectedRequest.getType(), result.getType());

            verify(requestRepository, times(1)).save(any(Request.class));
        }
    }

    @Nested
    @DisplayName("findChatRoomByPatientId 메소드 테스트")
    class FindChatRoomByPatientIdTest {
        private ChatRoom chatRoom;
        private ChatRoomDto expectedDto;

        @BeforeEach
        void setUp() {
            chatRoom = new ChatRoom();
            chatRoom.setChatRoomId(defaultChatRoomId);
            chatRoom.setPatientId(defaultPatientId);
            chatRoom.setMedicalStaffId(defaultMedicalStaffId);
            
            expectedDto = new ChatRoomDto();
            expectedDto.setRoomId(defaultChatRoomId);
            expectedDto.setPatientId(defaultPatientId);
            expectedDto.setMedicalStaffId(defaultMedicalStaffId);
        }

        @Test
        @DisplayName("환자 ID로 채팅방을 성공적으로 찾는 경우")
        void findChatRoomByPatientId_Success() {
            // given
            lenient().when(chatRoomRepository.findByPatientId(defaultPatientId)).thenReturn(Optional.of(chatRoom));
            lenient().when(chatRoomMapper.toDto(chatRoom)).thenReturn(expectedDto);
            
            // when
            ChatRoomDto result = callBellService.findChatRoomByPatientId(defaultPatientId);
            
            // then
            assertNotNull(result);
            assertEquals(expectedDto.getRoomId(), result.getRoomId());
            assertEquals(expectedDto.getPatientId(), result.getPatientId());
            assertEquals(expectedDto.getMedicalStaffId(), result.getMedicalStaffId());
            
            verify(chatRoomRepository, times(1)).findByPatientId(defaultPatientId);
            verify(chatRoomMapper, times(1)).toDto(chatRoom);
        }
        
        @Test
        @DisplayName("환자 ID가 null인 경우 IllegalArgumentException 발생")
        void findChatRoomByPatientId_NullPatientId() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> callBellService.findChatRoomByPatientId(null));
            
            verify(chatRoomRepository, never()).findByPatientId(any());
            verify(chatRoomMapper, never()).toDto(any());
        }
        
        @Test
        @DisplayName("환자의 채팅방이 존재하지 않는 경우 NoSuchElementException 발생")
        void findChatRoomByPatientId_ChatRoomNotFound() {
            // given
            Integer notExistingPatientId = 999;
            when(chatRoomRepository.findByPatientId(notExistingPatientId)).thenReturn(Optional.empty());
            
            // when & then
            assertThrows(NoSuchElementException.class, () -> callBellService.findChatRoomByPatientId(notExistingPatientId));
            
            verify(chatRoomRepository, times(1)).findByPatientId(notExistingPatientId);
            verify(chatRoomMapper, never()).toDto(any());
        }
    }

    @Nested
    @DisplayName("createChatRoom 메소드 테스트")
    class CreateChatRoomTest {
        private String roomId;
        private ChatRoomDto expectedDto;
        private ChatRoom chatRoom;

        @BeforeEach
        void setUp() {
            roomId = defaultMedicalStaffId + "_" + defaultPatientId;
            
            expectedDto = new ChatRoomDto();
            expectedDto.setRoomId(roomId);
            expectedDto.setPatientId(defaultPatientId);
            expectedDto.setMedicalStaffId(defaultMedicalStaffId);
            
            chatRoom = new ChatRoom();
            chatRoom.setChatRoomId(roomId);
            chatRoom.setPatientId(defaultPatientId);
            chatRoom.setMedicalStaffId(defaultMedicalStaffId);
        }

        @Test
        @DisplayName("새로운 채팅방을 성공적으로 생성하는 경우")
        void createChatRoom_Success() {
            // given
            lenient().when(medicalStaffService.findAllByDepartment(defaultDepartment)).thenReturn(defaultMedicalStaff);
            lenient().when(chatRoomRepository.findByChatRoomId(roomId)).thenReturn(Optional.empty());
            lenient().when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(chatRoom);
            lenient().when(chatRoomMapper.toDto(any(ChatRoom.class))).thenReturn(expectedDto);
            
            // when
            ChatRoomDto result = callBellService.createChatRoom(defaultPatientId, defaultDepartment);
            
            // then
            assertNotNull(result);
            assertEquals(expectedDto.getRoomId(), result.getRoomId());
            assertEquals(expectedDto.getPatientId(), result.getPatientId());
            assertEquals(expectedDto.getMedicalStaffId(), result.getMedicalStaffId());
            
            verify(medicalStaffService, times(1)).findAllByDepartment(defaultDepartment);
            verify(chatRoomRepository, times(1)).findByChatRoomId(roomId);
            verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
            verify(chatRoomMapper, times(1)).toDto(any(ChatRoom.class));
        }
        
        @Test
        @DisplayName("필수 입력값이 null인 경우 IllegalArgumentException 발생")
        void createChatRoom_NullInputs() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> callBellService.createChatRoom(null, defaultDepartment));
            assertThrows(IllegalArgumentException.class, () -> callBellService.createChatRoom(defaultPatientId, null));
            
            verify(medicalStaffService, never()).findAllByDepartment(any());
            verify(chatRoomRepository, never()).findByChatRoomId(any());
            verify(chatRoomRepository, never()).save(any(ChatRoom.class));
        }
        
        @Test
        @DisplayName("이미 존재하는 채팅방인 경우 IllegalArgumentException 발생")
        void createChatRoom_AlreadyExists() {
            // given
            ChatRoom existingChatRoom = new ChatRoom();
            existingChatRoom.setChatRoomId(roomId);
            
            lenient().when(medicalStaffService.findAllByDepartment(defaultDepartment)).thenReturn(defaultMedicalStaff);
            lenient().when(chatRoomRepository.findByChatRoomId(roomId)).thenReturn(Optional.of(existingChatRoom));
            
            // when & then
            assertThrows(IllegalArgumentException.class, () -> callBellService.createChatRoom(defaultPatientId, defaultDepartment));
            
            verify(medicalStaffService, times(1)).findAllByDepartment(defaultDepartment);
            verify(chatRoomRepository, times(1)).findByChatRoomId(roomId);
            verify(chatRoomRepository, never()).save(any(ChatRoom.class));
        }
    }

    @Nested
    @DisplayName("updateRequestStatus 메소드 테스트")
    class UpdateRequestStatusTest {
        private Integer requestId;
        private Request request;

        @BeforeEach
        void setUp() {
            requestId = 1;
            
            request = new Request();
            request.setRequestId(requestId);
            request.setStatus(Request.RequestStatus.PENDING);
        }
        
        @Test
        @DisplayName("요청 상태를 성공적으로 업데이트하는 경우")
        void updateRequestStatus_Success() {
            // given
            String status = "COMPLETED";
            lenient().when(requestRepository.findByRequestId(requestId)).thenReturn(Optional.of(request));
            
            // when
            callBellService.updateRequestStatus(requestId, status);
            
            // then
            assertEquals(Request.RequestStatus.COMPLETED, request.getStatus());
            verify(requestRepository, times(1)).findByRequestId(requestId);
            verify(requestRepository, times(1)).save(request);
        }
        
        @Test
        @DisplayName("입력값이 null인 경우 IllegalArgumentException 발생")
        void updateRequestStatus_NullInputs() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> callBellService.updateRequestStatus(null, "COMPLETED"));
            assertThrows(IllegalArgumentException.class, () -> callBellService.updateRequestStatus(1, null));
            
            verify(requestRepository, never()).findByRequestId(any());
            verify(requestRepository, never()).save(any(Request.class));
        }
        
        @Test
        @DisplayName("요청이 존재하지 않는 경우 NoSuchElementException 발생")
        void updateRequestStatus_RequestNotFound() {
            // given
            Integer notExistingRequestId = 999;
            String status = "COMPLETED";
            lenient().when(requestRepository.findByRequestId(notExistingRequestId)).thenReturn(Optional.empty());
            
            // when & then
            assertThrows(NoSuchElementException.class, () -> callBellService.updateRequestStatus(notExistingRequestId, status));
            
            verify(requestRepository, times(1)).findByRequestId(notExistingRequestId);
            verify(requestRepository, never()).save(any(Request.class));
        }
        
        @Test
        @DisplayName("잘못된 상태값인 경우 IllegalArgumentException 발생")
        void updateRequestStatus_InvalidStatus() {
            // given
            String invalidStatus = "INVALID_STATUS";
            lenient().when(requestRepository.findByRequestId(requestId)).thenReturn(Optional.of(request));
            
            // when & then
            assertThrows(IllegalArgumentException.class, () -> callBellService.updateRequestStatus(requestId, invalidStatus));
            
            verify(requestRepository, times(1)).findByRequestId(requestId);
            verify(requestRepository, never()).save(any(Request.class));
        }
    }

    /**
     * 테스트용 Request 객체 생성 헬퍼 메소드
     * 
     * @param requestId 요청 ID
     * @param patientId 환자 ID
     * @param medicalStaffId 의료진 ID
     * @param status 요청 상태
     * @param content 요청 내용
     * @return 생성된 Request 객체
     */
    private Request createTestRequest(Integer requestId, Integer patientId, Integer medicalStaffId, 
                                      Request.RequestStatus status, String content) {
        Request request = new Request();
        request.setRequestId(requestId);
        request.setPatientId(patientId);
        request.setMedicalStaffId(medicalStaffId);
        request.setStatus(status);
        request.setRequestContent(content);
        request.setRequestTime(LocalDateTime.now());
        request.setType(Request.MessageType.REQUEST);
        return request;
    }
    
    /**
     * 테스트용 RequestDto 객체 생성 헬퍼 메소드
     * 
     * @param requestId 요청 ID
     * @param patientId 환자 ID
     * @param medicalStaffId 의료진 ID
     * @param status 요청 상태
     * @param content 요청 내용
     * @return 생성된 RequestDto 객체
     */
    private RequestDto createTestRequestDto(Integer requestId, Integer patientId, Integer medicalStaffId,
                                           String status, String content) {
        RequestDto dto = new RequestDto();
        dto.setRequestId(requestId);
        dto.setPatientId(patientId);
        dto.setMedicalStaffId(medicalStaffId);
        dto.setStatus(status);
        dto.setRequestContent(content);
        return dto;
    }

    @Nested
    @DisplayName("getAllRequests 메소드 테스트")
    class GetAllRequestsTest {
        private List<Request> requests;
        private Request request1;
        private Request request2;
        private RequestDto requestDto1;
        private RequestDto requestDto2;

        @BeforeEach
        void setUp() {
            // Request 객체 생성
            request1 = createTestRequest(1, defaultPatientId, defaultMedicalStaffId, 
                                        Request.RequestStatus.PENDING, "도움이 필요합니다");
            request2 = createTestRequest(2, defaultPatientId, defaultMedicalStaffId, 
                                        Request.RequestStatus.COMPLETED, "물이 필요합니다");
            
            // RequestDto 객체 생성
            requestDto1 = createTestRequestDto(1, defaultPatientId, defaultMedicalStaffId, 
                                              "PENDING", "도움이 필요합니다");
            requestDto2 = createTestRequestDto(2, defaultPatientId, defaultMedicalStaffId, 
                                              "COMPLETED", "물이 필요합니다");
                                              
            requests = new ArrayList<>();
            requests.add(request1);
            requests.add(request2);
        }

        @Test
        @DisplayName("의료진 ID로 모든 요청을 성공적으로 조회하는 경우")
        void getAllRequests_Success() {
            // given
            lenient().when(requestRepository.findByMedicalStaffId(defaultMedicalStaffId)).thenReturn(Optional.of(requests));
            lenient().when(requestMapper.toDto(request1)).thenReturn(requestDto1);
            lenient().when(requestMapper.toDto(request2)).thenReturn(requestDto2);
            
            // when
            List<RequestDto> result = callBellService.getAllRequests(defaultMedicalStaffId);
            
            // then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(requestDto1.getRequestId(), result.get(0).getRequestId());
            assertEquals(requestDto2.getRequestId(), result.get(1).getRequestId());
            
            verify(requestRepository, times(1)).findByMedicalStaffId(defaultMedicalStaffId);
            verify(requestMapper, times(1)).toDto(request1);
            verify(requestMapper, times(1)).toDto(request2);
        }
        
        @Test
        @DisplayName("의료진 ID가 null인 경우 IllegalArgumentException 발생")
        void getAllRequests_NullMedicalStaffId() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> callBellService.getAllRequests(null));
            
            verify(requestRepository, never()).findByMedicalStaffId(any());
            verify(requestMapper, never()).toDto(any(Request.class));
        }
        
        @Test
        @DisplayName("요청이 존재하지 않는 경우 NoSuchElementException 발생")
        void getAllRequests_RequestsNotFound() {
            // given
            Integer notExistingMedicalStaffId = 999;
            when(requestRepository.findByMedicalStaffId(notExistingMedicalStaffId)).thenReturn(Optional.empty());
            
            // when & then
            assertThrows(NoSuchElementException.class, () -> callBellService.getAllRequests(notExistingMedicalStaffId));
            
            verify(requestRepository, times(1)).findByMedicalStaffId(notExistingMedicalStaffId);
            verify(requestMapper, never()).toDto(any(Request.class));
        }
    }

    @Nested
    @DisplayName("getPatientRequests 메소드 테스트")
    class GetPatientRequestsTest {
        private List<Request> requests;
        private Request request1;
        private Request request2;
        private RequestDto requestDto1;
        private RequestDto requestDto2;

        @BeforeEach
        void setUp() {
            // Request 객체 생성
            request1 = createTestRequest(1, defaultPatientId, defaultMedicalStaffId, 
                                        Request.RequestStatus.PENDING, "도움이 필요합니다");
            request2 = createTestRequest(2, defaultPatientId, defaultMedicalStaffId, 
                                        Request.RequestStatus.COMPLETED, "물이 필요합니다");
            
            // RequestDto 객체 생성
            requestDto1 = createTestRequestDto(1, defaultPatientId, defaultMedicalStaffId, 
                                              "PENDING", "도움이 필요합니다");
            requestDto2 = createTestRequestDto(2, defaultPatientId, defaultMedicalStaffId, 
                                              "COMPLETED", "물이 필요합니다");
                                              
            requests = new ArrayList<>();
            requests.add(request1);
            requests.add(request2);
        }

        @Test
        @DisplayName("환자 ID로 모든 요청을 성공적으로 조회하는 경우")
        void getPatientRequests_Success() {
            // given
            lenient().when(requestRepository.findByPatientIdOrderByRequestTime(defaultPatientId)).thenReturn(Optional.of(requests));
            lenient().when(requestMapper.toDto(request1)).thenReturn(requestDto1);
            lenient().when(requestMapper.toDto(request2)).thenReturn(requestDto2);
            
            // when
            List<RequestDto> result = callBellService.getPatientRequests(defaultPatientId);
            
            // then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(requestDto1.getRequestId(), result.get(0).getRequestId());
            assertEquals(requestDto2.getRequestId(), result.get(1).getRequestId());
            
            verify(requestRepository, times(1)).findByPatientIdOrderByRequestTime(defaultPatientId);
            verify(requestMapper, times(1)).toDto(request1);
            verify(requestMapper, times(1)).toDto(request2);
        }
        
        @Test
        @DisplayName("환자 ID가 null인 경우 IllegalArgumentException 발생")
        void getPatientRequests_NullPatientId() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> callBellService.getPatientRequests(null));
            
            verify(requestRepository, never()).findByPatientIdOrderByRequestTime(any());
            verify(requestMapper, never()).toDto(any(Request.class));
        }
        
        @Test
        @DisplayName("요청이 존재하지 않는 경우 NoSuchElementException 발생")
        void getPatientRequests_RequestsNotFound() {
            // given
            Integer notExistingPatientId = 999;
            when(requestRepository.findByPatientIdOrderByRequestTime(notExistingPatientId)).thenReturn(Optional.empty());
            
            // when & then
            assertThrows(NoSuchElementException.class, () -> callBellService.getPatientRequests(notExistingPatientId));
            
            verify(requestRepository, times(1)).findByPatientIdOrderByRequestTime(notExistingPatientId);
            verify(requestMapper, never()).toDto(any(Request.class));
        }
    }

    @Nested
    @DisplayName("deleteRequest 메소드 테스트")
    class DeleteRequestTest {
        private Integer requestId;

        @BeforeEach
        void setUp() {
            requestId = 1;
        }
        
        @Test
        @DisplayName("요청을 성공적으로 삭제하는 경우")
        void deleteRequest_Success() {
            // given
            lenient().when(requestRepository.existsById(requestId)).thenReturn(true);
            doNothing().when(requestRepository).deleteByRequestId(requestId);
            
            // when
            callBellService.deleteRequest(requestId);
            
            // then
            verify(requestRepository, times(1)).existsById(requestId);
            verify(requestRepository, times(1)).deleteByRequestId(requestId);
        }
        
        @Test
        @DisplayName("요청 ID가 null인 경우 IllegalArgumentException 발생")
        void deleteRequest_NullRequestId() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> callBellService.deleteRequest(null));
            
            verify(requestRepository, never()).existsById(any());
            verify(requestRepository, never()).deleteByRequestId(any());
        }
        
        @Test
        @DisplayName("요청이 존재하지 않는 경우 NoSuchElementException 발생")
        void deleteRequest_RequestNotFound() {
            // given
            Integer notExistingRequestId = 999;
            when(requestRepository.existsById(notExistingRequestId)).thenReturn(false);
            
            // when & then
            assertThrows(NoSuchElementException.class, () -> callBellService.deleteRequest(notExistingRequestId));
            
            verify(requestRepository, times(1)).existsById(notExistingRequestId);
            verify(requestRepository, never()).deleteByRequestId(any());
        }
    }

    @Nested
    @DisplayName("updateRequestAcceptTime 메소드 테스트")
    class UpdateRequestAcceptTimeTest {
        private Integer requestId;
        private Request request;
        private String acceptTimeISOString;

        @BeforeEach
        void setUp() {
            requestId = 1;
            request = new Request();
            request.setRequestId(requestId);
            
            // ISO 8601 형식의 시간 문자열 생성
            acceptTimeISOString = Instant.now().toString();
        }
        
        // 공통 검증 메소드
        private void verifyCommonInteractions(int findTimes, int saveTimes) {
            verify(requestRepository, times(findTimes)).findByRequestId(requestId);
            verify(requestRepository, times(saveTimes)).save(any(Request.class));
        }

        @Test
        @DisplayName("요청 수락 시간을 성공적으로 업데이트하는 경우")
        void updateRequestAcceptTime_Success() {
            // given
            when(requestRepository.findByRequestId(requestId)).thenReturn(Optional.of(request));
            when(requestRepository.save(any(Request.class))).thenReturn(request);
            
            // when
            callBellService.updateRequestAcceptTime(requestId, acceptTimeISOString);
            
            // then
            assertNotNull(request.getAcceptTime());
            verifyCommonInteractions(1, 1);
        }

        @Test
        @DisplayName("입력값이 null인 경우 IllegalArgumentException 발생")
        void updateRequestAcceptTime_NullInputs() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> callBellService.updateRequestAcceptTime(null, acceptTimeISOString));
            assertThrows(IllegalArgumentException.class, () -> callBellService.updateRequestAcceptTime(requestId, null));
            
            verifyCommonInteractions(0, 0);
        }

        @Test
        @DisplayName("요청이 존재하지 않는 경우 NoSuchElementException 발생")
        void updateRequestAcceptTime_RequestNotFound() {
            // given
            when(requestRepository.findByRequestId(requestId)).thenReturn(Optional.empty());
            
            // when & then
            assertThrows(NoSuchElementException.class, () -> callBellService.updateRequestAcceptTime(requestId, acceptTimeISOString));
            
            verifyCommonInteractions(1, 0);
        }

        @Test
        @DisplayName("잘못된 시간 형식인 경우 IllegalArgumentException 발생")
        void updateRequestAcceptTime_InvalidTimeFormat() {
            // given
            String invalidTimeFormat = "not-a-valid-time-format";
            when(requestRepository.findByRequestId(requestId)).thenReturn(Optional.of(request));
            
            // when & then
            assertThrows(IllegalArgumentException.class, () -> callBellService.updateRequestAcceptTime(requestId, invalidTimeFormat));
            
            verifyCommonInteractions(1, 0);
        }
    }
} 