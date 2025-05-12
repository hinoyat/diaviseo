package com.s206.health.nutrition.meal.repository;

import com.s206.health.nutrition.meal.entity.MealTime;
import com.s206.health.nutrition.meal.entity.MealType;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MealTimeRepository extends JpaRepository<MealTime, Integer> {

	// 점심 미입력 유저 조회 쿼리
	@Query("SELECT DISTINCT m.userId FROM Meal m JOIN m.mealTimes mt " +
			"WHERE m.userId IN :userIds " +
			"AND m.mealDate = :date " +
			"AND mt.mealType = :mealType " +
			"AND m.isDeleted = false " +
			"AND mt.isDeleted = false")
	List<Integer> findUserIdsWithMealTime(
			@Param("userIds") List<Integer> userIds,
			@Param("date") LocalDate date,
			@Param("mealType") MealType mealType);
}