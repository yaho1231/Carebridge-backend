package com.example.carebridge.dto;

import lombok.*;

import java.util.List;

/**
 * 모델에 대한 요청 객체를 관리 : gpt-4, gpt-4 turbo, gpt-3.5-turbo
 * prompt 에 넣어서 보낼 때
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatCompletionDto {

    // 사용할 모델
    private String model;

    private List<ChatRequestMsgDto> messages;
}