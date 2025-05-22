package com.s206.user.user.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SmsService {

    private DefaultMessageService messageService;

    @Value("${coolsms.api-key}")
    private String apiKey;

    @Value("${coolsms.api-secret}")
    private String apiSecret;

    @Value("${coolsms.from-number}")
    private String fromNumber;

    // Jakarta EE의 PostConstruct 사용
    @PostConstruct
    public void init() {
        log.info("CoolSMS 서비스 초기화 - API Key: {}, From: {}",
                apiKey.substring(0, 4) + "...", fromNumber);
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    public void sendSms(String to, String code) {
        Message message = new Message();
        message.setFrom(fromNumber);
        message.setTo(to);
        message.setText(String.format("[디아비서] 인증번호는 [%s] 입니다.", code));

        log.info("CoolSMS 메시지 전송 시도 - 수신자: {}, 인증번호: {}", to, code);

        try {
            SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));
            log.info("CoolSMS 응답: {}", response);
        } catch (Exception e) {
            log.error("CoolSMS 요청 실패: {}", e.getMessage(), e);
            throw new RuntimeException("SMS 전송에 실패했습니다.");
        }
    }
}