package com.s206.health.bodyinfo.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class WeekRange {

	private int weekNumber;
	private LocalDate startDate;
	private LocalDate endDate;
}
