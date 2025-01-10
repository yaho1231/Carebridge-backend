package com.example.carebridge.dto;

import lombok.*;

/*
* ChatCompletionDto의 messages
 */
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRequestMsgDto {

    private String role;

    @Setter
    private String content;

    @Builder
    public ChatRequestMsgDto(String role, String content) {
        this.role = role;
        this.content = content;
    }

}