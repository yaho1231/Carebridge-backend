package com.example.carebridge.controller;

import net.nurigo.sdk.message.model.Balance;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sms-verify")
public class SmsVerificationController {

    final DefaultMessageService smsMessageService;

    public SmsVerificationController(DefaultMessageService messageService) {
        this.smsMessageService = messageService;
    }

    /**
     * 단일 메시지 발송 예제
     */
    @PostMapping("/send-one/{phone}")
    public SingleMessageSentResponse sendOne(@PathVariable String phone) {
        Message message = new Message();
        // 발신번호 및 수신번호는 반드시 01012345678 형태로 입력되어야 합니다.
        message.setFrom("01032330241");
        message.setTo(phone);
        message.setText("[CareBridge] Test Send !!! 인증번호는 [123456]입니다");

        SingleMessageSentResponse response = this.smsMessageService.sendOne(new SingleMessageSendingRequest(message));
        System.out.println(response);

        return response;
    }

    /**
     * 잔액 조회 예제
     */
    @GetMapping("/get-balance")
    public Balance getBalance() {
        Balance balance = this.smsMessageService.getBalance();
        System.out.println(balance);

        return balance;
    }
}
