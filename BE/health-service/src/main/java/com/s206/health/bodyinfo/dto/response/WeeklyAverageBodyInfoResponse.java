package com.s206.health.bodyinfo.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WeeklyAverageBodyInfoResponse {

	private Integer weekLabel;
	private BigDecimal avgWeight;
	private BigDecimal avgMuscleMass;
	private BigDecimal avgBodyFat;
}
