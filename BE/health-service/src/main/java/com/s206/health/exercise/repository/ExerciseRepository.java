package com.s206.health.exercise.repository;

import com.s206.health.exercise.entity.Exercise;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {

	// 특정 사용자의 운동 기록 전체 조회 (삭제되지 않은 것만)
	List<Exercise> findByUserIdAndIsDeletedFalseOrderByExerciseDateDesc(Integer userId);

	// 특정 사용자의 특정 운동 기록 조회
	Optional<Exercise> findByExerciseIdAndUserId(Integer exerciseId, Integer userId);

	// 특정 날짜 범위 내의 운동 기록 조회
	List<Exercise> findByUserIdAndExerciseDateBetweenAndIsDeletedFalseOrderByExerciseDateDesc(
			Integer userId,
			LocalDateTime startDate,
			LocalDateTime endDate
	);


	// 운동 기록 입력한 유저 목록 반환
	@Query(value = "select DISTINCT user_id from exercise_tb WHERE user_id IN (:userIds) AND DATE(exercise_date) = :date AND is_deleted = false", nativeQuery = true)
	List<Integer> findUserIdsWithExerciseOnDate(@Param("userIds") List<Integer> userIds,
			@Param("date") LocalDate date);
}