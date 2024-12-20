package com.example.carebridge.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "messages") // MySQL의 messages 테이블과 매핑
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 증가 설정
    private Integer messageId; // 메시지 고유 ID

    @Column(name = "senderId", nullable = false) // 발신자 ID 컬럼과 매핑
    private Integer senderId; // 발신자 ID

    @Column(name = "receiverId", nullable = false) // 수신자 ID 컬럼과 매핑
    private Integer receiverId; // 수신자 ID

    @Column(name = "messageContent", nullable = false) // 메시지 내용 컬럼과 매핑
    private String messageContent; // 메시지 내용

    @Column(name = "timestamp", nullable = false) // 타임스탬프 컬럼과 매핑
    private String timestamp; // 메시지 발송 시간

    @Column(name = "readStatus", nullable = false) // 읽음 여부 컬럼과 매핑 (기본값 false)
    private Boolean readStatus; // 메시지 읽음 여부

    @Column(name = "conversationId") // 선택적 필드 (스레드 메시지 지원)
    private Integer conversationId; // 대화 ID

    // Getter와 Setter 메서드 정의
    public Integer getMessageId() { return messageId; }
    public void setMessageId(Integer messageId) { this.messageId = messageId; }

    public Integer getSenderId() { return senderId; }
    public void setSenderId(Integer senderId) { this.senderId = senderId; }

    public Integer getReceiverId() { return receiverId; }
    public void setReceiverId(Integer receiverId) { this.receiverId = receiverId; }

    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public Boolean getReadStatus() { return readStatus; }
    public void setReadStatus(Boolean readStatus) { this.readStatus = readStatus; }

    public Integer getConversationId() { return conversationId; }
    public void setConversationId(Integer conversationId) { this.conversationId = conversationId; }
}
