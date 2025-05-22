package com.s206.health.notification.service;

import com.s206.health.bodyinfo.repository.BodyInfoRepository;
import com.s206.health.client.UserClient;
import com.s206.health.exercise.repository.ExerciseRepository;
import com.s206.health.notification.dto.response.UserResponse;
import com.s206.health.nutrition.meal.entity.MealType;
import com.s206.health.nutrition.meal.repository.MealTimeRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

	private final UserClient userClient;

	private final MealTimeRepository mealTimeRepository;

	private final ExerciseRepository exerciseRepository;

	private final BodyInfoRepository bodyInfoRepository;


	// 점심 식단을 입력하지 않은 유저 찾아서 알림 전송
	@Transactional(readOnly = true)
	public List<UserResponse> findUsersWithIncompleteMealLog(MealType mealType) {
		List<UserResponse> enabledUsers = userClient.getUsersWithNotificationsEnabled().getData();

		List<Integer> userIdsWithMealLog = mealTimeRepository.findUserIdsWithMealTime(
				enabledUsers.stream().map(UserResponse::getUserId).toList(),
				LocalDate.now(), mealType);

		return enabledUsers.stream()
				.filter(user -> !userIdsWithMealLog.contains(user.getUserId()))
				.toList();
	}

//	public List<UserResponse> findUsersWithCalorieDeficit() {
//		return null;
//	}

	public List<UserResponse> findUsersWithoutExerciseLog() {
		log.info("findUsersWithoutExerciseLog 메서드 실행");
		List<UserResponse> enabledUsers = userClient.getUsersWithNotificationsEnabled().getData();
		log.info("알림 활성화된 사용자 수: {}", enabledUsers.size());

		List<Integer> userIdsWithExerciseLog = exerciseRepository.findUserIdsWithExerciseOnDate(
				enabledUsers.stream().map(UserResponse::getUserId).toList(),
				LocalDate.now());
		log.info("오늘 운동 기록이 있는 사용자 수: {}", userIdsWithExerciseLog.size());

		List<UserResponse> usersWithoutExerciseLog = enabledUsers.stream()
				.filter(user -> !userIdsWithExerciseLog.contains(user.getUserId())).toList();
		log.info("운동 기록이 없는 사용자 수: {}", usersWithoutExerciseLog.size());

		return usersWithoutExerciseLog;
	}

	@Transactional(readOnly = true)
	public List<UserResponse> findUsersNeedingWeightUpdate() {
		log.info("findUsersNeedingWeightUpdate 메서드 실행");
		List<UserResponse> enabledUsers = userClient.getUsersWithNotificationsEnabled().getData();
		log.info("알림 활성화된 사용자 수: {}", enabledUsers.size());

		List<Integer> userIdsWithWeightUpdate = bodyInfoRepository.findUserIdsWithWeightUpdate(
				enabledUsers.stream().map(UserResponse::getUserId).toList(),
				LocalDate.now());
		log.info("오늘 체중 기록이 있는 사용자 수: {}", userIdsWithWeightUpdate.size());

		List<UserResponse> usersWithWeightUpdate = enabledUsers.stream()
				.filter(user -> !userIdsWithWeightUpdate.contains(user.getUserId())).toList();
		log.info("체중 기록이 필요한 사용자 수: {}", usersWithWeightUpdate.size());

		return usersWithWeightUpdate;
	}

	/**
	 * 일정 기간 동안 접속하지 않은 사용자 목록을 찾습니다. 예를 들어, days가 2인 경우 3일 이상(현재 기준 2일 이상 경과) 접속하지 않은 사용자 목록을
	 * 반환합니다.
	 *
	 * @param days 마지막 접속 이후 경과일 기준 (days일 이상 지난 사용자)
	 * @return 지정된 일수동안 접속하지 않은 사용자 목록
	 */
	@Transactional(readOnly = true)
	public List<UserResponse> findUsersInactiveForDays(int days) {
		log.info("findUsersInactiveForDays 메서드 실행: {}일 이상 미접속 사용자 조회", days);
		List<UserResponse> enabledUsers = userClient.getUsersWithNotificationsEnabled().getData();
		log.info("알림 활성화된 사용자 수: {}", enabledUsers.size());

		List<UserResponse> userIdsInactiveForDays = enabledUsers.stream()
				.filter(userResponse -> userResponse.getUpdatedAt().toLocalDate().plusDays(days)
						.isBefore(LocalDate.now()))
				.toList();
		log.info("{}일 이상 접속하지 않은 사용자 수: {}", days, userIdsInactiveForDays.size());

		return userIdsInactiveForDays;
	}
}
