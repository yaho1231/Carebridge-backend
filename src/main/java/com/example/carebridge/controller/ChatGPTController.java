package com.example.carebridge.controller;

import com.example.carebridge.dto.ChatCompletionDto;
//import com.example.carebridge.dto.ChatRequestMsgDto;
import com.example.carebridge.service.ChatGPTService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ChatGPT API
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/chatGpt")
public class ChatGPTController {

    private final ChatGPTService chatGPTService;

    public ChatGPTController(ChatGPTService chatGPTService) {
        this.chatGPTService = chatGPTService;
    }
    /**
     * [API] ChatGPT 프롬프트 명령어를 수행합니다. : gpt-4, gpt-4 turbo, gpt-3.5-turbo
     *
     * @param chatCompletionDto
     * @return
     */
    @PostMapping("/prompt")
    public ResponseEntity<Map<String, Object>> selectPrompt(@RequestBody ChatCompletionDto chatCompletionDto) {
        log.debug("param :: " + chatCompletionDto.toString());
        Map<String, Object> result = chatGPTService.prompt(chatCompletionDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    /**
     * [API] 프롬프트 내용을 기반으로 카테고리를 분류합니다.
     *
     * @param chatCompletionDto
     * @return
     */
    @PostMapping("/category")
    public ResponseEntity<Map<String, Object>> selectCategoryPrompt(@RequestBody ChatCompletionDto chatCompletionDto) {
        log.debug("param :: " + chatCompletionDto.toString());

        chatCompletionDto.getMessages().forEach(message -> {
            message.setContent("다음 메시지를 다음 범주 중 하나로 분류하라: {정보성 질문,긴급 요청,의료진 요청} 메시지 : " + message.getContent());
        });

        Map<String, Object> result = chatGPTService.prompt(chatCompletionDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 더미 데이터
    @PostMapping("/promptData")
    public ResponseEntity<Map<String, Object>> selectPromptData(@RequestBody ChatCompletionDto chatCompletionDto) {
        return null;
    }

    @PostMapping("/trim")
    public ResponseEntity<Map<String, Object>> selectTrim(@RequestBody ChatCompletionDto chatCompletionDto) {
        return null;
    }
}