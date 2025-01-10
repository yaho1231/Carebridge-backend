package com.example.carebridge.config;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsConfig {
    @Bean
    public DefaultMessageService messageService(
            @Value("${message.api-key}") String apiKey,
            @Value("${message.api-secret}") String apiSecret
    ) {
        DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
        System.out.println("api key : " + apiKey);
        return messageService;
    }
}
