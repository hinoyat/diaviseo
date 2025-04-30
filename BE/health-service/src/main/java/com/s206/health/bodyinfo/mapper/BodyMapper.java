package com.s206.health.bodyinfo.mapper;

import com.s206.health.bodyinfo.dto.request.BodyInfoCreateRequest;
import com.s206.health.bodyinfo.dto.response.BodyInfoResponse;
import com.s206.health.bodyinfo.entity.BodyInfo;

import com.s206.health.bodyinfo.entity.InputType;
import org.springframework.stereotype.Component;

@Component
public class BodyMapper {
	public BodyInfo toEntity(Integer userId, BodyInfoCreateRequest request) {
		return BodyInfo.builder()
				.userId(userId)
				.weight(request.getWeight())
				.bodyFat(request.getBodyFat())
				.muscleMass(request.getMuscleMass())
				.inputType(InputType.MANUAL)
				.isDeleted(false)
				.build();
	}
	public BodyInfoResponse toDto(BodyInfo bodyInfo) {
		return BodyInfoResponse.builder()
				.bodyId(bodyInfo.getBodyId())
				.userId(bodyInfo.getUserId())
				.weight(bodyInfo.getWeight())
				.bodyFat(bodyInfo.getBodyFat())
				.muscleMass(bodyInfo.getMuscleMass())
				.createdAt(bodyInfo.getCreatedAt())
				.build();
	}
}
