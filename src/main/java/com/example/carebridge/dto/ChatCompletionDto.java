package com.example.carebridge.dto;

import lombok.*;

import java.util.List;

/**
 * 모델에 대한 요청 객체를 관리 : gpt-4, gpt-4 turbo, gpt-3.5-turbo
 * prompt에 넣어서 보낼 때
 */
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatCompletionDto {

    // 사용할 모델
    private String model;

    @Setter
    private List<ChatRequestMsgDto> messages;

    @Builder
    public ChatCompletionDto(String model, List<ChatRequestMsgDto> messages) {
        this.model = model;
        this.messages = messages;
    }

}