package com.s206.health.notification.dto.request;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationMessageDto implements Serializable {

	private Integer userId;

	private NotificationType notificationType;

	private String message;

	private LocalDateTime sentAt;

	private NotificationChannel channel; // PUSH, INAPP, BOTH
}

