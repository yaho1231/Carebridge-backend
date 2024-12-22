package com.example.carebridge.service;

import com.example.carebridge.dto.ClassificationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassificationService {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    public ClassificationResponse classify(String message) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + openAiApiKey);

        String requestBody = "{"
                + "\"model\": \"gpt-3.5-turbo\","
                + "\"messages\": [{\"role\": \"user\", \"content\": \"" + message + "\"}]"
                + "}";

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.openai.com/v1/chat/completions",
                HttpMethod.POST,
                entity,
                String.class
        );

        List<String> categories = extractCategoriesFromResponse(response.getBody());

        return new ClassificationResponse(categories);
    }

    private List<String> extractCategoriesFromResponse(String responseBody) {
        return List.of("진통제 복용", "물품 요청", "기타");
    }
}