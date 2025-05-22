package com.s206.health.client.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponse {

	private String name;

	private String nickname;

	private Gender gender;

	private BigDecimal height;

	private BigDecimal weight;

	private Goal goal;

	private LocalDate birthday;

	private String phone;

	private String email;

	private Boolean consentPersonal;

	private Boolean locationPersonal;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	private LocalDateTime deletedAt;

	private Boolean isDeleted;

	private Boolean notificationEnabled;
}
