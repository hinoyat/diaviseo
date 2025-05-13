package com.s206.health.notification.scheduler;

import com.s206.health.notification.dto.request.NotificationChannel;
import com.s206.health.notification.dto.request.NotificationMessageDto;
import com.s206.health.notification.dto.request.NotificationType;
import com.s206.health.notification.messaging.NotificationProducer;
import com.s206.health.notification.service.NotificationService;
import com.s206.health.nutrition.meal.entity.MealType;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

	private final NotificationProducer notificationProducer;
	private final NotificationService notificationService;


	@Scheduled(cron = "0 0 14 * * *", zone = "Asia/Seoul")
	public void sendLunchReminder() {
		notificationService.findUsersWithIncompleteMealLog(MealType.LUNCH).forEach(user -> {
			notificationProducer.sendNotification(
					NotificationMessageDto.builder()
							.userId(user.getUserId())
							.notificationType(NotificationType.DIET)
							.message("점심식사 맛있게 하셨나요? 오늘 드신 식사를 기록해보세요!")
							.sentAt(LocalDateTime.now())
							.channel(NotificationChannel.BOTH)
							.build()
			);
		});
	}

	// 저녁 기록 안한 유저들에게 보내기
	@Scheduled(cron = "0 0 21 * * *", zone = "Asia/Seoul") // 오후 9시
	public void sendDinnerReminder() {
		notificationService.findUsersWithIncompleteMealLog(MealType.DINNER).forEach(user -> {
			notificationProducer.sendNotification(
					NotificationMessageDto.builder()
							.userId(user.getUserId())
							.notificationType(NotificationType.DIET)
							.message("오늘 하루도 잘 보내셨나요? 식단 기록을 남겨보세요!")
							.sentAt(LocalDateTime.now())
							.channel(NotificationChannel.BOTH)
							.build()
			);
		});
	}

	// 당일 칼로리 소모 목표 도달 하지 않은 유저에게 보내기
	// TODO : 유저별 남은 칼로리 계산하기


	// 당일 운동 기록 안한 유저에게 보내기.
	@Scheduled(cron = "0 0 22 * * *", zone = "Asia/Seoul")
	public void sendExerciseLogReminder() {
		notificationService.findUsersWithoutExerciseLog().forEach(user -> {
			notificationProducer.sendNotification(
					NotificationMessageDto.builder()
							.userId(user.getUserId())
							.notificationType(NotificationType.EXERCISE)
							.message("오늘 하루 운동한 기록을 채워주세요")
							.sentAt(LocalDateTime.now())
							.channel(NotificationChannel.BOTH)
							.build()
			);
		});
	}

	// 체중 입력 안한 유저에게 보내기.
	@Scheduled(cron = "0 30 7 * * *", zone = "Asia/Seoul") // 오전 7시 30분
	public void sendWeightReminder() {
		notificationService.findUsersNeedingWeightUpdate().forEach(user -> {
			notificationProducer.sendNotification(
					NotificationMessageDto.builder()
							.userId(user.getUserId())
							.notificationType(NotificationType.WEIGHT)
							.message("체중을 업데이트하고 더 정확한 분석을 받아보세요!")
							.sentAt(LocalDateTime.now())
							.channel(NotificationChannel.BOTH)
							.build()
			);
		});
	}

	// 마지막 접속일이 이틀이상인 유저에게 보내기
	@Scheduled(cron = "0 0 8 * * *", zone = "Asia/Seoul") // 오전 8시: 매일 실행 → 2일 이상 미접속자 감지
	public void sendInactiveUserReminder() {
		notificationService.findUsersInactiveForDays(2).forEach(user -> {
			notificationProducer.sendNotification(
					NotificationMessageDto.builder()
							.userId(user.getUserId())
							.notificationType(NotificationType.INACTIVE)
							.message(user.getName() + "님! 디아비서는 꾸준한 기록을 통해 " + user.getName()
									+ " 님의 건강 관리를 도와드립니다!")
							.sentAt(LocalDateTime.now())
							.channel(NotificationChannel.PUSH)
							.build()
			);
		});
	}
}
