package com.s206.user.user.controller;

import com.s206.common.dto.ResponseDto;
import com.s206.user.user.dto.request.UserCreateRequest;
import com.s206.user.user.dto.response.UserDetailResponse;
import com.s206.user.user.dto.response.UserExistResponse;
import com.s206.user.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResponseDto<UserDetailResponse>> createUser(
            @RequestBody UserCreateRequest userCreateRequest
    ) {
        UserDetailResponse userDetailResponse = userService.createUser(userCreateRequest);

        return ResponseEntity.ok(ResponseDto.success(HttpStatus.CREATED, "회원가입 성공", userDetailResponse));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ResponseDto<UserDetailResponse>> getUserByEmail(@PathVariable String email) {
        UserDetailResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "회원 조회 성공", user));
    }

    @GetMapping("/exist")
    public ResponseEntity<ResponseDto<UserExistResponse>> checkUserExists(
            @RequestParam("email") String email,
            @RequestParam("provider") String provider) {

        UserExistResponse response = userService.existsByEmail(email, provider);
        log.info("exists: {}", response.toString());
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "회원 존재 조회 성공", response));
    }
}
