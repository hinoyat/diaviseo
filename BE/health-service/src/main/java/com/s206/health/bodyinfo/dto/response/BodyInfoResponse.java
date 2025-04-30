package com.s206.health.bodyinfo.dto.response;

import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class BodyInfoResponse {
	private Integer bodyId;
	private Integer userId;
	private BigDecimal weight;

	private BigDecimal bodyFat;
	private BigDecimal muscleMass;
	private LocalDateTime createdAt;
}
