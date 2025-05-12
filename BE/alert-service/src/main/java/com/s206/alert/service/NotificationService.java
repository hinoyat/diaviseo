package com.s206.alert.service;

import com.s206.alert.data.request.NotificationChannel;
import com.s206.alert.data.request.NotificationMessageDto;
import com.s206.alert.data.request.NotificationType;
import com.s206.alert.entity.Notification;
import com.s206.alert.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final FcmService fcmService;

	public void process(NotificationMessageDto dto) {
		// 1. 인앱 알림 저장
		if (dto.getChannel() == NotificationChannel.INAPP
				|| dto.getChannel() == NotificationChannel.BOTH) {
			Notification entity = Notification.builder()
					.userId(dto.getUserId())
					.notificationType(dto.getNotificationType())
					.message(dto.getMessage())
					.sentAt(dto.getSentAt())
					.isRead(false)
					.build();

			notificationRepository.save(entity);
			log.info("인앱 알림 저장 완료: userId={}", dto.getUserId());
		}

		// 2. FCM 푸시 전송
		if (dto.getChannel() == NotificationChannel.PUSH
				|| dto.getChannel() == NotificationChannel.BOTH) {
			try {
				fcmService.send(
						dto.getUserId(), // userId 기준으로 토큰 조회
						getTitle(dto.getNotificationType()),
						dto.getMessage()
				);
				log.info("FCM 푸시 전송 성공: userId={}", dto.getUserId());
			} catch (Exception e) {
				log.error("FCM 푸시 전송 실패", e);
			}
		}
	}

	private String getTitle(NotificationType type) {
		return switch (type) {
			case DIET -> "DIET";
			case EXERCISE -> "EXERCISE";
			case WEIGHT -> "WEIGHT";
			case INACTIVE -> "INACTIVE";
		};
	}
}
