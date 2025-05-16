package com.s206.health.bodyinfo.service;

import com.s206.common.exception.types.NotFoundException;
import com.s206.health.bodyinfo.dto.request.BodyInfoCreateRequest;
import com.s206.health.bodyinfo.dto.request.BodyInfoPatchRequest;
import com.s206.health.bodyinfo.dto.response.BodyInfoProjection;
import com.s206.health.bodyinfo.dto.response.BodyInfoResponse;
import com.s206.health.bodyinfo.dto.response.MonthRange;
import com.s206.health.bodyinfo.dto.response.MonthlyAverageBodyInfoResponse;
import com.s206.health.bodyinfo.dto.response.WeekRange;
import com.s206.health.bodyinfo.dto.response.WeeklyAverageBodyInfoResponse;
import com.s206.health.bodyinfo.entity.BodyInfo;
import com.s206.health.bodyinfo.mapper.BodyMapper;
import com.s206.health.bodyinfo.repository.BodyInfoRepository;
import com.s206.health.bodyinfo.util.HealthCalculator;
import com.s206.health.client.UserClient;
import com.s206.health.client.dto.response.UserDetailResponse;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
	private final UserClient userClient;

	@Transactional
	public BodyInfoResponse create(Integer userId, BodyInfoCreateRequest request) {
		log.info("사용자 ID: {}의 신체 정보 생성 시작", userId);
		log.debug("입력 요청 데이터: {}", request);
		BodyInfo bodyInfo = bodyMapper.toEntity(userId, request);

		bodyInfoRepository.save(bodyInfo);
		log.info("사용자 ID: {}의 신체 정보 저장 완료 (ID: {})", userId, bodyInfo.getBodyId());
		UserDetailResponse userDetailResponse = userClient.getUserByUserId(userId).getData();

		BigDecimal bmi = HealthCalculator.calculateBMI(bodyInfo.getWeight(),
				userDetailResponse.getHeight());
		int age = Period.between(userDetailResponse.getBirthday(), LocalDate.now()).getYears();
		BigDecimal bmr = HealthCalculator.calculateBMR(userDetailResponse.getGender(), age,
				bodyInfo.getWeight(), userDetailResponse.getHeight());

		return bodyMapper.toDto(bodyInfo, bmi, bmr);
	}

	@Transactional(readOnly = true)
	public List<BodyInfoResponse> findByUserId(Integer userId) {
		log.info("사용자 ID: {}의 신체 정보 조회 시작", userId);

		List<BodyInfo> bodyInfos = bodyInfoRepository.findByUserIdAndIsDeletedFalse(userId);

		// 사용자 정보 조회 (빈 리스트 여부와 상관없이 항상 필요)
		UserDetailResponse userDetailResponse = userClient.getUserByUserId(userId).getData();
		int age = Period.between(userDetailResponse.getBirthday(), LocalDate.now()).getYears();

		if (bodyInfos.isEmpty()) {
			log.info("사용자 ID: {}의 체성분 정보가 없습니다. 초기 응답 생성", userId);
			// 빈 리스트일 경우, 회원 가입 시 정보로 초기 응답 생성
			BodyInfoResponse initialResponse = bodyMapper.createInitialResponse(userId, userDetailResponse);
			return List.of(initialResponse);
		}

		// BMI, BMR 포함하여 응답 생성
		List<BodyInfoResponse> responses = bodyInfos.stream()
				.map(bodyInfo -> {
					BigDecimal bmi = HealthCalculator.calculateBMI(bodyInfo.getWeight(),
							userDetailResponse.getHeight());
					BigDecimal bmr = HealthCalculator.calculateBMR(userDetailResponse.getGender(),
							age, bodyInfo.getWeight(), userDetailResponse.getHeight());
					return bodyMapper.toDto(bodyInfo, bmi, bmr);
				})
				.toList();

		log.info("사용자 ID: {}의 신체 정보 {}건 조회 완료", userId, responses.size());
		return responses;
	}

	@Transactional
	public BodyInfoResponse updateBodyInfo(Integer userId, Integer bodyId,
			BodyInfoPatchRequest dto) {
		log.info("사용자 ID: {}, 체성분 정보 ID: {}의 부분 업데이트 시작", userId, bodyId);

		BodyInfo bodyInfo = bodyInfoRepository.findByBodyIdAndIsDeletedFalse(bodyId)
				.orElseThrow(() -> new EntityNotFoundException("해당 ID의 체성분 정보가 없습니다."));

		// 요청한 사용자의 데이터인지 검증
		if (!bodyInfo.getUserId().equals(userId)) {
			log.warn("사용자 ID: {}가 타인의 체성분 정보(ID: {})에 접근 시도", userId, bodyId);
			throw new IllegalArgumentException("자신의 체성분 정보만 수정할 수 있습니다.");
		}

		try {
			bodyInfo = bodyInfo.updatePartial(dto.getWeight(), dto.getBodyFat(),
					dto.getMuscleMass(), dto.getHeight(), dto.getMeasurementDate());
			log.info("사용자 DTO : {}", bodyInfo);
			bodyInfoRepository.save(bodyInfo);
			log.info("사용자 ID: {}의 체성분 정보(ID: {}) 업데이트 완료", userId, bodyId);
		} catch (Exception e) {
			log.error("사용자 ID: {}의 체성분 정보(ID: {}) 업데이트 실패: {}", userId, bodyId, e.getMessage());
			throw e;
		}

		log.info("사용자 ID: {}의 체성분 정보(ID: {}) 업데이트 완료", userId, bodyId);


		UserDetailResponse userDetailResponse = userClient.getUserByUserId(userId).getData();

		BigDecimal bmi = HealthCalculator.calculateBMI(bodyInfo.getWeight(),
				userDetailResponse.getHeight());
		int age = Period.between(userDetailResponse.getBirthday(), LocalDate.now()).getYears();
		BigDecimal bmr = HealthCalculator.calculateBMR(userDetailResponse.getGender(), age,
				bodyInfo.getWeight(), userDetailResponse.getHeight());

		return bodyMapper.toDto(bodyInfo, bmi, bmr);
	}

	@Transactional
	public void deleteBodyInfo(Integer userId, Integer bodyId) {
		log.info("사용자 ID: {}, 체성분 정보(ID: {}) 삭제 시작", userId, bodyId);

		BodyInfo bodyInfo = bodyInfoRepository.findByBodyIdAndIsDeletedFalse(bodyId)
				.orElseThrow(() -> new EntityNotFoundException("해당 ID의 체성분 정보가 없습니다."));

		// 요청한 사용자의 데이터인지 검증
		if (!bodyInfo.getUserId().equals(userId)) {
			log.warn("사용자 ID: {}가 타인의 체성분 정보(ID: {})에 접근 시도", userId, bodyId);
			throw new IllegalArgumentException("자신의 체성분 정보만 삭제할 수 있습니다.");
		}

		bodyInfo.markAsDeleted();
		bodyInfoRepository.save(bodyInfo);
		log.info("사용자 ID: {}의 체성분 정보(ID: {})가 삭제 처리되었습니다.", userId, bodyId);
	}


	@Transactional(readOnly = true)
	public BodyInfoResponse findByUserIdAndDate(Integer userId, LocalDate date) {
		log.info("사용자 ID: {}의 {} 날짜 신체 정보 조회", userId, date);

		// Optional을 사용하여 체성분 정보 조회
		Optional<BodyInfo> bodyInfoOpt = bodyInfoRepository.findLatestBodyInfoByMeasurementDate(userId, date);

		// 데이터가 없는 경우 NotFoundException 발생
		if (bodyInfoOpt.isEmpty()) {
			log.info("사용자 ID: {}의 {} 날짜 체성분 정보가 없습니다.", userId, date);
			throw new NotFoundException(
					String.format("사용자 ID: %d의 %s 날짜 체성분 정보가 없습니다.", userId, date));
		}

		BodyInfo bodyInfo = bodyInfoOpt.get();

		// 요청한 사용자의 데이터인지 검증
		if (!bodyInfo.getUserId().equals(userId)) {
			log.warn("사용자 ID: {}가 타인의 체성분 정보(ID: {})에 접근 시도", userId, bodyInfo.getBodyId());
			throw new IllegalArgumentException("자신의 체성분 정보만 조회할 수 있습니다.");
		}

		UserDetailResponse userDetailResponse = userClient.getUserByUserId(userId).getData();

		BigDecimal bmi = HealthCalculator.calculateBMI(bodyInfo.getWeight(),
				userDetailResponse.getHeight());
		int age = Period.between(userDetailResponse.getBirthday(), LocalDate.now()).getYears();
		BigDecimal bmr = HealthCalculator.calculateBMR(userDetailResponse.getGender(), age,
				bodyInfo.getWeight(), userDetailResponse.getHeight());

		log.info("사용자 ID: {}의 {} 날짜 체성분 정보 조회 완료", userId, date);
		return bodyMapper.toDto(bodyInfo, bmi, bmr);
	}

	@Transactional(readOnly = true)
	public List<BodyInfoProjection> getWeeklyBodyInfo(Integer userId, LocalDate endDate
	) {
		LocalDate startDate = endDate.minusDays(6);
		log.debug("조회 기간: {} ~ {}", startDate, endDate);

		List<BodyInfoProjection> bodyInfos = bodyInfoRepository.findByUserIdAndMeasurementDateBetween(
				userId, startDate, endDate);

		bodyInfos = fillMissingDates(bodyInfos, startDate, endDate);
		log.info("사용자 ID: {}의 주간 체성분 정보 조회 완료 ({}개 데이터)", userId, bodyInfos.size());
		return bodyInfos;
	}

	@Transactional(readOnly = true)
	public List<WeeklyAverageBodyInfoResponse> getWeeklyAverages(Integer userId,
			LocalDate endDate) {
		log.info("사용자 ID: {}의 월간 체성분 정보 조회 시작", userId);

		List<WeekRange> weekRanges = generateRecentSevenWeeks(endDate);
		log.debug("생성된 주차 범위: {}", weekRanges);

		LocalDate start = weekRanges.get(0).getStartDate();
		LocalDate end = weekRanges.get(6).getEndDate();

		List<BodyInfoProjection> rawData = bodyInfoRepository.findByUserIdAndMeasurementDateBetween(
				userId, start, end);
		log.debug("조회된 데이터 수: {}", rawData.size());

		List<WeeklyAverageBodyInfoResponse> result = calculateWeeklyAverages(rawData, weekRanges);
		log.info("사용자 ID: {}의 월간 체성분 정보 조회 완료 ({}개 주차)", userId, result.size());

		return result;
	}

	@Transactional(readOnly = true)
	public List<MonthlyAverageBodyInfoResponse> getMonthlyAverages(Integer userId,
			LocalDate endDate) {
		log.info("사용자 ID: {}의 7개월간 월별 평균 체성분 정보 조회 시작", userId);

		List<MonthRange> monthRanges = generateRecentSevenMonths(endDate);
		log.debug("생성된 월별 범위: {}", monthRanges);

		LocalDate start = monthRanges.get(0).getStartDate();
		LocalDate end = monthRanges.get(6).getEndDate();
		log.debug("조회 기간: {} ~ {}", start, end);

		List<BodyInfoProjection> rawData =
				bodyInfoRepository.findByUserIdAndMeasurementDateBetween(userId, start, end);
		log.debug("조회된 데이터 수: {}", rawData.size());

		List<MonthlyAverageBodyInfoResponse> result = calculateMonthlyAverages(rawData,
				monthRanges);
		log.info("사용자 ID: {}의 7개월간 월별 평균 체성분 정보 조회 완료 ({}개 월)", userId, result.size());

		return result;
	}


	private List<MonthRange> generateRecentSevenMonths(LocalDate referenceDate) {
		List<MonthRange> monthRanges = new ArrayList<>(7);

		for (int monthOffset = 6; monthOffset >= 0; monthOffset--) {
			LocalDate endMonth = referenceDate.minusMonths(monthOffset);
			LocalDate startDate = endMonth.withDayOfMonth(1);
			LocalDate endDate = endMonth.withDayOfMonth(endMonth.lengthOfMonth());
			int monthIndex = 7 - monthOffset; // 가장 과거 월을 1번으로 시작

			monthRanges.add(new MonthRange(monthIndex, startDate, endDate));
		}

		return monthRanges;
	}

	private List<WeekRange> generateRecentSevenWeeks(LocalDate referenceDate) {
		List<WeekRange> weekRanges = new ArrayList<>(7);

		for (int weekOffset = 6; weekOffset >= 0; weekOffset--) {
			LocalDate endDate = referenceDate.minusDays(weekOffset * 7L);
			LocalDate startDate = endDate.minusDays(6);
			int weekNumber = 7 - weekOffset; // 가장 과거 주차를 1번으로 시작

			weekRanges.add(new WeekRange(weekNumber, startDate, endDate));
		}

		return weekRanges;
	}

	private List<MonthlyAverageBodyInfoResponse> calculateMonthlyAverages(
			List<BodyInfoProjection> data,
			List<MonthRange> monthRanges
	) {
		List<MonthlyAverageBodyInfoResponse> result = new ArrayList<>();

		for (MonthRange month : monthRanges) {
			List<BodyInfoProjection> monthlyData = data.stream()
					.filter(d -> !d.getMeasurementDate().isBefore(month.getStartDate()) &&
							!d.getMeasurementDate().isAfter(month.getEndDate()))
					.toList();

			BigDecimal avgWeight = averageExcludingZero(
					monthlyData.stream().map(BodyInfoProjection::getWeight));
			BigDecimal avgMuscle = averageExcludingZero(
					monthlyData.stream().map(BodyInfoProjection::getMuscleMass));
			BigDecimal avgFat = averageExcludingZero(
					monthlyData.stream().map(BodyInfoProjection::getBodyFat));
			BigDecimal avgHeight = averageExcludingZero(
					monthlyData.stream().map(BodyInfoProjection::getHeight));

			result.add(new MonthlyAverageBodyInfoResponse(
					month.getMonthIndex(),
					roundToTwo(avgWeight),
					roundToTwo(avgMuscle),
					roundToTwo(avgFat),
					roundToTwo(avgHeight)
			));
		}

		return result;
	}

	private List<WeeklyAverageBodyInfoResponse> calculateWeeklyAverages(
			List<BodyInfoProjection> data,
			List<WeekRange> weekRanges
	) {
		List<WeeklyAverageBodyInfoResponse> result = new ArrayList<>();

		for (WeekRange week : weekRanges) {
			// 주간 범위 내 데이터 필터링
			List<BodyInfoProjection> weekData = data.stream()
					.filter(d -> !d.getMeasurementDate().isBefore(week.getStartDate()) &&
							!d.getMeasurementDate().isAfter(week.getEndDate()))
					.toList();

			// 0 제외 후 평균 계산
			BigDecimal avgWeight = averageExcludingZero(
					weekData.stream().map(BodyInfoProjection::getWeight));
			BigDecimal avgMuscle = averageExcludingZero(
					weekData.stream().map(BodyInfoProjection::getMuscleMass));
			BigDecimal avgFat = averageExcludingZero(
					weekData.stream().map(BodyInfoProjection::getBodyFat));
			BigDecimal avgHeight = averageExcludingZero(
					weekData.stream().map(BodyInfoProjection::getHeight));

			result.add(new WeeklyAverageBodyInfoResponse(
					week.getWeekNumber(),
					roundToTwo(avgWeight), roundToTwo(avgMuscle), roundToTwo(avgFat),
					roundToTwo(avgHeight)
			));
		}

		return result;
	}

	private BigDecimal averageExcludingZero(Stream<BigDecimal> values) {
		List<BigDecimal> valid = values
				.filter(v -> v != null && v.compareTo(BigDecimal.ZERO) != 0)
				.toList();

		if (valid.isEmpty()) {
			return BigDecimal.ZERO;
		}

		BigDecimal sum = valid.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
		return sum.divide(BigDecimal.valueOf(valid.size()), 10, RoundingMode.HALF_UP); // 반올림은 M5에서
	}

	private BigDecimal roundToTwo(BigDecimal value) {
		return value.setScale(2, RoundingMode.HALF_UP);
	}


	private List<BodyInfoProjection> fillMissingDates(
			List<BodyInfoProjection> rawData,
			LocalDate startDate,
			LocalDate endDate
	) {
		Map<LocalDate, BodyInfoProjection> dataMap = rawData.stream()
				.collect(Collectors.toMap(
						BodyInfoProjection::getMeasurementDate,
						Function.identity()
				));

		List<BodyInfoProjection> filled = new ArrayList<>();

		for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
			BodyInfoProjection projection = dataMap.getOrDefault(date, zeroProjection(date));
			filled.add(projection);
		}

		return filled;
	}

	private BodyInfoProjection zeroProjection(LocalDate date) {
		return new BodyInfoProjection() {
			public LocalDate getMeasurementDate() {
				return date;
			}

			public BigDecimal getWeight() {
				return BigDecimal.ZERO;
			}

			public BigDecimal getMuscleMass() {
				return BigDecimal.ZERO;
			}

			public BigDecimal getBodyFat() {
				return BigDecimal.ZERO;
			}

			public BigDecimal getHeight() {
				return BigDecimal.ZERO;
			}
		};
	}
}

