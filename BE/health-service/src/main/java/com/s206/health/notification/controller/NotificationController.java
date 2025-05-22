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

	@PostMapping("/notification/exercise-all")
	public ResponseEntity<String> sendExerciseNotificationToAllUsers() {
		try {
			List<UserResponse> users = userClient.getUsersWithNotificationsEnabled().getData();
			int sentCount = 0;

			for (UserResponse user : users) {
				notificationProducer.sendNotification(
						NotificationMessageDto.builder()
								.userId(user.getUserId())
								.notificationType(NotificationType.EXERCISE)
								.message(user.getName() + "님, 오늘 하루 운동한 기록을 채워주세요")
								.sentAt(LocalDateTime.now())
								.channel(NotificationChannel.BOTH)
								.build()
				);
				sentCount++;
			}

			return ResponseEntity.ok("운동 알림 전송 완료: " + sentCount + "명의 사용자에게 전송됨");
		} catch (Exception e) {
			log.error("운동 알림 전송 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("운동 알림 전송 중 오류가 발생했습니다: " + e.getMessage());
		}
	}

	@PostMapping("/notification/weight-all")
	public ResponseEntity<String> sendWeightNotificationToAllUsers() {
		try {
			List<UserResponse> users = userClient.getUsersWithNotificationsEnabled().getData();
			int sentCount = 0;

			for (UserResponse user : users) {
				notificationProducer.sendNotification(
						NotificationMessageDto.builder()
								.userId(user.getUserId())
								.notificationType(NotificationType.WEIGHT)
								.message(user.getName() + "님, 체중을 업데이트하고 더 정확한 분석을 받아보세요!")
								.sentAt(LocalDateTime.now())
								.channel(NotificationChannel.BOTH)
								.build()
				);
				sentCount++;
			}

			return ResponseEntity.ok("체중 업데이트 알림 전송 완료: " + sentCount + "명의 사용자에게 전송됨");
		} catch (Exception e) {
			log.error("체중 알림 전송 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("체중 알림 전송 중 오류가 발생했습니다: " + e.getMessage());
		}
	}

	@PostMapping("/notification/diet-all")
	public ResponseEntity<String> sendDietNotificationToAllUsers() {
		try {
			List<UserResponse> users = userClient.getUsersWithNotificationsEnabled().getData();
			int sentCount = 0;

			for (UserResponse user : users) {
				notificationProducer.sendNotification(
						NotificationMessageDto.builder()
								.userId(user.getUserId())
								.notificationType(NotificationType.DIET)
								.message(user.getName() + "님, 오늘 하루도 잘 보내셨나요? 식단 기록을 남겨보세요!")
								.sentAt(LocalDateTime.now())
								.channel(NotificationChannel.BOTH)
								.build()
				);
				sentCount++;
			}

			return ResponseEntity.ok("식단 기록 알림 전송 완료: " + sentCount + "명의 사용자에게 전송됨");
		} catch (Exception e) {
			log.error("식단 알림 전송 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("식단 알림 전송 중 오류가 발생했습니다: " + e.getMessage());
		}
	}
}

