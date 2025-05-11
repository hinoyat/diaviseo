package com.s206.health.bodyinfo.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyAverageBodyInfoResponse {

	private int monthIndex; // 1~7
	private BigDecimal avgWeight;
	private BigDecimal avgMuscleMass;
	private BigDecimal avgBodyFat;

}
