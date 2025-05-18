package com.example.carebridge.controller;

import com.example.carebridge.dto.ChatMessageDto;
import com.example.carebridge.dto.MessageNotificationDto;
import com.example.carebridge.dto.MessageSummaryDto;
import com.example.carebridge.dto.RequestDto;
import com.example.carebridge.entity.Message;
import com.example.carebridge.entity.Request;
import com.example.carebridge.service.CallBellService;
import com.example.carebridge.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import javax.management.Notification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/chat/message")
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private final MessageService messageService;
    private final SimpMessageSendingOperations messagingTemplate;
    private final CallBellService callBellService;

    public MessageController(MessageService messageService, SimpMessageSendingOperations messagingTemplate, CallBellService callBellService) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
        this.callBellService = callBellService;
    }

    /**
     * 클라이언트로부터 채팅 메시지를 수신하여 처리하는 메서드입니다.
     *
     * @param message 수신된 채팅 메시지 객체
     */
    @MessageMapping("chat/message")
    public void message(ChatMessageDto message) {
        try {
            // 수신된 메시지를 로그에 기록합니다.
            logger.info("Received message: {}", message);

            // 메시지를 데이터베이스에 저장합니다.
            Message savedMessage = messageService.saveMessage(message);

            // 환자의 메세지를 의료진에게 전송합니다.
            if(savedMessage.getIsPatient())
                messagingTemplate.convertAndSend("/sub/user/chat/" + message.getMedicalStaffId(), savedMessage);
            // 의료진의 메세지를 환자에게 전송합니다.
            else
                messagingTemplate.convertAndSend("/sub/chat/room/" + message.getChatRoomId(), savedMessage);

            // 환자가 보낸 정보성 질문이라면 gpt를 통한 답변을 구독자들에게 전송합니다.
            if (savedMessage.getCategory().equals("정보성 질문") && message.getIsPatient()){
                Message chatGptMessage = messageService.chatGptMessage(message);
                messagingTemplate.convertAndSend("/sub/chat/room/" + message.getChatRoomId(), chatGptMessage); // 자동 답변 환자에게 전송
                messagingTemplate.convertAndSend("/sub/user/chat/" + message.getMedicalStaffId(), chatGptMessage); // 환자에게 보낸 자동 답변 의료진한테도 전송
            }
            // 환자가 보낸 의료진 도움요청이라면 Request를 생성합니다. 생성한 Request를 의료진에게 전송합니다.
            else if (savedMessage.getCategory().equals("의료진 도움요청") && message.getIsPatient()) {
                Request req = callBellService.createRequestByMessage(savedMessage);
                messagingTemplate.convertAndSend("/sub/user/chat/" + message.getMedicalStaffId(), req);
                logger.info("Request : "+req.getRequestContent());

                //요청 사항 생성 확인 메세지
                Message reqMessage = messageService.makeReqMessage(message);
                messagingTemplate.convertAndSend("/sub/chat/room/" + message.getChatRoomId(), reqMessage);
//                messagingTemplate.convertAndSend("/sub/user/chat/" + message.getMedicalStaffId(),reqMessage);
            }
        } catch (IllegalArgumentException e) {
            logger.error("잘못된 메시지 데이터: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("메시지 처리 중 오류 발생: {}", e.getMessage(), e);
        }

    }

//    //테스트용
//    @PostMapping
//    public ResponseEntity<Message> sendMessage(@RequestBody ChatMessageDto message) {
//        try {
//            // 수신된 메시지를 로그에 기록합니다.
//            logger.info("Received message: {}", message);
//
//            // 메시지를 데이터베이스에 저장합니다.
//            Message savedMessage = messageService.saveMessage(message);
//
//            // 환자의 메세지를 의료진에게 전송합니다.
//            if(savedMessage.getIsPatient())
//                messagingTemplate.convertAndSend("/sub/user/chat/" + message.getMedicalStaffId(), savedMessage);
//                // 의료진의 메세지를 환자에게 전송합니다.
//            else
//                messagingTemplate.convertAndSend("/sub/chat/room/" + message.getChatRoomId(), savedMessage);
//
//            // 환자가 보낸 정보성 질문이라면 gpt를 통한 답변을 구독자들에게 전송합니다.
//            if (savedMessage.getCategory().equals("정보성 질문") && message.getIsPatient()){
//                Message chatGptMessage = messageService.chatGptMessage(message);
//                messagingTemplate.convertAndSend("/sub/chat/room/" + message.getChatRoomId(), chatGptMessage);
//                logger.info(chatGptMessage.getMessageContent());
//                logger.info(message.getChatRoomId());
//            }
//            // 환자가 보낸 의료진 도움요청이라면 Request를 생성합니다. 생성한 Request를 의료진에게 전송합니다.
//            else if (savedMessage.getCategory().equals("의료진 도움요청") && message.getIsPatient()) {
//                Request req = callBellService.createRequestByMessage(savedMessage);
//                messagingTemplate.convertAndSend("/sub/user/chat/" + message.getMedicalStaffId(), req);
//                logger.info(req.getRequestContent());
//            }
//            return ResponseEntity.status(HttpStatus.CREATED).body(savedMessage);
//        } catch (IllegalArgumentException e) {
//            logger.error("잘못된 메시지 데이터: {}", e.getMessage(), e);
//        } catch (Exception e) {
//            logger.error("메시지 처리 중 오류 발생: {}", e.getMessage(), e);
//        }
//        return null;
//    }

    /**
     * 모든 환자의 메시지 목록을 반환합니다.
     *
     * @return 환자 ID를 키로 하고 메시지 리스트를 값으로 가지는 맵과 HTTP 상태 코드
     */
    @GetMapping("/users")
    @ResponseBody
    public ResponseEntity<Map<Integer, List<Message>>> getMessageList() {
        try {
            Map<Integer, List<Message>> messages = messageService.getAll();
            if (messages.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(messages, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching messages for all patients", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 특정 환자의 메시지 목록을 반환합니다.
     *
     * @param patientId 환자의 ID
     * @return 메시지 목록과 HTTP 상태 코드
     */
    @GetMapping("/user")
    @ResponseBody
    public ResponseEntity<List<Message>> getMessageList(@RequestParam Integer patientId) {
        try {
            List<Message> messages = messageService.getMessagesByPatientId(patientId);
            if (messages.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(messages, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching messages for patientId: {}", patientId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 특정 텍스트를 포함하는 메시지 목록을 반환합니다.
     *
     * @param patientId 환자의 ID
     * @param text 검색할 텍스트
     * @return 메시지 목록과 HTTP 상태 코드
     */
    @GetMapping("/containing")
    @ResponseBody
    public ResponseEntity<List<Message>> getMessagesContainingText(@RequestParam Integer patientId, @RequestParam String text) {
        try {
            List<Message> messages = messageService.getMessagesContainingText(patientId, text);
            if (messages.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(messages, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching messages containing text for patientId: {}", patientId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 특정 메시지의 읽음 상태를 반환합니다.
     *
     * @param patientId 환자의 ID
     * @param messageId 메시지의 ID
     * @return 읽음 상태와 HTTP 상태 코드
     */
    @GetMapping("/status")
    @ResponseBody
    public ResponseEntity<Boolean> getMessageReadStatus(@RequestParam Integer patientId, @RequestParam Integer messageId) {
        try {
            Boolean readStatus = messageService.getReadStatus(patientId, messageId);
            if (readStatus != null) {
                return new ResponseEntity<>(readStatus, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error fetching read status for patientId: {} and messageId: {}", patientId, messageId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 특정 메시지의 타임스탬프를 반환합니다.
     *
     * @param patientId 환자의 ID
     * @param messageId 메시지의 ID
     * @return 타임스탬프와 HTTP 상태 코드
     */
    @GetMapping("/timestamp")
    @ResponseBody
    public ResponseEntity<LocalDateTime> getMessageTimestamp(@RequestParam Integer patientId, @RequestParam Integer messageId) {
        try {
            List<Message> patientMessageList = messageService.getMessagesByPatientId(patientId);
            for (Message message : patientMessageList) {
                if (message.getMessageId().equals(messageId)) {
                    return new ResponseEntity<>(message.getTimestamp(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error fetching timestamp for patientId: {} and messageId: {}", patientId, messageId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 메시지의 읽음 상태를 업데이트합니다.
     *
     * @param messageId 메시지의 ID
     * @return HTTP 상태 코드
     */
    @PutMapping("/read")
    @ResponseBody
    public ResponseEntity<Void> updateMessageReadStatus(@RequestParam Integer messageId) {
        try {
            // 메시지의 읽음 상태를 업데이트합니다.
            messageService.updateReadStatus(messageId);

            MessageNotificationDto notificationDto = new MessageNotificationDto();
            notificationDto.setMessageId(messageId);
            notificationDto.setMessageType(MessageNotificationDto.MessageType.NOTIFICATION);
            Message message = messageService.getMessageById(messageId);

            if(message.getIsPatient())
                messagingTemplate.convertAndSend("/sub/chat/room/" + message.getChatRoomId(), notificationDto);
            else
                messagingTemplate.convertAndSend("/sub/user/chat/" + message.getMedicalStaffId(), notificationDto);

            // HTTP 상태 코드 200(OK)을 반환합니다.
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            // 예외 발생 시, HTTP 상태 코드 500(내부 서버 오류)을 반환합니다.
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 메인화면용 사람들 이름, 마지막 메시지, 시간 반환
     *
     * @return 메시지 요약 정보 리스트와 HTTP 상태 코드
     */
    @GetMapping("/main/{staff_id}")
    @ResponseBody
    public ResponseEntity<List<MessageSummaryDto>> getMessages(@PathVariable Integer staff_id) {
        try {
            // 메시지 요약 정보를 가져옵니다.
            List<MessageSummaryDto> messageSummaryList = messageService.getSummaryMessageInformation(staff_id);

            // 메시지 요약 정보가 비어있는지 확인합니다.
            if (messageSummaryList.isEmpty()) {
                // 비어있다면 HTTP 상태 코드 204 (No Content)를 반환합니다.
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            // 메시지 요약 정보를 포함한 HTTP 상태 코드 200 (OK)을 반환합니다.
            return new ResponseEntity<>(messageSummaryList, HttpStatus.OK);
        } catch (Exception e) {
            // 예외 발생 시, 오류 로그를 기록합니다.
            logger.error("Error fetching message summaries", e);

            // HTTP 상태 코드 500 (Internal Server Error)을 반환합니다.
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}