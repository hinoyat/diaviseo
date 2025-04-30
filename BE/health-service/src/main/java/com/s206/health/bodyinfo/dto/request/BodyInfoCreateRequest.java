package com.s206.health.bodyinfo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class BodyInfoCreateRequest {
	@NotNull(message = "체중은 필수 입력값입니다.")
	@Positive(message = "체중은 양수여야 합니다.")
	private BigDecimal weight;

	@PositiveOrZero(message = "체지방은 0 이상이어야 합니다.")
	private BigDecimal bodyFat;

	@PositiveOrZero(message = "근육량은 0 이상이어야 합니다.")
	private BigDecimal muscleMass;
}
