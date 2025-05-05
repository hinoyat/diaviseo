package com.s206.health.nutrition.meal.repository;

import com.s206.health.nutrition.meal.entity.MealTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealTimeRepository extends JpaRepository<MealTime, Integer> {
}