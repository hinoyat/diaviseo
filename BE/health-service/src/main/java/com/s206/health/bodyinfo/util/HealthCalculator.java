package com.s206.health.bodyinfo.util;

import com.s206.health.client.dto.response.Gender;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class HealthCalculator {

	/**
	 * BMI 계산: 체중(kg) / 키(m)²
	 */
	public static BigDecimal calculateBMI(BigDecimal weightKg, BigDecimal heightCm) {
		BigDecimal heightM = heightCm.divide(BigDecimal.valueOf(100.0), 10, RoundingMode.HALF_UP);
		return weightKg.divide(heightM.pow(2), 10, RoundingMode.HALF_UP);
	}

	/**
	 * 해리스-베네딕트 공식을 이용한 BMR 계산
	 */
	public static BigDecimal calculateBMR(Gender gender, int age, BigDecimal weightKg,
			BigDecimal heightCm) {
		BigDecimal ageValue = BigDecimal.valueOf(age);

		return switch (gender) {
			case M -> BigDecimal.valueOf(88.362)
					.add(BigDecimal.valueOf(13.397).multiply(weightKg))
					.add(BigDecimal.valueOf(4.799).multiply(heightCm))
					.subtract(BigDecimal.valueOf(5.677).multiply(ageValue));
			case F -> BigDecimal.valueOf(447.593)
					.add(BigDecimal.valueOf(9.247).multiply(weightKg))
					.add(BigDecimal.valueOf(3.098).multiply(heightCm))
					.subtract(BigDecimal.valueOf(4.330).multiply(ageValue));
		};
	}

	/**
	 * 미핏린-세인트 조 공식을 이용한 BMR 계산 (더 정확한 공식)
	 */
	public static BigDecimal calculateMifflinStJeorBMR(boolean isMale, int age, BigDecimal weightKg,
			BigDecimal heightCm) {
		BigDecimal ageValue = BigDecimal.valueOf(age);

		BigDecimal base = BigDecimal.valueOf(10).multiply(weightKg)
				.add(BigDecimal.valueOf(6.25).multiply(heightCm))
				.subtract(BigDecimal.valueOf(5).multiply(ageValue));

		if (isMale) {
			return base.add(BigDecimal.valueOf(5));
		} else {
			return base.subtract(BigDecimal.valueOf(161));
		}
	}
}
