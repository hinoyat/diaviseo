package com.s206.health.bodyinfo.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BodyInfoPatchRequest {

	@Min(value = 0, message = "체중은 0 이상이어야 합니다")
	@Max(value = 500, message = "체중은 500 이하여야 합니다")
	private BigDecimal weight;

	@Min(value = 0, message = "체지방률은 0 이상이어야 합니다")
	@Max(value = 100, message = "체지방률은 100% 이하여야 합니다")
	private BigDecimal bodyFat;

	@Min(value = 0, message = "근육량은 0 이상이어야 합니다")
	@Max(value = 1000, message = "근육량은 1000 이하여야 합니다")
	private BigDecimal muscleMass;
	
	@PastOrPresent(message = "측정 날짜는 현재 또는 과거 날짜만 가능합니다.")
	private LocalDate measurementDate;
}