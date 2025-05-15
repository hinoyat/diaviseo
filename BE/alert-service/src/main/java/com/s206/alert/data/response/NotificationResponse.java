package com.s206.alert.data.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class NotificationResponse {

	private Long notificationId;
	private Integer userId;
	private String message;
	private String notificationType;
	private boolean isRead;
	private LocalDateTime sentAt;
}