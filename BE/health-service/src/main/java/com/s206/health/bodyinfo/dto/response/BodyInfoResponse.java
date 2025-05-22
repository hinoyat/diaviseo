package com.s206.health.bodyinfo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class BodyInfoResponse {

	private Integer bodyId;
	private Integer userId;
	private BigDecimal height;
	private BigDecimal weight;

	private BigDecimal bodyFat;
	private BigDecimal muscleMass;
	private LocalDateTime createdAt;
	private LocalDate measurementDate;

	private BigDecimal bmi;
	private BigDecimal bmr;

}
