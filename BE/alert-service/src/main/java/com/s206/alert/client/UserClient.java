package com.s206.alert.client;

import com.s206.alert.client.dto.response.FcmTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/api/users")
public interface UserClient {

	@GetMapping("/{userId}/fcm-token")
	ResponseEntity<FcmTokenResponse> getFcmToken(@PathVariable Integer userId);
}