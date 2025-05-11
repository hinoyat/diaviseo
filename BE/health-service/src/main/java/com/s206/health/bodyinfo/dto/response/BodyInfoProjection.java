package com.s206.health.bodyinfo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface BodyInfoProjection {

	LocalDate getMeasurementDate();

	BigDecimal getWeight();

	BigDecimal getMuscleMass();

	BigDecimal getBodyFat();
}
