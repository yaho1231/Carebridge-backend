package com.example.carebridge.service;

import com.example.carebridge.config.ChatGPTConfig;
import com.example.carebridge.dto.ChatCompletionDto;
import com.example.carebridge.dto.ChatRequestMsgDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * ChatGPTService 클래스에 대한 단위 테스트
 * ChatGPT API 통신 서비스의 주요 기능을 검증합니다.
 */
@Tag("service")
@DisplayName("ChatGPT 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class ChatGPTServiceTest {

    @Mock
    private ChatGPTConfig chatGPTConfig;

    @Mock
    private RestTemplate restTemplate;

    private HttpHeaders realHttpHeaders;

    @InjectMocks
    private ChatGPTServiceImpl chatGPTService;

    private ChatCompletionDto validChatCompletionDto;
    private static final String TEST_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MOCK_API_KEY = "mock-api-key";
    private static final String TEST_USER_MESSAGE_CONTENT = "테스트 메시지입니다.";
    private static final String MOCK_SUCCESS_RESPONSE_ID = "chatcmpl-123";
    private static final String MOCK_ASSISTANT_RESPONSE_CONTENT = "안녕하세요! 무엇을 도와드릴까요?";
    private static final String MOCK_SUCCESS_JSON_RESPONSE = 
        "{\"id\":\"" + MOCK_SUCCESS_RESPONSE_ID + "\",\"object\":\"chat.completion\",\"choices\":[{\"message\":{\"role\":\"assistant\",\"content\":\"" + MOCK_ASSISTANT_RESPONSE_CONTENT + "\"},\"index\":0}]}}";
    private static final String INVALID_JSON_RESPONSE = "Invalid JSON";
    private static final String REST_CALL_ERROR_MESSAGE = "REST 호출 오류";

    @BeforeEach
    void setUp() {
        realHttpHeaders = new HttpHeaders();
        realHttpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + MOCK_API_KEY);
        realHttpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");

        ReflectionTestUtils.setField(chatGPTService, "promptUrl", TEST_URL);
        validChatCompletionDto = createValidChatCompletionDto();
    }

    private ChatRequestMsgDto createTestChatRequestMsgDto(String role, String content) {
        ChatRequestMsgDto chatRequestMsgDto = new ChatRequestMsgDto();
        chatRequestMsgDto.setRole(role);
        chatRequestMsgDto.setContent(content);
        return chatRequestMsgDto;
    }

    private ChatCompletionDto createValidChatCompletionDto() {
        ChatCompletionDto dto = new ChatCompletionDto();
        dto.setModel("gpt-3.5-turbo");
        List<ChatRequestMsgDto> messages = new ArrayList<>();
        messages.add(createTestChatRequestMsgDto("user", TEST_USER_MESSAGE_CONTENT));
        dto.setMessages(messages);
        return dto;
    }

    @Nested
    @DisplayName("ChatGPT 서비스 기본 테스트")
    class ChatGPTServiceBasicTests {
        
        @Test
        @DisplayName("ChatGPT 서비스가 초기화되었는지 확인")
        void testServiceInitialized() {
            // 단순한 테스트: 서비스가 null이 아닌지 확인
            assertNotNull(chatGPTService);
            assertNotNull(ReflectionTestUtils.getField(chatGPTService, "promptUrl"));
        }
        
        @Test
        @DisplayName("ChatGPTConfig 의존성 주입 확인")
        void testConfigDependencyInjected() {
            // ChatGPTConfig 의존성이 주입되었는지 확인
            ChatGPTConfig config = (ChatGPTConfig) ReflectionTestUtils.getField(chatGPTService, "chatGPTConfig");
            assertNotNull(config);
            assertEquals(chatGPTConfig, config);
        }
    }

    @Nested
    @DisplayName("DTO 유효성 검증 테스트")
    class DtoValidationTests {
        @Test
        @DisplayName("ChatCompletionDto의 필수값 확인 테스트")
        void validChatCompletionDto_ContainsRequiredFields() {
            assertNotNull(validChatCompletionDto.getModel(), "모델은 null이 아니어야 합니다.");
            assertNotNull(validChatCompletionDto.getMessages(), "메시지 목록은 null이 아니어야 합니다.");
            assertFalse(validChatCompletionDto.getMessages().isEmpty(), "메시지 목록은 비어있지 않아야 합니다.");
            
            ChatRequestMsgDto firstMessage = validChatCompletionDto.getMessages().get(0);
            assertNotNull(firstMessage.getRole(), "첫 번째 메시지의 역할은 null이 아니어야 합니다.");
            assertEquals("user", firstMessage.getRole(), "첫 번째 메시지의 역할은 'user'여야 합니다.");
            assertNotNull(firstMessage.getContent(), "첫 번째 메시지의 내용은 null이 아니어야 합니다.");
            assertEquals(TEST_USER_MESSAGE_CONTENT, firstMessage.getContent(), "첫 번째 메시지의 내용이 일치해야 합니다.");
        }
    }
    
    @Nested
    @DisplayName("prompt 메소드 테스트")
    class PromptMethodTest {

        @Test
        @DisplayName("API 호출 성공 시 응답 반환 테스트")
        void prompt_Success() {
            // given
            ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(MOCK_SUCCESS_JSON_RESPONSE, HttpStatus.OK);
            
            when(chatGPTConfig.httpHeaders()).thenReturn(realHttpHeaders);
            when(chatGPTConfig.restTemplate()).thenReturn(restTemplate);
            when(restTemplate.exchange(
                    eq(TEST_URL), 
                    eq(HttpMethod.POST), 
                    any(HttpEntity.class), 
                    eq(String.class)
            )).thenReturn(mockResponseEntity);

            // when
            Map<String, Object> result = chatGPTService.prompt(validChatCompletionDto);

            // then
            assertNotNull(result, "결과는 null이 아니어야 합니다.");
            assertEquals(MOCK_SUCCESS_RESPONSE_ID, result.get("id"), "응답 ID가 일치해야 합니다.");
            
            Object choicesObject = result.get("choices");
            assertNotNull(choicesObject, "Choices 객체는 null이 아니어야 합니다.");
            assertTrue(choicesObject instanceof List, "Choices는 리스트 형태여야 합니다.");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) choicesObject;
            assertFalse(choices.isEmpty(), "Choices 리스트는 비어있지 않아야 합니다.");
            
            Map<String, Object> firstChoice = choices.get(0);
            assertNotNull(firstChoice, "첫 번째 choice는 null이 아니어야 합니다.");
            
            Object messageObject = firstChoice.get("message");
            assertNotNull(messageObject, "Message 객체는 null이 아니어야 합니다.");
            assertTrue(messageObject instanceof Map, "Message는 Map 형태여야 합니다.");
            @SuppressWarnings("unchecked")
            Map<String, String> message = (Map<String, String>) messageObject;
            
            assertEquals("assistant", message.get("role"), "메시지 역할이 'assistant'여야 합니다.");
            assertEquals(MOCK_ASSISTANT_RESPONSE_CONTENT, message.get("content"), "메시지 내용이 일치해야 합니다.");
            
            verify(chatGPTConfig, times(1)).httpHeaders();
            verify(chatGPTConfig, times(1)).restTemplate();
        }
        
        @Test
        @DisplayName("JSON 파싱 예외 발생 시 빈 Map 반환 테스트")
        void prompt_JsonProcessingException_ReturnsEmptyMap() {
            // given
            ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(INVALID_JSON_RESPONSE, HttpStatus.OK);
            
            when(chatGPTConfig.httpHeaders()).thenReturn(realHttpHeaders);
            when(chatGPTConfig.restTemplate()).thenReturn(restTemplate);
            when(restTemplate.exchange(
                    eq(TEST_URL), 
                    eq(HttpMethod.POST), 
                    any(HttpEntity.class), 
                    eq(String.class)
            )).thenReturn(mockResponseEntity);

            // when
            Map<String, Object> result = chatGPTService.prompt(validChatCompletionDto);

            // then
            assertNotNull(result, "결과는 null이 아니어야 합니다.");
            assertTrue(result.isEmpty(), "JSON 파싱 예외 시 결과는 빈 Map이어야 합니다.");
            
            verify(chatGPTConfig, times(1)).httpHeaders();
            verify(chatGPTConfig, times(1)).restTemplate();
        }
        
        @Test
        @DisplayName("REST 호출 예외 발생 시 예외 발생 테스트")
        void prompt_RestTemplateException_ThrowsException() {
            // given
            when(chatGPTConfig.httpHeaders()).thenReturn(realHttpHeaders);
            when(chatGPTConfig.restTemplate()).thenReturn(restTemplate);
            when(restTemplate.exchange(
                    eq(TEST_URL), 
                    eq(HttpMethod.POST), 
                    any(HttpEntity.class), 
                    eq(String.class)
            )).thenThrow(new RuntimeException(REST_CALL_ERROR_MESSAGE));

            // when & then
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                chatGPTService.prompt(validChatCompletionDto);
            }, "REST 호출 오류가 발생해야 합니다.");
            assertEquals(REST_CALL_ERROR_MESSAGE, exception.getMessage(), "예외 메시지가 일치해야 합니다.");
            
            verify(chatGPTConfig, times(1)).httpHeaders();
            verify(chatGPTConfig, times(1)).restTemplate();
        }
    }
} 