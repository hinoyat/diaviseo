package com.s206.user.user.entity;

import com.s206.user.user.enums.Gender;
import com.s206.user.user.enums.Goal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Table(name = "user_tb")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userId;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(nullable = false, length = 50)
	private String nickname;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Goal goal;

	@Column(nullable = false, precision = 6, scale = 2)
	private BigDecimal height;

	@Column(nullable = false, precision = 6, scale = 2)
	private BigDecimal weight;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 1)
	private Gender gender;    // M: 남자 F: 여자

	@Column(nullable = false)
	private LocalDate birthday;

	@Column(nullable = false, unique = true, length = 20)
	private String phone;

	@Column(nullable = false, length = 50)
	private String email;

	@Column(nullable = false, length = 50)
	private String provider;

	@Column(nullable = false)
	private Boolean consentPersonal;

	@Column(nullable = false, columnDefinition = "char(1)")
	private Boolean locationPersonal;

	@CreationTimestamp
	@Column(nullable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@Column(nullable = true)
	private LocalDateTime deletedAt;

	@Column(nullable = false)
	@Builder.Default
	private Boolean isDeleted = false;

	@Column(nullable = false)
	@Builder.Default
	private Boolean notificationEnabled = true;

	@Column(nullable = true)
	private String fcmToken;

	public void updateNickname(String nickname) {
		this.nickname = nickname;
	}

	public void updatePhone(String phone) {
		this.phone = phone;
	}

	public void updateHeight(BigDecimal height) {
		this.height = height;
	}

	public void updateWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public void updateBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}

	public void updateGoal(Goal goal) {
		this.goal = goal;
	}

	public void updateNotificationEnabled(Boolean notificationEnabled) {
		this.notificationEnabled = notificationEnabled;
	}

	public void delete() {
		this.isDeleted = true;
		this.deletedAt = LocalDateTime.now();
	}

	public void updateFcmToken(String fcmToken) {
		this.fcmToken = fcmToken;
	}
}
