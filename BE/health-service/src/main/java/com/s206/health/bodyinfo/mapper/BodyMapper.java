package com.s206.health.bodyinfo.mapper;

import com.s206.health.bodyinfo.dto.request.BodyInfoCreateRequest;
import com.s206.health.bodyinfo.dto.response.BodyInfoResponse;
import com.s206.health.bodyinfo.entity.BodyInfo;
import com.s206.health.bodyinfo.entity.InputType;
import java.math.BigDecimal;
import java.util.List;
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
}
