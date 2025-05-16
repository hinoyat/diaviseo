package com.s206.health.bodyinfo.mapper;

import com.s206.health.bodyinfo.dto.request.BodyInfoCreateRequest;
import com.s206.health.bodyinfo.dto.response.BodyInfoResponse;
import com.s206.health.bodyinfo.entity.BodyInfo;
import com.s206.health.bodyinfo.entity.InputType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import com.s206.health.bodyinfo.util.HealthCalculator;
import com.s206.health.client.dto.response.Gender;
import com.s206.health.client.dto.response.UserDetailResponse;
import org.springframework.stereotype.Component;

@Component
public class BodyMapper {

	public BodyInfo toEntity(Integer userId, BodyInfoCreateRequest request) {
		return BodyInfo.builder()
				.userId(userId)
				.weight(request.getWeight())
				.bodyFat(request.getBodyFat())
				.muscleMass(request.getMuscleMass())
				.height(request.getHeight())
				.inputType(InputType.MANUAL)
				.isDeleted(false)
				.measurementDate(request.getMeasurementDate())
				.build();
	}

	public BodyInfoResponse toDto(BodyInfo bodyInfo) {
		return BodyInfoResponse.builder()
				.bodyId(bodyInfo.getBodyId())
				.userId(bodyInfo.getUserId())
				.weight(bodyInfo.getWeight())
				.bodyFat(bodyInfo.getBodyFat())
				.height(bodyInfo.getHeight())
				.muscleMass(bodyInfo.getMuscleMass())
				.createdAt(bodyInfo.getCreatedAt())
				.measurementDate(bodyInfo.getMeasurementDate())
				.build();
	}


	public List<BodyInfoResponse> toDtoList(List<BodyInfo> bodyInfos) {
		return bodyInfos.stream()
				.map(this::toDto)
				.toList();
	}

	public BodyInfoResponse toDto(BodyInfo bodyInfo, BigDecimal bmi, BigDecimal bmr) {
		return BodyInfoResponse.builder()
				.bodyId(bodyInfo.getBodyId())
				.userId(bodyInfo.getUserId())
				.weight(bodyInfo.getWeight())
				.bodyFat(bodyInfo.getBodyFat())
				.height(bodyInfo.getHeight())
				.muscleMass(bodyInfo.getMuscleMass())
				.createdAt(bodyInfo.getCreatedAt())
				.measurementDate(bodyInfo.getMeasurementDate())
				.bmi(bmi)
				.bmr(bmr)
				.build();
	}
	// BodyMapper 클래스에 아래 메서드 추가
	public List<BodyInfoResponse> toDtoListWithBmiAndBmr(List<BodyInfo> bodyInfos, BigDecimal height, Gender gender, int age) {
		return bodyInfos.stream()
				.map(bodyInfo -> {
					BigDecimal bmi = HealthCalculator.calculateBMI(bodyInfo.getWeight(), height);
					BigDecimal bmr = HealthCalculator.calculateBMR(gender, age, bodyInfo.getWeight(), height);
					return toDto(bodyInfo, bmi, bmr);
				})
				.toList();
	}

	public BodyInfoResponse createInitialResponse(Integer userId, UserDetailResponse userDetail) {
		// 회원 가입시 입력한 키, 몸무게로 BMI, BMR 계산
		int age = Period.between(userDetail.getBirthday(), LocalDate.now()).getYears();
		BigDecimal bmi = HealthCalculator.calculateBMI(userDetail.getWeight(), userDetail.getHeight());
		BigDecimal bmr = HealthCalculator.calculateBMR(userDetail.getGender(), age,
				userDetail.getWeight(), userDetail.getHeight());

		return BodyInfoResponse.builder()
				.bodyId(null)
				.userId(userId)
				.weight(userDetail.getWeight())
				.height(userDetail.getHeight())
				.bodyFat(BigDecimal.ZERO)
				.muscleMass(BigDecimal.ZERO)
				.createdAt(null)
				.measurementDate(null)
				.bmi(bmi)
				.bmr(bmr)
				.build();
	}
}
