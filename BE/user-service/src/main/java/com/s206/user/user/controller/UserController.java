package com.s206.user.user.controller;

import com.s206.common.dto.ResponseDto;
import com.s206.user.user.dto.request.PhoneVerificationRequest;
import com.s206.user.user.dto.request.UserCreateRequest;
import com.s206.user.user.dto.request.UserUpdateRequest;
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

    @PostMapping("/signup")
    public ResponseEntity<ResponseDto<UserDetailResponse>> createUser(
            @RequestBody UserCreateRequest userCreateRequest
    ) {
        log.info("Creating user: {}", userCreateRequest.toString());
        UserDetailResponse userDetailResponse = userService.createUser(userCreateRequest);

        return ResponseEntity.ok(ResponseDto.success(HttpStatus.CREATED, "회원가입 성공", userDetailResponse));
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseDto<UserDetailResponse>> getUserByEmail(
            @RequestHeader("X-USER-ID") Integer userId) {
        log.info("Getting user by userId: {}", userId);
        UserDetailResponse user = userService.getUserByUserId(userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "내 정보 조회 성공", user));
    }

    // Auth-Service 통신 API
    @GetMapping("/exist")
    public ResponseEntity<ResponseDto<UserExistResponse>> checkUserExists(
            @RequestParam("email") String email,
            @RequestParam("provider") String provider) {

        UserExistResponse response = userService.existsByEmail(email, provider);
        log.info("exists: {}", response.toString());
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "회원 존재 조회 성공", response));
    }

    @PutMapping
    public ResponseEntity<ResponseDto<UserDetailResponse>> updateUser(
            @RequestHeader("X-USER-ID") Integer userId,
            @RequestBody UserUpdateRequest request
    ) {
        log.info("Updating user (userId={}): {}", userId, request.toString());
        UserDetailResponse updated = userService.updateUser(userId, request);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "회원정보 수정 성공", updated));
    }

    @DeleteMapping
    public ResponseEntity<ResponseDto<Void>> deleteUser(
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Deleting user with id: {}", userId);
        userService.deleteUser(userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "회원 탈퇴 처리 완료"));
    }

}
