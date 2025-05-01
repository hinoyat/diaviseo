package com.s206.user.user.controller;

import com.s206.common.dto.ResponseDto;
import com.s206.user.user.dto.request.PhoneVerificationRequest;
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

    @PostMapping("/phone")
    public ResponseEntity<ResponseDto<Void>> verifyPhone(@RequestBody PhoneVerificationRequest request) {
        log.info("[Stub] 인증번호 요청 - phone: {}", request.getPhone());

        // TODO: coolSMS 연동 시 구현 예정
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "인증 요청 처리 완료 (stub)"));
    }
}
