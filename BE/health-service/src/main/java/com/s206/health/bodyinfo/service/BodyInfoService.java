package com.s206.health.bodyinfo.service;

import com.s206.health.bodyinfo.dto.request.BodyInfoCreateRequest;
import com.s206.health.bodyinfo.dto.request.BodyInfoPatchRequest;
import com.s206.health.bodyinfo.dto.response.BodyInfoResponse;
import com.s206.health.bodyinfo.entity.BodyInfo;
import com.s206.health.bodyinfo.mapper.BodyMapper;
import com.s206.health.bodyinfo.repository.BodyInfoRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
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

	@Transactional(readOnly = true)
	public List<BodyInfoResponse> findByUserId(Integer userId) {
		log.info("사용자 ID: {}의 신체 정보 조회 시작", userId);

		List<BodyInfo> bodyInfos = bodyInfoRepository.findByUserId(userId);
		log.info("사용자 ID: {}의 신체 정보 {}건 조회 완료", userId, bodyInfos.size());

		return bodyMapper.toDtoList(bodyInfos);
	}

	@Transactional
	public BodyInfoResponse updateBodyInfo(Integer userId, Integer bodyId, BodyInfoPatchRequest dto) {
		log.info("사용자 ID: {}, 체성분 정보 ID: {}의 부분 업데이트 시작", userId, bodyId);

		BodyInfo bodyInfo = bodyInfoRepository.findByBodyIdAndIsDeletedFalse(bodyId)
				.orElseThrow(() -> new EntityNotFoundException("해당 ID의 체성분 정보가 없습니다."));

		// 요청한 사용자의 데이터인지 검증
		if (!bodyInfo.getUserId().equals(userId)) {
			log.warn("사용자 ID: {}가 타인의 체성분 정보(ID: {})에 접근 시도", userId, bodyId);
			throw new IllegalArgumentException("자신의 체성분 정보만 수정할 수 있습니다.");
		}

		bodyInfo.updatePartial(dto.getWeight(), dto.getBodyFat(), dto.getMuscleMass());
		bodyInfoRepository.save(bodyInfo);

		log.info("사용자 ID: {}의 체성분 정보(ID: {}) 업데이트 완료", userId, bodyId);

		return bodyMapper.toDto(bodyInfo);
	}
}

