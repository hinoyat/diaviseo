package com.s206.alert.consumer;

import com.s206.alert.data.request.NotificationMessageDto;
import com.s206.alert.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PushNotificationConsumer {

	private final NotificationService notificationService;

	@RabbitListener(queues = "notification-queue")
	public void receive(NotificationMessageDto message) {
		notificationService.process(message);
	}
}