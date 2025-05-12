package com.s206.alert.data.request;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NotificationMessageDto implements Serializable {

	private Integer userId;

	private NotificationType notificationType;

	private String message;

	private LocalDateTime sentAt;

	private NotificationChannel channel; // PUSH, INAPP, BOTH
}

