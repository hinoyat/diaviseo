package com.s206.health.nutrition.meal.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MealNotificationPublisher {
	private static final String EXCHANGE = "alert-exchange";
	private static final String ROUTING_KEY = "alert.push.meal.requested";

	private final RabbitTemplate rabbitTemplate;

	public void send(String message) {
		try {
			rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, message);
			log.info("Message sent successfully (로그 성공): {}", message);
		} catch (Exception e) {
			log.error("Failed to send message: {}", e.getMessage(), e);
		}
	}
}
