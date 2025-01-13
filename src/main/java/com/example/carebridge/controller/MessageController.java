package com.example.carebridge.controller;

import com.example.carebridge.entity.Message;
import com.example.carebridge.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/chat/message")
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * 모든 환자의 메시지 목록을 반환합니다.
     *
     * @return 환자 ID를 키로 하고 메시지 리스트를 값으로 가지는 맵과 HTTP 상태 코드
     */
    @GetMapping("/users")
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
    public ResponseEntity<String> getMessageTimestamp(@RequestParam Integer patientId, @RequestParam Integer messageId) {
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
    @PostMapping("/read")
    public ResponseEntity<Void> updateMessageReadStatus(@RequestParam Integer messageId) {
        try {
            // 메시지의 읽음 상태를 업데이트합니다.
            messageService.updateReadStatus(messageId);
            // HTTP 상태 코드 200(OK)을 반환합니다.
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            // 예외 발생 시, HTTP 상태 코드 500(내부 서버 오류)을 반환합니다.
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}