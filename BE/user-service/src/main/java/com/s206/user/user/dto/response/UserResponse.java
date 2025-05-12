package com.s206.user.user.dto.response;

import java.time.LocalDateTime;

public interface UserResponse {

	Integer getUserId();

	String getName();

	String getNickname();

	LocalDateTime getUpdatedAt();
}
