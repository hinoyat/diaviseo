package com.s206.health.bodyinfo.service;

import com.s206.health.bodyinfo.dto.request.BodyInfoCreateRequest;
import com.s206.health.bodyinfo.dto.response.BodyInfoResponse;
import com.s206.health.bodyinfo.entity.BodyInfo;
import com.s206.health.bodyinfo.entity.InputType;
import com.s206.health.bodyinfo.mapper.BodyMapper;
import com.s206.health.bodyinfo.repository.BodyInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BodyInfoService {
	private final BodyInfoRepository bodyInfoRepository;
	private final BodyMapper bodyMapper;

	@Transactional
	public BodyInfoResponse create(Integer userId, BodyInfoCreateRequest request) {
		log.info("사용자 ID: {}의 신체 정보 생성 시작", userId);
		log.debug("입력 요청 데이터: {}", request);
		BodyInfo bodyInfo = bodyMapper.toEntity(userId, request);

		bodyInfoRepository.save(bodyInfo);
		log.info("사용자 ID: {}의 신체 정보 저장 완료 (ID: {})", userId, bodyInfo.getBodyId());

		return bodyMapper.toDto(bodyInfo);
	}
}
