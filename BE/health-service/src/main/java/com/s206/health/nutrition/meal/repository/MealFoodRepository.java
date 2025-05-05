package com.s206.health.nutrition.meal.repository;

import com.s206.health.nutrition.meal.entity.MealFood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealFoodRepository extends JpaRepository<MealFood, Integer> {
}