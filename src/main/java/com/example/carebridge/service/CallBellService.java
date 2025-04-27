package com.example.carebridge.service;

import com.example.carebridge.dto.ChatRoomDto;
import com.example.carebridge.dto.RequestDto;
import com.example.carebridge.entity.ChatRoom;
import com.example.carebridge.entity.Message;
import com.example.carebridge.entity.Request;
import com.example.carebridge.mapper.ChatRoomMapper;
import com.example.carebridge.mapper.RequestMapper;
import com.example.carebridge.repository.ChatRoomRepository;
import com.example.carebridge.repository.RequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 콜벨 서비스
 * 채팅방 및 요청 관리와 관련된 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Slf4j
@Service
public class CallBellService {

    private final ChatRoomRepository chatRoomRepository;
    private final RequestRepository requestRepository;
    private final ChatRoomMapper chatRoomMapper;
    private final RequestMapper requestMapper;
    private final MedicalStaffService medicalStaffService;

    @Autowired
    public CallBellService(
            ChatRoomRepository chatRoomRepository,
            RequestRepository requestRepository,
            ChatRoomMapper chatRoomMapper,
            RequestMapper requestMapper,
            MedicalStaffService medicalStaffService) {
        this.chatRoomRepository = chatRoomRepository;
        this.requestRepository = requestRepository;
        this.chatRoomMapper = chatRoomMapper;
        this.requestMapper = requestMapper;
        this.medicalStaffService = medicalStaffService;
    }

    /**
     * 새로운 요청을 생성합니다.
     */
    @Transactional
    public Request createRequestByMessage(Message message) {
        Request request = new Request();
        request.setPatientId(message.getPatientId());
        request.setRequestContent(message.getMessageContent());
        request.setStatus(Request.RequestStatus.PENDING);
        request.setRequestTime(LocalDateTime.now());
        request.setMedicalStaffId(message.getMedicalStaffId());
        request.setType(Request.MessageType.REQUEST);
        request = requestRepository.save(request);
        return request;
    }

    /**
     * 특정 환자의 ID로 채팅방을 조회합니다.
     *
     * @param patientId 환자의 ID
     * @return 채팅방 DTO
     * @throws IllegalArgumentException 환자 ID가 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public ChatRoomDto findChatRoomByPatientId(Integer patientId) {
        log.debug("환자 ID {}의 채팅방 조회 시도", patientId);
        try {
            if (patientId == null) {
                log.error("환자 ID가 null입니다.");
                throw new IllegalArgumentException("환자 ID는 null일 수 없습니다.");
            }
            ChatRoom chatRoom = chatRoomRepository.findByPatientId(patientId)
                    .orElseThrow(() -> {
                        log.error("환자 ID {}에 해당하는 채팅방을 찾을 수 없습니다.", patientId);
                        return new NoSuchElementException("해당 환자의 채팅방이 존재하지 않습니다.");
                    });
            log.info("환자 ID {}에 대한 채팅방 조회 성공", patientId);
            return chatRoomMapper.toDto(chatRoom);
        } catch (IllegalArgumentException e) {
            log.error("채팅방 조회 실패 - 환자 ID: {}, 오류: {}", patientId, e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("채팅방 조회 실패 - 환자 ID: {}, 오류: {}", patientId, e.getMessage());
            throw new NoSuchElementException("해당 환자의 채팅방이 존재하지 않습니다.");
        }
    }

    /**
     * 새로운 채팅방을 생성합니다.
     *
     * @param patientId 환자의 ID
     * @param department 의료진 소속 분과
     * @return 생성된 채팅방 DTO
     * @throws IllegalArgumentException 잘못된 환자 ID 또는 부서 정보인 경우
     */
    @Transactional
    public ChatRoomDto createChatRoom(Integer patientId, String department) {
        log.debug("새로운 채팅방 생성 시도 - 환자 ID: {}, 부서: {}", patientId, department);
        try {
            if (patientId == null || department == null) {
                log.error("필수 입력값이 누락되었습니다 - 환자 ID: {}, 부서: {}", patientId, department);
                throw new IllegalArgumentException("환자 ID와 부서는 null이거나 비어 있을 수 없습니다.");
            }
            Integer medicalStaffId = medicalStaffService.findAllByDepartment(department).getMedicalStaffId();
            String roomId = medicalStaffId.toString() + "_" + patientId.toString();
            // 기존 채팅방 존재 여부 확인
            if (chatRoomRepository.findByChatRoomId(roomId).isPresent()) {
                log.error("이미 존재하는 채팅방입니다 - 방 ID: {}", roomId);
                throw new IllegalArgumentException("해당 채팅방이 이미 존재합니다.");
            }

            ChatRoomDto chatRoomDto = new ChatRoomDto();
            chatRoomDto.setRoomId(roomId);
            chatRoomDto.setPatientId(patientId);
            chatRoomDto.setMedicalStaffId(medicalStaffId);
            ChatRoom chatRoom = chatRoomMapper.toEntity(chatRoomDto);
            chatRoomRepository.save(chatRoom);

            log.info("채팅방 생성 완료 - 방 ID: {}", roomId);
            return chatRoomDto;
        } catch (IllegalArgumentException e) {
            log.error("채팅방 생성 실패 - 환자 ID: {}, 부서: {}, 오류: {}", patientId, department, e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("채팅방 생성 실패 - 환자 ID: {}, 부서: {}, 오류: {}", patientId, department, e.getMessage());
            throw new NoSuchElementException("해당 데이터를 찾을 수 없습니다.");
        }
    }

    /**
     * 요청 상태를 업데이트합니다.
     *
     * @param requestId 요청 ID
     * @param status 새로운 상태
     * @throws IllegalArgumentException 요청 ID가 존재하지 않거나 잘못된 상태값인 경우
     */
    @Transactional
    public void updateRequestStatus(Integer requestId, String status) {
        log.debug("요청 상태 업데이트 시도 - 요청 ID: {}, 새로운 상태: {}", requestId, status);
        try {
            if (requestId == null || status == null) {
                log.error("필수 입력값이 누락되었습니다 - 요청 ID: {}, 상태: {}", requestId, status);
                throw new IllegalArgumentException("요청 ID와 상태는 null이거나 비어 있을 수 없습니다.");
            }
            Request request = requestRepository.findByRequestId(requestId)
                    .orElseThrow(() -> {
                        log.error("요청 ID {}에 해당하는 요청을 찾을 수 없습니다.", requestId);
                        return new NoSuchElementException("해당 요청이 존재하지 않습니다.");
                    });

            request.setStatus(Request.RequestStatus.valueOf(status.toUpperCase()));
            requestRepository.save(request);

            log.info("요청 상태 업데이트 완료 - 요청 ID: {}, 상태: {}", requestId, status);
        } catch (IllegalArgumentException e) {
            log.error("요청 상태 업데이트 실패 - 요청 ID: {}, 상태: {}, 오류: {}", requestId, status, e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("요청 상태 업데이트 실패 - 요청 ID: {}, 오류: {}", requestId, e.getMessage());
            throw new NoSuchElementException("해당 요청이 존재하지 않습니다.");
        }
    }

    /**
     * 특정 의료진의 모든 요청을 조회합니다.
     *
     * @param medicalStaffId 의료진 ID
     * @return 요청 DTO 리스트
     * @throws IllegalArgumentException 의료진 ID가 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public List<RequestDto> getAllRequests(Integer medicalStaffId) {
        log.debug("의료진 ID {}의 모든 요청 조회 시도", medicalStaffId);
        try {
            if (medicalStaffId == null) {
                log.error("의료진 ID가 null입니다.");
                throw new IllegalArgumentException("의료진 ID는 null일 수 없습니다.");
            }
            List<Request> requests = requestRepository.findByMedicalStaffId(medicalStaffId)
                    .orElseThrow(() -> {
                        log.error("의료진 ID {}에 해당하는 요청이 존재하지 않습니다.", medicalStaffId);
                        return new NoSuchElementException("요청이 존재하지 않습니다.");
                    });
            List<RequestDto> requestDtoList = new ArrayList<>();
            for (Request request : requests) {
                requestDtoList.add(requestMapper.toDto(request));
            }
            log.info("의료진 ID {}의 요청 조회 완료 - 총 {}건", medicalStaffId, requestDtoList.size());
            return requestDtoList;
        } catch (IllegalArgumentException e) {
            log.error("모든 요청 조회 실패 - 의료진 ID: {}, 오류: {}", medicalStaffId, e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("모든 요청 조회 실패 - 의료진 ID: {}, 오류: {}", medicalStaffId, e.getMessage());
            throw new NoSuchElementException("요청이 존재하지 않습니다.");
        }
    }

    /**
     * 특정 환자의 모든 요청을 조회합니다.
     *
     * @param patientId 환자 ID
     * @return 요청 DTO 리스트
     * @throws IllegalArgumentException 환자 ID가 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public List<RequestDto> getPatientRequests(Integer patientId) {
        log.debug("환자 ID {}의 모든 요청 조회 시도", patientId);
        try {
            if (patientId == null) {
                log.error("환자 ID가 null입니다.");
                throw new IllegalArgumentException("환자 ID는 null일 수 없습니다.");
            }
            List<Request> requests = requestRepository.findByPatientIdOrderByRequestTime(patientId)
                    .orElseThrow(() -> {
                        log.error("환자 ID {}에 해당하는 요청이 존재하지 않습니다.", patientId);
                        return new NoSuchElementException("요청이 존재하지 않습니다.");
                    });
            List<RequestDto> requestDtoList = new ArrayList<>();
            for (Request request : requests) {
                requestDtoList.add(requestMapper.toDto(request));
            }
            log.info("환자 ID {}의 요청 조회 완료 - 총 {}건", patientId, requestDtoList.size());
            return requestDtoList;
        } catch (IllegalArgumentException e) {
            log.error("환자 요청 조회 실패 - 환자 ID: {}, 오류: {}", patientId, e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("환자 요청 조회 실패 - 환자 ID: {}, 오류: {}", patientId, e.getMessage());
            throw new NoSuchElementException("요청이 존재하지 않습니다.");
        }
    }

    /**
     * 특정 요청을 삭제합니다.
     *
     * @param requestId 요청 ID
     * @throws IllegalArgumentException 요청 ID가 존재하지 않는 경우
     */
    @Transactional
    public void deleteRequest(Integer requestId) {
        log.debug("요청 ID {}의 삭제 시도", requestId);
        try {
            if (requestId == null) {
                log.error("요청 ID가 null입니다.");
                throw new IllegalArgumentException("요청 ID는 null일 수 없습니다.");
            }
            if (!requestRepository.existsById(requestId)) {
                log.error("요청 ID {}에 해당하는 요청을 찾을 수 없습니다.", requestId);
                throw new NoSuchElementException("해당 요청이 존재하지 않습니다.");
            }
            requestRepository.deleteByRequestId(requestId);
            log.info("요청 ID {} 삭제 완료", requestId);
        } catch (IllegalArgumentException e) {
            log.error("요청 삭제 실패 - 요청 ID: {}, 오류: {}", requestId, e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("요청 삭제 실패 - 요청 ID: {}, 오류: {}", requestId, e.getMessage());
            throw new NoSuchElementException("해당 요청이 존재하지 않습니다.");
        }
    }

    /**
     * 요청 수락 시간을 업데이트합니다.
     *
     * @param requestId 요청 ID
     * @param acceptTime 수락 시간
     */
    @Transactional
    public void updateRequestAcceptTime(Integer requestId, String acceptTime) {
        log.debug("요청 수락 시간 업데이트 시도 - 요청 ID: {}, 수락 시간: {}", requestId, acceptTime);
        try {
            if (requestId == null || acceptTime == null) {
                log.error("요청 ID 또는 수락 시간이 null입니다. 요청 ID: {}, 수락 시간: {}", requestId, acceptTime);
                throw new IllegalArgumentException("요청 ID와 수락 시간은 null일 수 없습니다.");
            }
            Request request = requestRepository.findByRequestId(requestId)
                    .orElseThrow(() -> {
                        log.error("요청 ID {}에 해당하는 요청을 찾을 수 없습니다.", requestId);
                        return new NoSuchElementException("해당 요청이 존재하지 않습니다.");
                    });
            LocalDateTime time;
            try {
                time = LocalDateTime.ofInstant(
                        Instant.parse(acceptTime),
                        ZoneId.systemDefault()
                );
            } catch (Exception e) {
                log.error("수락 시간 파싱 실패 - 수락 시간: {}, 오류: {}", acceptTime, e.getMessage());
                throw new IllegalArgumentException("잘못된 시간 형식입니다: " + e.getMessage());
            }
            request.setAcceptTime(time);
            requestRepository.save(request);
            log.info("요청 수락 시간 업데이트 완료 - 요청 ID: {}, 수락 시간: {}", requestId, acceptTime);
        } catch (IllegalArgumentException e) {
            log.error("요청 수락 시간 업데이트 실패 - 요청 ID: {}, 수락 시간: {}, 오류: {}", requestId, acceptTime, e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("요청 수락 시간 업데이트 실패 - 요청 ID: {}, 오류: {}", requestId, e.getMessage());
            throw new NoSuchElementException("해당 요청이 존재하지 않습니다.");
        }
    }
}