package com.example.carebridge.dto;

import lombok.*;

/*
* ChatCompletionDto 의 messages
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestMsgDto {

    private String role;

    private String content;
}