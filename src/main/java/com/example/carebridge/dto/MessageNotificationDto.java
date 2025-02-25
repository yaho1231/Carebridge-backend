package com.example.carebridge.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageNotificationDto {

    private  Integer messageId;

    private MessageType messageType;

    public enum MessageType {
        MESSAGE,
        REQUEST,
        NOTIFICATION
    }
}
