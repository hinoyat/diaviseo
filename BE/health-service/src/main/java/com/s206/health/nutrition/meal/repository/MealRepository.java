package com.s206.health.nutrition.meal.repository;

import com.s206.health.nutrition.meal.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MealRepository extends JpaRepository<Meal, Integer> {

    List<Meal> findByUserIdAndMealDate(Integer userId, LocalDate mealDate);

    List<Meal> findByUserIdAndMealDateBetween(Integer userId, LocalDate startDate, LocalDate endDate);

    List<Meal> findByUserIdAndMealDateAndIsDeletedFalse(Integer userId, LocalDate mealDate);

    @Query("SELECT " +
            "COALESCE(SUM(f.calorie * mf.quantity), 0), " +
            "COALESCE(SUM(f.carbohydrate * mf.quantity), 0), " +
            "COALESCE(SUM(f.protein * mf.quantity), 0), " +
            "COALESCE(SUM(f.fat * mf.quantity), 0), " +
            "COALESCE(SUM(f.sweet * mf.quantity), 0), " +
            "COALESCE(SUM(f.sodium * mf.quantity), 0) " +
            "FROM Meal m JOIN m.mealTimes mt JOIN mt.mealFoods mf JOIN mf.food f " +
            "WHERE m.userId = :userId AND m.mealDate = :date " +
            "AND m.isDeleted = false AND mt.isDeleted = false")
    Object[] calculateDailyNutritionRaw(@Param("userId") Integer userId, @Param("date") LocalDate date);

    @Query("SELECT m.mealDate as date, " +
            "SUM(f.calorie * mf.quantity) as totalCalorie, " +
            "SUM(f.carbohydrate * mf.quantity) as totalCarbohydrate, " +
            "SUM(f.protein * mf.quantity) as totalProtein, " +
            "SUM(f.fat * mf.quantity) as totalFat, " +
            "SUM(f.sweet * mf.quantity) as totalSugar, " +
            "SUM(f.sodium * mf.quantity) as totalSodium " +
            "FROM Meal m JOIN m.mealTimes mt JOIN mt.mealFoods mf JOIN mf.food f " +
            "WHERE m.userId = :userId AND m.mealDate BETWEEN :startDate AND :endDate " +
            "AND m.isDeleted = false AND mt.isDeleted = false " +
            "GROUP BY m.mealDate")
    List<Object[]> calculateWeeklyNutrition(@Param("userId") Integer userId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
}