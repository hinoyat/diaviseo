package com.s206.alert.mapper;

import com.s206.alert.data.response.NotificationResponse;
import com.s206.alert.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

	public NotificationResponse toResponse(Notification entity) {
		return new NotificationResponse(
				entity.getNotificationId(),
				entity.getUserId(),
				entity.getMessage(),
				entity.getNotificationType().name(),
				entity.getIsRead(),
				entity.getSentAt()
		);
	}

	public Page<NotificationResponse> toResponsePage(Page<Notification> entities) {
		return entities.map(this::toResponse);
	}
}
