package com.example.carebridge.service;

import com.example.carebridge.dto.ChatRoomDto;
import com.example.carebridge.dto.RequestDto;
import com.example.carebridge.entity.ChatRoom;
import com.example.carebridge.entity.Request;
import com.example.carebridge.repository.ChatRoomRepository;
import com.example.carebridge.repository.MedicalStaffRepository;
import com.example.carebridge.repository.RequestRepository;
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
    private final RequestRepository requestRepository;
    private final MedicalStaffRepository medicalStaffRepository;
    private MedicalStaffService medicalStaffService;

    @Autowired
    public CallBellService(ChatRoomRepository chatRoomRepository, RequestRepository requestRepository, MedicalStaffRepository medicalStaffRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.requestRepository = requestRepository;
        this.medicalStaffRepository = medicalStaffRepository;
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
        String roomId = medicalStaffRepository.findAllByDepartment(department).getMedicalStaffId().toString() + "_" + patientId.toString();
        chatRoomDto.setRoomId(roomId);
        chatRoomDto.setPatientId(patientId);
        chatRoomDto.setMedicalStaffId(medicalStaffService.findAllByDepartment(department).getMedicalStaffId());

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setChatRoomId(chatRoomDto.getRoomId());
        chatRoom.setPatientId(chatRoomDto.getPatientId());
        chatRoom.setMedicalStaffId(chatRoomDto.getMedicalStaffId());
        chatRoomRepository.save(chatRoom);

        return chatRoomDto;
    }

    /**
     * 요청 상태를 업데이트합니다.
     *
     * @param requestId 요청 ID
     * @param status 새로운 상태
     */
    public void updateRequestStatus(Integer requestId, String status) {
        Request request = requestRepository.findByRequestId(requestId);
        switch (status) {
            case "pending":
                request.setStatus(Request.RequestStatus.valueOf("pending"));
                break;
            case "completed":
                request.setStatus(Request.RequestStatus.valueOf("completed"));
                break;
            case "in_progress":
                request.setStatus(Request.RequestStatus.valueOf("in_progress"));
                break;
            case "scheduled":
                request.setStatus(Request.RequestStatus.valueOf("scheduled"));
                break;
        }
        requestRepository.save(request);
    }

    /**
     * 특정 의료진의 모든 요청을 조회합니다.
     *
     * @param medicalStaffId 의료진 ID
     * @return 요청 DTO 리스트
     */
    public List<RequestDto> getAllRequests(Integer medicalStaffId) {
        List<Request> requests = requestRepository.findByMedicalStaffId(medicalStaffId);
        List<RequestDto> requestDtoList = new ArrayList<>();
        for (Request request : requests) {
            RequestDto requestDto = new RequestDto();
            requestDto.setRequestId(request.getRequestId());
            requestDto.setPatientId(request.getPatientId());
            requestDto.setRequestContent(request.getRequestContent());
            requestDto.setStatus(request.getStatus().toString());
            requestDto.setRequestTime(request.getRequestTime());
            requestDto.setAcceptTime(request.getAcceptTime());
            requestDtoList.add(requestDto);
        }
        return requestDtoList;
    }

    /**
     * 특정 환자의 모든 요청을 조회합니다.
     *
     * @param patientId 환자 ID
     * @return 요청 DTO 리스트
     */
    public List<RequestDto> getPatientRequests(Integer patientId) {
        List<Request> requests = requestRepository.findByPatientIdOrderByRequestTime(patientId);
        List<RequestDto> requestDtoList = new ArrayList<>();
        for (Request request : requests) {
            RequestDto requestDto = new RequestDto();
            requestDto.setRequestId(request.getRequestId());
            requestDto.setMedicalStaffId(request.getMedicalStaffId());
            requestDto.setRequestContent(request.getRequestContent());
            requestDto.setStatus(request.getStatus().toString());
            requestDto.setRequestTime(request.getRequestTime());
            requestDto.setAcceptTime(request.getAcceptTime());
            requestDtoList.add(requestDto);
        }
        return requestDtoList;
    }

    /**
     * 특정 요청을 삭제합니다.
     *
     * @param requestId 요청 ID
     */
    public void deleteRequest(Integer requestId) {
        requestRepository.deleteByRequestId(requestId);
    }
}