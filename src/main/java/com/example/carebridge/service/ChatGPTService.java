package com.example.carebridge.service;

import com.example.carebridge.dto.ChatCompletionDto;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public interface ChatGPTService {
    Map<String, Object> prompt(ChatCompletionDto chatCompletionDto);
}