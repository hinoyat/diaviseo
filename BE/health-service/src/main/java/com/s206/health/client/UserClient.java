package com.s206.health.client;

import com.s206.common.dto.ResponseDto;
import com.s206.health.client.dto.request.BodyCompositionRequest;
import com.s206.health.client.dto.response.UserDetailResponse;
import com.s206.health.notification.dto.response.UserResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service", path = "/api/users")
public interface UserClient {

	@GetMapping("/me")
	ResponseDto<UserDetailResponse> getUserByUserId(@RequestHeader("X-USER-ID") Integer userId);

	@GetMapping("/notifications-enabled")
	ResponseDto<List<UserResponse>> getUsersWithNotificationsEnabled();

	@PostMapping("/body-composition")
	void updateBodyComposition(
			@RequestBody BodyCompositionRequest request);
}