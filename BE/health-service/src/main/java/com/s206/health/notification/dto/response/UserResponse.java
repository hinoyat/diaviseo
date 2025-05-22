package com.s206.health.notification.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserResponse {

	private Integer userId;
	private String name;
	private String nickname;
	private LocalDateTime updatedAt;
}
