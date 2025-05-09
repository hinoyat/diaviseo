package com.s206.alert.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "notification_tb")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long notificationId;
	@Column(nullable = false)
	private Long userId;

	private String title;
	private String content;

	@Enumerated(EnumType.STRING)
	private NotificationType type;   // Enum으로 변경됨
	@CreationTimestamp
	private LocalDateTime createdAt;

	@Column(nullable = true)
	private LocalDateTime checkedAt;

	@Column
	private LocalDateTime deletedAt;

	@Column(nullable = false)
	private Boolean isDeleted = false;

}