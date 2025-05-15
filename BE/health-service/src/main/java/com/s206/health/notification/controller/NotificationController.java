package com.s206.health.notification.controller;

import com.s206.health.client.UserClient;
import com.s206.health.notification.dto.request.NotificationChannel;
import com.s206.health.notification.dto.request.NotificationMessageDto;
import com.s206.health.notification.dto.request.NotificationType;
import com.s206.health.notification.dto.response.UserResponse;
import com.s206.health.notification.messaging.NotificationProducer;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO : 알림 전송 테스트 기능.
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/bodies")
public class NotificationController {

	private final NotificationProducer notificationProducer;
	private final UserClient userClient;

	@PostMapping("/notification/test")
	public ResponseEntity<String> testNotificationSendToAllUser() {
		try {
			List<UserResponse> users = userClient.getUsersWithNotificationsEnabled().getData();
			int sentCount = 0;

			for (UserResponse user : users) {
				NotificationMessageDto message = NotificationMessageDto.builder()
						.userId(user.getUserId())
						.sentAt(LocalDateTime.now())
						.notificationType(NotificationType.INACTIVE)
						.channel(NotificationChannel.BOTH)
						.message(user.getName() + "님 에게 보낸 테스트 알림입니다.")
						.build();

				notificationProducer.sendNotification(message);
				sentCount++;
			}

			return ResponseEntity.ok("알림 전송 완료: " + sentCount + "명의 사용자에게 전송됨");
		} catch (Exception e) {
			log.error("알림 전송 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("알림 전송 중 오류가 발생했습니다: " + e.getMessage());
		}
	}
}
