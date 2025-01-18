package com.example.carebridge.controller;

import net.nurigo.sdk.message.model.Balance;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sms")
public class SmsVerificationController {

    final DefaultMessageService smsMessageService;

    public SmsVerificationController(DefaultMessageService messageService) {
        this.smsMessageService = messageService;
    }

    /**
     * 단일 메시지 발송 예제
     */
//    @PostMapping("/verify-phone/{phone}")
////    public SingleMessageSentResponse sendOne(@PathVariable String phone) {
//    public VerifyResponseDto sendOne(@PathVariable String phone) {
//        Message message = new Message();
//
//        // Random Verify Number Generation
//        double randomValue = Math.random();
//        int intValue = (int) (randomValue * 899999 + 100000);
//
//        message.setFrom("01032330241");
//        message.setTo(phone);
//        message.setText("[CareBridge] 인증번호는 [ " + intValue + " ] 입니다");
//
//        SingleMessageSentResponse response = this.smsMessageService.sendOne(new SingleMessageSendingRequest(message));
//        System.out.println(response);
//
////        return response;
//        return new VerifyResponseDto(intValue, phone);
//    }

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
