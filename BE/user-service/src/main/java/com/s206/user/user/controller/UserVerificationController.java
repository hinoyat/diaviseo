package com.s206.user.user.controller;

import com.s206.common.dto.ResponseDto;
import com.s206.user.user.dto.request.PhoneCodeConfirmRequest;
import com.s206.user.user.dto.request.PhoneVerificationRequest;
import com.s206.user.user.service.UserVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/verify")
@RequiredArgsConstructor
@Slf4j
public class UserVerificationController {

    private final UserVerificationService userVerificationService;

    @PostMapping("/phone")
    public ResponseEntity<ResponseDto<Void>> verifyPhone(@RequestBody PhoneVerificationRequest request) {
        log.info("인증번호 요청 - phone: {}", request.getPhone());
        userVerificationService.sendVerificationCode(request.getPhone());
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "인증번호가 발송되었습니다."));
    }

//    @PostMapping("/phone")
//    public ResponseEntity<ResponseDto<Void>> verifyPhone(@RequestBody PhoneVerificationRequest request) {
//        log.info("인증번호 요청 - phone: {}", request.getPhone());
//        String testCode = userVerificationService.sendVerificationCode(request.getPhone());
//        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "CoolSMS 발송 대신 인증 번호 드립니다" + testCode));
//    }

    @PostMapping("/phone/confirm")
    public ResponseEntity<ResponseDto<Void>> confirmPhoneCode(@RequestBody PhoneCodeConfirmRequest request) {
        log.info("인증번호 검증 요청 - phone: {}, code: {}", request.getPhone(), request.getCode());
        userVerificationService.verifyCode(request.getPhone(), request.getCode());
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "인증 성공"));
    }

}
