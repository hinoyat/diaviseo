package com.s206.alert.service;

import com.s206.alert.data.request.NotificationChannel;
import com.s206.alert.data.request.NotificationMessageDto;
import com.s206.alert.data.request.NotificationType;
import com.s206.alert.data.response.NotificationResponse;
import com.s206.alert.entity.Notification;
import com.s206.alert.mapper.NotificationMapper;
import com.s206.alert.repository.NotificationRepository;
import com.s206.common.exception.types.NotFoundException;
import jakarta.ws.rs.ForbiddenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final FcmService fcmService;
	private final NotificationMapper notificationMapper;

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

	@Transactional(readOnly = true)
	public Page<NotificationResponse> getUserNotifications(Integer userId, Pageable pageable) {
		log.info("사용자 알림 조회 시작: userId={}, page={}, size={}", userId, pageable.getPageNumber(),
				pageable.getPageSize());
		Page<Notification> notifications = notificationRepository.findByUserId(userId, pageable);
		log.info("사용자 알림 조회 결과: userId={}, 총 건수={}, 현재 페이지 건수={}",
				userId, notifications.getTotalElements(), notifications.getNumberOfElements());
		Page<NotificationResponse> response = notificationMapper.toResponsePage(notifications);
		return response;
	}

	@Transactional
	public void markAsRead(Integer userId, Long notificationId) {
		Notification notification = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new NotFoundException("알림이 존재하지 않습니다."));

		if (!notification.getUserId().equals(userId)) {
			throw new ForbiddenException("다른 사용자의 알림입니다.");
		}

		if (!notification.getIsRead()) {
			notification.updateReadState();
		}
		notificationRepository.save(notification);
	}

	@Transactional
	public int markAllAsRead(Integer userId) {
		int updatedCount = notificationRepository.markAllAsReadByUserId(userId);
		log.info("사용자({})의 읽지 않은 알림 {}건을 읽음 처리 완료", userId, updatedCount);
		return updatedCount;
	}
}
