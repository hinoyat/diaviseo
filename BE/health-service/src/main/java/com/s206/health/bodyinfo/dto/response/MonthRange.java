package com.s206.health.bodyinfo.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MonthRange {

	private int monthIndex; // 1 ~ 7
	private LocalDate startDate;
	private LocalDate endDate;

}
