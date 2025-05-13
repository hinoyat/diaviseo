package com.s206.health.notification.messaging;

import com.s206.health.notification.dto.request.NotificationMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer {

	private static final String EXCHANGE = "alert-exchange";
	private static final String ROUTING_KEY = "alert.push.exercise.requested";

	private final RabbitTemplate rabbitTemplate;


	public void sendNotification(NotificationMessageDto message) {
		try {
			rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, message);
			log.info("Message sent successfully: {}", message);
		} catch (Exception e) {
			log.error("Failed to send message: {}", e.getMessage(), e);
		}
	}
}
