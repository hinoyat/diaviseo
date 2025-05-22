package com.s206.user.user.controller;

import com.s206.common.dto.ResponseDto;
import com.s206.user.user.dto.request.BodyCompositionRequest;
import com.s206.user.user.dto.request.FcmTokenRequest;
import com.s206.user.user.dto.request.UserCreateRequest;
import com.s206.user.user.dto.request.UserUpdateRequest;
import com.s206.user.user.dto.response.FcmTokenResponse;
import com.s206.user.user.dto.response.UserDetailResponse;
import com.s206.user.user.dto.response.UserExistResponse;
import com.s206.user.user.dto.response.UserResponse;
import com.s206.user.user.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

		return ResponseEntity.ok(
				ResponseDto.success(HttpStatus.CREATED, "회원가입 성공", userDetailResponse));
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

	@GetMapping("/notifications-enabled")
	public ResponseEntity<ResponseDto<List<UserResponse>>> getNotificationsEnabled() {
		List<UserResponse> enabledUsers = userService.findNotificationEnabled();
		return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "알림 허용된 유저 조회 성공",
				enabledUsers));
	}

	@GetMapping("/{userId}/fcm-token")
	public ResponseEntity<FcmTokenResponse> getFcmToken(@PathVariable Integer userId) {
		String fcmToken = userService.getFcmTokenByUserId(userId);
		return ResponseEntity.ok(new FcmTokenResponse(fcmToken));
	}

	@PostMapping("/fcm-token")
	public ResponseEntity<ResponseDto<Void>> registerFcmToken(
			@RequestHeader("X-USER-ID") Integer userId,
			@Valid @RequestBody FcmTokenRequest request) {
		log.info("Registering FCM token for userId: {}", userId);
		userService.saveOrUpdateFcmToken(userId, request.getToken());
		return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "FCM 토큰 등록 성공"));
	}

	@PostMapping("/body-composition")
	void updateBodyComposition(
			@RequestBody BodyCompositionRequest request) {
		userService.updateUserBodyInfo(request);
	}
}
