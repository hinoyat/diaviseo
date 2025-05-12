package com.s206.alert.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.s206.alert.client.UserClient;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {

	private final UserClient userClient;

	public void send(Integer userId, String title, String body) {
		String fcmToken = Objects.requireNonNull(userClient.getFcmToken(userId).getBody())
				.getFcmToken();

		if (fcmToken == null || fcmToken.isBlank()) {
			log.warn("유저 {} 의 FCM 토큰이 없습니다. 푸시 전송 생략", userId);
			return;
		}

		Message message = Message.builder()
				.setToken(fcmToken)
				.setNotification(
						Notification.builder()
								.setTitle(title)
								.setBody(body)
								.build()
				)
				// Data 페이로드 (앱 로직용)
				.putData("title", title)
				.putData("body", body)
				.build();

		try {
			String response = FirebaseMessaging.getInstance().send(message);
			log.info("FCM 전송 성공: response={}", response);
		} catch (FirebaseMessagingException e) {
			log.error("FCM 전송 실패: userId={}, reason={}", userId, e.getMessage());
		}
	}
}
