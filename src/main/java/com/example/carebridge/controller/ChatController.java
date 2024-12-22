package com.example.carebridge.controller;
import com.example.carebridge.dto.ClassificationResponse;
import com.example.carebridge.service.ClassificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class ChatController {

    @Autowired
    private ClassificationService classificationService;

    @PostMapping("/classify-text")
    public ClassificationResponse classifyText(@RequestBody String message) {
        ClassificationResponse response = classificationService.classify(message);
        return response;
    }
}