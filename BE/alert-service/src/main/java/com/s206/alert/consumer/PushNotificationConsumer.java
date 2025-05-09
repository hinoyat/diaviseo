package com.s206.alert.consumer;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PushNotificationConsumer {

	@RabbitListener(queues = "push-notification-queue")
	public void receive(String messageBody) {
		log.info("Received: {}",messageBody);
		sendToFcm(messageBody);
	}

	private void sendToFcm(String body) {
		try {
			Message message = Message.builder()
					.putData("message", body)
					.setTopic("general") // 또는 setToken(...)
					.build();

			String response = FirebaseMessaging.getInstance().send(message);
			log.info("FCM Sent: {}",response);
		} catch (Exception e) {
			log.error("FCM Error: {}", e.getMessage());
		}
	}
}