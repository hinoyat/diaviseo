package com.s206.health.nutrition.meal.repository;

import com.s206.health.nutrition.food.entity.Food;
import com.s206.health.nutrition.meal.entity.MealFood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MealFoodRepository extends JpaRepository<MealFood, Integer> {

    @Query(value = "SELECT f.* FROM food_information_tb f " +
            "JOIN (SELECT mf.food_id, MAX(m.meal_date) as latest_date, MAX(mt.eating_time) as latest_time " +
            "FROM meal_food_tb mf " +
            "JOIN meal_time_tb mt ON mf.meal_time_id = mt.meal_time_id " +
            "JOIN meal_tb m ON mt.meal_id = m.meal_id " +
            "WHERE m.user_id = :userId AND m.is_deleted = 0 AND mt.is_deleted = 0 " +
            "GROUP BY mf.food_id " +
            "ORDER BY latest_date DESC, latest_time DESC " +
            "LIMIT :limit) latest_foods " +
            "ON f.food_id = latest_foods.food_id",
            nativeQuery = true)
    List<Food> findDistinctRecentFoodsByUserId(@Param("userId") Integer userId, @Param("limit") int limit);
}