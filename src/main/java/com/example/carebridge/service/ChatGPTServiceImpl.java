package com.example.carebridge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.carebridge.config.ChatGPTConfig;
import com.example.carebridge.dto.ChatCompletionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * ChatGPT Service 구현체
 * ChatGPT API 와의 통신을 담당하는 서비스 클래스입니다.
 *
 * @author : lee
 * @since : 12/29/23
 */
@Slf4j
@Service
public class ChatGPTServiceImpl implements ChatGPTService {

    private final ChatGPTConfig chatGPTConfig;

    // ChatGPTConfig 를 주입받는 생성자
    public ChatGPTServiceImpl(ChatGPTConfig chatGPTConfig) {
        this.chatGPTConfig = chatGPTConfig;
    }

    @Value("${openai.url.prompt}")
    private String promptUrl; // 프롬프트 요청 URL

    /**
     * 신규 모델에 대한 프롬프트
     * ChatGPT API 에 프롬프트 요청을 보내고 응답을 처리합니다.
     *
     * @param chatCompletionDto 프롬프트 요청 데이터
     * @return 프롬프트 응답 데이터
     */
    @Override
    public Map<String, Object> prompt(ChatCompletionDto chatCompletionDto) {
        log.debug("[+] 신규 프롬프트를 수행합니다.");

        Map<String, Object> resultMap = new HashMap<>(); // 응답 데이터를 저장할 맵

        HttpHeaders headers = chatGPTConfig.httpHeaders(); // HTTP 헤더 설정

        HttpEntity<ChatCompletionDto> requestEntity = new HttpEntity<>(chatCompletionDto, headers); // 요청 엔티티 생성
        ResponseEntity<String> response = chatGPTConfig
                .restTemplate()
                .exchange(promptUrl, HttpMethod.POST, requestEntity, String.class); // API 요청 및 응답 받기
        try {
            ObjectMapper om = new ObjectMapper(); // JSON 파싱을 위한 ObjectMapper 생성
            resultMap = om.readValue(response.getBody(), new TypeReference<>() {}); // 응답 본문을 맵으로 변환
        } catch (JsonProcessingException e) {
            log.debug("JsonMappingException :: {}", e.getMessage()); // JSON 처리 예외 로그 출력
        } catch (RuntimeException e) {
            log.debug("RuntimeException :: {}", e.getMessage()); // 런타임 예외 로그 출력
        }
        return resultMap; // 결과 맵 반환
    }
}